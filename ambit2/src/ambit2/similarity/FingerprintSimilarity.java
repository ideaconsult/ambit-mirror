/*
Copyright (C) 2005-2006  

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
*/

package ambit2.similarity;

import java.util.BitSet;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.similarity.Tanimoto;

import ambit2.config.AmbitCONSTANTS;
import ambit2.exceptions.AmbitException;
import ambit2.processors.IAmbitResult;
import ambit2.processors.results.FingerprintProfile;
import ambit2.processors.structure.FingerprintGenerator;
import ambit2.processors.structure.FingerprintProfileGenerator;

public class FingerprintSimilarity extends DefaultSimilarityProcessor  {
	protected FingerprintGenerator generator = null;
	protected BitSet consensusBitSet = null;
	//protected ArrayList<BitSet> bitsets;

	
	public FingerprintSimilarity() {
		super();
		setPredicting(false);
		
		
	}

	public void buildInitialize() throws AmbitException {
		super.buildInitialize();
		setResult(null);
		generator = new FingerprintProfileGenerator();
		//bitsets = new ArrayList<BitSet>();
	}
	/*
	public IAmbitResult getResult() {
		
		if (isPairwiseSimilarity()) return super.getResult();
		else return similarityMatrix;
	}
	*/
	public void setResult(IAmbitResult result) {
		IAmbitResult oldresult = this.summary;
		if (result == null) consensusBitSet = null;
		else consensusBitSet = ((FingerprintProfile) result).profileToBitSet(0.01);
		super.setResult(result);
		propertyChangeSupport.firePropertyChange("Result",
                oldresult,
                result);
	}

	public void buildCompleted() throws AmbitException {
        if (generator ==null) setResult(null);
        else
		setResult(generator.getResult());
		generator = null;
		super.buildCompleted();
		//AmbitCONSTANTS.Fingerprint 		
	}
	public void incrementalBuild(Object object) throws AmbitException {
		try {
			generator.process(object);
			IAtomContainer ac = (IAtomContainer) object;
			Object bitset = ac.getProperty(AmbitCONSTANTS.Fingerprint);
            
			//bitsets.add((BitSet)bitset);
		} catch (Exception x) {
			//bitsets.add(null);
			throw new AmbitException(x);
		}
	}
	protected BitSet calculateBitSet(Object object)  throws AmbitException {
        if (object == null) return null;
        Object bitset = null;
        try {
    		IAtomContainer ac = (IAtomContainer) object;
    		bitset = ac.getProperty(AmbitCONSTANTS.Fingerprint);
    		if (bitset == null) {
    			if (generator == null) {
                    //System.out.println("FingerprintGenerator");
                    generator = new FingerprintGenerator();
                }    
    			generator.process(object);
    			bitset = ac.getProperty(AmbitCONSTANTS.Fingerprint);
    		}
        } catch (Exception x) {
            throw new AmbitException(x.getMessage(),x);
        }            
    		if (bitset == null) throw new AmbitException("Can't generate fingerprint!");
    		return (BitSet)bitset;

		
	}
	public double predict(Object object) throws AmbitException {
        if (object  == null) return Double.NaN;
		if (isPredicting())
			
			try {
				BitSet bs = calculateBitSet(object);
				
				/*
				Float[] d = new Float[bitsets.size()];
				for (int i=0; i < bitsets.size();i++) 
					if (bitsets.get(i) != null) 
						d[i] = new Float(Tanimoto.calculate(bs,(BitSet)bitsets.get(i)));
					else d[i] = new Float(Float.NaN);
					//((IAtomContainer) object).setProperty(AmbitCONSTANTS.Tanimoto, d);
				((IAtomContainer) object).setProperty(getPairwiseSimilarityProperty(), d);
				//similarityMatrix.addRow(d);
				*/
				float tanimoto = Tanimoto.calculate(bs,consensusBitSet);
					((IAtomContainer) object).setProperty(getTrainingPrefix() + "." + getSimilarityProperty(), new Float(tanimoto));
				return tanimoto;
			} catch (Exception x) {
				throw new AmbitException(x);
			}
		else return super.predict(object);

	}
	public double predict(Object object1, Object object2) throws AmbitException {
		try {
			return Tanimoto.calculate(calculateBitSet(object1),calculateBitSet(object2));
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}
	public void close() {
		super.close();
		/*
		if (bitsets != null)
		bitsets.clear();
		*/
		/*
			propertyChangeSupport.firePropertyChange("Result",
	                null,
	                similarityMatrix);
			LightSimilarityMatrix newmatrix = new LightSimilarityMatrix(
					((LightSimilarityMatrix)similarityMatrix).getMoleculesRows(),
					new ArrayList(),
					this);
			setSimilarityMatrix(newmatrix);
		*/
	}

	public String toString() {

		return "Similarity by Tanimoto distance between 1024 length fingerprints";
	}
	public void setParameter(Object parameter, Object value) {
	    // TODO Auto-generated method stub
	    
	}
    public Object getParameter(Object parameter) {
        // TODO Auto-generated method stub
        return null;
    }
}


