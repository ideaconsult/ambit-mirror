package ambit2.tautomers;

import org.openscience.cdk.interfaces.IAtomContainer;

public class CACTVSRanking 
{
	public static double calcRank(IAtomContainer mol)
	{
		int nAromAt = 0;
		int nOximGroup = 0;
		int nC2O = 0;
		int nN2O = 0;
		int nP2O = 0;
		int nC2X = 0;
		int nCH3 = 0;
		
		int nYH = 0; //P-H, S-H, Se-H and Te-H
		int nAciNitro = 0;
		
		double rank = nAromAt*(100.0/6) + nOximGroup * 4;
		return rank;
	}
	
	
	
}
