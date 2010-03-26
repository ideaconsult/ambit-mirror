package ambit2.domain.test;

import java.util.BitSet;
import java.util.List;

import junit.framework.Assert;
import ambit2.model.structure.DataCoverageFingerprintsTanimoto;


public class DataCoverageFingerprintsTanimotoTest  extends 
							DataCoverageFingerprintsTest<DataCoverageFingerprintsTanimoto> {
	@Override
	protected DataCoverageFingerprintsTanimoto createMethod() {
		return new DataCoverageFingerprintsTanimoto();
	}
	
	@Override
	public void verifyTraining(List<BitSet> data, double[] results)
			throws Exception {
		for (double d: results) System.out.println(d);
		for (double d : results) Assert.assertTrue(d>0); //all are in domain 
		
		
	}
}
