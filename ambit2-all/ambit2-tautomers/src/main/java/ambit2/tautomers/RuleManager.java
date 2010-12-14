package ambit2.tautomers;

import java.util.Vector;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;

public class RuleManager 
{
	TautomerManager tman;
	Vector<IRuleInstance> extendedRuleInstances;
	Vector<IRuleInstance> ruleInstances;
	Vector<Rule> generatedRules;
	Vector<IRuleInstance> unprocessedInstances = new Vector<IRuleInstance>();
		
	
	public RuleManager(TautomerManager man)
	{
		tman = man;
		extendedRuleInstances = tman.extendedRuleInstances;
		ruleInstances = tman.ruleInstances;
		generatedRules = tman.generatedRules;
	}
	
	public int handleOverlappingRuleInstances()
	{
		unprocessedInstances.clear();
		unprocessedInstances.addAll(extendedRuleInstances);
				
		while (unprocessedInstances.size() > 1)
		{
			IRuleInstance r = unprocessedInstances.lastElement();
			unprocessedInstances.remove(unprocessedInstances.size()-1);
			
			int k = 0;
			while (k < unprocessedInstances.size())
			{
				IRuleInstance r1 = unprocessedInstances.get(k);				
				int nOverlappedAtoms = overlapInstances(r, r1);
				
				if (nOverlappedAtoms > 0)
				{
					//TODO - handle the case where one instance is entirely overlapped by the other one
					//Currently not needed hence not critical. 
					//It depends from the rule definitions added in the future.
					
					r = addRuleInstanceToCombination(r,r1);
					unprocessedInstances.remove(r1);
					k=0;
					continue;
				}
				k++;
			}
			
			//handle rule combination
			if (r instanceof CombinedRuleInstance)
			{	
				CombinedRuleInstance cri = (CombinedRuleInstance)r;
				prepareOverlappedAtoms(cri);
				cri.generateCombinedRuleStates();
			}	
			
			ruleInstances.add(r);			
		}
		
		//Adding the last one 
		if (unprocessedInstances.size() == 1)
			ruleInstances.add(unprocessedInstances.get(0));
		
		return(0);
	}
	
	
	int overlapInstances(IRuleInstance r1, IRuleInstance r2)
	{
		int n = 0;
		if (r1 instanceof RuleInstance)
		{
			if (r2 instanceof RuleInstance)			
				n = getNumOfOverlappedAtoms((RuleInstance)r1, (RuleInstance)r2);			
			else
				n = getNumOfOverlappedAtoms((RuleInstance)r1, (CombinedRuleInstance)r2);
		}
		else
		{
			if (r2 instanceof RuleInstance)			
				n = getNumOfOverlappedAtoms((RuleInstance)r2, (CombinedRuleInstance)r1);			
			else
			{	
				//This case should not happen!!!
			}	
		}		
		
		return(n);
	}
	
	int getNumOfOverlappedAtoms(RuleInstance r1, RuleInstance r2)
	{
		int n = 0;
		for (int i = 0; i < r1.atoms.size(); i++)
			if (r2.atoms.contains(r1.atoms.get(i)))
				n++;
		return (n);
	}
	
	int getNumOfOverlappedAtoms(RuleInstance r1, CombinedRuleInstance r2)
	{
		int n = 0;
		for (int i = 0; i < r1.atoms.size(); i++)
		{	
			for (int k = 0; k < r2.instances.size(); k++)
				if (r2.instances.get(k).atoms.contains(r1.atoms.get(i)))
					n++;
		}	
		return (n);
	}
	
	
	
	Vector<IAtom> getOverlappedAtoms(RuleInstance r1, RuleInstance r2)
	{
		Vector<IAtom> oa = new Vector<IAtom>();		
		for (int i = 0; i < r1.atoms.size(); i++)
			if (r2.atoms.contains(r1.atoms.get(i)))
				oa.add(r1.atoms.get(i));
				
		return (oa);
	}
	
	Vector<IBond> getOverlappedBonds(RuleInstance r1, RuleInstance r2)
	{
		Vector<IBond> ob = new Vector<IBond>();		
		for (int i = 0; i < r1.bonds.size(); i++)
			if (r2.bonds.contains(r1.bonds.get(i)))
				ob.add(r1.bonds.get(i));
				
		return (ob);
	}
	
	void prepareOverlappedAtoms(CombinedRuleInstance cri)
	{
		int n = cri.instances.size();
		for (int i = 0; i < n; i++)
		{
			RuleInstance r = cri.instances.get(i);
			for (int j = 0; j < n; j++)
			if ( i != j)
			{
				Vector<IAtom> va = getOverlappedAtoms(r, cri.instances.get(j));
				for (int k = 0; k < va.size(); k++)
					if (!r.overlappedAtoms.contains(va.get(k))) //This check is needed since an could be part of two overlapping
						r.overlappedAtoms.add(va.get(k));
				
				Vector<IBond> vb = getOverlappedBonds(r, cri.instances.get(j));
				for (int k = 0; k < vb.size(); k++)
					if (!r.overlappedBonds.contains(vb.get(k))) 
						r.overlappedBonds.add(vb.get(k));
			}	
		}
	}
	
	
	IRuleInstance addRuleInstanceToCombination(IRuleInstance baseRule, IRuleInstance addRule)
	{
		if (baseRule instanceof RuleInstance)
		{
			CombinedRuleInstance cri = new CombinedRuleInstance();
			cri.instances.add((RuleInstance)baseRule);
			cri.instances.add((RuleInstance)addRule);
			cri.molecule = ((RuleInstance)baseRule).molecule;
			return(cri);
		}
		else
		{
			//baseRule is a CombinedRuleInstance
			CombinedRuleInstance cri = (CombinedRuleInstance)baseRule;
			cri.instances.add((RuleInstance)addRule);
			return(baseRule);
		}
	}
	
	
	
	
	
	/*
	IRuleInstance combineRuleInstances(IRuleInstance ir1, IRuleInstance ir2)
	{
		RuleInstance r1 = (RuleInstance)ir1;
		RuleInstance r2 = (RuleInstance)ir2;
		Vector<int[]> combStates = new Vector<int[]>(); 
		
		//Checking of all possible combined states in order to generate a new combined rule. 
		for (int i = 0; i < r1.getNumberOfStates(); i++)
			for (int k = 0; k < r2.getNumberOfStates(); k++)
			{
				r1.gotoState(i);
			}			
		
		// TODO   see also for the generation of combined states 
		//and eventually if needed new CombinedRule		
		return(null);
	}
	*/
	
}
