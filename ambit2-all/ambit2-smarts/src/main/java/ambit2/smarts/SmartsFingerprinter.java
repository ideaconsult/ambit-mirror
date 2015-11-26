package ambit2.smarts;

import java.util.BitSet;

import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;

public class SmartsFingerprinter {
	Fingerprinter fp = new Fingerprinter();
	SmartsToChemObject convertor;

	public SmartsFingerprinter(IChemObjectBuilder builder) {
		super();
		convertor = new SmartsToChemObject(builder);
	}

	public BitSet getFingerprint(IQueryAtomContainer query) throws Exception {
		IAtomContainer ac = convertor.extractAtomContainer(query);
		BitSet bs = fp.getBitFingerprint(ac).asBitSet();
		return (bs);
	}
}
