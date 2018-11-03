package ambit2.tautomers.zwitterion;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

public interface ICenter 
{
	public IAtom [] getAtoms();
	public boolean explicitHAtoms();
	public IAtomContainer getMolecule();
}
