/*
 * Created on 2005-4-1
 *
 */
package ambit.domain;

import java.util.ArrayList;
import java.util.BitSet;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;

import ambit.data.molecule.Compound;
import ambit.data.molecule.MoleculeTools;
import ambit.stats.Tools;
import ambit.stats.datastructures.Sort;



/**
 * A class to estimate applicability domain by fingerprints
 * 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class DataCoverageFingerprints extends DataCoverage {
	protected int fingerPrintLength = MoleculeTools._FPLength;
	protected int fingerprintsCalculated = 0;
	protected BitSet consensusFingerprint = null;
	protected double[] fpProfile = null;
	protected FingerprintDistanceType fpComparison = null; 
	protected ArrayList bitsets = null;
	/**
	 * 
	 */
	public DataCoverageFingerprints() {
		this(ADomainMethodType._modeFINGERPRINTS);
	}

	/**
	 * @param mode
	 */
	public DataCoverageFingerprints(int mode) {
		super(ADomainMethodType._modeFINGERPRINTS);
		fpComparison = new FingerprintDistanceType(mode);
		bitsets = new ArrayList();
		fpProfile = new double[fingerPrintLength];
	}
	public void clear() {
		super.clear();
		fingerprintsCalculated = 0;
		if (consensusFingerprint != null) consensusFingerprint.clear();
		clearProfile(fpProfile);
		bitsets.clear();
	}
	
	protected void clearProfile(double[] profile) {
		if (profile != null) 
			for (int i = 0; i < profile.length; i++) profile[i] = 0;
	}
	/**
	 * calculates fingerpprint of the compound  
	 * @param c
	 * @return
	 * XXX weird error - fingerprint calculation via Java Web Start differ from that in a standalone application ...<br>
	 * Perhaps a diferent randomgenerator is used ???
	 */
	protected BitSet calcFingerprint(Compound c) throws Exception {
		if (c == null) return null;
		try {
		    c.updateMolecule();
			return MoleculeTools.getFingerPrint(c.getSMILES(),fingerPrintLength);
		} catch (CDKException x) {
			return null;
		}
	}
	public boolean buildCompleted() {
//		if tanimoto , get maximum tanimoto distance between fp and consensus FP
		if (fpComparison.getId() > 0) {
			estimateConsensusFP(fpProfile,0);			
			double [] values = new double[bitsets.size()];
			threshold = 0;
			int v = 0;
			for (int i = 0; i < bitsets.size(); i++ ){ 
				double t = tanimoto((BitSet) bitsets.get(i),consensusFingerprint);
				if (new Double(t).isNaN()) continue;
				else {
					values[v] = t; v++;
				}
			}	
			threshold = estimateThresholdValues(pThreshold,values,v);
		} else	threshold = estimateThreshold(fpProfile,fingerprintsCalculated);

		//clear bitsets
		//for (int i = 0; i < data.size(); i++ ){ if (bs[i] != null) bs[i].clear(); bs[i] = null;}
		bitsets.clear();		
		
		setNotModified();
		return (fingerprintsCalculated > 0);
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
	public boolean build(Object data) {
		if (data instanceof AllData) return build((AllData)data);
		return false;
	}

	public boolean build(AllData data) {
		buildStart();	
		//BitSet bs[] = new BitSet[data.size()];
		
		fingerprintsCalculated = 0; 
		for (int i = 0; i < data.size(); i++ ) {
			BitSet bs = null;
		    try {
		    	bs = calcFingerprint(data.getCompound(i));
		    } catch (Exception x) {
		        //TODO exception
		        bs = null;
		    }
			
			if (bs != null) {
				//System.err.println((i+1)+"\t"+bs.toString());
				for (int j=bs.nextSetBit(0); j>= 0; j=bs.nextSetBit(j+1) )  
					fpProfile[j]++;
				fingerprintsCalculated++;
			}			
			bitsets.add(bs);
		}

		return buildCompleted();
	}
	protected void estimateConsensusFP(double[] profile, double athreshold) {
		if (consensusFingerprint == null) consensusFingerprint = new BitSet();
		else consensusFingerprint.clear();
		System.out.println("\nBits set to one by less than " + Math.floor(athreshold) + " compounds are set to zero");
		for (int i=0; i < fingerPrintLength; i++) 
			if (profile[i] > athreshold) consensusFingerprint.set(i);
		System.out.println("Consensus Fingerprint cardinality\t"+consensusFingerprint.cardinality()
		        	+"\n"+consensusFingerprint.toString());
		        
	}
	protected double estimateThreshold(double[] profile, int npoints) {
		estimateConsensusFP(profile,npoints * (1-pThreshold));
		return 0;
	}
	protected double estimateThresholdValues(double percent, double[] values, int npoints) {
		if (npoints == 0) return 0;
		double t;
		int tIndex = npoints;
		if (percent == 1) {
			t = Tools.max(values,npoints);
		}
		else {
		    Sort sort = new Sort();
		    sort.QuickSortArray(values, npoints);
		    sort = null;
		    tIndex = (int) Math.round(npoints * percent);
		    if (tIndex > npoints) tIndex = npoints;
			t = values[tIndex-1];
		}
		System.err.println("Percent\t"+pThreshold*100+"\tThreshold\t"+t+"\tIndex\t"+tIndex);
		return t;
		
	}	
	public boolean isEmpty() {
		return (consensusFingerprint == null) || (fingerprintsCalculated == 0);
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
			//System.out.println(bs.toString());
			b1 = bs1.cardinality();
			bs1.and(bs2);
			bc = bs1.cardinality();
			t = 1- (double)  bc / ( (double) (b1+b2-bc));
		} else {
			return Double.NaN;
		}
		return t;
	}
	protected double[] tanimoto(AllData data) {
		double[] values = new double[data.size()];
		BitSet bs = null;
		int b1 = 0, bc = 0, b2=  consensusFingerprint.cardinality();
		for (int i = 0; i < data.size(); i++ ) {
		    try {
			bs = calcFingerprint(data.getCompound(i));
		    } catch (Exception x) {
		        //TODO exception
		        bs = null;
		    }
			if (bs != null) {
				//System.out.println(bs.toString());
				b1 = bs.cardinality();
				bs.and(consensusFingerprint);
				bc = bs.cardinality();
				values[i] = 1 - (double)  bc / ( (double) (b1+b2-bc));
				//if (values[i]> threshold)
				//System.out.println((i+1)+"\tTanimoto\t"+values[i]+"\t"+bs.toString()+"\t"+data.getCompound(i).toString());				
			} else {
				values[i] = Double.NaN;
				//System.out.println((i+1)+"\tTanimoto\t"+values[i]+"\t" +data.getCompound(i).toString());			
				
			}
		}
		return values;
	}
	protected double[] zeroBits(AllData data) {
		double[] values = new double[data.size()];
		BitSet bs = null;
		for (int i = 0; i < data.size(); i++ ) {
		    try {
		        bs = calcFingerprint(data.getCompound(i));
		    } catch (Exception x) {
		        //TODO exception
		        bs=null;
		    }
			if (bs != null) {
				//System.out.println(bs.toString());
				bs.andNot(consensusFingerprint);
				values[i] = bs.cardinality();
				//if (values[i] > threshold) System.out.print("OUT\t"); else System.out.print("IN \t");
				//System.out.println((i+1)+"\tFP allzero bits set\t"+values[i]+"\t"+bs.toString()+"\t"+data.getCompound(i).toString());
				
			} else {
				values[i] = Double.NaN;
				//System.out.println((i+1)+"\tFP allzero bits set\t"+values[i]+"\t" +data.getCompound(i).toString());			
				
			}
		}
		return values;
	}
		
	public double[] predict(AllData data) {
		switch (fpComparison.getId()) {
		case 1: {
			return tanimoto(data);
		}
		default: {
			return zeroBits(data);
		}
		}
	}
	
	public int getFingerPrintLength() {
		return fingerPrintLength;
	}
	public void setFingerPrintLength(int fpLength) {
		if (this.fingerPrintLength != fpLength) {
			this.fingerPrintLength = fpLength;
			clear();
			fpProfile = null;
			setModified(true);
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
		return "Fingerprints (" + fpComparison.getName() + ")";
	}
	public FingerprintDistanceType getFpComparison() {
		return fpComparison;
	}
	public void setFpComparison(FingerprintDistanceType fpComparison) {
		this.fpComparison.setId(fpComparison.getId());
	}
	public void setFpComparison(String fpType) {
		this.fpComparison.setName(fpType);
	}
	public double[] predict(Object data) {
		if (data instanceof AllData) return predict( (AllData) data);
		else return null;
	}
}
