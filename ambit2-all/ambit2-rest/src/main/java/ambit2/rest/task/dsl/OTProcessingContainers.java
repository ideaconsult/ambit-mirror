package ambit2.rest.task.dsl;

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.rest.task.RemoteTask;
import ambit2.rest.task.RemoteTaskPool;

public abstract class OTProcessingContainers<T extends OTProcessingResource> extends OTContainers<T> {

	@Override
	 public OTDataset process(OTDataset inputDataset) throws Exception  {
		 System.out.println(getClass().getName());	
		 if (size()==0) return inputDataset;
		 
		 
		 OTDataset dataset_copy = (size()==1)?
				inputDataset:
			 	OTDataset.dataset().withDatasetService(dataset_service).copy(inputDataset);
		 
		 long now = System.currentTimeMillis();
		 RemoteTaskPool pool = new RemoteTaskPool();
		 for (T item : getItems()) {
			 RemoteTask task = item.processAsync(dataset_copy);
			 if (task!=null) pool.add(task);

		 }
		 while (pool.poll()>0) {
			 Thread.sleep(pollInterval);
			 Thread.yield();
			 if ((System.currentTimeMillis()-now) > pollTimeout)
				break;
		}
		 
		OTDatasets datasets = null; 
		for (RemoteTask task: pool.getTasks()) {
			 if (task.getError()!=null) 
					throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,
							String.format("%s %s",task.getUrl(),task.getError().getMessage()));
			 if (size()>1) {
				 datasets= (datasets==null)?OTDatasets.datasets():datasets;
				 datasets.add(OTDataset.dataset().withUri(task.getResult()))	;
			 } else return OTDataset.dataset().withUri(task.getResult());
			 
		}		
		return dataset_copy.put(datasets);
	 }		 
}
