package ambit2.rest.task.dsl;


/**
 * Same as OTModel, but calculates descriptors, if necessary
 * @author nina
 *
 */
public class OTSuperModel extends OTModel {
	
     public static OTSuperModel model() throws Exception  { 
		    return new OTSuperModel();
	 }
     @Override
    public OTDataset process(OTDataset inputDataset) throws Exception {
		 OTDataset subsetToCalculate = subsetWithoutPredictedValues(inputDataset);
		 if (!subsetToCalculate.isEmpty())
			 super.predict(calculateDescriptors(subsetToCalculate));
		//else evertyhing is already calculated, return URI of this dataset with predicted features
		 return OTDataset.dataset().withUri(inputDataset.uri).withDatasetService(dataset_service).
	 		removeColumns().addColumns(getPredictedVariables());			 
    }
}
