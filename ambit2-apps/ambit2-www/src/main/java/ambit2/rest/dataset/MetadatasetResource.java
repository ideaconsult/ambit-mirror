package ambit2.rest.dataset;

import java.util.Map;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.q.update.AbstractUpdate;
import net.idea.restnet.i.freemarker.IFreeMarkerApplication;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

import ambit2.base.data.ISourceDataset;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.SourceDataset;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.StringCondition;
import ambit2.db.update.dataset.AbstractReadDataset;
import ambit2.db.update.dataset.QueryDatasetByFeatures;
import ambit2.db.update.dataset.ReadDataset;
import ambit2.db.update.dataset.UpdateDataset;
import ambit2.db.update.storedquery.ReadStoredQuery;
import ambit2.rest.DisplayMode;
import ambit2.rest.OpenTox;
import ambit2.rest.bundle.AbstractMetadataResource;
import ambit2.rest.error.InvalidResourceIDException;
import ambit2.rest.query.QueryResource;
import ambit2.rest.rdf.RDFMetaDatasetIterator;
import ambit2.rest.rdf.RDFObjectIterator;

public class MetadatasetResource<M extends ISourceDataset> extends AbstractMetadataResource<M> {

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
	}
	

	
	@Override
	protected IQueryRetrieval<M> createQuery(Context context,
			Request request, Response response) throws ResourceException {
		return getQuery(context, request, response,false);
	}	

	protected IQueryRetrieval<M> getQuery(Context context,Request request, Response response, boolean IDcanBeEmpty) throws ResourceException {
		
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
			dataset = (M)new SourceDataset();
			dataset.setID(idnum);
			query.setValue(dataset);
		} catch (NumberFormatException x) {
			if (id.toString().startsWith(DatasetStructuresResource.QR_PREFIX)) {
				String key = id.toString().substring(DatasetStructuresResource.QR_PREFIX.length());
				try {
					IQueryRetrieval<M> q = (IQueryRetrieval<M>)new ReadStoredQuery(Integer.parseInt(key.toString()));
					return q;
				} catch (NumberFormatException xx) {
					throw new InvalidResourceIDException(id);
				}
			} else {
				dataset = (M)new SourceDataset();
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
				map.put("datasetid",dataset.getID());
			} else {
				map.put("datasetid","R"+ dataset.getID());
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
	protected M createObjectFromWWWForm(Representation entity)
			throws ResourceException {
		Form form = new Form(entity);
		//only name and license updated
		SourceDataset dataset = new SourceDataset();
		dataset.setName(form.getFirstValue(ISourceDataset.fields.title.name()));
		String licenseOptions = form.getFirstValue("licenseOptions");
		String license = form.getFirstValue(ISourceDataset.fields.license.name());
		if ((licenseOptions==null) || "Other".equals(licenseOptions))
			dataset.setLicenseURI(license);
		else 
			dataset.setLicenseURI(licenseOptions);
		
		dataset.setrightsHolder(form.getFirstValue(ISourceDataset.fields.rightsHolder.name()));
		return (M) dataset;
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
	protected RDFObjectIterator<M> createObjectIterator(Representation entity) throws ResourceException {

		RDFMetaDatasetIterator iterator = new RDFMetaDatasetIterator(entity,entity.getMediaType()) {
			@Override
			protected ISourceDataset createRecord() {
				return new SourceDataset();
			}
		};
		iterator.setForceReadRDFLocalObjects(true);
		iterator.setBaseReference(getRequest().getRootRef());
		return iterator;
	}	
}
