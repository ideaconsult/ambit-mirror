package ambit2.reactions.retrosynth;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import ambit2.reactions.rules.ReactionParser;
import ambit2.reactions.rules.RetroSynthRule;


public class ReactionKnowledgeBase 
{
	public boolean FlagSkipRuleParsingErrors = false;	
	public ArrayList<String> errors = new ArrayList<String>();
	public ArrayList<RetroSynthRule> retroSynthRules = new ArrayList<RetroSynthRule>(); 	
	
	ReactionParser reactionParser = new ReactionParser();
	
	
	public ReactionKnowledgeBase() throws Exception
	{
		loadPredefinedBase();
	}
	
	public ReactionKnowledgeBase(String fileName) throws Exception
	{
		loadKnowledgeBaseFromTextFile(fileName);
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
	
	public void loadKnowledgeBaseFromTextFile(String fileName) throws Exception 
	{
		errors.clear();

		File file = new File(fileName);
		RandomAccessFile f = new RandomAccessFile(file,"r");			
		long length = f.length();

		int lineNum = 0;
		int ruleNum = 1;
		while (f.getFilePointer() < length)
		{	
			lineNum++;
			String line = f.readLine();
			String ruleString = line.trim();
			if (!ruleString.isEmpty())
			{	
				addRule(ruleString, ruleNum);
				ruleNum++;
			}	
		}
		f.close();
		
		if (!FlagSkipRuleParsingErrors)
			if (!errors.isEmpty())
				throw new Exception(errorsToString());
	}
		
	public void addRule(String newRule, int ruleNum)
	{	 
		RetroSynthRule rule = reactionParser.parseRetroSynthRule(newRule);
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
	
	public String errorsToString()
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
