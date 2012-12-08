package ambit2.reactions;

import java.util.ArrayList;



public class ReactionKnowledgeBase 
{
	ArrayList<String> errors = new ArrayList<String>();
	ArrayList<IRetroSynthRule> retroSynthRules = new ArrayList<IRetroSynthRule> (); 
		
	ReactionParser reactionParser = new ReactionParser();
	
	
	public ReactionKnowledgeBase() throws Exception
	{
		loadPredefinedBase();
	}
	
	
	public void loadPredefinedBase() throws Exception 
	{
		errors.clear();
		
		for (int i = 0; i < PredefinedReactionKnowledgeBase.retroSynthRules.length; i++)
		{	
			System.out.println("Loading rule:  " + PredefinedReactionKnowledgeBase.retroSynthRules[i]);
			addRule(PredefinedReactionKnowledgeBase.retroSynthRules[i], i);
		}
		
		
		if (!errors.isEmpty())
			throw new Exception(errorsToString());
	}
	
	public void addRule(String newRule, int ruleNum)
	{	
		IRetroSynthRule rule = reactionParser.parseRetroSynthRule(newRule);
		if (rule == null)
		{	
			ArrayList<String> newErrors = reactionParser.getErrors(); 
			for (int i = 0; i < newErrors.size(); i++)
				errors.add("Rule " + (ruleNum+1) + ":  " + newErrors.get(i));
		}	
		else
		{
			retroSynthRules.add(rule);
			//System.out.println(rule.toString());
		}
	}
	
	String errorsToString()
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < errors.size(); i++)
			sb.append(errors.get(i) + "\n");
		return (sb.toString());
	}
}
