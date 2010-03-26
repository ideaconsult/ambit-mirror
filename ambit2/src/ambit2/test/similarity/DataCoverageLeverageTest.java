package ambit2.domain.test;

import junit.framework.Assert;
import Jama.Matrix;
import ambit2.model.numeric.DataCoverageLeverage;

public class DataCoverageLeverageTest extends DataCoverageTest<Matrix,DataCoverageLeverage> {

	@Override
	protected DataCoverageLeverage createMethod() {
		return new DataCoverageLeverage();
	}

	int nfactors = 2;
	@Override
	protected Matrix initTestData() {
		dataTest = Matrix.random(23, nfactors);
		return dataTest;
	}	
	@Override
	protected Matrix initTrainingData() {
		dataTrain =Matrix.random(77, nfactors);
		return dataTrain;
	}

	@Override
	public void verifyTest(Matrix data, double[] results) throws Exception {
		for (double d : results) System.out.println(d);
		
	}

	@Override
	public void verifyTraining(Matrix data, double[] results)
			throws Exception {
		for (double d : results) System.out.println(d);
		
	}

	public void verifySerialized(Object coverage) throws Exception {
		Assert.assertTrue(coverage instanceof DataCoverageLeverage);
	}

}
