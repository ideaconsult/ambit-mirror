package ambit2.fastox.users;

import java.util.Hashtable;
import java.util.Iterator;

import ambit2.rest.task.RemoteTask;
import ambit2.rest.task.RemoteTaskPool;


/**
 * Session data , quick implementation in-memory, to be changed
 * @author nina
 *
 */
public class ToxPredictSession implements IToxPredictSession {
	protected IToxPredictUser user;
	protected String datasetURI;
	protected String endpoint = "http://www.opentox.org/echaEndpoints.owl#Endpoints";
	protected String endpointName = "Endpoints";
	public String getEndpointName() {
		return endpointName;
	}
	public void setEndpointName(String endpointName) {
		this.endpointName = endpointName;
	}
	protected Hashtable<String, Object> models;
	protected Exception x;
	protected RemoteTaskPool pool = null;
	
	public Exception getError() {
		return x;
	}
	public void setError(Exception x) {
		this.x = x;
	}
	public String getModelResultsURI(String model) {
		Object o = getModelStatus(model);
		if (o==null) return null;
		if (o instanceof RemoteTask) return ((RemoteTask)o).getResult().toString();
		else return null;
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
	}
	public void removeModel(String model) {
		if (models !=null) models.remove(model);
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
	
	
}
