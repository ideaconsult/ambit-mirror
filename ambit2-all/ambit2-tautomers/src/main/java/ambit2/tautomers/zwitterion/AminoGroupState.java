package ambit2.tautomers.zwitterion;

import org.openscience.cdk.interfaces.IAtom;

public class AminoGroupState implements IBasicCenter 
{
	public IAtom nitrogen = null;
	public int numHAtoms = 0;
	public int numHeavyAtoms = 0;
	public int charge = 0;
}
