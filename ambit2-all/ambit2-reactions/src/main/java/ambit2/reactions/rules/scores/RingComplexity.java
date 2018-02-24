package ambit2.reactions.rules.scores;

import org.openscience.cdk.interfaces.IAtomContainer;

public class RingComplexity 
{
	public static double ringComplexity(IAtomContainer mol)
	{
		//TODO
		return 0.0;
	}
	
	public static int cyclomaticNumber(IAtomContainer mol)
	{	
		return (mol.getBondCount() - mol.getAtomCount() + 1);
	}
}
