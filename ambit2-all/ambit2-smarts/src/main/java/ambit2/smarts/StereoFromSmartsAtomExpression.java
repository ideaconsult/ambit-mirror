package ambit2.smarts;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.silent.SilentChemObjectBuilder;

public class StereoFromSmartsAtomExpression 
{
	public static int getStereo(SmartsAtomExpression a)
	{
		// In order to extract the stereo from a SmartsAtomExpresion
		// following rules are applied:
		// 1. The expression is represented as a sequence of sub expressions
		// separated by "LOW_AND" operation: sub1; sub2; sub3; ...
		// 2. Each expression is checked whether it defines clearly chirality
		// 3. If only one expression defines chirality it is assigned
		// as the result stereo chiraliry 
		// If two or more expressions define chirality these 
		// must be the same otherwise 'unspecified' chirality is returned
		
		int chirality = SmartsConst.ChC_Unspec;
		List<SmartsAtomExpression> subs = getSubExpressions(a, SmartsConst.LO
				+ SmartsConst.LO_ANDLO);
		
		for (int i = 0; i < subs.size(); i++) 
		{
			int subChirality = analyzeSubExpressionsFromLowAnd(a, subs.get(i));
			
			if (subChirality != SmartsConst.ChC_Unspec) 
			{
				if (chirality == SmartsConst.ChC_Unspec)
					chirality = subChirality;
				else 
				{
					if (chirality != subChirality) {
						chirality = SmartsConst.ChC_Unspec; // Chirality is not defined correctly
						break;
					}
				}
			}
		}
		
		return chirality;
	}
	
	public static List<SmartsAtomExpression> getSubExpressions(
			SmartsAtomExpression a, int separator) {
		List<SmartsAtomExpression> v = new ArrayList<SmartsAtomExpression>();
		SmartsAtomExpression sub = new SmartsAtomExpression(
				SilentChemObjectBuilder.getInstance());
		for (int i = 0; i < a.tokens.size(); i++) {
			if (a.tokens.get(i).type == separator) {
				v.add(sub);
				sub = new SmartsAtomExpression(
						SilentChemObjectBuilder.getInstance());
			} else
				sub.tokens.add(a.tokens.get(i));
		}
		v.add(sub);
		return v;
	}
	
	public static int analyzeSubExpressionsFromLowAnd(SmartsAtomExpression atExp,
			SmartsAtomExpression sub) 
	{
		// The input sub-expression 'sub' is represented as a sequence of sub-sub
		// expressions separated by logical 'OR'
		// Following rule is applied
		// If at least one sub-sub expression has defined chirality
		// then all other sub-sub expressions must have the same chirality
		
		List<SmartsAtomExpression> sub_subs = getSubExpressions(sub,
				SmartsConst.LO + SmartsConst.LO_OR);
		
		int subChirality[] = new int[sub_subs.size()];
		for (int i = 0; i < sub_subs.size(); i++) {
			subChirality[i] = getExpressionAtomChirality(atExp, sub_subs.get(i));
		}
		
		int chirality = subChirality[0];
		for (int i = 1; i < subChirality.length; i++) {
			if (chirality != subChirality[i]) {
				chirality = SmartsConst.ChC_Unspec;
				break;
			}
		}
		
		return chirality;
	}	
		
	public static int getExpressionAtomChirality(SmartsAtomExpression atExp, SmartsAtomExpression sub) 
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

		int chirality = SmartsConst.ChC_Unspec;
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
				// When given primitive defines stereo it must not be
				// negated
				switch (seTok.type) 
				{
				case SmartsConst.AP_Chiral:
					if (seTok.param > 0)
						if (!FlagNot)
							chirality = seTok.param;
					break;

				
				case SmartsConst.AP_Recursive:
					/*
					int recExpAtStereo = getRecursiveExpressionAtomStereo(atExp,
							seTok.param);
					if (recExpAtStereo > 0)
						if (!FlagNot) {
							chirality = recExpAtType;
							mCurSubArom = mRecCurSubArom;
							// TODO - handle charge
						}
					*/	
					break;

				// All other token types do not effect function result
				}
			}
		}

		return (chirality);
	}
}
