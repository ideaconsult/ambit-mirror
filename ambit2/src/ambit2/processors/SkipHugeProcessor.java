package ambit2.processors;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.MFAnalyser;

import ambit2.log.AmbitLogger;
import ambit2.ui.editors.DefaultProcessorEditor;
import ambit2.ui.editors.IAmbitEditor;
import ambit2.exceptions.AmbitException;

/**
 * 
 * Skips huge compounds (returns null if number of heavy atoms is above the number specified, etc.), otherwise return the object itself.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 29, 2006
 */
public class SkipHugeProcessor extends DefaultAmbitProcessor {
	protected static AmbitLogger logger = new AmbitLogger(SkipHugeProcessor.class);
	protected int maxHeavyAtoms = 70;
	protected int maxLightAtoms = 70;
	protected int maxCyclicBonds = 37;
	protected ArrayList table ;
	
	public SkipHugeProcessor() {
		super();
		table = new ArrayList();
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
	        if (heavy>70) {
	            logger.info("Skipping - heavy atoms > 70 / "+heavy);
	            return null;
	        } else if (light > 120) {
	        	logger.info("Skipping - light atoms > 120 / "+light);
	            return null;            
	        }
	        
	        List<String> v = mfa.getElements();
	        for (int i=0; i < v.size();i++) {
	            if (!table.contains(v.get(i).toString().trim())) {
	            	logger.info("Skipping "+v.get(i));
	                return null;
	            }
	        }
			return object;
		} else return null;
	}

	public IAmbitResult createResult() {
		return null;
	}

	public IAmbitResult getResult() {
		return null;
	}

	public void setResult(IAmbitResult result) {

	}
	/* (non-Javadoc)
     * @see ambit2.processors.IAmbitProcessor#close()
     */
    public void close() {
        // TODO Auto-generated method stub

    }
    public IAmbitEditor getEditor() {

    	return new DefaultProcessorEditor(this);
    }
    public String toString() {
    	return "Skip compounds with >"+Integer.toString(maxHeavyAtoms) + " heavy atoms";
    }
    public synchronized int getMaxCyclicBonds() {
        return maxCyclicBonds;
    }
    public synchronized void setMaxCyclicBonds(int maxCyclicBonds) {
        this.maxCyclicBonds = maxCyclicBonds;
    }
    public synchronized int getMaxHeavyAtoms() {
        return maxHeavyAtoms;
    }
    public synchronized void setMaxHeavyAtoms(int maxHeavyAtoms) {
        this.maxHeavyAtoms = maxHeavyAtoms;
    }
    public synchronized int getMaxLightAtoms() {
        return maxLightAtoms;
    }
    public synchronized void setMaxLightAtoms(int maxLightAtoms) {
        this.maxLightAtoms = maxLightAtoms;
    }
}
