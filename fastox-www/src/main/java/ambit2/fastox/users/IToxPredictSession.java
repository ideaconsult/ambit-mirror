package ambit2.fastox.users;

import java.util.Iterator;

import org.opentox.dsl.OTModels;

import ambit2.fastox.users.ToxPredictSession.SearchMode;



public interface IToxPredictSession {
	public void clear();
	public IToxPredictUser getUser();
	
	public String getEndpoint();
	public void setEndpoint(String endpoint);
	public String getEndpointName();
	public void setEndpointName(String endpointName);
	
	public String getDatasetURI();
	//public Reference getSearchQuery();
	public void setDatasetURI(String datasetURI);
	/*
	public Form getFeatures();
	public void setFeatures(Form features);	
	*/
	public void clearModels();
	public void addModel(String model, Object status) ;
	public void removeModel(String model);
	public Iterator<String> getModels();
	public int getNumberOfModels();
	public int getNumberOfRunningModels();
	public Object getModelStatus(String model);
	public String getModelResultsURI(String model);
	public Iterator<String> getErrorKeys() ;
	public Exception getError(String key);
	public void setError(String key,Exception x);
	public int pollModels();

	
	public int getAllModels();
	public void setAllModels(int allModels);
	public int getAllEndpoints();
	public void setAllEndpoints(int allEndpoints) ;
	
	public String getPageSize();
	public void setPageSize(String pageSize);	
	public void clearErrors();
	
	public String getSearch();
	public String getCondition();
	public void setSearch(String s);
	public SearchMode getSearchMode();
	public void setSearchMode(SearchMode searchMode);
	public void setCondition(String c);	
	
	public OTModels getSelectedModels();
}
