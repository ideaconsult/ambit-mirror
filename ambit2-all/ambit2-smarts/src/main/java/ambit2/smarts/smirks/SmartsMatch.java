package ambit2.smarts.smirks;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;
import org.openscience.cdk.isomorphism.matchers.smarts.AliphaticAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.AnyAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.AnyOrderQueryBond;
import org.openscience.cdk.isomorphism.matchers.smarts.AromaticAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.AromaticQueryBond;
import org.openscience.cdk.isomorphism.matchers.smarts.OrderQueryBond;
import org.openscience.cdk.isomorphism.matchers.smarts.SMARTSAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.SMARTSBond;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import ambit2.core.data.MoleculeTools;
import ambit2.smarts.AliphaticSymbolQueryAtom;
import ambit2.smarts.AromaticSymbolQueryAtom;
import ambit2.smarts.DoubleBondAromaticityNotSpecified;
import ambit2.smarts.DoubleNonAromaticBond;
import ambit2.smarts.RingQueryBond;
import ambit2.smarts.SingleBondAromaticityNotSpecified;
import ambit2.smarts.SingleNonAromaticBond;
import ambit2.smarts.SingleOrAromaticBond;
import ambit2.smarts.SmartsAtomExpression;
import ambit2.smarts.SmartsBondExpression;

public class SmartsMatch 
{
	public static enum MatchMode {
		SPECIFIC_MATCHES_GENERIC, GENERIC_MATCHES_SPECIFIC, EXACT
	}
	
	public static enum AttributeComparison {
		NO_COMPARISON, EXACT, EXACT_WHEN_DEFINED, LESS_THAN, GREAT_THAN
	}
	
	public MatchMode mode = MatchMode.EXACT;	
	public AttributeComparison compareAtomSymbol = AttributeComparison.EXACT_WHEN_DEFINED;
	public AttributeComparison compareAromaticity = AttributeComparison.EXACT_WHEN_DEFINED;
	public AttributeComparison compareIsotope = AttributeComparison.EXACT_WHEN_DEFINED;
	public AttributeComparison compareHAtoms = AttributeComparison.EXACT_WHEN_DEFINED;
	public AttributeComparison compareCharge = AttributeComparison.EXACT_WHEN_DEFINED;
	public AttributeComparison compareBondOrder = AttributeComparison.EXACT_WHEN_DEFINED;
	
	public boolean match(IQueryAtom qa, IAtom ta)
	{
		if (qa instanceof AliphaticSymbolQueryAtom)
			return matchAliphaticSymbolQueryAtom((AliphaticSymbolQueryAtom)qa, ta);
		
		if (qa instanceof AromaticSymbolQueryAtom)
			return matchAromaticSymbolQueryAtom((AromaticSymbolQueryAtom)qa, ta);
		
		if (qa instanceof SmartsAtomExpression)
			return matchSmartsAtomExpression((SmartsAtomExpression)qa, ta);
		
		if (qa instanceof AliphaticAtom)
			return matchAliphaticAtom((AliphaticAtom)qa, ta);
		
		if (qa instanceof AromaticAtom)
			return matchAromaticAtom((AromaticAtom)qa, ta);
		
		if (qa instanceof AnyAtom)
			return matchAnyAtom((AnyAtom)qa, ta);
		
		return false;
	}
	
	public boolean match(IQueryBond qb, IBond tb)
	{
		if (qb instanceof SmartsBondExpression)
			return matchSmartsBondExpression((SmartsBondExpression)qb, tb);
		
		if (qb instanceof SingleOrAromaticBond)
			return matchSingleOrAromaticBond((SingleOrAromaticBond)qb, tb);
		
		if (qb instanceof AromaticQueryBond)
			return matchAromaticQueryBond((AromaticQueryBond)qb, tb);
		
		if (qb instanceof OrderQueryBond)
			return matchOrderQueryBond((OrderQueryBond)qb, tb);
		
		if (qb instanceof SingleNonAromaticBond)
			return matchSingleNonAromaticBond((SingleNonAromaticBond)qb, tb);
		
		if (qb instanceof SingleBondAromaticityNotSpecified)
			return matchSingleBondAromaticityNotSpecified((SingleBondAromaticityNotSpecified)qb, tb);
		
		if (qb instanceof DoubleNonAromaticBond)
			return matchDoubleNonAromaticBond((DoubleNonAromaticBond)qb, tb);
		
		if (qb instanceof DoubleBondAromaticityNotSpecified)
			return matchDoubleBondAromaticityNotSpecified((DoubleBondAromaticityNotSpecified)qb, tb);
		
		if (qb instanceof AnyOrderQueryBond)
			return matchAnyOrderQueryBond((AnyOrderQueryBond)qb, tb);
		
		if (qb instanceof RingQueryBond)
			return matchRingQueryBond((RingQueryBond)qb, tb);
		
		return false;
	}
	
	//Atom match utilities -----------
	
	boolean matchAliphaticSymbolQueryAtom (AliphaticSymbolQueryAtom qa, IAtom ta)
	{
		//TODO
		return false;
	}
	
	boolean matchAromaticSymbolQueryAtom (AromaticSymbolQueryAtom qa, IAtom ta)
	{
		//TODO
		return false;
	}
	
	boolean matchSmartsAtomExpression (SmartsAtomExpression qa, IAtom ta)
	{
		//TODO
		return false;
	}
	
	boolean matchAliphaticAtom (AliphaticAtom qa, IAtom ta)
	{
		//TODO
		return false;
	}
	
	boolean matchAromaticAtom (AromaticAtom qa, IAtom ta)
	{
		//TODO
		return false;
	}
	
	boolean matchAnyAtom (AnyAtom qa, IAtom ta)
	{
		//TODO
		return false;
	}
	
	//Bond match utilities -----------
	
	boolean matchSmartsBondExpression(SmartsBondExpression qb, IBond tb)
	{
		//TODO
		return false;
	}
	
	boolean matchSingleOrAromaticBond(SingleOrAromaticBond qb, IBond tb)
	{
		//TODO
		return false;
	}
	
	boolean matchAromaticQueryBond(AromaticQueryBond qb, IBond tb)
	{
		//TODO
		return false;
	}
	
	boolean matchOrderQueryBond(OrderQueryBond qb, IBond tb)
	{
		//TODO
		return false;
	}
	
	boolean matchSingleNonAromaticBond(SingleNonAromaticBond qb, IBond tb)
	{
		//TODO
		return false;
	}
	
	boolean matchSingleBondAromaticityNotSpecified(SingleBondAromaticityNotSpecified qb, IBond tb)
	{
		//TODO
		return false;
	}
	
	boolean matchDoubleNonAromaticBond(DoubleNonAromaticBond qb, IBond tb)
	{
		//TODO
		return false;
	}
	
	boolean matchDoubleBondAromaticityNotSpecified(DoubleBondAromaticityNotSpecified qb, IBond tb)
	{
		//TODO
		return false;
	}
	
	boolean matchAnyOrderQueryBond(AnyOrderQueryBond qb, IBond tb)
	{
		//TODO
		return false;
	}
	
	boolean matchRingQueryBond(RingQueryBond qb, IBond tb)
	{
		//TODO
		return false;
	}
	
}
