package ambit2.tautomers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RuleSelector 
{
	protected int selectionMode = TautomerConst.RSM_ALL;
	protected int ruleNumberLimit = 10;
	protected int limitInterval = 3; //This is used for fuzzines in the limit handling
	protected int limitSwitchToCombinatorial = 15;  
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
	
	public int getLimitInterval(){
		return limitInterval;
	}
	
	public void setLimitInterval(int limitInterval){
		this.limitInterval = limitInterval;
	}
	
	public int getSwitchToCombinatorial(){
		return limitSwitchToCombinatorial;
	}
	
	public void setLimitSwitchToCombinatorial(int limitSwitchToCombinatorial){
		this.limitSwitchToCombinatorial = limitSwitchToCombinatorial;
	}
	
	public boolean switchToCombinatorial(){
		return FlagSwitchToCombinatorial;
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
		if (ruleNumberLimit + limitInterval >= ruleList.size())
		{
			selected.addAll(ruleList);
			return selected;
		}
		
		Random random = new Random();
		int n = ruleList.size();
		int index;
		
		while (selected.size() < ruleNumberLimit)
		{
			index = random.nextInt(n);
			IRuleInstance r = ruleList.get(index);
			if (selected.contains(r))
				continue;
			else
				selected.add(r);
		}
		
		if (limitSwitchToCombinatorial < ruleList.size())
			FlagSwitchToCombinatorial = true;
			
		return selected;
	}
	
	public static RuleSelector getDefaultSelectorAll()
	{
		RuleSelector selector = new RuleSelector();  
		selector.setSelectionMode(TautomerConst.RSM_ALL);
		return selector;
	}

	
}
