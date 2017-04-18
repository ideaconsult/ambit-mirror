package ambit2.smarts.smirks;

import java.util.List;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.periodictable.PeriodicTable;

import ambit2.smarts.SmartsAtomExpression;
import ambit2.smarts.SmartsConst;
import ambit2.smarts.SmartsExpressionToken;
import ambit2.smarts.SmartsToChemObject;

public class HAtomManager 
{
	/**
	 * 
	 * @param a
	 * @return 0,1,2,3,4,... if specified correctly
	 *         -1 if not specified anywhere 
	 *         -2 if H atoms are defined ambiguously
	 */
	public static int getHAtoms(SmartsAtomExpression a) 
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
		
		int hAtoms = -1;

		List<SmartsAtomExpression> subs = SmartsToChemObject.getSubExpressions(a, SmartsConst.LO
				+ SmartsConst.LO_ANDLO);
		
		for (int i = 0; i < subs.size(); i++) 
		{
			int ha = analyzeSubExpressionsHAtomsFromLowAnd(a, subs.get(i));
			
			if (ha == -2)
				return -2; //H atoms are not defined unambiguously
			
			if (ha != -1) 
			{
				if (hAtoms == -1)
					hAtoms = ha;
				else 
				{
					if (hAtoms != ha) 
						return -2;  //H atoms are not defined unambiguously
				}
			}
		}
					
		return hAtoms;
	}
	
	static int analyzeSubExpressionsHAtomsFromLowAnd(SmartsAtomExpression atExp,
			SmartsAtomExpression sub) 
	{
		// The sub expression sub is represented as a sequence of sub-sub
		// expressions
		// separated by logical 'OR'
		// Following rule is applied
		// If at least one sub-sub expression has a defined H atoms
		// then all other sub-subs must have the same type
		
		List<SmartsAtomExpression> sub_subs = SmartsToChemObject.getSubExpressions(sub,
				SmartsConst.LO + SmartsConst.LO_OR);
		
		int subHAtoms[] = new int[sub_subs.size()];
		for (int i = 0; i < sub_subs.size(); i++) 
			subHAtoms[i] = getExpressionHAtoms(atExp, sub_subs.get(i));
		

		int hAtoms = -100;
		boolean FlagUnspecified = false;		
		for (int i = 0; i < subHAtoms.length; i++)
			if (subHAtoms[i] >= 0) 	
			{
				if (hAtoms == -100)
					hAtoms = subHAtoms[i];
				else
				{	
					if (hAtoms != subHAtoms[i]) 
					{
						hAtoms = -2;
						break;
					}
				}
			}
			else
				FlagUnspecified = true;
		
		if (hAtoms == -100)
			return -1;
		
		if (hAtoms != -2)
			//if there is at least one unspecified token
			//the final result is unspecified (-1) 
			//since logical separator is OR
			if (FlagUnspecified) 
				return -1;
		
		return hAtoms;
	}
		
	
	static int getExpressionHAtoms(SmartsAtomExpression atExp,
			SmartsAtomExpression sub) 
	{
		// 'sub' expression is represented only by HI_AND and NOT operations
		
		// Getting the positions of HI_AND tokens
		int pos[] = new int[sub.tokens.size() + 2];
		pos[0] = -1;
		int n = 0;
		for (int i = 0; i < sub.tokens.size(); i++) {
			if (sub.tokens.get(i).type == SmartsConst.LO + SmartsConst.LO_AND) {
				n++;
				pos[n] = i;
			}
		}

		n++;
		pos[n] = sub.tokens.size();

		int expHAtoms = -1;
		boolean FlagNot;
		SmartsExpressionToken seTok;

		// using 1-based indexing for 'pos' array.
		// pos[0] = -1 and pos[n] = sub.tokens.size() have special use for both
		// ends of the token sequence
		for (int i = 1; i <= n; i++) {
			// Handling the tokens between pos[i-1] and pos[i]
			FlagNot = false;
			for (int k = pos[i - 1] + 1; k < pos[i]; k++) {
				seTok = sub.tokens.get(k);
				if (seTok.isLogicalOperation()) {
					if (seTok.getLogOperation() == SmartsConst.LO_NOT)
						FlagNot = !FlagNot;

					if (seTok.getLogOperation() == SmartsConst.LO_AND)
						FlagNot = false; // 'Not' flag is reseted

					continue;
				}

				// Handling atom primitives.
				// When given primitive defines H-atoms it must not be
				// negated
				//If more than H<n> primitive is present more than once, the last one takes precedence
				//(if there is contradiction this smarts expression will not be matched anyway)
				switch (seTok.type) {
		
				case SmartsConst.AP_H:
					if (seTok.param >= 0)
						if (!FlagNot)
							expHAtoms = seTok.param;
					break;
				
				case SmartsConst.AP_Recursive:
					int ha = getRecursiveExpressionHAtoms(atExp,
							seTok.param);
					//if (ha == -2)
					//	return -2;  ???
					if (ha >= 0)
						if (!FlagNot)
							expHAtoms = ha;
					break;
					
				// All other token types do not effect function result
				}
			}
		}

		return (expHAtoms);
	}
	
	static int getRecursiveExpressionHAtoms(SmartsAtomExpression atExp, int n) 
	{
		IAtom a0 = atExp.recSmartsContainers.get(n).getAtom(0);
		if (a0 instanceof SmartsAtomExpression)
		{
			//recursion here: getHAtoms() calls getRecursiveExpressionHAtoms()
			return getHAtoms((SmartsAtomExpression) a0);
		}
		return -1;
	}	
		
}
