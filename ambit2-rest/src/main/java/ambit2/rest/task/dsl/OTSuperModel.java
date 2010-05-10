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
    	return super.process(calculateDescriptors(inputDataset));
    }
}
