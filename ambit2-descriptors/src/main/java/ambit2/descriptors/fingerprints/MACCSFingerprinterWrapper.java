package ambit2.descriptors.fingerprints;

import org.openscience.cdk.fingerprint.MACCSFingerprinter;
import org.openscience.cdk.qsar.IMolecularDescriptor;

/**
 *  Wrapper for {@link MACCSFingerprinter}, implementing {@link IMolecularDescriptor}
 * @author nina
 *
 */
public class MACCSFingerprinterWrapper extends Fingerprint2DescriptorWrapper {

	public MACCSFingerprinterWrapper() {
		super(new MACCSFingerprinter());
	}

}
