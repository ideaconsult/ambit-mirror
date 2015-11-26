package ambit2.rest.legacy;

import org.restlet.data.Reference;

/**
 * OpenTox Model http://opentox.org/dev/apis/api-1.1/Model
 * @author nina
 *
 */
@Deprecated
public interface IOTModel extends IOTObject {
	/**
	 * Specify which dataset service to use for results
	 * @param uri
	 * @return
	 * @throws Exception
	 */
	 IOTModel withDatasetService(Reference uri) throws Exception;
	 /**
	  * Specify which dataset service to use for results
	  * @param uri
	  * @return
	  * @throws Exception
	  */
	 IOTModel withDatasetService(String uri) throws Exception;
	/**
	 * Independent variables
	 * @return
	 */
	OTFeatures getIndependentVariables();
	/**
	 * Predicted variables
	 * @return
	 */
	OTFeatures getPredictedVariables();
	/**
	 * Identify which descriptors are used as independent variables and have valid ot:algorithm entry
	 * and run the calculations
	 * @param inputDataset
	 * @return dataset with calculated descriptors
	 * @throws Exception
	 */
	OTDataset calculateDescriptors(OTDataset inputDataset) throws Exception;
	/**
	 * Run descriptor calculations for specified set of features
	 * @param features
	 * @param inputDataset
	 * @return
	 * @throws Exception
	 */
	OTDataset calculateDescriptors(OTFeatures features, OTDataset inputDataset) throws Exception;
	
	OTDataset subsetWithoutPredictedValues(OTDataset inputDataset) throws Exception;
	/**
	 * Synchronous processing
	 * @param inputDataset
	 * @return
	 * @throws Exception
	 */
	OTDataset process(OTDataset inputDataset) throws Exception;
	/**
	 * Initiate asyncronous processing of the dataset
	 * @param inputDataset
	 * @return
	 * @throws Exception
	 */
	OTRemoteTask processAsync(OTDataset inputDataset) throws Exception;
}
