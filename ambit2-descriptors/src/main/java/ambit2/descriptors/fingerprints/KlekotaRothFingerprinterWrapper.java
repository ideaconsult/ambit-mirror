package ambit2.descriptors.fingerprints;

import org.openscience.cdk.fingerprint.KlekotaRothFingerprinter;

public class KlekotaRothFingerprinterWrapper  extends Fingerprint2DescriptorWrapper {

	public KlekotaRothFingerprinterWrapper() {
		super(new KlekotaRothFingerprinter());
	}

}
