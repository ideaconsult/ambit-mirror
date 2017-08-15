package ambit2.smarts.smirks;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;
import org.openscience.cdk.isomorphism.matchers.smarts.SMARTSAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.SMARTSBond;

public class SmartsMatch 
{
	public static enum MatchMode {
		GENERIC, EXACT
	}
	
	public static enum AttributeComparison {
		NO_COMPARISON, EXACT, EXACT_WHEN_DEFINED, LESS_THAN, GREAT_THAN
	}
	
	public MatchMode mode = MatchMode.GENERIC;
	public AttributeComparison compareAtomSymbol = AttributeComparison.EXACT_WHEN_DEFINED;
	public AttributeComparison compareHAtoms = AttributeComparison.EXACT_WHEN_DEFINED;
	public AttributeComparison compareCharge = AttributeComparison.EXACT_WHEN_DEFINED;
	public AttributeComparison compareBondOrder = AttributeComparison.EXACT_WHEN_DEFINED;
	
	public boolean match(IQueryAtom qa, IAtom ta)
	{
		//TODO
		return true;
	}
	
	public boolean match(IQueryBond qb, IBond tb)
	{
		//TODO
		return true;
	}
}
