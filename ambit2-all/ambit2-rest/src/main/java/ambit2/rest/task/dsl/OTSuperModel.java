package ambit2.rest.task.dsl;

import org.restlet.data.Reference;


/**
 * Same as OTModel, but calculates descriptors, if necessary
 * @author nina
 *
 */
public class OTSuperModel extends OTModel {

	 
	protected OTSuperModel(Reference ref) {
			super(ref);
	}
	protected OTSuperModel(String ref) {
			super(ref);
	}
     public static OTSuperModel model(Reference datasetURI) throws Exception  { 
			    return new OTSuperModel(datasetURI);
	}
	
	public static OTSuperModel model(String datasetURI) throws Exception  { 
			    return new OTSuperModel(datasetURI);
	}     
     @Override
    public OTDataset process(OTDataset inputDataset) throws Exception {
    	 System.out.println(getClass().getName() + inputDataset);
		 OTDataset subsetToCalculate = subsetWithoutPredictedValues(inputDataset);
		 if (!subsetToCalculate.isEmpty()) {
			 OTDataset subset = null;
			 try {
				 subset = subsetToCalculate.withDatasetService(dataset_service).copy();
				 
			 }catch (Exception x) {
				 x.printStackTrace();
				 subset = inputDataset;
			 }
			 super.predict(calculateDescriptors(subset));
		 }
		//else evertyhing is already calculated, return URI of this dataset with predicted features
		 return OTDataset.dataset(inputDataset.uri).withDatasetService(dataset_service).
	 		removeColumns().addColumns(getPredictedVariables());			 
    }
}
