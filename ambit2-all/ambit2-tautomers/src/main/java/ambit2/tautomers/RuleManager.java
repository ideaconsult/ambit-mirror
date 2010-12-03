package ambit2.tautomers;

import java.util.Vector;

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
				if (overlapInstances(r, r1))
				{
					r = combineRuleInstances(r,r1);
					unprocessedInstances.remove(r1);
					k=0;
					continue;
				}
				k++;
			}
			
			ruleInstances.add(r);
			
		}
		
		//Adding the last one 
		if (unprocessedInstances.size() == 1)
			ruleInstances.add(unprocessedInstances.get(0));
		
		return(0);
	}
	
	
	boolean overlapInstances(IRuleInstance r1, IRuleInstance r2)
	{
		//TODO
		return(false);
	}
	
	IRuleInstance combineRuleInstances(IRuleInstance r1, IRuleInstance r2)
	{
		//TODO
		
		
		// TODO   see also for the generation of combined states 
		//and eventually if needed new CombinedRule		
		return(null);
	}
	
	
	
	
}
