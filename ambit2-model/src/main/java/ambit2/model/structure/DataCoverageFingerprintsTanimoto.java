package ambit2.model.structure;

import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

import ambit2.domain.stats.Tools;

public class DataCoverageFingerprintsTanimoto extends DataCoverageFingerprints {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2253589622326446088L;

	public void evaluate(List<BitSet> bitsets) {
		estimateConsensusFP(fpProfile,0);			
		double [] values = new double[bitsets.size()];
		threshold = 0;
		int v = 0;
		for (int i = 0; i < bitsets.size(); i++ ){ 
			double t = tanimoto(bitsets.get(i),consensusFingerprint);
			if (new Double(t).isNaN()) continue;
			else {
				values[v] = t; v++;
			}
		}	
		threshold = estimateThresholdValues(pThreshold,values,v);

	}

	/**
	 * Returns (1-Tanimoto distance) where 
	 * Tanimoto = ( bc / (b1.cardinality() + b2.cardinality() - bc)
	 * bc = bs1.and(bs2).cardinality (the number of common bits set to 1)
	 * 
	 * (1- Tanimoto) is used to make it compatible with distances approach 
	 * (the larger the distance value, the more dissimilar the compounds 
	 * @param bs1  fingerprint 1
	 * @param bs2  fingerprint 2
	 * @return (1-Tanimoto distance)
	 */
	protected double tanimoto(BitSet bs1, BitSet bs2) {
		int b1 = 0, bc = 0, b2=  bs2.cardinality();
		double t = 0;
		if (bs1 != null) {
			b1 = bs1.cardinality();
			bs1.and(bs2);
			bc = bs1.cardinality();
			//t = 1- (double)  bc / ( (double) (b1+b2-bc));
			t =  (double)  bc / ( (double) (b1+b2-bc));
		} else {
			return Double.NaN;
		}
		return t;
	}
	protected double[] tanimoto(List<BitSet> data) {
		double[] values = new double[data.size()];
		BitSet bs = null;
		int b1 = 0, bc = 0, b2=  consensusFingerprint.cardinality();
		for (int i = 0; i < data.size(); i++ ) {
		    try {
			bs = data.get(i);
		    } catch (Exception x) {
		        //TODO exception
		        bs = null;
		    }
			if (bs != null) {
				b1 = bs.cardinality();
				bs.and(consensusFingerprint);
				bc = bs.cardinality();
				//values[i] = 1 - (double)  bc / ( (double) (b1+b2-bc));
				values[i] = (double)  bc / ( (double) (b1+b2-bc));
			} else {
				values[i] = Double.NaN;
			}
		}
		return values;
	}
	
	protected double estimateThresholdValues(double percent, double[] values, int npoints) {
		if (npoints == 0) return 0;
		double t;
		int tIndex = npoints;
		if (percent == 1) {
			t = Tools.min(values,npoints);
		}
		else {
			Arrays.sort(values);
		    tIndex = (int) Math.round(npoints * (1-percent));
		    if (tIndex > npoints) tIndex = npoints;
			t = values[tIndex-1];
		}
		logger.fine("Percent\t"+pThreshold*100+"\tThreshold\t"+t+"\tIndex\t"+tIndex);
		return t;
		
	}	

	public double[] predict(List<BitSet> data) {
		return tanimoto(data);
	}
	public String getName() {
		return "Fingerprints (Tanimoto)";
	}
	
	public String getDomainName() {
		return String.format("AppDomain_Tanimoto");
	}
	public String getMetricName() {
		return String.format("Tanimoto");
	}	
}
