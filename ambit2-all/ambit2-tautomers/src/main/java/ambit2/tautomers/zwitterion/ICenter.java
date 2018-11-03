package ambit2.tautomers.zwitterion;

import org.openscience.cdk.interfaces.IAtom;

public interface ICenter 
{
	public IAtom [] getAtoms();
	public boolean explicitHAtoms();
}
