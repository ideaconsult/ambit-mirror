package ambit2.fastox.users;

import java.util.Hashtable;
import java.util.Iterator;

import org.opentox.dsl.OTModel;
import org.opentox.dsl.OTModels;
import org.opentox.dsl.task.RemoteTask;
import org.opentox.dsl.task.RemoteTaskPool;
import org.opentox.rdf.OT.OTProperty;


/**
 * Session data , quick implementation in-memory, to be changed
 * @author nina
 *
 */
public class ToxPredictSession implements IToxPredictSession {
	public enum SearchMode {smiles,inchi,text,cas,einecs,dataset,mol,smarts};
	protected SearchMode searchMode = SearchMode.text;
	public SearchMode getSearchMode() {
		return searchMode;
	}
	public void setSearchMode(SearchMode searchMode) {
		this.searchMode = searchMode;
	}
	protected IToxPredictUser user;
	protected String datasetURI;
	/*
	protected Form features;
	public Form getFeatures() {
		return features;
	}
	public void setFeatures(Form features) {
		this.features = features;
	}
	*/
	protected String endpoint = "http://www.opentox.org/echaEndpoints.owl#Endpoints";
	protected String endpointName = "Endpoints";
	protected int allModels = 0;
	protected int allEndpoints = 0;
	protected String pageSize = "1";
	protected Hashtable<String, Exception> errors;
	protected String search;
	protected String condition;
	protected Hashtable<String, Object> models;
	protected Hashtable<String, Boolean> models_preprocessing;
	protected OTModels selectedModels= null; //todo - replace models var above.
	
	public OTModels getSelectedModels() {
		return selectedModels;
	}

	protected RemoteTaskPool pool = null;	
	
	public String getPageSize() {
		return pageSize;
	}
	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}
	public int getAllModels() {
		return allModels;
	}
	public void setAllModels(int allModels) {
		this.allModels = allModels;
	}
	public int getAllEndpoints() {
		return allEndpoints;
	}
	public void setAllEndpoints(int allEndpoints) {
		this.allEndpoints = allEndpoints;
	}
	
	public String getEndpointName() {
		return endpointName;
	}
	public void setEndpointName(String endpointName) {
		this.endpointName = endpointName;
	}


	public Iterator<String> getErrorKeys() {
		if (errors == null) return null;
		else return errors.keySet().iterator();
	}
	public void clearErrors() {
		if (errors!=null) errors.clear();
		
	}
	public Exception getError(String key) {
		if (errors == null) return null;
		else return errors.get(key);
	}
	public void setError(String key, Exception x) {
		if (errors == null) errors = new Hashtable<String, Exception>();
		if (x == null) errors.remove(key); else errors.put(key, x);
	}
	public String getModelResultsURI(String model) {
		Object o = getModelStatus(model);
		if (o==null) return null;
		if (o instanceof RemoteTask) return ((RemoteTask)o).getResult().toString();
		else return null;
	}
	
	public boolean needsPreprocessing(String model) {
		return models_preprocessing==null?null:model==null?null:models_preprocessing.get(model);
	}
	public void setPreprocessing(String model,Boolean required) {
		if (models_preprocessing == null) models_preprocessing = new Hashtable<String, Boolean>();
		models_preprocessing.put(model,required);
		return;
	}	
	
	public Object getModelStatus(String model) {
		return models==null?null:model==null?null:models.get(model);
	}
	public int getNumberOfModels() {
		return models==null?0:models.size();
	}
	public void setModels(Hashtable<String, Object> models) {
		this.models = models;
	}
	public int pollModels() {
		return pool==null?0:pool.poll();
	}
	public int getNumberOfRunningModels() {
		return pool==null?0:pool.running();
	}	
	public void clearModels() {
		if (models !=null)  {
			//TODO verify if there are running models
			if (pool!=null) pool.clear();
			models.clear();
		}
	}
	public void addModel(String model, Object status) {
		if (models ==null) models = new Hashtable<String,Object>();
		if (status instanceof RemoteTask) {
			if (pool==null) pool = new RemoteTaskPool();
			pool.add((RemoteTask)status);
		}
		models.put(model,status);
		try { 
			selectedModels.add(OTModel.model(model).load(new OTProperty[] {OTProperty.predictedVariables,OTProperty.model})); 
		} catch (Exception x) {}

	}
	public void removeModel(String model) {
		if (models !=null) models.remove(model);
		try { selectedModels.remove(model); } catch (Exception x) {}
	}	
	public Iterator<String> getModels() {
		if (models == null) return null;
		return models.keySet().iterator();
	}
	public String getEndpoint() {
		return endpoint;
	}
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	public String getDatasetURI() {
		return datasetURI;
	}
	public void setDatasetURI(String datasetURI) {
		this.datasetURI = datasetURI;
	}
	
	
	public ToxPredictSession(IToxPredictUser user) {
		super();
		this.user = user;
		this.datasetURI = null;
		models = null;
		try {selectedModels = OTModels.models(); } catch (Exception x) {};
	}
	public void clear() {
		clearModels();
		datasetURI = null;
	}

	public IToxPredictUser getUser() {
		return user;
	}
	@Override
	public String toString() {

		return user.toString();
	}
	public String getCondition() {
		return condition;
	}
	public String getSearch() {
		return search;
	}
	public void setCondition(String c) {
		condition =c;
		
	}
	public void setSearch(String s) {
		search = s;
		
	}
	/*
	public Reference getSearchQuery() {
		Reference ref = new Reference(getDatasetURI());
		if (getFeatures()!=null)
		for (String value : getFeatures().getValuesArray("feature_uris[]"))
			ref.addQueryParameter("feature_uris[]", value);
		return ref;
	}
	*/
	
}
