package ambit2.smarts;

import org.openscience.cdk.interfaces.IAtom;

public class DoubleBondStereoInfo 
{
	public static enum DBStereo {
		OPPOSITE, TOGETHER, OPPOSITE_OR_UNDEFINED, TOGETHER_OR_UNDEFINED, UNDEFINED
	}
	
	public IAtom ligand1 = null;
	public IAtom ligand2 = null;
	public DBStereo conformation = DBStereo.UNDEFINED;
	
}
