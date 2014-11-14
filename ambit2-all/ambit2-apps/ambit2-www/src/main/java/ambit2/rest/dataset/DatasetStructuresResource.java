package ambit2.rest.dataset;

import java.util.HashMap;
import java.util.Map;

import net.idea.modbcum.i.IQueryRetrieval;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.restlet.routing.Template;

import ambit2.base.data.Property;
import ambit2.base.data.SourceDataset;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.readers.PropertyValue;
import ambit2.db.search.NumberCondition;
import ambit2.db.search.StoredQuery;
import ambit2.db.search.structure.QueryDataset;
import ambit2.db.search.structure.QueryDatasetByID;
import ambit2.db.search.structure.QueryStoredResults;
import ambit2.db.update.dataset.DatasetQueryFieldGeneric;
import ambit2.db.update.dataset.DatasetQueryFieldNumeric;
import ambit2.db.update.dataset.DatasetQueryFieldString;
import ambit2.rest.OpenTox;
import ambit2.rest.error.InvalidResourceIDException;
import ambit2.rest.property.PropertyResource;
import ambit2.rest.propertyvalue.FeatureResource;
import ambit2.rest.query.QueryResource;
import ambit2.rest.query.StructureQueryResource;

/**
 * All compounds from a dataset
 * @author nina
 *
 */
public class DatasetStructuresResource<Q extends IQueryRetrieval<IStructureRecord>> extends StructureQueryResource<Q> {
	//public final static String resource = String.format("%s%s",DatasetsResource.datasetID,CompoundResource.compound);
	public final static String dataset = OpenTox.URI.dataset.getURI();	
	public final static String datasetKey = OpenTox.URI.dataset.getKey();	
	public final static String QR_PREFIX="R";
	protected Integer datasetID;
	protected Integer queryResultsID;
	protected String datasetName;
	protected String search;
	protected int property;
	protected String cond;



	public void setIncludeMol(boolean includeMol) {
		this.includeMol = includeMol;
	}

	
	@Override
	public String getCompoundInDatasetPrefix() {
		if (dataset_prefixed_compound_uri)
		return
			datasetID!=null?String.format("%s/%d", dataset,datasetID):
				queryResultsID!=null?String.format("%s/R%d", dataset,queryResultsID):"";
		else return "";
	}
	@Override
	protected Q createQuery(Context context, Request request,
			Response response) throws ResourceException {
		try {
			setGroupProperties(context, request, response);
			setTemplate(createTemplate(context, request, response));
			
			Form form = getResourceRef(getRequest()).getQueryAsForm();
			try { includeMol = "true".equals(form.getFirstValue("mol")); } catch (Exception x) { includeMol=false;}
			/**
			 * ?search=<value>
			 */
			search = form.getFirstValue(QueryResource.search_param);
			
			cond = form.getFirstValue(QueryResource.condition);
			/**
			 * ?property=<feature_uri>
			 */
			try {
				String p  = form.getFirstValue(QueryResource.property);
				Reference basereference = getRequest().getRootRef();
				Template featureTemplate = new Template(String.format("%s%s%s",basereference==null?"":basereference,PropertyResource.featuredef,PropertyResource.featuredefID));
				Map<String, Object> vars = new HashMap<String, Object>();
				featureTemplate.parse(p, vars);
				try {property = Integer.parseInt(vars.get(FeatureResource.featureID).toString()); } 
				catch (Exception x) { property = -1;};

			} catch (Exception x) {
				property  = -1;
			}
			
			Object id = request.getAttributes().get(datasetKey);
			if (id != null)  try {
				
				id = Reference.decode(id.toString());
				return getQueryById(new Integer(id.toString()));

			} catch (NumberFormatException x) {
				return getQueryById(id.toString());
			} catch (ResourceException x) {
				throw x;
			} catch (Exception x) {
				throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x.getMessage(),x);
			}
			else throw new InvalidResourceIDException("");
		} catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(
					Status.SERVER_ERROR_INTERNAL,x.getMessage(),x
					);
		}		
	}
	
	
	protected Q getQueryById(Integer key) throws ResourceException {
		Q query = null;
		datasetID = key;
		if ((property>0) && (search != null)) {
			Property p = new Property("");
			p.setId(property);
			
			DatasetQueryFieldGeneric q = getSearchQuery(search,p,cond);			
			SourceDataset d = new SourceDataset();
			d.setId(key);
			q.setFieldname(d);
			Form form = getResourceRef(getRequest()).getQueryAsForm();
			setPaging(form, q);
			return (Q)q;
		} else {
			QueryDatasetByID q = new QueryDatasetByID();
			((QueryDatasetByID)q).setValue(key);
			Form form = getResourceRef(getRequest()).getQueryAsForm();
			setPaging(form, q);
			return (Q)q;
		}
	}
	
	protected Q getDatasetByName(String key) throws ResourceException {
		datasetID = null;
		queryResultsID = null;
		datasetName = key;
		QueryDataset query = new QueryDataset();
		query.setValue(new SourceDataset(key));
		Form form = getResourceRef(getRequest()).getQueryAsForm();
		setPaging(form, query);
		return (Q)query;
	}
	
	protected Q getQueryById(String key) throws ResourceException {
		if (key.startsWith(QR_PREFIX)) {
			key = key.substring(QR_PREFIX.length());
			try {
				queryResultsID = Integer.parseInt(key.toString());
			} catch (NumberFormatException x) {
				throw new InvalidResourceIDException(key);
			}
		} else 
			return getDatasetByName(key);
			//throw new InvalidResourceIDException(key);
		
		if ((property>0) && (search != null)) {
			Property p = new Property("");
			p.setId(property);
			DatasetQueryFieldGeneric q = getSearchQuery(search,p,cond);
			StoredQuery d = new StoredQuery(Integer.parseInt(key.toString()));
			q.setFieldname(d);
			Form form = getResourceRef(getRequest()).getQueryAsForm();
			setPaging(form, q);
			return (Q)q;
		} else {	
			QueryStoredResults q = new QueryStoredResults();
			q.setChemicalsOnly(true);
			q.setFieldname(new StoredQuery(Integer.parseInt(key.toString())));
			q.setValue(null);
			return (Q)q;
		}
	}

	protected DatasetQueryFieldGeneric getSearchQuery(String search,Property p, String cond) {
		try {
			Double d = Double.parseDouble(search);
			DatasetQueryFieldNumeric q = new DatasetQueryFieldNumeric();
			 
			PropertyValue<Double> pv = new PropertyValue<Double>();
			pv.setProperty(p);
			pv.setValue(d);
			q.setValue(pv);
			try {
				q.setCondition(NumberCondition.getInstance(cond));
			} catch (Exception x) {
				q.setCondition(NumberCondition.getInstance("="));
			}
			return q;
		} catch (Exception x) {
			DatasetQueryFieldString q = new DatasetQueryFieldString();
			PropertyValue<String> pv = new PropertyValue<String>();
			pv.setProperty(p);
			pv.setValue(search);
			q.setValue(pv);
			return q;
		}
	}
	
	@Override
	protected String getLicenseURI() {
		return retrieveLicense(getContext(), getRequest(), datasetID);
	}
}
