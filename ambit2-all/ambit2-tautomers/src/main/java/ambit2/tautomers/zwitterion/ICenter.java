package ambit2.tautomers.zwitterion;

import org.openscience.cdk.interfaces.IAtom;

public interface ICenter 
{
	public IAtom [] getAtom();
	public boolean explicitHAtoms();
}
