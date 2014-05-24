/*
 * Created on 2005-4-1
 *
 */
package ambit2.model.structure;

import java.util.BitSet;
import java.util.List;

import ambit2.core.data.MoleculeTools;
import ambit2.model.numeric.ADomainMethodType;
import ambit2.model.numeric.DataCoverage;



/**
 * A class to estimate applicability domain by fingerprints
 * 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public abstract class DataCoverageFingerprints extends DataCoverage<List<BitSet>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8338115538206282818L;
	protected int fingerPrintLength = MoleculeTools._FPLength;
	protected int fingerprintsCalculated = 0;
	protected BitSet consensusFingerprint = null;
	protected double[] fpProfile = null;

	/**
	 * @param mode
	 */
	public DataCoverageFingerprints() {
		super(ADomainMethodType._modeFINGERPRINTS_CONSENSUS);
		fpProfile = new double[fingerPrintLength];
	}
	public void clear() {
		super.clear();
		fingerprintsCalculated = 0;
		if (consensusFingerprint != null) consensusFingerprint.clear();
		clearProfile(fpProfile);
	}
	
	protected void clearProfile(double[] profile) {
		if (profile != null) 
			for (int i = 0; i < profile.length; i++) profile[i] = 0;
	}


	public boolean buildStart() {
		clear();
		/*
		if (fpProfile == null) 
			fpProfile = new double[fingerPrintLength];
		else clearProfile(fpProfile);
		*/
		return true;
	}
	public abstract void evaluate(List<BitSet> bitsets) ;

	
	
	public boolean build(List<BitSet> bitsets) {
		buildStart();	
		//BitSet bs[] = new BitSet[data.size()];
		fingerprintsCalculated = 0; 
		for (int i = 0; i < bitsets.size(); i++ ) {
			BitSet bs = bitsets.get(i);
			if (bs != null) {
				for (int j=bs.nextSetBit(0); j>= 0; j=bs.nextSetBit(j+1) )  
					fpProfile[j]++;
				fingerprintsCalculated++;
			}			
		}

		evaluate(bitsets);
		return (fingerprintsCalculated > 0);
	}
	protected void estimateConsensusFP(double[] profile, double athreshold) {
		if (consensusFingerprint == null) consensusFingerprint = new BitSet();
		else consensusFingerprint.clear();
		logger.fine("\nBits set to one by less than " + Math.floor(athreshold) + " compounds are set to zero");
		for (int i=0; i < fingerPrintLength; i++) 
			if (profile[i] > athreshold) consensusFingerprint.set(i);
		logger.fine("Consensus Fingerprint cardinality\t"+consensusFingerprint.cardinality()
		        	+"\n"+consensusFingerprint.toString());
		        
	}

	public boolean isEmpty() {
		return (consensusFingerprint == null) || (fingerprintsCalculated == 0);
	}

	
	public int getFingerPrintLength() {
		return fingerPrintLength;
	}
	public void setFingerPrintLength(int fpLength) {
		if (this.fingerPrintLength != fpLength) {
			this.fingerPrintLength = fpLength;
			clear();
			fpProfile = null;

		}
	}
	public static String arrayToString(double[] array) {
		StringBuffer b = new StringBuffer();
		for (int i = 0; i < array.length; i++) {
			b.append(array[i]);
			b.append("\t");
		}
		return b.toString();
	}
	public String histToString(double[][] hist) {
		if (hist == null) return "";
		StringBuffer b = new StringBuffer();
		for (int i = 0; i < hist[0].length; i++) {
			b.append(">");			
			b.append(hist[1][i]);			
			b.append(":");			
			b.append((int)Math.floor(hist[0][i]));
			b.append("\t");
		}
		return b.toString();
	}	
	public String profileToString() {
		if (fpProfile == null) return "";
		StringBuffer b = new StringBuffer();
		for (int i = 0; i < fpProfile.length; i++) {
			b.append((int)Math.floor(fpProfile[i]));
			b.append("\t");
		}
		return b.toString();
	}
	public int getFingerprintsCalculated() {
		return fingerprintsCalculated;
	}
	public double[][] profileToHistogram(int bins) {
		if (isEmpty()) return null;
		if (bins < 1) bins = 10;
		double[][] hist = new double[2][bins];	
		for (int j = 0; j < bins; j++) { 
			hist[0][j] = 0;
			hist[1][j] = ((double) j)/bins;
		}
		
		for (int i=0; i < fingerPrintLength; i++) { 
			double r = fpProfile[i] / fingerprintsCalculated;
			for (int j=(bins-1); j >=0; j--) 
				if (r >= ((double) j)/bins) {
					hist[0][j]++; 
					break;
				}
		}
		return hist;
	}
	public String getName() {
		return "Fingerprints";
	}

	public int getDomain(double coverage) {
		int domain;
		if (coverage >= threshold) domain = 0;
		else domain =1;
		return domain;
	}
	public int[] getDomain(double[] coverage) {
		if (coverage == null) return null;
		int np = coverage.length;
		int[] domain = new int[np];
		for (int i = 0; i < np; i++) 
			if (new Double(coverage[i]).isNaN()) domain[i] = 2;
			else if (coverage[i] >= threshold) domain[i] = 0;
			else domain[i] =1;
		return domain;
	}	
}
