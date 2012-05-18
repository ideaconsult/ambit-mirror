package ambit2.tautomers;

import java.util.Vector;

import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;

import ambit2.smarts.SmartsParser;

public class KnowledgeBase 
{
	public Vector<Rule> rules = new Vector<Rule>();
	public Vector<RankingRule> rankingRules = new Vector<RankingRule>();
	public Vector<Filter> warningFilters = new Vector<Filter>();
	public Vector<Filter> excludeFilters = new Vector<Filter>();
	
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
		QueryAtomContainer q = sp.parse(fRule);			
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
	
	
	public String getAllErrors()
	{
		StringBuffer sb = new StringBuffer();
		
		for (int i = 0; i < errors.size(); i++)
			sb.append(errors.get(i) + "\n");
		
		return(sb.toString());		
	}
	
}

