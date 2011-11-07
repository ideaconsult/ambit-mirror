package ambit2.descriptors.fingerprints;

import org.openscience.cdk.fingerprint.EStateFingerprinter;
import org.openscience.cdk.qsar.IMolecularDescriptor;

/**
 * Wrapper for {@link EStateFingerprinter}, implementing {@link IMolecularDescriptor}
 * @author nina
 *
 */
public class EStateFingerprinterWrapper extends Fingerprint2DescriptorWrapper {

	public EStateFingerprinterWrapper() {
		super(new EStateFingerprinter());
	}
}
