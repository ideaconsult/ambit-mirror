package ambit2.descriptors.fingerprints;

import org.openscience.cdk.fingerprint.ExtendedFingerprinter;


public class ExtendedFingerprinterWrapper extends Fingerprint2DescriptorWrapper {

	public ExtendedFingerprinterWrapper() {
		super(new ExtendedFingerprinter());
	}

}
