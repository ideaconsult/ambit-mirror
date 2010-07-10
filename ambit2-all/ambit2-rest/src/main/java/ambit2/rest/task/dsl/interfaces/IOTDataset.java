package ambit2.rest.task.dsl.interfaces;

import org.restlet.data.Reference;

import ambit2.rest.task.dsl.OTDataset;
import ambit2.rest.task.dsl.OTDatasets;
import ambit2.rest.task.dsl.OTFeatures;

public interface IOTDataset extends IOTObject , IOTPageable<IOTDataset>{
	IOTDataset withDatasetService(Reference uri) throws Exception;
	/**
	 * Copies the dataset and returns URI to a new dataset
	 * @return
	 * @throws Exception
	 */
	OTDataset copy() throws Exception;
	/**
	 * Adds datasets to the current one
	 * @param datasets
	 * @return
	 * @throws Exception
	 */
	OTDataset put(OTDatasets datasets) throws Exception;

	/**
	  * Return dataset entries (comnpounds),not having values for specified features
	  */
	 OTDataset filteredSubsetWithoutFeatures(OTFeatures features)  throws Exception;
	 
	 /**
	  * Return dataset entries (compounds), having values for specified features
	  * @param features
	  * @return
	  * @throws Exception
	  */
	 OTDataset filteredSubsetWithFeatures(OTFeatures features)  throws Exception;
	 
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
	 OTDataset addColumns(OTFeatures features) throws Exception;
	 
	 /**
	  * 
	  * @param features
	  * @return
	  * @throws Exception
	  */
	 OTDataset getFeatures(OTFeatures features) throws Exception;
}
