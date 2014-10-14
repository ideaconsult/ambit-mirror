package ambit2.rest.dataset;

import java.sql.Connection;
import java.util.Map;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.restnet.i.freemarker.IFreeMarkerApplication;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.ISourceDataset;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.SourceDataset;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.StringCondition;
import ambit2.db.update.AbstractUpdate;
import ambit2.db.update.dataset.AbstractReadDataset;
import ambit2.db.update.dataset.QueryDatasetByFeatures;
import ambit2.db.update.dataset.ReadDataset;
import ambit2.db.update.dataset.UpdateDataset;
import ambit2.db.update.storedquery.ReadStoredQuery;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.DisplayMode;
import ambit2.rest.OpenTox;
import ambit2.rest.OutputWriterConvertor;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.RDFJenaConvertor;
import ambit2.rest.RepresentationConvertor;
import ambit2.rest.StringConvertor;
import ambit2.rest.error.InvalidResourceIDException;
import ambit2.rest.query.QueryResource;
import ambit2.rest.rdf.RDFMetaDatasetIterator;
import ambit2.rest.rdf.RDFObjectIterator;

public class MetadatasetResource extends QueryResource<IQueryRetrieval<ISourceDataset>, ISourceDataset> {
	protected SourceDataset dataset;
	public final static String metadata = "/metadata";	
	protected DisplayMode _dmode;
	protected IStructureRecord structureParam;
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
		},
		feature_id {
			@Override
			public void setProperty(Property p, Object arg1) {
				try {
					p.setId(Integer.parseInt(arg1.toString()));
				} catch (Exception x) {
					
				}
			}
		}

		;	
		public abstract void setProperty(Property p, Object value);
	}
	
	public MetadatasetResource() {
		super();
		_dmode = DisplayMode.singleitem;
		setHtmlbyTemplate(true);
	}
	
	@Override
	public String getTemplateName() {
		return "datasets.ftl";
	}
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		customizeVariants(new MediaType[] {
				MediaType.TEXT_HTML,
				MediaType.TEXT_URI_LIST,
				ChemicalMediaType.CHEMICAL_SMILES,
				ChemicalMediaType.CHEMICAL_CML,
				ChemicalMediaType.CHEMICAL_MDLSDF,
				ChemicalMediaType.CHEMICAL_MDLMOL,
				ChemicalMediaType.WEKA_ARFF,
				ChemicalMediaType.THREECOL_ARFF,
				MediaType.APPLICATION_RDF_XML,
				MediaType.APPLICATION_RDF_TURTLE,
				MediaType.TEXT_RDF_N3,
				MediaType.TEXT_RDF_NTRIPLES,
				MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JAVA_OBJECT});
	
		
	}
	
	@Override
	public RepresentationConvertor createConvertor(Variant variant)
			throws AmbitException, ResourceException {
	String filenamePrefix = getRequest().getResourceRef().getPath();
	if (variant.getMediaType().equals(MediaType.APPLICATION_JSON)) {
			return new OutputWriterConvertor(new MetadatasetJSONReporter<IQueryRetrieval<ISourceDataset>>(getRequest()),MediaType.APPLICATION_JSON);			
	} else if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
		return new OutputWriterConvertor(
				new DatasetsHTMLReporter(getRequest(),_dmode,getDocumentation(),headless),MediaType.TEXT_HTML);
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
		},MediaType.TEXT_URI_LIST,filenamePrefix);
	} else if (variant.getMediaType().equals(MediaType.APPLICATION_RDF_XML) ||
			variant.getMediaType().equals(MediaType.APPLICATION_RDF_TURTLE) ||
			variant.getMediaType().equals(MediaType.TEXT_RDF_N3) ||
			variant.getMediaType().equals(MediaType.TEXT_RDF_NTRIPLES) ||
			variant.getMediaType().equals(MediaType.APPLICATION_RDF_TRIG) ||
			variant.getMediaType().equals(MediaType.APPLICATION_RDF_TRIX) 
			) {
		return new RDFJenaConvertor<ISourceDataset, IQueryRetrieval<ISourceDataset>>(
				new MetadataRDFReporter<IQueryRetrieval<ISourceDataset>>(getRequest(),
						getDocumentation(),variant.getMediaType()),variant.getMediaType(),filenamePrefix);			

		
	} else //html 	
		return new OutputWriterConvertor(
				new DatasetsHTMLReporter(getRequest(),_dmode,getDocumentation(),headless),MediaType.TEXT_HTML);
	}
	
	@Override
	protected IQueryRetrieval<ISourceDataset> createQuery(Context context,
			Request request, Response response) throws ResourceException {
		return getQuery(context, request, response,false);
	}	

	protected IQueryRetrieval<ISourceDataset> getQuery(Context context,Request request, Response response, boolean IDcanBeEmpty) throws ResourceException {
		
		Form form = getResourceRef(request).getQueryAsForm();
		try { headless = Boolean.parseBoolean(form.getFirstValue("headless")); } catch (Exception x) { headless=false;}
		AbstractReadDataset query = null;
		
		structureParam = getStructureParameter();
		StringCondition condition;
		try {
			condition = StringCondition.getInstance(form.getFirstValue(QueryResource.condition));
		} catch   (Exception x) {
			condition = StringCondition.getInstance(StringCondition.C_EQ);
		}
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
				if (query == null) {
					query = new QueryDatasetByFeatures(property,condition);
					((QueryDatasetByFeatures)query).setStructure(structureParam);
				}
			}
		}
		
		if (query == null) {
			query = new ReadDataset();
			query.setValue(null);
		}

		
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
		if (!IDcanBeEmpty && (query.getValue()==null)) 
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Empty dataset ID!");

		return query;
	}
	
	@Override
	public void configureTemplateMap(Map<String, Object> map, Request request,
			IFreeMarkerApplication app) {
		super.configureTemplateMap(map, request, app);
		if (dataset!=null) {
			if (dataset instanceof SourceDataset) {
				map.put("datasetid",dataset.getId());
			} else {
				map.put("datasetid","R"+ dataset.getId());
			}
			
		}
	}
	protected IStructureRecord getStructureParameter() {
		String uri = getParams().getFirstValue(OpenTox.params.compound_uri.toString());

		Reference base = getRequest().getRootRef();

		int index = uri==null?-1:uri.indexOf("/dataset/");
		if (index>0) {
			index = uri.indexOf("/",index+10);
			base = new Reference(uri.substring(0,index));
		}
		
		if (uri!= null) {
			IStructureRecord record = new StructureRecord();
			Object id = OpenTox.URI.compound.getId(uri,base);
			if (id == null) {
				Object[] ids;
				ids = OpenTox.URI.conformer.getIds(uri,base);
				if (ids != null)
					if (ids[0]!=null)
						record.setIdchemical(((Integer) ids[0]).intValue());
					if (ids[1]!=null)
						record.setIdstructure(((Integer) ids[1]).intValue());
			} else 
				record.setIdchemical(((Integer)id).intValue());
			return record;
		} else return null;
	}
	//udpate support
	@Override
	protected ISourceDataset createObjectFromWWWForm(Representation entity)
			throws ResourceException {
		Form form = new Form(entity);
		//only name and license updated
		SourceDataset dataset = new SourceDataset();
		dataset.setName(form.getFirstValue("title"));
		String licenseOptions = form.getFirstValue("licenseOptions");
		String license = form.getFirstValue("license");
		if ((licenseOptions==null) || "Other".equals(licenseOptions))
			dataset.setLicenseURI(license);
		else 
			dataset.setLicenseURI(licenseOptions);
		
		dataset.setrightsHolder(form.getFirstValue("rightsHolder"));
		return dataset;
	}
	@Override
	protected AbstractUpdate createUpdateObject(ISourceDataset entry)
			throws ResourceException {
		if (entry instanceof SourceDataset) {
			UpdateDataset ds = new UpdateDataset((SourceDataset) entry);
			return ds;
		} else throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED);
	}
	@Override
	protected void customizeEntry(ISourceDataset entry, Connection conection)
			throws ResourceException {
		super.customizeEntry(entry, conection);
	}
	@Override
	protected RDFObjectIterator<ISourceDataset> createObjectIterator(
			Representation entity) throws ResourceException {

		RDFMetaDatasetIterator iterator = new RDFMetaDatasetIterator(entity,entity.getMediaType());
		iterator.setForceReadRDFLocalObjects(true);
		iterator.setBaseReference(getRequest().getRootRef());
		return iterator;
	}	
	@Override
	protected QueryURIReporter<ISourceDataset, IQueryRetrieval<ISourceDataset>> getURUReporter(
			Request baseReference) throws ResourceException {
		return new MetadatasetURIReporter<IQueryRetrieval<ISourceDataset>>(baseReference,getDocumentation());
	}
	@Override
	protected RDFObjectIterator<ISourceDataset> createObjectIterator(
			Reference reference, MediaType mediaType) throws ResourceException {
		return super.createObjectIterator(reference, mediaType);
	}
	/**
	 * PUT allowed for metadata resources only (updates the metadata representation)
	 */
	@Override
	protected Representation put(Representation entity, Variant variant)
			throws ResourceException {
		if (getRequest().getAttributes().get(DatasetStructuresResource.datasetKey)!=null)
			createNewObject(entity);
		else throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		
		return getResponse().getEntity();
	}
	
	@Override
	public void executeUpdate(Representation entity, ISourceDataset entry,
			AbstractUpdate updateObject) throws ResourceException {
		Object id = getRequest().getAttributes().get(DatasetStructuresResource.datasetKey);
		if (id != null)  try {
			Integer idnum = new Integer(Reference.decode(id.toString()));
			entry.setID(idnum);
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		}
		super.executeUpdate(entity, entry, updateObject);
	}
	/**
	 * POST not allowed, use PUT for update
	 */
	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		throw new ResourceException(Status.CLIENT_ERROR_FORBIDDEN);
	}

	/**
	 * Delete entire metadata is not allowed. It will be deleted when the dataset is removed
	 */
	@Override
	protected Representation delete(Variant variant) throws ResourceException {
		throw new ResourceException(Status.CLIENT_ERROR_FORBIDDEN);
	}
}
