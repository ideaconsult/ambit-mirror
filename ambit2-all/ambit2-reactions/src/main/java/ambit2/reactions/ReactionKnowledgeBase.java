package ambit2.reactions;

import java.util.ArrayList;


public class ReactionKnowledgeBase 
{
	public boolean FlagSkipRuleParsingErrors = false;
	
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
		
		//Loading RetroSynthRules
		reactionParser.setParserMetaInfoForRetroSynthRule();
		for (int i = 0; i < PredefinedReactionKnowledgeBase.retroSynthRules.length; i++)
			addRule(PredefinedReactionKnowledgeBase.retroSynthRules[i], i);
				
		if (!FlagSkipRuleParsingErrors)
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
			boolean FlagOK = true;
			ArrayList<String> postErrors = rule.postProcessRule();
			if (postErrors != null)
				if (!postErrors.isEmpty())
				{
					FlagOK = false;
					for (int i = 0; i < postErrors.size(); i++)
						errors.add("Rule " + (ruleNum+1) + ":  " + postErrors.get(i));
				}
			
			if (FlagOK)
			{
				rule.setID(ruleNum);
				retroSynthRules.add(rule);
			}	
		}
	}
	
	String errorsToString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("\n");
		for (int i = 0; i < errors.size(); i++)
			sb.append(errors.get(i) + "\n");
		return (sb.toString());
	}
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < retroSynthRules.size(); i++)
			sb.append("RetroSynthRule \n" + "ID = " + retroSynthRules.get(i).getID() + "\n" + 
					retroSynthRules.get(i).toString() + "\n");
		
		return (sb.toString());
	}
}
