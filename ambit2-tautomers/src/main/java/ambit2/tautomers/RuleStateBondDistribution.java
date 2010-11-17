package ambit2.tautomers;

import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;

public class RuleStateBondDistribution 
{
	int DBPositions[];
	
	public void calcDistribution(QueryAtomContainer statePattern)
	{
		int n = 0; 
		for (int i = 0; i < statePattern.getBondCount(); i++)
		{	
			//if (statePattern.getBond(i) instanceof )
			//TODO
		}		
	}
}
