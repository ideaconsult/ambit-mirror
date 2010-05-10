package ambit2.rest.task.dsl;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.rest.OpenTox;
import ambit2.rest.task.RemoteTask;

public class OTDataset extends OTObject {
	 
	 public static OTDataset dataset() throws Exception  { 
		    return new OTDataset();
	 }
	 @Override
	 public OTDataset withUri(Reference datasetURI) throws Exception { 
		  this.uri = datasetURI;
		  return this; 
	 }	 
	 @Override
	 public OTDataset withUri(String datasetURI) throws Exception { 
		  return withUri(new Reference(datasetURI));
	 }		 
 	 
	 public OTDataset missingValues(Reference dataset) throws Exception  { 
		 return this;
	 }

	 public OTDataset copy(OTDataset dataset) throws Exception  { 
		 if (dataset_service==null) throw new Exception("No dataset_service!");
		long now = System.currentTimeMillis(); 
		Form form = new Form(); 
		form.add(OpenTox.params.dataset_uri.toString(),dataset.toString());
		RemoteTask task = new RemoteTask(new Reference(dataset_service),MediaType.TEXT_URI_LIST,form.getWebRepresentation(),Method.POST,authentication);
		task = wait(task,now);
		if (task.getError()!=null) throw task.getError();
		return withUri(task.getResult());
	 }
	 /**
	  * sends PUT request , adding datasets to the current one

	  */
	 public synchronized OTDataset put(OTDatasets datasets) throws Exception  { 
		    long now = System.currentTimeMillis();
		    if ((datasets==null) || (datasets.size()==0)) throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND,"No dataset uri to add");
			Form params = new Form();
			for (OTDataset dataset : datasets.getItems())
				params.add(OpenTox.params.dataset_uri.toString(),dataset.toString());
			if (dataset_service!=null)
				params.add(OpenTox.params.dataset_service.toString(),dataset_service.toString());
			
			RemoteTask task = null;
			if (uri==null)
				task = new RemoteTask(dataset_service,MediaType.TEXT_URI_LIST,params.getWebRepresentation(),Method.POST,authentication);
			else
				task = new RemoteTask(uri,MediaType.TEXT_URI_LIST,params.getWebRepresentation(),Method.PUT,authentication);
			task = wait(task,now);
			Reference ref = task.getResult();
			datasets.clear();
			if (ref.equals(uri)) return this; else return dataset().withUri(uri); 
			
	 }	 

	 public OTDataset withDatasetService(Reference uri) throws Exception { 
		  this.dataset_service = uri;
		  return this; 
	 }	
	 public OTDataset withDatasetService(String uri) throws Exception { 
		  return withDatasetService(new Reference(uri));
	 }	
}
