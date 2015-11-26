package ambit2.rest.legacy;

import org.restlet.data.Reference;
@Deprecated
public interface IOTDataset extends IOTObject , IOTPageable<IOTDataset>{
	public enum dataset_size  {empty,nonempty,unknown};
	IOTDataset withDatasetService(Reference uri) throws Exception;
	/**
	 * Copies the dataset and returns URI to a new dataset
	 * @return
	 * @throws Exception
	 */
	IOTDataset copy() throws Exception;
	/**
	 * Adds datasets to the current one
	 * @param datasets
	 * @return
	 * @throws Exception
	 */
	IOTDataset put(OTDatasets datasets) throws Exception;

	/**
	  * Return dataset entries (comnpounds),not having values for specified features
	  */
	 IOTDataset filteredSubsetWithoutFeatures(OTFeatures features)  throws Exception;
	 
	 /**
	  * Return dataset entries (compounds), having values for specified features
	  * @param features
	  * @return
	  * @throws Exception
	  */
	 IOTDataset filteredSubsetWithFeatures(OTFeatures features)  throws Exception;
	 
	 /**
	  * Tries to GET the URI and returns true if the dataset is empty, false otherwise.
	  * @return true if the dataset is empty, false otherwise.
	  * @throws Exception
	  */
	 boolean isEmpty()  throws Exception;
	 
	 /**
	  * Add features to the dataset
	  * @param features
	  * @return
	  * @throws Exception
	  */
	 IOTDataset addColumns(OTFeatures features) throws Exception;
	 
	 /**
	  * 
	  * @param features
	  * @return
	  * @throws Exception
	  */
	 IOTDataset getFeatures(OTFeatures features) throws Exception;
}
