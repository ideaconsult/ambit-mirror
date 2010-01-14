package ambit2.core.processors.structure;

import java.util.BitSet;

import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.tools.MFAnalyser;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.exceptions.EmptyMoleculeException;
import ambit2.base.log.AmbitLogger;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.core.config.AmbitCONSTANTS;


/**
 * **
 * Fingerprint generator processor. 
 * Fingerprints are calculated by {@link org.openscience.cdk.fingerprint.Fingerprinter} 
 * and assigned as a molecule property with a name {@link AmbitCONSTANTS#Fingerprint}<br>
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 30, 2006
 */
public class FingerprintGenerator extends DefaultAmbitProcessor<IAtomContainer,BitSet>  {
    /**
	 * 
	 */
	private static final long serialVersionUID = 3905797414723139887L;
	protected static AmbitLogger logger = new AmbitLogger(FingerprintGenerator.class);
	protected int FPLength = 1024;
	protected Fingerprinter fingerprinter;
    protected boolean hydrogens = false;
    protected HydrogenAdder hAdder = null;
	protected AtomConfigurator config = new AtomConfigurator();
	
    public FingerprintGenerator() {
		this(1024);
	}
	public FingerprintGenerator(int length) {
		super();
		this.FPLength = length;
		fingerprinter = new Fingerprinter(length);
	}
	

	public BitSet process(IAtomContainer object)
			throws AmbitException {
			
			try {
				if ((object == null) || (object.getAtomCount()==0)) throw new EmptyMoleculeException();
				long fp_time = System.currentTimeMillis();
				IAtomContainer c = (IMolecule) object; 
                if (hydrogens) {
                    if (hAdder == null) hAdder = new HydrogenAdder();
                    c = (IMolecule) ((IMolecule) object).clone(); 
                    hAdder.addExplicitHydrogensToSatisfyValency((IMolecule)c);

                } else {
                    MFAnalyser mfa = new MFAnalyser((IAtomContainer) object);
                    c = mfa.removeHydrogensPreserveMultiplyBonded();
                }
    			fp_time = System.currentTimeMillis() - fp_time;
      			((IMolecule)object).setProperty(AmbitCONSTANTS.FingerprintTIME,new Long(fp_time));                
				return fingerprinter.getFingerprint(c);
			
			} catch (AmbitException x) {
				throw x;
			} catch (Exception x) {
				throw new AmbitException("Error generating fingerprint\t",x);
			}


	}
	/*
	public IAmbitResult createResult() {
		return null;
	}
	public IAmbitResult getResult() {
		return null;
	}
	public void setResult(IAmbitResult result) {
		
	}
	*/
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
