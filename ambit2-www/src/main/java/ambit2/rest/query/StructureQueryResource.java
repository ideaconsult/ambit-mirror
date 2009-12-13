package ambit2.rest.query;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.logging.Logger;

import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.restlet.Client;
import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.exceptions.NotFoundException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.data.MoleculeTools;
import ambit2.core.processors.structure.AtomConfigurator;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.ARFFReporter;
import ambit2.db.reporters.CMLReporter;
import ambit2.db.reporters.CSVReporter;
import ambit2.db.reporters.ImageReporter;
import ambit2.db.reporters.PDFReporter;
import ambit2.db.reporters.SDFReporter;
import ambit2.db.reporters.SmilesReporter;
import ambit2.db.search.structure.QueryStructureByID;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.DocumentConvertor;
import ambit2.rest.ImageConvertor;
import ambit2.rest.OutputStreamConvertor;
import ambit2.rest.OutputWriterConvertor;
import ambit2.rest.PDFConvertor;
import ambit2.rest.RDFJenaConvertor;
import ambit2.rest.RepresentationConvertor;
import ambit2.rest.StringConvertor;
import ambit2.rest.dataset.DatasetRDFReporter;
import ambit2.rest.property.PropertyDOMParser;
import ambit2.rest.structure.CompoundHTMLReporter;
import ambit2.rest.structure.CompoundURIReporter;

/**
 * Abstract parent class for all resources that retrieve compounds/conformers from database
 * @author nina
 *
 * @param <Q>
 */
public abstract class StructureQueryResource<Q extends IQueryRetrieval<IStructureRecord>>  
									extends QueryResource<Q,IStructureRecord> {
	public static final String feature_URI = "feature_uris[]";
	protected String media;
	protected Template template;
	protected enum QueryType  {smiles,url};
	public Template getTemplate() {
		return template;
	}
	public void setTemplate(Template template) {
		this.template = (template==null)?new Template(null):template;

	}
	protected String getDefaultTemplateURI(Context context, Request request,Response response) {
		return null;//return String.format("riap://application%s/All/Identifiers/view/tree",OntologyResource.resource);
	}
	protected Template createTemplate(Context context, Request request,
			Response response) throws ResourceException {
		
		try {
			Template profile = new Template(null);
			profile.setId(-1);				
			
			Form form = request.getResourceRef().getQueryAsForm();
			String[] featuresURI =  form.getValuesArray(feature_URI);

			for (String featureURI:featuresURI) 
				readFeatures(featureURI, profile);
			
			if (profile.size() == 0) {
				readFeatures(getDefaultTemplateURI(context,request,response), profile);

			}
			return profile;
		} catch (Exception x) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x);
		}
		
	}	
	protected void readFeatures(String uri,final Template profile) {
		if (uri==null) return;
		Representation r = null;
		try {
			
			Logger.getLogger(uri);
			ClientResource client = new ClientResource(uri);
			client.setClientInfo(getRequest().getClientInfo());
			client.setReferrerRef(getRequest().getOriginalRef());
			r = client.get(MediaType.TEXT_XML);
			
			PropertyDOMParser parser = new PropertyDOMParser() {
				@Override
				public void handleItem(Property property)
						throws AmbitException {
					if (property!= null)  {
						property.setEnabled(true);
						profile.add(property);
					}
				}
			};		
			parser.parse(new InputStreamReader(r.getStream(),"UTF-8"));
		
		} catch (Exception x) {
			Logger.getLogger(getClass().getName()).severe(x.getMessage());

		} finally {
			
			try {if (r != null) r.getStream().close(); } catch (Exception x) {}
			
		}
	}	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		customizeVariants(new MediaType[] {
				MediaType.TEXT_HTML,
				ChemicalMediaType.CHEMICAL_MDLSDF,
				ChemicalMediaType.CHEMICAL_SMILES,
				ChemicalMediaType.CHEMICAL_CML,
				MediaType.IMAGE_PNG,
				MediaType.IMAGE_BMP,
				MediaType.IMAGE_JPEG,
				MediaType.IMAGE_TIFF,
				MediaType.IMAGE_GIF,
				MediaType.APPLICATION_PDF,
				MediaType.TEXT_XML,
				MediaType.TEXT_URI_LIST,
				MediaType.TEXT_PLAIN,
				ChemicalMediaType.TEXT_YAML,
				ChemicalMediaType.WEKA_ARFF,
				MediaType.TEXT_CSV,
				MediaType.APPLICATION_RDF_XML,
				MediaType.APPLICATION_RDF_TURTLE,
				MediaType.APPLICATION_RDF_TRIG,
				MediaType.APPLICATION_RDF_TRIX,
				MediaType.TEXT_RDF_N3,
				MediaType.TEXT_RDF_NTRIPLES,
				MediaType.APPLICATION_JAVA_OBJECT
				});
				
	}
	@Override
	public RepresentationConvertor createConvertor(Variant variant)
			throws AmbitException, ResourceException {
		/* workaround for clients not being able to set accept headers */
		if ((queryObject == null) && !(variant.getMediaType().equals(MediaType.TEXT_HTML))) 
			throw new NotFoundException();
		
		setTemplate(template);
		Form acceptform = getRequest().getResourceRef().getQueryAsForm();
		String media = acceptform.getFirstValue("accept-header");
		if (media != null) {
			variant.setMediaType(new MediaType(Reference.decode(media)));
		}
		if (variant.getMediaType().equals(ChemicalMediaType.CHEMICAL_MDLSDF)) {
			return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(
					new SDFReporter<QueryStructureByID>(template),ChemicalMediaType.CHEMICAL_MDLSDF);
		} else if (variant.getMediaType().equals(ChemicalMediaType.CHEMICAL_CML)) {
				return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(
						new CMLReporter<QueryStructureByID>(),ChemicalMediaType.CHEMICAL_CML);				
		} else if (variant.getMediaType().equals(ChemicalMediaType.CHEMICAL_SMILES)) {
				return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(
						new SmilesReporter<QueryStructureByID>(),ChemicalMediaType.CHEMICAL_SMILES);
		} else if (variant.getMediaType().equals(MediaType.APPLICATION_PDF)) {
			return new PDFConvertor<IStructureRecord, QueryStructureByID,PDFReporter<QueryStructureByID>>(
					new PDFReporter<QueryStructureByID>(getTemplate()));				
		} else if (variant.getMediaType().equals(MediaType.TEXT_PLAIN)) {
			return new StringConvertor(
					new SmilesReporter<QueryStructureByID>(),MediaType.TEXT_PLAIN);
		} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
			CompoundURIReporter<QueryStructureByID> reporter = new CompoundURIReporter<QueryStructureByID>(getRequest(),queryObject.isPrescreen());
			reporter.setDelimiter("\n");
			return new StringConvertor(reporter,MediaType.TEXT_URI_LIST);			
		} else if (variant.getMediaType().equals(MediaType.IMAGE_PNG)) {
			return new ImageConvertor<IStructureRecord, QueryStructureByID>(
					new ImageReporter<QueryStructureByID>(),MediaType.IMAGE_PNG);	
		} else if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
			return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(
					new CompoundHTMLReporter(getRequest(),true,getTemplate()),MediaType.TEXT_HTML);
		} else if (variant.getMediaType().equals(ChemicalMediaType.WEKA_ARFF)) {
			return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(
					new ARFFReporter(getTemplate()),ChemicalMediaType.WEKA_ARFF);			
		} else if (variant.getMediaType().equals(MediaType.TEXT_CSV)) {
			return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(
					new CSVReporter(getTemplate()),MediaType.TEXT_CSV);
		} else if (variant.getMediaType().equals(MediaType.APPLICATION_RDF_XML) ||
				variant.getMediaType().equals(MediaType.APPLICATION_RDF_TURTLE) ||
				variant.getMediaType().equals(MediaType.TEXT_RDF_N3) ||
				variant.getMediaType().equals(MediaType.TEXT_RDF_NTRIPLES) ||
				variant.getMediaType().equals(MediaType.APPLICATION_RDF_TRIG) ||
				variant.getMediaType().equals(MediaType.APPLICATION_RDF_TRIX)
				) {
			return new RDFJenaConvertor<IStructureRecord, IQueryRetrieval<IStructureRecord>>(
					new DatasetRDFReporter(getRequest(),variant.getMediaType(),getTemplate()),variant.getMediaType());			

					
		} else
			return new DocumentConvertor(new QueryXMLReporter<Q>(getRequest()));
	}
	
	protected String getMediaParameter(Request request) {
		try {
			Object m = request.getAttributes().get("media");
			return m==null?null:Reference.decode(m.toString());
		} catch (Exception x) {
			return null;
		}	
	}
	
	protected IMolecule getFromURI(String uri) throws ResourceException {
		Reader reader = null;
		try {
			Request rq = new Request();
			Client client = new Client(Protocol.HTTP);
			rq.setResourceRef(uri);
			rq.setMethod(Method.GET);
			rq.getClientInfo().getAcceptedMediaTypes().clear();
			rq.getClientInfo().getAcceptedMediaTypes().add(new Preference<MediaType>(ChemicalMediaType.CHEMICAL_MDLSDF));
			Response resp = client.handle(rq);
			if (resp.getStatus().equals(Status.SUCCESS_OK)) {
				reader = new InputStreamReader(resp.getEntity().getStream());
				return MoleculeTools.readMolfile(reader);
			} else return null;				
		} catch (InvalidSmilesException x) {
			throw new ResourceException(
					Status.CLIENT_ERROR_BAD_REQUEST,
					String.format("Invalid smiles %s",getRequest().getAttributes().get("smiles")),
					x
					);

		} catch (Exception x) {
			throw new ResourceException(
					Status.SERVER_ERROR_INTERNAL,x.getMessage(),x
					);
		} finally {
			try {reader.close();} catch (Exception x){};
		}		
	}	
	protected String getSMILES(Form form,boolean aromatic) throws ResourceException {
		QueryType query_type;
		String query_smiles;
		try {
			query_type = QueryType.valueOf(form.getFirstValue("type"));

		} catch (Exception x) {
			query_type = QueryType.smiles;
		}		
		switch (query_type) {
		case smiles:

				return Reference.decode(form.getFirstValue(QueryResource.search_param));

		case url: try {
				query_smiles = Reference.decode(form.getFirstValue(QueryResource.search_param));
				if (query_smiles==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Empty query");
				IAtomContainer mol = getFromURI(query_smiles.trim());
				AtomConfigurator c = new AtomConfigurator();
				mol=c.process(mol);
				CDKHueckelAromaticityDetector.detectAromaticity(mol);   
				SmilesGenerator g = new SmilesGenerator();
				g.setUseAromaticityFlag(aromatic);
				return g.createSMILES((IMolecule)mol);
		} catch (Exception x) {
				throw new ResourceException(
						Status.CLIENT_ERROR_BAD_REQUEST,
						x.getMessage(),
						x
						);
			}	
		default:
			return null;
		}

	}		
	protected IMolecule getMolecule(Form form) throws ResourceException {
		QueryType query_type;
		String query_smiles;
		try {
			query_type = QueryType.valueOf(form.getFirstValue("type"));

		} catch (Exception x) {
			query_type = QueryType.smiles;
		}		
		switch (query_type) {
		case smiles:
			try {
				query_smiles = Reference.decode(form.getFirstValue(QueryResource.search_param));
				if (query_smiles==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Empty smiles");
				return MoleculeTools.getMolecule(query_smiles);			
				
			} catch (InvalidSmilesException x) {
				throw new ResourceException(
						Status.CLIENT_ERROR_BAD_REQUEST,
						String.format("Invalid smiles %s",getRequest().getAttributes().get("smiles")),
						x
						);
			} catch (ResourceException x) {
				throw x;
			} catch (Exception x) {
				throw new ResourceException(
						Status.SERVER_ERROR_INTERNAL,
						x.getMessage(),
						x
						);
			}			

		case url: {
			query_smiles = Reference.decode(form.getFirstValue(QueryResource.search_param));
			if (query_smiles==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Empty query");
			return getFromURI(query_smiles);

			}
		default:
			return null;
		}

	}	
}
