package ambit2.smarts.smirks;

import org.openscience.cdk.Atom;
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
import ambit2.smarts.SmartsExpressionToken;

public class SmartsMatch 
{
	public static enum MatchMode {
		SPECIFIC_MATCHES_GENERIC, GENERIC_MATCHES_SPECIFIC, EXACT
	}
	
	public static enum AttributeComparison {
		NO_COMPARISON, EXACT, EXACT_WHEN_DEFINED, LESS_THAN, GREAT_THAN
	}
	
	public MatchMode mode = MatchMode.EXACT;	
	
	/*
	public AttributeComparison compareAtomSymbol = AttributeComparison.EXACT_WHEN_DEFINED;
	public AttributeComparison compareAromaticity = AttributeComparison.EXACT_WHEN_DEFINED;
	public AttributeComparison compareIsotope = AttributeComparison.EXACT_WHEN_DEFINED;
	public AttributeComparison compareHAtoms = AttributeComparison.EXACT_WHEN_DEFINED;
	public AttributeComparison compareCharge = AttributeComparison.EXACT_WHEN_DEFINED;
	public AttributeComparison compareBondOrder = AttributeComparison.EXACT_WHEN_DEFINED;
	*/
	public boolean match(IQueryAtom qa, IAtom ta)
	{	
		if (!(ta instanceof IQueryAtom))
		{
			//This is the case when the target atom is a simple non-query atom
			return qa.matches(ta);
		}
		
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
		if (!(tb instanceof IQueryBond))
		{
			//This is the case when the target bond is a simple non-query bond
			return qb.matches(tb);
		}
		
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
		switch (mode)
		{
		case EXACT:
			if (ta instanceof AliphaticSymbolQueryAtom)
			{
				AliphaticSymbolQueryAtom atom = (AliphaticSymbolQueryAtom)ta;
				return qa.getSymbol().equals(atom.getSymbol());
			}
		break;
		}
		
		//TODO
		return false;
	}
	
	boolean matchAromaticSymbolQueryAtom (AromaticSymbolQueryAtom qa, IAtom ta)
	{
		switch (mode)
		{
		case EXACT:
			if (ta instanceof AromaticSymbolQueryAtom)
			{
				AromaticSymbolQueryAtom atom = (AromaticSymbolQueryAtom)ta;
				return qa.getSymbol().equals(atom.getSymbol());
			}
			break;
		}
		
		//TODO
		return false;
	}
	
	boolean matchSmartsAtomExpression (SmartsAtomExpression qa, IAtom ta)
	{
		switch (mode)
		{
		case EXACT:
			if (ta instanceof SmartsAtomExpression)
			{
				return exactMatch(qa, (SmartsAtomExpression) ta);
			}
			break;
		}
		
		//TODO
		return false;
	}
	
	boolean matchAliphaticAtom (AliphaticAtom qa, IAtom ta)
	{
		switch (mode)
		{
		case EXACT:
			if (ta instanceof AliphaticAtom)
				return true;
			break;
		}
		
		//TODO
		return false;
	}
	
	boolean matchAromaticAtom (AromaticAtom qa, IAtom ta)
	{
		switch (mode)
		{
		case EXACT:
			if (ta instanceof AromaticAtom)
				return true;
			break;
		}
		
		//TODO
		return false;
	}
	
	boolean matchAnyAtom (AnyAtom qa, IAtom ta)
	{
		switch (mode)
		{
		case EXACT:
			if (ta instanceof AnyAtom)
				return true;
			break;
		}
		
		//TODO
		return false;
	}
	
	//Bond match utilities -----------
	
	boolean matchSmartsBondExpression(SmartsBondExpression qb, IBond tb)
	{
		switch (mode)
		{
		case EXACT:
			if (tb instanceof SmartsBondExpression)
			{
				return exactMatch(qb, (SmartsBondExpression) tb);
			}
			break;
		}
		
		//TODO
		return false;
	}
	
	boolean matchSingleOrAromaticBond(SingleOrAromaticBond qb, IBond tb)
	{
		switch (mode)
		{
		case EXACT:
			if (tb instanceof SingleOrAromaticBond)
				return true;
			break;
		}
		
		//TODO
		return false;
	}
	
	boolean matchAromaticQueryBond(AromaticQueryBond qb, IBond tb)
	{
		switch (mode)
		{
		case EXACT:
			if (tb instanceof AromaticQueryBond)
				return true;
			break;
		}
		
		//TODO
		return false;
	}
	
	boolean matchOrderQueryBond(OrderQueryBond qb, IBond tb)
	{
		switch (mode)
		{
		case EXACT:
			if (tb instanceof OrderQueryBond)
			{	
				OrderQueryBond bond = (OrderQueryBond)tb;
				if (qb.getOrder() == bond.getOrder())
					return true;
				else
					return false;
			}	
			break;
		}
		
		//TODO
		return false;
	}
	
	boolean matchSingleNonAromaticBond(SingleNonAromaticBond qb, IBond tb)
	{
		switch (mode)
		{
		case EXACT:
			if (tb instanceof SingleNonAromaticBond)
				return true;
			break;
		}
		
		//TODO
		return false;
	}
	
	boolean matchSingleBondAromaticityNotSpecified(SingleBondAromaticityNotSpecified qb, IBond tb)
	{
		switch (mode)
		{
		case EXACT:
			if (tb instanceof SingleBondAromaticityNotSpecified)
				return true;
			break;
		}
		
		//TODO
		return false;
	}
	
	boolean matchDoubleNonAromaticBond(DoubleNonAromaticBond qb, IBond tb)
	{
		switch (mode)
		{
		case EXACT:
			if (tb instanceof DoubleNonAromaticBond)
				return true;
			break;
		}
		
		//TODO
		return false;
	}
	
	boolean matchDoubleBondAromaticityNotSpecified(DoubleBondAromaticityNotSpecified qb, IBond tb)
	{
		switch (mode)
		{
		case EXACT:
			if (tb instanceof DoubleBondAromaticityNotSpecified)
				return true;
			break;
		}
		
		//TODO
		return false;
	}
	
	boolean matchAnyOrderQueryBond(AnyOrderQueryBond qb, IBond tb)
	{
		switch (mode)
		{
		case EXACT:
			if (tb instanceof AnyOrderQueryBond)
				return true;
			break;
		}
		
		//TODO
		return false;
	}
	
	boolean matchRingQueryBond(RingQueryBond qb, IBond tb)
	{
		switch (mode)
		{
		case EXACT:
			if (tb instanceof RingQueryBond)
				return true;
			break;
		}
		
		//TODO
		return false;
	}
	
	boolean exactMatch(SmartsAtomExpression qa, SmartsAtomExpression ta)
	{
		if (qa.tokens.size() != ta.tokens.size())
			return false;
		
		for (int i = 0; i < qa.tokens.size(); i++)
		{
			SmartsExpressionToken qaTok = qa.tokens.get(i);
			SmartsExpressionToken taTok = ta.tokens.get(i);
			if (qaTok.type != taTok.type)
				return false;
			if (qaTok.param != taTok.param)
				return false;			
		}
		
		return true;
	}
	
	boolean exactMatch(SmartsBondExpression qb, SmartsBondExpression tb)
	{
		if (qb.tokens.size() != tb.tokens.size())
			return false;
		
		for (int i = 0; i < qb.tokens.size(); i++)
		{
			if (qb.tokens.get(i).intValue() != tb.tokens.get(i).intValue())
				return false;
		}
		
		return true;
	}
	
}
