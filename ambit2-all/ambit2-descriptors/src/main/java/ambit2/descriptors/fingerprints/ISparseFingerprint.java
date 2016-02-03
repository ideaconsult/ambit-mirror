package ambit2.descriptors.fingerprints;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;

public interface ISparseFingerprint<T> {
	public Iterable<T> getSparseFingerprint(IAtomContainer mol) throws CDKException;
}
