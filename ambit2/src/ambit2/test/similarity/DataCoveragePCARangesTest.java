package ambit2.domain.test;

import junit.framework.Assert;
import Jama.Matrix;
import ambit2.model.numeric.DataCoverageDescriptors;

public class DataCoveragePCARangesTest extends DataCoverageTest<Matrix,DataCoverageDescriptors>{
	int nfactors = 1;
	protected DataCoverageDescriptors createMethod() {
		return new DataCoverageDescriptors();
	}
	@Override
	protected Matrix initTestData() {
		dataTest = Matrix.random(20, nfactors);
		return dataTest;
	}	
	@Override
	protected Matrix initTrainingData() {
		dataTrain =Matrix.random(10, nfactors);
		return dataTrain;
	}

	@Override
	public void verifyTest(Matrix data, double[] results) throws Exception {
		
		for (double d : results) System.out.println(d);
	}
	@Override
	public void verifyTraining(Matrix data, double[] results)
			throws Exception {
		for (double d : results) Assert.assertEquals(0.0,d,1E-3); //all are in domain
	}
	public void verifySerialized(Object coverage) throws Exception {
		Assert.assertTrue(coverage instanceof DataCoverageDescriptors);
	}
}
