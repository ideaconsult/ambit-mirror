package ambit2.smarts;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;



public class SequenceElement 
{
	//This class represents two types of objects: 
	//(1) an atom with a part of its first topological layer or
	//(2) a bond between two atoms which has already been sequenced  
	//For the second case center == null and atoms.length = 2 and bonds.length = 1
	IAtom center;	
	IAtom atoms[];	
	IBond bonds[];	
	int atomNums[];
	int centerNum;
	
	public void setAtomNums(IAtomContainer container)
	{
		if (center != null)
			centerNum = container.getAtomNumber(center);
		else
			centerNum = -1;
		atomNums = new int[atoms.length];
		for (int i = 0; i < atoms.length; i++)
			atomNums[i] = container.getAtomNumber(atoms[i]);
	}
	
	public String toString() 
	{
		StringBuffer sb = new StringBuffer();
		if (center == null)
		{
			sb.append("Bond " + SmartsHelper.atomToString(atoms[0]) + " " 
					+ SmartsHelper.atomToString(atoms[1]) + 
					"   "+SmartsHelper.bondToString((IBond)bonds[0]));
		}
		else
		{
			sb.append("Center = " + SmartsHelper.atomToString(center) + "  atoms: ");
			for (int i = 0; i < atoms.length; i++)
				sb.append("("+SmartsHelper.atomToString(atoms[i])+","+ 
						SmartsHelper.bondToString((IBond)bonds[i]) + ") ");
		}
		return sb.toString();
	}
	
}
