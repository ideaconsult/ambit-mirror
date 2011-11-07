package ambit2.descriptors.fingerprints;

import org.openscience.cdk.fingerprint.SubstructureFingerprinter;
import org.openscience.cdk.qsar.IMolecularDescriptor;

/**
 * Wrapper for {@link SubstructureFingerprinter} , implementing {@link IMolecularDescriptor}
 * @author nina
 *
 */
public class SubstructureFingerprinterWrapper extends Fingerprint2DescriptorWrapper {

	public SubstructureFingerprinterWrapper() {
		super(new SubstructureFingerprinter());
	}

}
