package ambit2.tautomers;

import java.util.Vector;

public class KnowledgeBase 
{
	public Vector<Rule> rules = new Vector<Rule>();
	
	Vector<String> errors = new Vector<String>(); 
	RuleParser ruleParser = new RuleParser();
	
	KnowledgeBase()
	{
		loadPredefinedBase();
	}
	
	
	public void loadPredefinedBase()
	{
		errors.clear();
		for (int i = 0; i < PredefinedKnowledgeBase.rules.length; i++)
			addRule(PredefinedKnowledgeBase.rules[i]);
	}
	
	
	public void addRule(String newRule)
	{	
		Rule rule = ruleParser.parse(newRule);
		if (rule == null)
			errors.add(ruleParser.errors);
		else
			rules.add(rule);
	}
	
	public String getAllErrors()
	{
		//TODO
		return("");
	}
	
}

