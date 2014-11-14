package ambit2.core.processors.structure;

import java.util.BitSet;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.p.DefaultAmbitProcessor;

import org.openscience.cdk.fingerprint.IFingerprinter;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.base.exceptions.EmptyMoleculeException;
import ambit2.core.config.AmbitCONSTANTS;


/**
 * **
 * Fingerprint generator processor. 
 * Fingerprints are calculated by {@link org.openscience.cdk.fingerprint.Fingerprinter} 
 * and assigned as a molecule property with a name {@link AmbitCONSTANTS#Fingerprint}<br>
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 30, 2006
 */
public class FingerprintGenerator<FGenerator extends IFingerprinter> extends DefaultAmbitProcessor<IAtomContainer,BitSet>  {
    /**
	 * 
	 */
	private static final long serialVersionUID = 3905797414723139887L;

	protected int FPLength = 1024;
	protected FGenerator fingerprinter;
    protected boolean hydrogens = false;
    protected CDKHydrogenAdder hAdder = null;
	protected AtomConfigurator config = new AtomConfigurator();
	
	public FingerprintGenerator(FGenerator fingerprinter) {
		super();
		this.FPLength = 1024;
		this.fingerprinter = fingerprinter;
	}
	

	public BitSet process(IAtomContainer object)
			throws AmbitException {
			
			try {
				if ((object == null) || (object.getAtomCount()==0)) throw new EmptyMoleculeException();
				long fp_time = System.currentTimeMillis();
				IAtomContainer c = (IAtomContainer) object; 
                if (hydrogens) {
                    if (hAdder == null) hAdder = CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance());
                    c = (IAtomContainer) ((IAtomContainer) object).clone(); 
                    hAdder.addImplicitHydrogens(c);
                    HydrogenAdderProcessor.convertImplicitToExplicitHydrogens(c);

                } else {
                    if (((IAtomContainer) object).getBondCount()>1)
                    	c = AtomContainerManipulator.removeHydrogensPreserveMultiplyBonded((IAtomContainer) object);
                    else c = (IAtomContainer) object;
                }
    			fp_time = System.currentTimeMillis() - fp_time;
      			((IAtomContainer)object).setProperty(AmbitCONSTANTS.FingerprintTIME,new Long(fp_time));                
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
