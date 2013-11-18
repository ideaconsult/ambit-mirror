package ambit2.sln;

import org.openscience.cdk.isomorphism.matchers.IQueryAtom;

import ambit2.smarts.SmartsBondExpression;

public class RingClosure 
{
	IQueryAtom firstAtom;
	IQueryAtom secondAtom;	
	int firstBond;
	SLNBondExpression firstBondExpression = null;
}
