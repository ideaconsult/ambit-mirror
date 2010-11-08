package ambit2.rest.query;

import java.awt.Dimension;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;

import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.restlet.Client;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.wadl.WadlRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.exceptions.NotFoundException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.data.MoleculeTools;
import ambit2.core.processors.structure.AtomConfigurator;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.CMLReporter;
import ambit2.db.reporters.CSVReporter;
import ambit2.db.reporters.ImageReporter;
import ambit2.db.reporters.PDFReporter;
import ambit2.db.reporters.SDFReporter;
import ambit2.db.reporters.SmilesReporter;
import ambit2.db.reporters.SmilesReporter.Mode;
import ambit2.db.search.structure.QueryStructureByID;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.DBConnection;
import ambit2.rest.ImageConvertor;
import ambit2.rest.OpenTox;
import ambit2.rest.OutputWriterConvertor;
import ambit2.rest.PDFConvertor;
import ambit2.rest.RDFJenaConvertor;
import ambit2.rest.RDFStaXConvertor;
import ambit2.rest.RepresentationConvertor;
import ambit2.rest.ResourceDoc;
import ambit2.rest.StringConvertor;
import ambit2.rest.dataset.ARFFResourceReporter;
import ambit2.rest.dataset.DatasetRDFReporter;
import ambit2.rest.dataset.DatasetRDFStaxReporter;
import ambit2.rest.property.ProfileReader;
import ambit2.rest.structure.CompoundHTMLReporter;
import ambit2.rest.structure.ConformerURIReporter;

/**
 * Abstract parent class for all resources that retrieve compounds/conformers from database
 * @author nina
 *
 * @param <Q>
 */
public abstract class StructureQueryResource<Q extends IQueryRetrieval<IStructureRecord>>  
									extends QueryResource<Q,IStructureRecord> {

	protected String media;
	protected Template template;
	protected Profile groupProperties;

	public StructureQueryResource() {
		super();
		setDocumentation(new ResourceDoc("dataset","Dataset"));
	}
	public Profile getGroupProperties() {
		return groupProperties;
	}
	public void setGroupProperties(Profile groupProperties) {
		this.groupProperties = groupProperties;
	}
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
	protected void setGroupProperties(Context context, Request request,
			Response response) throws ResourceException {
		Form form = getParams();
		String[] gp = OpenTox.params.sameas.getValuesArray(form);
		if (gp!=null) {
			groupProperties = new Profile();
			for (String g: gp) {
				Property p = new Property(g);
				p.setEnabled(true);
				p.setLabel(g);
				groupProperties.add(p);
			}
				
		}
	}
	protected Template createTemplate(Context context, Request request,
			Response response) throws ResourceException {
		Form form = getParams();
		return createTemplate(form);
	}			
	protected Template createTemplate(Form form) throws ResourceException {
		String[] featuresURI =  OpenTox.params.feature_uris.getValuesArray(form);
		return createTemplate(getContext(),getRequest(),getResponse(), featuresURI);
	}
	protected Template createTemplate(Context context, Request request,
			Response response,String[] featuresURI) throws ResourceException {
		
		try {
			Template profile = new Template(null);
			profile.setId(-1);				
			
			ProfileReader reader = new ProfileReader(getRequest().getRootRef(),profile);
			reader.setCloseConnection(false);
			
			

			DBConnection dbc = new DBConnection(getContext());
			Connection conn = dbc.getConnection(getRequest());
			try {
				for (String featureURI:featuresURI) {
					if (featureURI == null) continue;
					reader.setConnection(conn);
					profile = reader.process(new Reference(featureURI));
					reader.setProfile(profile);
					
				}
				//	readFeatures(featureURI, profile);
				if (profile.size() == 0) {
					reader.setConnection(conn);
					String templateuri = getDefaultTemplateURI(context,request,response);
					if (templateuri!= null) profile = reader.process(new Reference(templateuri));
					reader.setProfile(profile);
				}
			} catch (Exception x) {
				x.printStackTrace();
			} finally {
				//the reader closes the connection
				reader.setCloseConnection(true);
				try { reader.close();} catch (Exception x) {}
				//try { conn.close();} catch (Exception x) {}
			}
			return profile;
		} catch (Exception x) {
			getLogger().info(x.getMessage());
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x);
		}
		
	}	
	


	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		customizeVariants(new MediaType[] {
				MediaType.TEXT_HTML,
				ChemicalMediaType.CHEMICAL_MDLSDF,
				ChemicalMediaType.CHEMICAL_MDLMOL,
				ChemicalMediaType.CHEMICAL_SMILES,
				ChemicalMediaType.CHEMICAL_INCHI,
				ChemicalMediaType.CHEMICAL_CML,
				MediaType.APPLICATION_PDF,
				MediaType.TEXT_XML,
				MediaType.TEXT_URI_LIST,
				MediaType.TEXT_PLAIN,
				ChemicalMediaType.TEXT_YAML,
				MediaType.APPLICATION_JSON,
				ChemicalMediaType.WEKA_ARFF,
				MediaType.TEXT_CSV,
				MediaType.APPLICATION_RDF_XML,
				MediaType.APPLICATION_RDF_TURTLE,
				MediaType.APPLICATION_RDF_TRIG,
				MediaType.APPLICATION_RDF_TRIX,
				MediaType.TEXT_RDF_N3,
				MediaType.APPLICATION_JSON,
				MediaType.TEXT_RDF_NTRIPLES,
				MediaType.APPLICATION_JAVA_OBJECT,
				MediaType.APPLICATION_WADL

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
			variant.setMediaType(new MediaType(media));
		}
		
		if (variant.getMediaType().equals(ChemicalMediaType.CHEMICAL_MDLSDF)) {
			return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(
					new SDFReporter<QueryStructureByID>(template,getGroupProperties()),ChemicalMediaType.CHEMICAL_MDLSDF);
		} else if (variant.getMediaType().equals(ChemicalMediaType.CHEMICAL_MDLMOL)) {
				return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(
						new SDFReporter<QueryStructureByID>(new Template(),getGroupProperties(),true),ChemicalMediaType.CHEMICAL_MDLMOL);			
		} else if (variant.getMediaType().equals(ChemicalMediaType.CHEMICAL_CML)) {
				return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(
						new CMLReporter<QueryStructureByID>(),ChemicalMediaType.CHEMICAL_CML);				
		} else if (variant.getMediaType().equals(ChemicalMediaType.CHEMICAL_SMILES)) {
				return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(
						new SmilesReporter<QueryStructureByID>(true,getTemplate()),ChemicalMediaType.CHEMICAL_SMILES);
		} else if (variant.getMediaType().equals(ChemicalMediaType.CHEMICAL_INCHI)) {
			return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(
					new SmilesReporter<QueryStructureByID>(false,Mode.InChI,getTemplate()),ChemicalMediaType.CHEMICAL_INCHI);				
		} else if (variant.getMediaType().equals(MediaType.APPLICATION_PDF)) {
			return new PDFConvertor<IStructureRecord, QueryStructureByID,PDFReporter<QueryStructureByID>>(
					new PDFReporter<QueryStructureByID>(getTemplate(),getGroupProperties()));				
		} else if (variant.getMediaType().equals(MediaType.TEXT_PLAIN)) {
			CSVReporter csvreporter = new CSVReporter(getTemplate(),groupProperties,getRequest().getRootRef().toString());
			csvreporter.setSeparator("\t");
			csvreporter.setNumberofHeaderLines(0);
			csvreporter.setWriteCompoundURI(false);
			return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(
					csvreporter,MediaType.TEXT_PLAIN);
		} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
			ConformerURIReporter<QueryStructureByID> reporter = 
				new ConformerURIReporter<QueryStructureByID>(getRequest(),queryObject.isPrescreen(),getDocumentation());
			reporter.setDelimiter("\n");
			return new StringConvertor(reporter,MediaType.TEXT_URI_LIST);			
		} else if (variant.getMediaType().equals(MediaType.IMAGE_PNG)) {
			return new ImageConvertor<IStructureRecord, QueryStructureByID>(
					new ImageReporter<QueryStructureByID>(),MediaType.IMAGE_PNG);	
		} else if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
			Dimension d = new Dimension(150,150);
			Form form = getRequest().getResourceRef().getQueryAsForm();
			try {
				
				d.width = Integer.parseInt(form.getFirstValue("w").toString());
			} catch (Exception x) {}
			try {
				d.height = Integer.parseInt(form.getFirstValue("h").toString());
			} catch (Exception x) {}			
			return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(
					new CompoundHTMLReporter(getRequest(),getDocumentation(),true,null,getTemplate(),getGroupProperties(),d),MediaType.TEXT_HTML);
		} else if (variant.getMediaType().equals(ChemicalMediaType.WEKA_ARFF)) {
			return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(
					new ARFFResourceReporter(getTemplate(),getGroupProperties(),getRequest(),getDocumentation()),ChemicalMediaType.WEKA_ARFF);			
		} else if (variant.getMediaType().equals(MediaType.TEXT_CSV)) {
			return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(
					new CSVReporter(getTemplate(),groupProperties,getRequest().getRootRef().toString()),MediaType.TEXT_CSV);
		} else if (variant.getMediaType().equals(MediaType.APPLICATION_RDF_XML)) {
			switch (rdfwriter) {
			case stax: {
				return new RDFStaXConvertor<IStructureRecord, IQueryRetrieval<IStructureRecord>>(
						new DatasetRDFStaxReporter(getRequest(),getDocumentation(),getTemplate(),getGroupProperties())				
						);				
			}
			default : { //jena
				return new RDFJenaConvertor<IStructureRecord, IQueryRetrieval<IStructureRecord>>(
						new DatasetRDFReporter(getRequest(),getDocumentation(),variant.getMediaType(),getTemplate(),getGroupProperties()),variant.getMediaType());
				
			}
			}
		} else if (
				variant.getMediaType().equals(MediaType.APPLICATION_RDF_TURTLE) ||
				variant.getMediaType().equals(MediaType.TEXT_RDF_N3) ||
				variant.getMediaType().equals(MediaType.TEXT_RDF_NTRIPLES) ||
				variant.getMediaType().equals(MediaType.APPLICATION_RDF_TRIG) ||
				variant.getMediaType().equals(MediaType.APPLICATION_RDF_TRIX) ||
				variant.getMediaType().equals(MediaType.APPLICATION_JSON)
				) {
			return new RDFJenaConvertor<IStructureRecord, IQueryRetrieval<IStructureRecord>>(
					new DatasetRDFReporter(getRequest(),getDocumentation(),variant.getMediaType(),getTemplate(),getGroupProperties()),variant.getMediaType());			
		} else
			return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(
					new SDFReporter<QueryStructureByID>(template,getGroupProperties()),ChemicalMediaType.CHEMICAL_MDLSDF);
	}
	
	protected String getMediaParameter(Request request) {
		try {
			Object m = request.getAttributes().get("media");
			return m==null?null:Reference.decode(m.toString());
		} catch (Exception x) {
			return null;
		}	
	}
	
	protected String getSDFFromURI(String uri) throws ResourceException {
		Reader reader = null;
		long now = System.currentTimeMillis();
		try {
			Request rq = new Request();
			Client client = new Client(Protocol.HTTP);
			client.setConnectTimeout(5*60*1000); //5 min
			rq.setResourceRef(uri);
			rq.setMethod(Method.GET);
			rq.getClientInfo().getAcceptedMediaTypes().clear();
			rq.getClientInfo().getAcceptedMediaTypes().add(new Preference<MediaType>(ChemicalMediaType.CHEMICAL_MDLSDF));
			Response resp = client.handle(rq);
			if (resp.getStatus().equals(Status.SUCCESS_OK)) {
				return resp.getEntityAsText();
			} else return null;				
		
		} catch (Exception x) {
			if ((System.currentTimeMillis()-now) > (5*60*1000)) 
				throw new ResourceException(
						Status.CLIENT_ERROR_BAD_REQUEST,"Timeout 5 min exceeded"
						);		
			else
				throw new ResourceException(
					Status.SERVER_ERROR_INTERNAL,x.getMessage(),x
					);
		} finally {
			try {reader.close();} catch (Exception x){};
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
			query_type = QueryType.valueOf(form.getFirstValue("type").trim());

		} catch (Exception x) {
			query_type = QueryType.smiles;
		}		
		if (form.getFirstValue(QueryResource.search_param)==null) return null;
		switch (query_type) {
		case smiles:

				return form.getFirstValue(QueryResource.search_param).trim();

		case url: try {
				query_smiles = form.getFirstValue(QueryResource.search_param).trim();
				if (query_smiles==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Empty query");
				IAtomContainer mol = getFromURI(query_smiles.trim());
				AtomConfigurator c = new AtomConfigurator();
				mol=c.process(mol);
				CDKHueckelAromaticityDetector.detectAromaticity(mol);   
				SmilesGenerator g = new SmilesGenerator(true);
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
		String query_smiles="";
		try {
			query_type = QueryType.valueOf(form.getFirstValue("type"));

		} catch (Exception x) {
			query_type = QueryType.smiles;
		}		
		switch (query_type) {
		case smiles:
			try {
				query_smiles = form.getFirstValue(QueryResource.search_param);
				if (query_smiles==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Empty smiles");
				return MoleculeTools.getMolecule(query_smiles);			
				
			} catch (InvalidSmilesException x) {
				throw new ResourceException(
						Status.CLIENT_ERROR_BAD_REQUEST,
						String.format("Invalid smiles %s",query_smiles),
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
			query_smiles = form.getFirstValue(QueryResource.search_param);
			if (query_smiles==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Empty query");
			return getFromURI(query_smiles);

			}
		default:
			return null;
		}

	}	
}
