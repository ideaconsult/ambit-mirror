package ambit2.model.structure;

import java.util.BitSet;
import java.util.List;

public class DataCoverageFingeprintsMissingFragments extends DataCoverageFingerprints {

	/**
	 * 
	 */
	private static final long serialVersionUID = -732145918036578793L;

	public void evaluate(List<BitSet> bitsets) {
		threshold = estimateThreshold(fpProfile,fingerprintsCalculated);	
	}
	protected double estimateThreshold(double[] profile, int npoints) {
		estimateConsensusFP(profile,npoints * (1-pThreshold));
		return 0;
	}

	protected double[] zeroBits(List<BitSet> data) {
		double[] values = new double[data.size()];
		BitSet bs = null;
		for (int i = 0; i < data.size(); i++ ) {
		    try {
		        bs = data.get(i);
		    } catch (Exception x) {
		        //TODO exception
		        bs=null;
		    }
			if (bs != null) {
				bs.andNot(consensusFingerprint);
				values[i] = bs.cardinality();
			
			} else {
				values[i] = Double.NaN;
			
			}
		}
		return values;
	}
	
	public int getDomain(double coverage) {
		int domain;
		if (coverage <= threshold) domain = 0;
		else domain =1;
		return domain;
	}
	public int[] getDomain(double[] coverage) {
		if (coverage == null) return null;
		int np = coverage.length;
		int[] domain = new int[np];
		for (int i = 0; i < np; i++) 
			if (new Double(coverage[i]).isNaN()) domain[i] = 2;
			else if (coverage[i] <= threshold) domain[i] = 0;
			else domain[i] =1;
		return domain;
	}	
	
	
	
	public double[] predict(List<BitSet> data) {
		return zeroBits(data);
	}
	public String getName() {
		return "Fingerprints (Missing fragments)";
	}
	public String getDomainName() {
		return String.format("AppDomain_MissingFragments");
	}
	public String getMetricName() {
		return String.format("MissingFragments");
	}		
}
