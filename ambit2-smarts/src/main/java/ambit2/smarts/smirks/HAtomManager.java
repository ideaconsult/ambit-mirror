package ambit2.smarts.smirks;

import java.util.List;

import org.openscience.cdk.interfaces.IAtom;

import ambit2.smarts.SmartsAtomExpression;
import ambit2.smarts.SmartsConst;
import ambit2.smarts.SmartsToChemObject;

public class HAtomManager 
{
	/**
	 * 
	 * @param a
	 * @return 0,1,2,3,4,... if specified correctly
	 *         -1 if not specified anywhere 
	 *         -2 if not specified correctly
	 */
	
	
	public static int getHAtomsParameter(SmartsAtomExpression a) 
	{
		// In order to extract the number H primitive value a SmartsAtomExpresion
		// following rules are applied:
		// 1. The expression is represented as a sequence of sub expressions
		// separated by "LOW_AND" operation: sub1; sub2; sub3; ...
		// 2. Each expression is checked whether it defines clearly H<n> primitive
		// 3. If only one expression defines an H<n> then this is assigned
		// as the result H<n> 
		// or if two or more expressions define H<n> 
		// they must be the same
		
		int pHAtoms = -1;

		List<SmartsAtomExpression> subs = SmartsToChemObject.getSubExpressions(a, SmartsConst.LO
				+ SmartsConst.LO_ANDLO);
		
		//TODO
		return pHAtoms;

	}	
}
