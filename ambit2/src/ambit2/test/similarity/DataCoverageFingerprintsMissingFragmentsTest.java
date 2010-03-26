package ambit2.domain.test;

import java.util.BitSet;
import java.util.List;

import junit.framework.Assert;
import ambit2.model.structure.DataCoverageFingeprintsMissingFragments;


public class DataCoverageFingerprintsMissingFragmentsTest  
			extends DataCoverageFingerprintsTest<DataCoverageFingeprintsMissingFragments> {

	@Override
	protected DataCoverageFingeprintsMissingFragments createMethod() {
		return new DataCoverageFingeprintsMissingFragments();
	}
	
	@Override
	public void verifyTraining(List<BitSet> data, double[] results)
			throws Exception {
		for (double d : results) Assert.assertEquals(0,d,1E-10); //all are in domain 
		
		
	}


}
