package ambit2.tautomers;

import java.util.Vector;

import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;

import ambit2.smarts.SmartsParser;

public class KnowledgeBase 
{
	public Vector<Rule> rules = new Vector<Rule>();
	public Vector<Filter> warningFilters = new Vector<Filter>();
	public Vector<Filter> excludeFilters = new Vector<Filter>();
	
	Vector<String> errors = new Vector<String>(); 
	RuleParser ruleParser = new RuleParser();
	SmartsParser sp = new SmartsParser();
	
	KnowledgeBase()
	{
		loadPredefinedBase();
	}
	
	
	public void loadPredefinedBase()
	{
		errors.clear();
		for (int i = 0; i < PredefinedKnowledgeBase.rules.length; i++)
			addRule(PredefinedKnowledgeBase.rules[i]);
		
		for (int i = 0; i < PredefinedKnowledgeBase.warningFragments.length; i++)
			addFilterRule(PredefinedKnowledgeBase.warningFragments[i], Filter.FT_WARNING);
		
		for (int i = 0; i < PredefinedKnowledgeBase.excludeFragments.length; i++)
			addFilterRule(PredefinedKnowledgeBase.excludeFragments[i], Filter.FT_EXCLUDE);
		
		if (errors.size() > 0)
		{
			System.out.println("There are errors in the knowledge base:");
			for (int i = 0; i < errors.size(); i++)
				System.out.println("Rule " + (i+1) + ":  " + errors.get(i));
		}
		
	}
	
	
	public void addRule(String newRule)
	{	
		Rule rule = ruleParser.parse(newRule);
		if (rule == null)
			errors.add(ruleParser.errors);
		else
		{
			rules.add(rule);
			//System.out.println(rule.toString());
		}
	}
	
	public void addFilterRule(String fRule, int filterType )
	{
		QueryAtomContainer q = sp.parse(fRule);			
		String errorMsg = sp.getErrorMessages();
		if (!errorMsg.equals(""))	
		{
			String newError = "Incorrect warning filter: " + fRule;
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
	
	
	
	public String getAllErrors()
	{
		//TODO
		return("");
	}
	
}

