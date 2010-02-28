package ambit2.fastox.users;

import java.util.Iterator;



public interface IToxPredictSession {
	public void clear();
	public IToxPredictUser getUser();
	
	public String getEndpoint();
	public void setEndpoint(String endpoint);
	public String getEndpointName();
	public void setEndpointName(String endpointName);
	
	public String getDatasetURI();
	public void setDatasetURI(String datasetURI);
	public void clearModels();
	public void addModel(String model, Object status) ;
	public void removeModel(String model);
	public Iterator<String> getModels();
	public int getNumberOfModels();
	public int getNumberOfRunningModels();
	public Object getModelStatus(String model);
	public String getModelResultsURI(String model);
	public Exception getError();
	public void setError(Exception x);
	public int pollModels();
	public boolean needsPreprocessing(String model);
	public void setPreprocessing(String model,Boolean required);
	
	public int getAllModels();
	public void setAllModels(int allModels);
	public int getAllEndpoints();
	public void setAllEndpoints(int allEndpoints) ;
}
