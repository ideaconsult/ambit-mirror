package ambit2.rest.task.dsl;

import ambit2.rest.task.RemoteTask;
import ambit2.rest.task.RemoteTaskPool;

public abstract class OTProcessingContainers<T extends OTProcessingResource> extends OTContainers<T> {

	@Override
	 public OTDataset process(OTDataset inputDataset) throws Exception  {
		 System.out.println(getClass().getName());	
		 if (size()==0) return inputDataset;
		 
		 
		 OTDataset dataset_copy = (size()==1)?
				inputDataset:
				inputDataset.withDatasetService(dataset_service).copy();
		 
		 
		 RemoteTaskPool pool = new RemoteTaskPool();
		 for (T item : getItems()) {
			 RemoteTask task = item.processAsync(dataset_copy);
			 if (task!=null) pool.add(task);

		 }
		 OTDatasets datasets = tasksCompleted(pool).taskResults(pool,null);
		 if (datasets.size()==1) for (OTDataset dataset: datasets.getItems()) return dataset;
		 return dataset_copy.put(datasets);
		
	 }		 

}
