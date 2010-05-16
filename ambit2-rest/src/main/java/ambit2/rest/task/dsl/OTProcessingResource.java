package ambit2.rest.task.dsl;

import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.rest.task.RemoteTask;
import ambit2.rest.task.RemoteTaskPool;

public class OTProcessingResource extends OTObject {
	public OTProcessingResource(Reference ref) {
		super(ref);
	}
	public OTProcessingResource(String ref) {
		super(ref);
	}
	public OTProcessingResource() {
		super((Reference) null);
	}	
	public RemoteTask processAsync(OTDataset inputDataset) throws Exception  {
		throw new Exception("Not implemented");
	}
	public OTDataset process(OTDataset inputDataset) throws Exception  {
		throw new Exception("Not implemented");
	 }	
	protected OTProcessingResource tasksCompleted(RemoteTaskPool pool) throws Exception {
		 long now = System.currentTimeMillis();
		 while (pool.poll()>0) {
			 Thread.sleep(pollInterval);
			 Thread.yield();
			 if ((System.currentTimeMillis()-now) > pollTimeout)
				break;
		}
		 return this;
	}
	protected OTDatasets taskResults(RemoteTaskPool pool, OTDatasets datasets) throws Exception {

		for (RemoteTask task: pool.getTasks()) {
			 if (task.getError()!=null) 
					throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,
							String.format("%s %s",task.getUrl(),task.getError().getMessage()));
			 if (pool.size()>1) {
				 datasets= (datasets==null)?OTDatasets.datasets():datasets;
				 datasets.add(OTDataset.dataset(task.getResult()))	;
			 } else {
				 datasets= (datasets==null)?OTDatasets.datasets():datasets;
				 datasets.add(OTDataset.dataset(task.getResult()));
			 }
		}		
		return datasets;
	}	
	
		

}
