package ambit2.tautomers;

import org.openscience.cdk.interfaces.IAtomContainer;

public class TautomerUtils 
{
	public static double getFastTautomerCountEstimation(TautomerManager tman, IAtomContainer mol) throws Exception
	{	
		tman.setStructure(mol); 
		tman.searchAllRulePositions();
		return Math.pow(2, tman.extendedRuleInstances.size());
	}
	
	public static double getTautomerCountEstimation(TautomerManager tman, IAtomContainer mol) throws Exception
	{	
		throw (new Exception("Not implemented yet"));
		
		/*
		tman.setStructure(mol); 
		tman.searchAllRulePositions();
		//TODO
		return Math.pow(2, tman.extendedRuleInstances.size());
		*/
	}
	
}
