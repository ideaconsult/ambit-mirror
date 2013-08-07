package ambit2.tautomers;

import java.util.Vector;

import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;

import ambit2.smarts.SmartsParser;

public class RuleParser 
{
	String errors = "";
	String atomIndexCheckError = "";
	String atomIndexFixError = "";
	Rule curRule = null;
	RankingRule curRankingRule = null;
	
	public Rule parse(String ruleString)
	{	
		//System.out.println("rule: " + ruleString);
		errors = "";
		Rule rule = new Rule();
		rule.OriginalRuleString = ruleString;
		curRule = rule;
		
		int res = ruleString.indexOf(TautomerConst.KeyWordPrefix, 0);
		int curPos = res;
		
		while (res != -1)
		{	
			res = ruleString.indexOf(TautomerConst.KeyWordPrefix, curPos+TautomerConst.KeyWordPrefix.length());
			String keyword;
			if (res == -1)
				keyword = ruleString.substring(curPos);
			else
			{	
				keyword = ruleString.substring(curPos,res);
				curPos = res;	
			}
			
			parseKeyWord(keyword);
		}			
		
		
		postProcessing();
		
		
		//System.out.println("errors: " + errors);		
		if (errors.equals(""))
			return(rule);
		else
		{	
			//adding rule name as a prefix
			if (rule.name != null)
				errors = "'" + rule.name + "'\n" + errors;
			return(null);
		}	
	}
	
	public RankingRule parseRankingRule(String ruleString)
	{
		errors = "";
		RankingRule rule = new RankingRule();
		rule.OriginalRuleString = ruleString;
		curRankingRule = rule;
		
		
		int res = ruleString.indexOf(TautomerConst.KeyWordPrefix, 0);
		int curPos = res;
		
		while (res != -1)
		{	
			res = ruleString.indexOf(TautomerConst.KeyWordPrefix, curPos+TautomerConst.KeyWordPrefix.length());
			String keyword;
			if (res == -1)
				keyword = ruleString.substring(curPos);
			else
			{	
				keyword = ruleString.substring(curPos,res);
				curPos = res;	
			}
			
			parseRankingKeyWord(keyword);
		}		
		
		
		if (errors.equals(""))
			return(rule);
		else
		{	
			//adding rule name as a prefix
			if (rule.name != null)
				errors = "'" + rule.name + "'\n" + errors;
			return(null);
		}
	}
	
	
	void postProcessing()
	{
		if (curRule.smartsStates.length <= 1)
		{	
			errors += "Too few states. At leats two states are needed! \n";
			return;
		}	
		
		if (curRule.smartsStates.length != curRule.mobileGroupPos[0].length)
		{
			errors += "The number of states and number of group positionas are not the same!\n";
			errors += "nStates = " + curRule.smartsStates.length + ", nPos = " + curRule.mobileGroupPos[0].length;
			return;
		}
		
		curRule.stateQueries = new QueryAtomContainer[curRule.smartsStates.length];
		curRule.stateFlags = new RuleStateFlags[curRule.smartsStates.length];
		curRule.stateBonds = new RuleStateBondDistribution[curRule.smartsStates.length];
		
		SmartsParser sp = new SmartsParser();
		sp.mSupportDoubleBondAromaticityNotSpecified = true;
		boolean FlagStateSmartsOK = true; 
		
		for (int i = 0; i<curRule.smartsStates.length; i++)
		{
			IQueryAtomContainer q = sp.parse(curRule.smartsStates[i]);			
			String errorMsg = sp.getErrorMessages();
			if (!errorMsg.equals(""))
			{	
				errors += "Incorrect state description: " + errorMsg + "\n";
				curRule.stateQueries[i] = null;
				FlagStateSmartsOK = false;
			}
			else
			{	
				sp.setNeededDataFlags();	
				RuleStateFlags flags = new RuleStateFlags();
				flags.hasRecursiveSmarts = sp.hasRecursiveSmarts;
				flags.mNeedExplicitHData = sp.needExplicitHData();
				flags.mNeedNeighbourData = sp.needNeighbourData();
				flags.mNeedParentMoleculeData = sp.needParentMoleculeData();
				flags.mNeedRingData = sp.needRingData();
				flags.mNeedRingData2 = sp.needRingData2();
				flags.mNeedValenceData = sp.needValencyData();
							
				curRule.stateQueries[i] = q;
				curRule.stateFlags[i] = flags;
				
			}
		}
		
		if (FlagStateSmartsOK)
			handleStateAtomsBondIndexesAndRingClosure();
		
		
		//Calculate bond distributions
		for (int i = 0; i<curRule.smartsStates.length; i++)
		{	
			RuleStateBondDistribution bdistr = new RuleStateBondDistribution();
			
			if (curRule.type == TautomerConst.RT_RingChain)
			{
				bdistr.hasRingClosure = true;	
				bdistr.ringClosureFA = curRule.ringClosureBondFA;
				bdistr.ringClosureSA = curRule.ringClosureBondSA;
				
				if (curRule.ringClosureState == i)
				{	
					bdistr.ringClosureBondOrder = curRule.ringClosureBondOrder;
					bdistr.ringClosureBondIndex = curRule.ringClosureBondNum;
				}
				else
				{	
					bdistr.ringClosureBondOrder = null;   //This is the state where 'ring is open'
					bdistr.ringClosureBondIndex = -1;  //This bond does not exist in the molecule
				}	
			}
			
			
			bdistr.calcDistribution(curRule.stateQueries[i]);
			curRule.stateBonds[i] = bdistr;
			//System.out.println("  BondDistribution  " + bdistr.toString());
		}
		
	}
	
	void parseKeyWord(String keyWord)
	{		
		//System.out.println("   keyword: " + keyWord);
		int sepPos = keyWord.indexOf(TautomerConst.KeyWordSeparator);		
		if (sepPos == -1)
		{	
			errors += "Incorrect key word syntax: " + keyWord + "\n";
			return;
		}
		
		String key = keyWord.substring(TautomerConst.KeyWordPrefix.length(), sepPos).trim();		
		String keyValue = keyWord.substring(sepPos+1).trim();
		//System.out.println(">"+keyValue+"<");
		
		if (key.equals("NAME"))	
		{	
			parseName(keyValue);
			return;
		}
				
		if (key.equals("TYPE"))
		{	
			parseType(keyValue);
			return;
		}
		
		if (key.equals("GROUP"))	
		{	
			parseGroup(keyValue);
			return;
		}
		
		if (key.equals("STATES"))	
		{	
			parseStates(keyValue);
			return;
		}
		
		if (key.equals("GROUP_POS"))	
		{	
			parseGroup_Pos(keyValue);
			return;
		}
		
		if (key.equals("INFO"))	
		{	
			parseInfo(keyValue);
			return;
		}
		
		errors += "Unknow key word: " + key + "\n";
		
	}

	
	void parseRankingKeyWord(String keyWord)
	{		
		//System.out.println("   keyword: " + keyWord);
		int sepPos = keyWord.indexOf(TautomerConst.KeyWordSeparator);		
		if (sepPos == -1)
		{	
			errors += "Incorrect key word syntax: " + keyWord + "\n";
			return;
		}
		
		String key = keyWord.substring(TautomerConst.KeyWordPrefix.length(), sepPos).trim();		
		String keyValue = keyWord.substring(sepPos+1).trim();
		
		
		if (key.equals("NAME"))	
		{	
			curRankingRule.name = keyValue;
			return;
		}
		
		if (key.equals("STATE_ENERGY"))	
		{	
			parseRankingRuleStateEnergies(keyValue);
			return;
		}
		
		if (key.equals("INFO"))	
		{	
			curRankingRule.RuleInfo = keyValue;
			return;
		}
		
		errors += "Unknow key word: " + key + "\n";
	}
	

	
	void parseName(String keyValue)
	{
		curRule.name = keyValue;
	}
	
	void parseType(String keyValue)
	{
		if (keyValue.equals("MOBILE_GROUP"))
		{	
			curRule.type = TautomerConst.RT_MobileGroup;
			return;
		}
		
		if (keyValue.equals("RING_CHAIN"))
		{	
			curRule.type = TautomerConst.RT_RingChain;
			return;
		}
		
		errors += "Unknow rule type: " + keyValue + "\n";
	}
	
	void parseGroup(String keyValue)
	{
		curRule.mobileGroup = keyValue;
		if (curRule.mobileGroup.equals("H"))
			curRule.isMobileH[0] = true;
		else
			curRule.isMobileH[0] = false;
	}
	
	
	void parseStates(String keyValue)
	{	
		String elements [] = keyValue.split(" ");
		Vector<String> vs = new Vector<String>(); 
		 
		for (int i = 0; i < elements.length; i++)
		{	
			String s = elements[i].trim();
			if(!s.equals(""))
				vs.add(s);
		}
		
		curRule.smartsStates = new String[vs.size()];
		for (int i = 0; i < vs.size(); i++)
			curRule.smartsStates[i] = vs.get(i);
	}
	
	void parseGroup_Pos(String keyValue)
	{
		Vector<String> elements = getStringElements(keyValue, TautomerConst.KeyWordElementSeparator);
		curRule.mobileGroupPos = new int[1][elements.size()]; 
		for (int i = 0; i < elements.size(); i++)
		{	
			try {
			int pos = Integer.parseInt(elements.get(i));
			curRule.mobileGroupPos[0][i]  = pos;
			}
			catch (Exception e)
			{
				errors += "Incorrect group position: " + keyValue + "\n";
			}
		}	
	}
	
	void parseInfo(String keyValue)
	{
		curRule.RuleInfo = keyValue.trim();
	}
	
	void parseRankingRuleStateEnergies(String keyValue)
	{	
		String elements [] = keyValue.split(",");
		Vector<String> vs = new Vector<String>(); 
		 
		for (int i = 0; i < elements.length; i++)
		{	
			String s = elements[i].trim();
			if(!s.equals(""))
				vs.add(s);
		}
		
		
		curRankingRule.stateEnergies = new double[vs.size()];
		for (int i = 0; i < vs.size(); i++)
		{	
			try
			{	
				curRankingRule.stateEnergies[i] = Double.parseDouble(vs.get(i));
			}
			catch(Exception e)
			{
				errors += "Incorrect value for state energy: " + e.getMessage();
			}
		}	
	}
	
	
	void handleStateAtomsBondIndexesAndRingClosure()
	{
		//This function checks whether all each bond with atom indexes (a1,a2)
		//from one state is present in the other state 
		//except the ring closure present
		
		//Currently this version works with two states
		//Actually at the moment rules with more than two states are not needed
		
		IQueryAtomContainer q0 = curRule.stateQueries[0];
		IQueryAtomContainer q1 = curRule.stateQueries[1];
		
		if (q0.getAtomCount() != q1.getAtomCount())
		{	
			errors += "The rule states have different number ot atoms: " + 
			q0.getAtomCount() + "  " +  q1.getAtomCount() + "\n";
			return;
		}
		
		if (!checkStateAtomsTypes())
			return;
		
		
		boolean FlagHasRingClosure = false;
		int closureState = 1;
		
		if (q0.getBondCount() != q1.getBondCount())
		{
			FlagHasRingClosure  = true;
			
			if (Math.abs(q0.getBondCount() - q1.getBondCount()) > 1)
			{	
				errors += "Too large difference between the number of bonds for the rule states: " + 
				q0.getBondCount() + "  " +  q1.getBondCount() + "\n";
				//return is not called in order to have additional error messages
			}
			
			if (q0.getBondCount() > q1.getBondCount())	
				closureState = 0;
		}
		
		
		IQueryAtomContainer q, q_2; 
		
		if (closureState == 1)
		{
			q = q0;
			q_2 = q1;
		}
		else
		{
			q = q1;
			q_2 = q0;
		}
		
		//Register atom indexes for each bond from the 'non-closure' state
		int a0[] = new int[q.getBondCount()];
		int a1[] = new int[q.getBondCount()];
		for (int i = 0; i < q.getBondCount(); i++)
		{	
			IBond b = q.getBond(i);
			a0[i] = q.getAtomNumber(b.getAtom(0));
			a1[i] = q.getAtomNumber(b.getAtom(1));
		}
		
		int maxDiffIndexes = 0;
		int nDiffIndexes = 0; 
		if (FlagHasRingClosure)
		{	
			if (curRule.type == TautomerConst.RT_RingChain)
				maxDiffIndexes = 1;
			else
			{
				maxDiffIndexes = 0;
				errors += "This rule is not a ring_chain but it contains a ring closure.\n";
			}
		}
		
			
		int closureBondFA = -1;
		int closureBondSA = -1;
		
		
		//Checking the atoms indexes of the other state query (q_2)		
		for (int i = 0; i < q_2.getBondCount(); i++)
		{
			IBond b = q_2.getBond(i);
			int ind0 = q_2.getAtomNumber(b.getAtom(0));
			int ind1 = q_2.getAtomNumber(b.getAtom(1));
			
			if (! containsBondIndexes(ind0, ind1,  a0, a1))
			{
				//closureBondIndex = i;
				closureBondFA = ind0;
				closureBondSA = ind1;
				nDiffIndexes++;
				if (nDiffIndexes > maxDiffIndexes)
				{
					errors += "The atom indexes for bond " + (i+1) + " of state " + (closureState + 1)
					+ " does not match the indexes at the other state.\n"
					+ "This means that this state has more closures than it is allowed for this rule.\n" 
					+ "States: " + curRule.smartsStates[0] + "  " + curRule.smartsStates[1] + "\n";
				}
			}
		}
				
		if ((nDiffIndexes == 0) && (curRule.type == TautomerConst.RT_RingChain))
			errors += "This is a ring_chain rule, but no ring closures are found.\n";
	
		if ((nDiffIndexes == 1) && (curRule.type == TautomerConst.RT_RingChain))
		{	
			curRule.ringClosureBondFA = closureBondFA;
			curRule.ringClosureBondSA = closureBondSA;
			curRule.ringClosureState = closureState;
			//curRule.ringClosureBondNum is set later after  matchAtomIndexes(q, q_2) since the bonds will be reordered;
		}
		
		
		//Reordering the bonds in the 'second' state to match the atom indexes in the bonds from the 'first' state. 
		//In some cases this is needed - typically in the ring_chain rules. 
		//Generally for the other cases it should not be needed to reorder the bonds.
		//The the correct reordering is impossible this means that the rule is incorrect!
		
		boolean reorderOK = matchAtomIndexes(q, q_2);
		if (!reorderOK)
			errors += atomIndexFixError;
		
		//Finally setting the ring closure bond index 
		if ((nDiffIndexes == 1) && (curRule.type == TautomerConst.RT_RingChain))
		{	
			curRule.ringClosureBondNum = q_2.getBondNumber(q_2.getAtom(closureBondFA), q_2.getAtom(closureBondSA));
		}
		
	}
	
	boolean containsBondIndexes(int ind0, int ind1,  int a0[], int a1[])
	{
		for (int i = 0; i < a0.length; i++)
		{	
			if ((ind0 == a0[i]) && (ind1 == a1[i]))
				return true;
			if ((ind1 == a0[i]) && (ind0 == a1[i]))
				return true;
		}	
		return false;
	}
	
	
	boolean checkStateAtomsTypes()
	{
		//So far this check is not so crucial.
		//Nothing is done currently.
		
		return true;
	}
	
	
	/*
	//This function is not used
	boolean checkAtomIndexes(QueryAtomContainer q)
	{
		//It is expected that the ring closure bond to be the last bond and
		//previous bonds to comply the rule: 
		//bond #i contains atoms with indexes i and i+1
		//
		//These indexes are expected to be in this way because of the SMARTS parser algorithm
		//and the fact that 
		//rule state definitions are written as linear simple SMARTS without branching "()" brackest  
		
		atomIndexCheckError = "";
		//Molecule with n atoms has at least n-1 bonds
		for (int i = 0; i < q.getAtomCount()-1; i++)
		{
			IBond b = q.getBond(i);
			int ind0 = q.getAtomNumber(b.getAtom(0));
			int ind1 = q.getAtomNumber(b.getAtom(1));
			
			if ((ind0 == i) && (ind1 == i+1))
				continue; //it is OK
			
			if ((ind0 == i+1) && (ind1 == i))
				continue; //it is OK
			
			//Error
			atomIndexCheckError = "bond #" + i + " has atom indexes: " + ind0 + ", " + ind1 + " ";
			return false;
		}
		
		return true;
	}
	
	
	boolean fixAtomIndexes_Linear(QueryAtomContainer q)
	{
		//Bonds are reordered so that atom indexes are in the sequence (0,1) (1,2) ... (n-2, n-1)  
		// plus (k1,k2) - ring closure at the end  
		
		atomIndexFixError = "";
		Vector<IBond> v = new Vector<IBond>(); 
		
		for (int i = 0; i < q.getBondCount(); i++)
			v.add(q.getBond(i));
		
		q.removeAllBonds();
		
		//Handling the first groups of bond (linear part)
		for (int i = 0; i < q.getAtomCount()-1; i++)
		{
			IBond b = getBondWithAtomIndexes(i, i+1, v, q);
			if (b == null)
				return false;
			
			v.remove(b);
			q.addBond(b);
		}
		
		//Handling the rest of the bonds
		for (int i = 0; i < v.size(); i++)
			q.addBond(v.get(i));
		
		return true;
	}
	
	*/
	
	
	boolean matchAtomIndexes(IQueryAtomContainer q, IQueryAtomContainer q2)
	{
		//The bonds in q2 are reordered so that the q and q2 has the 'same sequences' of atom indexes.
		//The only exception is the last bond (or maybe several bonds) 
		//which corresponds to the ring closure.
		
		atomIndexFixError = "";
		Vector<IBond> v = new Vector<IBond>(); 
		
		for (int i = 0; i < q2.getBondCount(); i++)
			v.add(q2.getBond(i));
		
		q2.removeAllBonds();
		
		//Handling the first (main) group of bonds (for the most of cases this is the 'linear' part without ring closure)
		for (int i = 0; i < q.getBondCount(); i++)
		{
			IBond b0 = q.getBond(i);
			int ind0 = q.getAtomNumber(b0.getAtom(0));
			int ind1 = q.getAtomNumber(b0.getAtom(1));
			
			IBond b = getBondWithAtomIndexes(ind0, ind1, v, q2);
			
			//This should not happen 
			if (b == null)
			{	
				atomIndexFixError += "Bond reorder error: bond with indexes (" + ind0 + ","+ind1+
					") could not be found in the second state\n";
				return false;
			}
				
			v.remove(b);
			q2.addBond(b);
		}
		
		//Handling the rest of the bonds (this is the ring closure for example)
		for (int i = 0; i < v.size(); i++)
			q2.addBond(v.get(i));
		
		return true;
	}
	
	
	IBond getBondWithAtomIndexes(int ind0, int ind1, Vector<IBond> v, IQueryAtomContainer q)
	{
		for (int i = 0; i < v.size(); i++)
		{
			IBond b = v.get(i);
			int i0 = q.getAtomNumber(b.getAtom(0));
			int i1 = q.getAtomNumber(b.getAtom(1));
			
			if ((ind0 == i0) && (ind1 == i1))
				return b;
			
			if ((ind0 == i1) && (ind1 == i0))
				return b;
		}
		return null;
	}
	
	
	
	
	//Helper function --------------------------------------------------
	
	Vector<String> getStringElements(String string, String separator)
	{
		Vector<String> elements = new Vector<String>();
		int curPos = 0;
		int res = string.indexOf(separator, curPos);
		
		if (res == -1)
		{	
			elements.add(string);
			return(elements);
		}
		else
		{
			String el = string.substring(curPos,res);
			elements.add(el);
			curPos = res + separator.length();
		}
			
				
		while (res != -1)
		{	
			res = string.indexOf(separator, curPos + separator.length());
						
			if (res == -1)
			{	
				String el = string.substring(curPos);
				elements.add(el);
			}	
			else
			{	
				String el = string.substring(curPos,res);
				elements.add(el);
				curPos = res + separator.length();	
			}
		}	
		return (elements);
	}
	 
	
	
}
