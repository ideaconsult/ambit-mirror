package ambit2.domain.test;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import junit.framework.Assert;

import org.openscience.cdk.templates.MoleculeFactory;

import ambit2.core.processors.structure.FingerprintGenerator;
import ambit2.model.structure.DataCoverageFingerprints;


public abstract class DataCoverageFingerprintsTest<M extends DataCoverageFingerprints> extends DataCoverageTest<List<BitSet>,M > {

	

	@Override
	protected List<BitSet> initTestData() {
		List<BitSet> bitsets = new ArrayList<BitSet>();
		FingerprintGenerator gen = new FingerprintGenerator();
		try {
			for (int i=5; i < 15; i++) {
				bitsets.add(gen.process(MoleculeFactory.makeAlkane(i+2)));
			}
			bitsets.add(gen.process(MoleculeFactory.makeCyclobutane()));
			bitsets.add(gen.process(MoleculeFactory.makeBranchedAliphatic()));
			return bitsets;
		} catch (Exception x) {
			x.printStackTrace();
			return null;
		}

	}

	@Override
	protected List<BitSet> initTrainingData() {
		List<BitSet> bitsets = new ArrayList<BitSet>();
		FingerprintGenerator gen = new FingerprintGenerator();
		try {
			for (int i=0; i < 10; i++) {
				bitsets.add(gen.process(MoleculeFactory.makeAlkane(i+2)));
			}
			return bitsets;
		} catch (Exception x) {
			x.printStackTrace();
			return null;
		}
	}

	@Override
	public void verifyTest(List<BitSet> data, double[] results)
			throws Exception {
		Assert.assertNotNull(results);
		Assert.assertEquals(data.size(),results.length);
		
	}


	/*

	protected void estimateCoverageFigerprints(QSARDataset ds, 
			int fpLength, double[] pThreshold, int fpMode) {
try {
	System.out.println();
	System.out.println(ds.getName());
	DataCoverageStats s = new DataCoverageStats();
	DataCoverage method = new DataCoverageFingerprints(fpMode);
	((DataCoverageFingerprints) method).setFingerPrintLength(fpLength);
	
	double[][] hist = null;
	for (int i = 0; i < pThreshold.length; i++) {
		method.setPThreshold(pThreshold[i]);
		s.setMethod(method);
		if (s.estimate(ds)) {
			int bins = 10;
			int n = ((DataCoverageFingerprints) method).getFingerprintsCalculated();					
			if (n < bins) bins = n;
			
			hist = ((DataCoverageFingerprints) method).profileToHistogram(bins); 
			System.out.println("Histogram (compound frequency:number of fingerprints set)\n"+ 
					((DataCoverageFingerprints) method).histToString( hist));					

			System.out.println(((DataCoverageFingerprints) method).profileToString());
			System.out.println("Threshold\t"+100*pThreshold[i]+"%\tIn\t"+s.getNoIn()+"\tOut\t"+s.getNoOut()+"\tNA\t"+s.getNoNotAssessed());
			if (pThreshold[i] == 1) assertEquals(n,s.getNoIn(),0.5);
			
		} else fail();
	}	
} catch (Exception x) {
	x.printStackTrace();
	fail();
}
}
*/
}
