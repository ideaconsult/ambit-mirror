package ambit2.rest.dataset;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.ISourceDataset;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.SourceDataset;
import ambit2.base.exceptions.AmbitException;
import ambit2.core.processors.structure.key.IStructureKey;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.readers.RetrieveDatasets;
import ambit2.db.search.StringCondition;
import ambit2.db.update.dataset.AbstractReadDataset;
import ambit2.db.update.dataset.QueryDatasetByFeatures;
import ambit2.db.update.dataset.ReadDataset;
import ambit2.db.update.storedquery.ReadStoredQuery;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.OutputWriterConvertor;
import ambit2.rest.RDFJenaConvertor;
import ambit2.rest.RepresentationConvertor;
import ambit2.rest.ResourceDoc;
import ambit2.rest.StringConvertor;
import ambit2.rest.YAMLConvertor;
import ambit2.rest.error.InvalidResourceIDException;
import ambit2.rest.query.QueryResource;

/**
 * Dataset resource - A set of chemical compounds and assigned features
 * 
 * http://opentox.org/development/wiki/dataset
 * 
 * Supported operations:
 * <ul>
 * <li>GET /dataset  ; returns text/uri-list or text/xml or text/html
 * <li>POST /dataset ; accepts chemical/x-mdl-sdfile or multipart/form-data (SDF,mol, txt, csv, xls,all formats supported in Ambit)
 * <li>GET 	 /dataset/{id}  ; returns text/uri-list or text/xml
 * <li>PUT and DELETE not yet supported
 * </ul>
 * 
 * @author nina 
 *
 */
public class DatasetsResource extends QueryResource<IQueryRetrieval<ISourceDataset>, ISourceDataset> {
	
	protected SourceDataset dataset;
	public final static String datasets = "/datasets";	
	public final static String metadata = "/metadata";	
	protected FileUpload upload;
	protected IStructureKey matcher;
	
	public enum search_features {

		feature_name {
			@Override
			public void setProperty(Property p, Object arg1) {
				p.setName(arg1.toString());
			}
		},
		feature_sameas {
			@Override
			public void setProperty(Property p, Object arg1) {
				p.setLabel(arg1.toString());
			}
		},
		feature_hassource {
			@Override
			public void setProperty(Property p, Object arg1) {
				p.setReference(new LiteratureEntry(arg1.toString(),""));

				
			}
		},
		feature_type {
			@Override
			public void setProperty(Property p, Object arg1) {
				p.setClazz(arg1.toString().equals("STRING")?String.class:Number.class);
				
			}
		};
		public abstract void setProperty(Property p, Object value);
	}

	//public final static String datasetID =  String.format("%s/{%s}",DatasetsResource.datasets,datasetKey);
	
	protected boolean collapsed;

	public DatasetsResource() {
		super();
		setDocumentation(new ResourceDoc("dataset","Dataset"));
	}
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		customizeVariants(new MediaType[] {
				MediaType.TEXT_HTML,
				MediaType.TEXT_URI_LIST,ChemicalMediaType.TEXT_YAML,
				ChemicalMediaType.CHEMICAL_SMILES,
				ChemicalMediaType.CHEMICAL_CML,
				ChemicalMediaType.CHEMICAL_MDLSDF,
				ChemicalMediaType.CHEMICAL_MDLMOL,
				ChemicalMediaType.WEKA_ARFF,
				MediaType.APPLICATION_RDF_XML,
				MediaType.APPLICATION_RDF_TURTLE,
				MediaType.TEXT_RDF_N3,
				MediaType.TEXT_RDF_NTRIPLES,
				MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JAVA_OBJECT});
		upload = new FileUpload();
		upload.setRequest(getRequest());
		upload.setResponse(getResponse());
		upload.setContext(getContext());
		upload.setApplication(getApplication());
		upload.setDataset(dataset);
		
	}
	@Override
	protected IQueryRetrieval<ISourceDataset> createQuery(Context context,
			Request request, Response response) throws ResourceException {
		
		Form form = request.getResourceRef().getQueryAsForm();
		AbstractReadDataset query = null;
		Property property = new Property(null);
		property.setClazz(null);property.setLabel(null);property.setReference(null);
		for (search_features sf : search_features.values()) {
			Object id = form.getFirstValue(sf.name());
			if (id != null)  { //because we are not storing full local references!
				if (search_features.feature_hassource.equals(sf)) {
					String parent = getRequest().getRootRef().toString();
					int p = id.toString().indexOf(parent);
					if (p>=0) {
						//yet one more hack ... should store at least prefixes
						id = id.toString().substring(p+parent.length()).replace("/algorithm/","").replace("/model/", "");
					}
				}
				
				sf.setProperty(property,id);
				if (query == null) query = new QueryDatasetByFeatures(property);
			}
		}
		
		if (query == null) {
			query = new ReadDataset();
			query.setValue(null);
		}

		collapsed = false;
		
		Object id = request.getAttributes().get(DatasetStructuresResource.datasetKey);
		if (id != null)  try {
			Integer idnum = new Integer(Reference.decode(id.toString()));
			dataset = new SourceDataset();
			dataset.setId(idnum);
			query.setValue(dataset);
		} catch (NumberFormatException x) {
			if (id.toString().startsWith(DatasetStructuresResource.QR_PREFIX)) {
				String key = id.toString().substring(DatasetStructuresResource.QR_PREFIX.length());
				try {
					IQueryRetrieval<ISourceDataset> q = new ReadStoredQuery(Integer.parseInt(key.toString()));
					return q;
				} catch (NumberFormatException xx) {
					throw new InvalidResourceIDException(id);
				}
			} else {
				dataset = new SourceDataset();
				dataset.setName(id.toString());
				query.setValue(dataset);
			}
		} catch (Exception x) {
			throw new InvalidResourceIDException(id);
		}
		
		
		Object key = form.getFirstValue(QueryResource.search_param);
		if (key != null) {
			RetrieveDatasets query_by_name = new RetrieveDatasets(null,new SourceDataset(Reference.decode(key.toString())));
			query_by_name.setCondition(StringCondition.getInstance(StringCondition.C_REGEXP));
			return query_by_name;
		}
		return query;
	}
	@Override
	public RepresentationConvertor createConvertor(Variant variant)
			throws AmbitException, ResourceException {

	if (variant.getMediaType().equals(ChemicalMediaType.TEXT_YAML)) {
			return new YAMLConvertor(new DatasetYamlReporter(getRequest(),getDocumentation()),ChemicalMediaType.TEXT_YAML);			
	} else if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
		return new OutputWriterConvertor(
				new DatasetsHTMLReporter(getRequest(),collapsed,getDocumentation()),MediaType.TEXT_HTML);
	} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
		return new StringConvertor(	new DatasetURIReporter<IQueryRetrieval<ISourceDataset>>(getRequest(),getDocumentation()) {
			@Override
			public Object processItem(ISourceDataset dataset) throws AmbitException  {
				super.processItem(dataset);
				try {
				output.write('\n');
				} catch (Exception x) {}
				return null;
			}
		},MediaType.TEXT_URI_LIST);
	} else if (variant.getMediaType().equals(MediaType.APPLICATION_RDF_XML) ||
			variant.getMediaType().equals(MediaType.APPLICATION_RDF_TURTLE) ||
			variant.getMediaType().equals(MediaType.TEXT_RDF_N3) ||
			variant.getMediaType().equals(MediaType.TEXT_RDF_NTRIPLES) ||
			variant.getMediaType().equals(MediaType.APPLICATION_RDF_TRIG) ||
			variant.getMediaType().equals(MediaType.APPLICATION_RDF_TRIX) ||
			variant.getMediaType().equals(MediaType.APPLICATION_JSON)
			) {
		return new RDFJenaConvertor<ISourceDataset, IQueryRetrieval<ISourceDataset>>(
				new MetadataRDFReporter<IQueryRetrieval<ISourceDataset>>(getRequest(),
						getDocumentation(),variant.getMediaType()),variant.getMediaType());			

		
	} else //html 	
		return new OutputWriterConvertor(
				new DatasetsHTMLReporter(getRequest(),collapsed,getDocumentation()),MediaType.TEXT_HTML);
	}
	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		return  upload.upload(entity,variant,true,false,
				getToken()
				);
		
	}
	/**
	 * Creates a dataset, but if a structure is found, import only properties 
	 */
	@Override
	protected Representation put(Representation entity, Variant variant)
			throws ResourceException {
		return  upload.upload(entity,variant,true,true,
				getToken()
				);
	}



}
