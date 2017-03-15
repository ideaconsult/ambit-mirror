package ambit2.smarts.smirks;

import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.smarts.SMARTSAtom;

import ambit2.smarts.IsomorphismTester;

public class SmartsIsomorphismTester extends IsomorphismTester
{
	protected IQueryAtomContainer smartsTarget;
	protected SmartsMatch smartsMatch = new SmartsMatch();
	
	boolean singleAtomSmartsIsomorphism()
	{	
		SMARTSAtom qa = (SMARTSAtom)query.getAtom(0);
		isomorphismFound = false;
		for (int i = 0; i < smartsTarget.getAtomCount(); i++)
		{	
			if (smartsMatch.match(qa, smartsTarget.getAtom(i)))
			{	
				isomorphismFound = true;
				break;
			}
		}
		return(isomorphismFound);
	}
}
