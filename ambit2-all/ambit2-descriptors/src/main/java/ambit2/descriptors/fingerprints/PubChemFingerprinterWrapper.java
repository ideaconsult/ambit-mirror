package ambit2.descriptors.fingerprints;

import org.openscience.cdk.fingerprint.PubchemFingerprinter;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

/**
 * Wrapper for {@link PubchemFingerprinter} , implementing {@link IMolecularDescriptor}
 * @author nina
 *
 */
public class PubChemFingerprinterWrapper extends Fingerprint2DescriptorWrapper {

	public PubChemFingerprinterWrapper() {
		super(new PubchemFingerprinter(SilentChemObjectBuilder.getInstance()));
	}

}
