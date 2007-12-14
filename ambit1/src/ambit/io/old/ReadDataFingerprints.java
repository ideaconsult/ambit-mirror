/**
 * Created on 06-Mar-2005
 *
 */
package ambit.io.old;

import java.util.ArrayList;
import java.util.BitSet;

import ambit.data.molecule.MoleculeTools;
import ambit.domain.AllData;

/**
 * This was a test for fingerprints, TODO to be updated
 * @author Nina Jeliazkova
 * <b>Modified</b> 2005-4-7
 */
public class ReadDataFingerprints extends AllData {
	protected ArrayList fps;
	protected double[] profile;
	protected double[] hist;
	protected ArrayList fpDistance;
	/**
	 * 
	 */
	public ReadDataFingerprints() {
		super();
		fps = new ArrayList();
		profile = null;
		hist = new double[10];
		fpDistance = null;
	}
	public BitSet rowToFingerprint(int row) throws Exception {
		String smiles = getSMILES(row);
		if (smiles == null) return null;
		return MoleculeTools.getFingerPrint(smiles);
	}	
	public double[] getFPProfile() {
		profile = null;
		double fpProfile[] = new double[1024];
		for (int i =0; i< 1024; i++) fpProfile[i] = 0;
		for (int i =0; i< 10; i++) hist[i] = 0;
		int nFP = 0;
		BitSet fp;
		for (int i = 0; i < (getNPoints()); i++) {
			
			try {
			    fp = rowToFingerprint(i);
			if (fp != null) {
				for (int j=fp.nextSetBit(0); j> 0; j=fp.nextSetBit(j+1) )  
					fpProfile[j]++;
				nFP++;
			}
			} catch (Exception x) {
				System.err.println("Can't generate fingerprint\t");
				System.out.println(rowToString(i,'\t'));
				x.printStackTrace();
				fp = null;
			}	
			fps.add(fp);
		}
		for (int i=0; i < 1024; i++) { 
			fpProfile[i] /= nFP;
			for (int j=9; j >=0; j--) 
				
				if (fpProfile[i] >= ((double) j)/10.0) {
					hist[j]++; break;
				}
		}
		profile = fpProfile;
		return fpProfile;
	}

	public double[] compare(BitSet bs) {
		int n = fps.size();
		double[] d = new double[n];
		double na = 0;
		double nb = bs.cardinality();
		double nab = 0;
		BitSet fp;

		for (int i = 0; i < (n); i++) {
			
			fp = (BitSet) fps.get(i);
			if (fp == null) { d[i] = 0; continue; } 
			fp = (BitSet) fp.clone();
			na = fp.cardinality();
			fp.and(bs);
			nab = fp.cardinality();
			d[i] = nab / (na+nb-nab);
		}
		return d;
	}
	public double[] getHist() {
		return hist;
	}
	public BitSet profileToBitSet(double threshold) {
		BitSet bs = new BitSet();
		for (int i =0; i < profile.length; i ++) {
			if (profile[i] >= threshold) bs.set(i);
		}
		return bs;
	}

}
