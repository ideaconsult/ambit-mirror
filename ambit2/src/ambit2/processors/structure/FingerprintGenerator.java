package ambit2.processors.structure;

import java.util.BitSet;

import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.tools.MFAnalyser;

import ambit2.config.AmbitCONSTANTS;
import ambit2.log.AmbitLogger;
import ambit2.ui.editors.DefaultProcessorEditor;
import ambit2.ui.editors.IAmbitEditor;
import ambit2.exceptions.AmbitException;
import ambit2.processors.DefaultAmbitProcessor;
import ambit2.processors.IAmbitResult;


/**
 * **
 * Fingerprint generator processor. 
 * Fingerprints are calculated by {@link org.openscience.cdk.fingerprint.Fingerprinter} 
 * and assigned as a molecule property with a name {@link AmbitCONSTANTS#Fingerprint}<br>
 * Used by {@link ambit2.database.writers.FingerprintWriter} to write fingerprints to database
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 30, 2006
 */
public class FingerprintGenerator extends DefaultAmbitProcessor  {
    protected static AmbitLogger logger = new AmbitLogger(FingerprintGenerator.class);
	protected int FPLength = 1024;
	protected Fingerprinter fingerprinter;
    protected boolean hydrogens = false;
    protected HydrogenAdder hAdder = null;
	public FingerprintGenerator() {
		this(1024);
	}
	public FingerprintGenerator(int length) {
		super();
		this.FPLength = length;
		fingerprinter = new Fingerprinter(length);
	}
	

	public Object process(Object object)
			throws AmbitException {
		if (object instanceof IMolecule) {
			long fp_time = System.currentTimeMillis();
			try {
				IAtomContainer c = (IMolecule) object; 
                if (hydrogens) {
                    if (hAdder == null) hAdder = new HydrogenAdder();
                    c = (IMolecule) ((IMolecule) object).clone(); 
                    hAdder.addExplicitHydrogensToSatisfyValency((IMolecule)c);

                } else {
                    MFAnalyser mfa = new MFAnalyser((IAtomContainer) object);
                    c = mfa.removeHydrogensPreserveMultiplyBonded();
                }
                
				BitSet bs = fingerprinter.getFingerprint(c);
				((IAtomContainer) object).setProperty(AmbitCONSTANTS.Fingerprint,bs);
			} catch (Exception x) {
			    //logger.error(x);
				((IAtomContainer) object).removeProperty(AmbitCONSTANTS.Fingerprint);
				//((AtomContainer) object).getProperties().remove(AmbitCONSTANTS.Fingerprint);
				//throw new AmbitException("Error generating fingerprint\t",x);
			}
			fp_time = System.currentTimeMillis() - fp_time;
  			((IMolecule)object).setProperty(AmbitCONSTANTS.FingerprintTIME,new Long(fp_time));
		}
		return object;
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

    }
    public IAmbitEditor getEditor() {

    	return new DefaultProcessorEditor(this);
    }
    public String toString() {
    
    	return "Generates hashed (1024 bits) fingerprints";
    }
    public synchronized int getFPLength() {
        return FPLength;
    }
    public synchronized boolean isHydrogens() {
        return hydrogens;
    }
    public synchronized void setHydrogens(boolean hydrogens) {
        this.hydrogens = hydrogens;
    }
}
