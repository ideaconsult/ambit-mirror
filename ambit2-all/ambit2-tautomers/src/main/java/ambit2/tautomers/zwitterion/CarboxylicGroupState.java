package ambit2.tautomers.zwitterion;

import org.openscience.cdk.interfaces.IAtom;

public class CarboxylicGroupState implements IAcidicCenter
{	
	public IAtom carbon = null;
	public IAtom oxygen1 = null; //hydroxyl group oxygen   
	public IAtom oxygen2 = null; //carbonil group oxygen
	public int numHAtoms = 0;
	public int charge = 0;
}
