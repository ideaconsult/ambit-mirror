package ambit2.descriptors.fingerprints;

import org.openscience.cdk.fingerprint.HybridizationFingerprinter;
import org.openscience.cdk.qsar.IMolecularDescriptor;

/**
 *  Wrapper for {@link HybridizationFingerprinter}, implementing {@link IMolecularDescriptor}
 * @author nina
 *
 */
public class HybridizationFingerprinterWrapper extends Fingerprint2DescriptorWrapper {

	public HybridizationFingerprinterWrapper() {
		super(new HybridizationFingerprinter());
	}

}
