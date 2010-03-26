package ambit2.domain.test;

import junit.framework.Assert;
import Jama.Matrix;
import ambit2.model.numeric.DataCoverageDensity;

public class DataCoverageDensityTest extends DataCoverageTest<Matrix,DataCoverageDensity> {
	int nfactors = 3;
	
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
	protected DataCoverageDensity createMethod() {
		return new DataCoverageDensity();
	}

	@Override
	public void verifyTest(Matrix data, double[] results) throws Exception {
		System.out.println("test");
		for (double d: results) System.out.println(d);
		
	}
	@Override
	public void verifyTraining(Matrix data, double[] results)
			throws Exception {
		for (double d : results) Assert.assertTrue(d>0); //all are in domain 
		for (double d: results) System.out.println(d);
		
	}
	
	public void verifySerialized(Object coverage) throws Exception {
		Assert.assertTrue(coverage instanceof DataCoverageDensity);
	}
}
