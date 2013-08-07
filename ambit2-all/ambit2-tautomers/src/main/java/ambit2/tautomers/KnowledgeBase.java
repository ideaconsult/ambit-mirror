package ambit2.tautomers;

import java.util.ArrayList;
import java.util.Vector;

import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;

import ambit2.smarts.SmartsParser;

public class KnowledgeBase 
{
	public Vector<Rule> rules = new Vector<Rule>();
	public Vector<RankingRule> rankingRules = new Vector<RankingRule>();
	public Vector<Filter> warningFilters = new Vector<Filter>();
	public Vector<Filter> excludeFilters = new Vector<Filter>();
	
	public boolean FlagUse13Shifts = true;
	public boolean FlagUse15Shifts = true;
	public boolean FlagUse17Shifts = true;
	public boolean FlagUse19Shifts = false;
	
	public boolean FlagUseRingChainRules = false;
	public boolean FlagUseChlorineRules = false;
	
	Vector<String> errors = new Vector<String>(); 
	RuleParser ruleParser = new RuleParser();
	SmartsParser sp = new SmartsParser();
	
	KnowledgeBase()
	{
		sp.mSupportDoubleBondAromaticityNotSpecified = true;
		loadPredefinedBase();
	}
	
	
	public void loadPredefinedBase()
	{
		errors.clear();
		for (int i = 0; i < PredefinedKnowledgeBase.rules.length; i++)
			addRule(PredefinedKnowledgeBase.rules[i], i);
		
		for (int i = 0; i < PredefinedKnowledgeBase.warningFragments.length; i++)
			addFilterRule(PredefinedKnowledgeBase.warningFragments[i], Filter.FT_WARNING);
		
		for (int i = 0; i < PredefinedKnowledgeBase.excludeFragments.length; i++)
			addFilterRule(PredefinedKnowledgeBase.excludeFragments[i], Filter.FT_EXCLUDE);
				
		for (int i = 0; i < PredefinedKnowledgeBase.rankingRules.length; i++)
			addRankingRule(PredefinedKnowledgeBase.rankingRules[i], i);
		
		synchronizeRankingRules();
	}
	
	
	public void addRule(String newRule, int ruleNum)
	{	
		Rule rule = ruleParser.parse(newRule);
		if (rule == null)
			errors.add("Rule " + (ruleNum+1) + ":  " + ruleParser.errors);
		else
		{
			rules.add(rule);
			//System.out.println(rule.toString());
		}
	}
	
	public void addFilterRule(String fRule, int filterType)
	{
		IQueryAtomContainer q = sp.parse(fRule);			
		String errorMsg = sp.getErrorMessages();
		if (!errorMsg.equals(""))	
		{
			String newError = "Incorrect filter: " + fRule;
			errors.add(newError);
		}			
		else
		{	
			Filter newFilter = new Filter();
			RuleStateFlags flags = new RuleStateFlags();
			flags.hasRecursiveSmarts = sp.hasRecursiveSmarts;
			flags.mNeedExplicitHData = sp.needExplicitHData();
			flags.mNeedNeighbourData = sp.needNeighbourData();
			flags.mNeedParentMoleculeData = sp.needParentMoleculeData();
			flags.mNeedRingData = sp.needRingData();
			flags.mNeedRingData2 = sp.needRingData2();
			flags.mNeedValenceData = sp.needValencyData();
			
			newFilter.fragmentSmarts = fRule;
			newFilter.fragmentQuery = q;
			newFilter.type = filterType;
			newFilter.fragmentFlags = flags;
			switch (filterType)
			{
			case Filter.FT_WARNING:
				warningFilters.add(newFilter);
				break;
			case Filter.FT_EXCLUDE:
				excludeFilters.add(newFilter);
				break;	
			}	
		}
	}
	
	public void addRankingRule(String rRule, int ruleNum)
	{
		RankingRule rule = ruleParser.parseRankingRule(rRule);
		if (rule == null)
			errors.add("Ranking Rule " + (ruleNum+1) + ":  " + ruleParser.errors);
		else
		{
			rankingRules.add(rule);
			//System.out.println(rule.toString());
		}
	}
	
	public void synchronizeRankingRules()
	{
		for (int i = 0; i < rankingRules.size(); i++)
		{
			RankingRule rankRule = rankingRules.get(i);
			Rule rule = getRuleByName(rankRule.name);
			if (rule == null)
			{
				errors.add("Ranking Rule " + rankRule.name + " is not synchronized with a master rule!");
			}
			else
			{
				rule.rankingRule = rankRule;
				rankRule.masterRule = rule;
			}
		}
		
		//TODO - check for rules which are not linked with a ranking rule
	}
	
	
	Rule getRuleByName(String name)
	{
		for (int i = 0; i < rules.size(); i++)
		{
			Rule r = rules.get(i);
			if (r.name.equals(name))
				return (r);
		}
		
		return null;
	}
	
	public void activateRule(String ruleName, boolean Fl_Active)
	{
		Rule rule = getRuleByName(ruleName);
		if (rule!= null)
			rule.isRuleActive = Fl_Active;
	}
	
	public void setActiveRules(ArrayList<String> ruleNames)
	{
		setAllRulesActive(false);
		for(String name:ruleNames)
		{
			Rule rule = getRuleByName(name);
			if (rule!= null)
				rule.isRuleActive = true;
		}
	}
	
	void setAllRulesActive(boolean Fl_Active)
	{
		for (int i = 0; i < rules.size(); i++)		
			rules.get(i).isRuleActive = Fl_Active;
	}
	
	public ArrayList<String> getAllRuleNames()
	{
		ArrayList<String> rnames = new ArrayList<String>();
		for (int i = 0; i < rules.size(); i++)
		{	
			Rule rule = rules.get(i);
			rnames.add(rule.name);
		}	
		return rnames;
	}
	
	public ArrayList<String> getActiveRuleNames()
	{
		ArrayList<String> activeRules = new ArrayList<String>();
		for (int i = 0; i < rules.size(); i++)
		{	
			Rule rule = rules.get(i);
			if (rule.isRuleActive)
				activeRules.add(rule.name);
		}	
		return activeRules;
	}
	
	public void activateRingChainRules(boolean FlagActivate)
	{
		FlagUseRingChainRules = FlagActivate;
		
		for (int i = 0; i < rules.size(); i++)
		{	
			Rule rule = rules.get(i);
			if (rule.type == TautomerConst.RT_RingChain)
				rule.isRuleActive = FlagActivate;
		}	
	}
	
	public void activateChlorineRules(boolean FlagActivate)
	{
		FlagUseChlorineRules = FlagActivate;		
		for (int i = 0; i < rules.size(); i++)
		{	
			Rule rule = rules.get(i);
			if (rule.type == TautomerConst.RT_MobileGroup)
				if (rule.mobileGroup.equals("Cl"))
					rule.isRuleActive = FlagActivate;
		}	
	}
	
	public void use13ShiftRulesOnly(boolean FlagUseOnly13)
	{
		if (FlagUseOnly13)
		{
			FlagUse15Shifts = false;
			FlagUse17Shifts = false;
			FlagUse19Shifts = false;
		}
		else
		{
			FlagUse15Shifts = true;
			FlagUse17Shifts = true;
			FlagUse19Shifts = true;
		}
		
				
		for (int i = 0; i < rules.size(); i++)
		{	
			Rule rule = rules.get(i);
			if (rule.type == TautomerConst.RT_MobileGroup)
			{
				if (rule.stateQueries[0].getAtomCount() > 3)
					rule.isRuleActive = !FlagUseOnly13;  //since logical condition is use only 1-3 shift negation is applied
			}
				
		}	
	}
	
	public void use15ShiftRules(boolean Fl_Use)
	{
		FlagUse15Shifts = Fl_Use;
		
		for (int i = 0; i < rules.size(); i++)
		{	
			Rule rule = rules.get(i);
			if (rule.type == TautomerConst.RT_MobileGroup)
			{
				if (rule.stateQueries[0].getAtomCount() == 5)
					rule.isRuleActive = Fl_Use; 
			}	
		}	
	}
	
	
	public void use17ShiftRules(boolean Fl_Use)
	{
		FlagUse17Shifts = Fl_Use;
		
		for (int i = 0; i < rules.size(); i++)
		{	
			Rule rule = rules.get(i);
			if (rule.type == TautomerConst.RT_MobileGroup)
			{
				if (rule.stateQueries[0].getAtomCount() == 7)
					rule.isRuleActive = Fl_Use; 
			}	
		}	
	}
	
	
	public void use19ShiftRules(boolean Fl_Use)
	{
		FlagUse19Shifts = Fl_Use;
		
		for (int i = 0; i < rules.size(); i++)
		{	
			Rule rule = rules.get(i);
			if (rule.type == TautomerConst.RT_MobileGroup)
			{
				if (rule.stateQueries[0].getAtomCount() == 9)
					rule.isRuleActive = Fl_Use; 
			}	
		}	
	}
	
	
	public String getAllErrors()
	{
		StringBuffer sb = new StringBuffer();
		
		for (int i = 0; i < errors.size(); i++)
			sb.append(errors.get(i) + "\n");
		
		return(sb.toString());		
	}
	
	
	
}

