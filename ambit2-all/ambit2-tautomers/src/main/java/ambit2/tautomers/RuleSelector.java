package ambit2.tautomers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RuleSelector 
{
	protected int selectionMode = TautomerConst.RSM_RANDOM;
	protected int ruleNumberLimit = 10;
	protected int limitFuzziness = 1; //This is used for fuzzines in the limit handling
	protected int limitDiffForSwitchToCombinatorial = 5;  
	protected int selectionOrder = TautomerConst.RULE_ORDER_BY_SIZE;
	
	private boolean FlagSwitchToCombinatorial = false;
	
	public int getSelectionMode(){
		return selectionMode;
	}
	
	public void setSelectionMode(int selectionMode){
		this.selectionMode = selectionMode;
	}
	
	public int getRuleNumberLimit() {
		return ruleNumberLimit;
	}

	public void setRuleNumberLimit(int ruleNumberLimit) {
		this.ruleNumberLimit = ruleNumberLimit;
	}
	
	public int getLimitDiffForSwitchToCombinatorial(){
		return limitDiffForSwitchToCombinatorial;
	}
	
	public void setLimitDiffForSwitchToCombinatorial(int limitDiffForSwitchToCombinatorial){
		this.limitDiffForSwitchToCombinatorial = limitDiffForSwitchToCombinatorial;
	}
	
	public boolean switchToCombinatorial(){
		return FlagSwitchToCombinatorial;
	}
	
	public int getSelectionOrder(){
		return selectionOrder;
	}
	
	public void setSelectionOrder(int selectionOrder){
		this.selectionOrder = selectionOrder;
	}
	
	
	public List<IRuleInstance> selectRules(TautomerManager tman, List<IRuleInstance> ruleList)
	{
		FlagSwitchToCombinatorial = false;
		List<IRuleInstance> selected = null;
		switch (selectionMode)
		{
		case TautomerConst.RSM_ALL:
			selected = new ArrayList<IRuleInstance>();
			selected.addAll(ruleList);
			break;
		case TautomerConst.RSM_NONE:
			selected = new ArrayList<IRuleInstance>();
			//NOTHING is added
			break;
		case TautomerConst.RSM_RANDOM:
			selected = selectRandom(tman, ruleList);
			break;	
		}
		return selected;
	}
	
	private List<IRuleInstance> selectRandom(TautomerManager tman, List<IRuleInstance> ruleList)
	{
		List<IRuleInstance> selected = new ArrayList<IRuleInstance>();
		
		if ((ruleNumberLimit + limitDiffForSwitchToCombinatorial) <= ruleList.size())
			FlagSwitchToCombinatorial = true;
		
		if (ruleNumberLimit + limitFuzziness >= ruleList.size())
		{
			selected.addAll(ruleList);
			return selected;
		}
		
		Random random = new Random();
		int index;
		
		if (selectionOrder == TautomerConst.RULE_ORDER_NONE)
		{			
			int n = ruleList.size();
			while (selected.size() < ruleNumberLimit)
			{
				index = random.nextInt(n);
				IRuleInstance r = ruleList.get(index);
				if (selected.contains(r))
					continue;
				else
					selected.add(r);
			}		
		}
		else //selectionOrder = TautomerConst.RULE_ORDER_BY_SIZE
		{
			//Handle first 1-3 rules
			List<IRuleInstance> rlist = getRules(ruleList, 3);
			
			if (rlist.size() < ruleNumberLimit)
			{
				//1-3 rules are not enough thus some 1-5 rules will be added as well
				selected.addAll(rlist);
				rlist = getRules(ruleList, 5);
				
				if (selected.size() + rlist.size() < ruleNumberLimit)
				{
					//1-3 and 1-5 rules together are not enough thus some 1-7 rules will be added as well
					selected.addAll(rlist);
					rlist = getRules(ruleList, 7);
				}
			}
			
			
			//Handle remaining rules from 1-3 or 1-5 or 1-7 set
			int n = rlist.size();
			while (selected.size() < ruleNumberLimit)
			{
				index = random.nextInt(n);
				IRuleInstance r = rlist.get(index);
				if (selected.contains(r))
					continue;
				else
					selected.add(r);
			}
			
			//TODO in the future: handle other types of rules if used 
			//such as: 1-9 or ring-chain with and various sizes (e.g. 6) etc. 
		}
		
		
			
		return selected;
	}
	
	List<IRuleInstance> getRules(List<IRuleInstance> ruleList, int size)
	{
		List<IRuleInstance> selected = new ArrayList<IRuleInstance>();
		for (IRuleInstance r : ruleList)
		{
			if (r.getRule().stateQueries[0].getAtomCount() == size)
				selected.add(r);
		}
		
		return selected;
	}
	
	public static RuleSelector getDefaultSelectorAll()
	{
		RuleSelector selector = new RuleSelector();  
		selector.setSelectionMode(TautomerConst.RSM_ALL);
		return selector;
	}
	
	public static RuleSelector getDefaultSelectorRandom()
	{
		RuleSelector selector = new RuleSelector();  
		selector.setSelectionMode(TautomerConst.RSM_RANDOM);
		return selector;
	}

	
}
