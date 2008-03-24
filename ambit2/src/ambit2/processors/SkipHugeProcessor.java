package ambit2.processors;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.MFAnalyser;

import ambit2.config.AmbitCONSTANTS;
import ambit2.exceptions.AmbitException;

/**
 * 
 * Skips huge compounds (returns null if number of heavy atoms is above the number specified, etc.),
 * otherwise return the object itself.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 29, 2006
 */
public class SkipHugeProcessor extends DefaultAmbitProcessor {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3223455056865721389L;
	protected ArrayList<String> table ;
	protected int maxCyclicBonds = AmbitCONSTANTS.maxCyclicBonds;
	protected int maxHeavyAtoms = AmbitCONSTANTS.maxHeavyAtoms;
	protected int maxHAtoms = AmbitCONSTANTS.maxHAtoms;
	public SkipHugeProcessor() {
		table = new ArrayList<String>();
        table.add("C");
        table.add("H");
        table.add("Cl");
        table.add("F");
        table.add("I");
        table.add("Cl");
        table.add("Br");
        table.add("O");
        table.add("N");
        table.add("S");
        table.add("P");
	}

	public Object process(Object object) throws AmbitException {
		if (object instanceof IAtomContainer) {
			IAtomContainer mol = (IAtomContainer) object;
	        MFAnalyser mfa = new MFAnalyser(mol);
	        int heavy = mfa.getHeavyAtoms().size();
	        int light = (mol.getAtomCount()-heavy);
	        if (heavy>AmbitCONSTANTS.maxHeavyAtoms) {
	            logger.info("Skipping - heavy atoms > " + AmbitCONSTANTS.maxHeavyAtoms+ " / "+heavy);
	            return null;
	        } else if (light > AmbitCONSTANTS.maxHAtoms) {
	        	logger.info("Skipping - light atoms > " + AmbitCONSTANTS.maxHAtoms + " / "+light);
	            return null;            
	        }
	        
	        List<String> elements = mfa.getElements();
	        for (String v : elements) {
	            if (!table.contains(v.trim())) {
	            	logger.info("Skipping "+v);
	                return null;
	            }
	        }
			return object;
		} 
		return null;
	}

	public IAmbitResult createResult() {
		return null;
	}

	public IAmbitResult getResult() {
		return null;
	}


    public String toString() {
    	return "Skip compounds with >"+Integer.toString(getMaxHeavyAtoms()) + " heavy atoms";
    }
    /**
     * max cyclic bonds
     * @return
     */
    public int getMaxCyclicBonds() {
        return maxCyclicBonds;
    }
    /**
     * max cyclic bonds
     * @param maxCyclicBonds
     */
    public void setMaxCyclicBonds(int maxCyclicBonds) {
        this.maxCyclicBonds = maxCyclicBonds;
    }
    /**
     * max heavy atoms
     * @return
     */
    public int getMaxHeavyAtoms() {
        return maxHeavyAtoms;
    }
    public void setMaxHeavyAtoms(int maxHeavyAtoms) {
        this.maxHeavyAtoms = maxHeavyAtoms;
    }
    /**
     * max H atoms
     * @return
     */
    public int getMaxHAtoms() {
        return maxHAtoms;
    }
    /**
     * Max H atoms
     * @param maxHAtoms
     */
    public void setMaxHAtoms(int maxHAtoms) {
        this.maxHAtoms = maxHAtoms;
    }
}
