package ambit2.processors.structure;

import java.util.BitSet;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.MFAnalyser;

import ambit2.config.AmbitCONSTANTS;
import ambit2.exceptions.AmbitException;
import ambit2.processors.IAmbitProcessor;
import ambit2.processors.IAmbitResult;
import ambit2.processors.results.FingerprintProfile;

/**
 * Generates {@link ambit2.processors.results.FingerprintProfile} of a set of molecules
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 30, 2006
 */
public class FingerprintProfileGenerator extends FingerprintGenerator implements
		IAmbitProcessor {
	protected FingerprintProfile profile ;
	
	public FingerprintProfileGenerator() {
		super();
		profile = null;
	}

	public FingerprintProfileGenerator(int length) {
		super(length);
		profile = null;
	}
	
	public IAmbitResult createResult() {
		System.out.println("createResult");
		return new FingerprintProfile("NA",FPLength);
		
	}
	public IAmbitResult getResult() {
		if (profile == null)
			profile = (FingerprintProfile)createResult();
		return profile;
	}
	public void setResult(IAmbitResult result) {
		if (result instanceof FingerprintProfile)
			this.profile = (FingerprintProfile)result;
	}	
	public Object process(Object object) throws AmbitException {
		if (object instanceof IAtomContainer) {
			try {
                IAtomContainer c = (IAtomContainer) object; 
                if (hydrogens) {
                    //add hydrogens
                } else {
                    MFAnalyser mfa = new MFAnalyser((IAtomContainer) object);
                    c = mfa.removeHydrogensPreserveMultiplyBonded();
                }                
				BitSet bs = fingerprinter.getFingerprint(c);
				((IAtomContainer) object).setProperty(AmbitCONSTANTS.Fingerprint,bs);
				getResult().update(object);
			} catch (Exception x) {
				((IAtomContainer) object).getProperties().remove(AmbitCONSTANTS.Fingerprint);
				throw new AmbitException("Error generating fingerprint\t",x);
			}
		} else if (object instanceof BitSet) {
			getResult().update(object);
		}
		return object;
	}
	public String toString() {
		return "Fingerprint profiler";
	}
}
