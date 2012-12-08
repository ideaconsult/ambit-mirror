package ambit2.reactions;

import java.util.ArrayList;



public class ReactionParser 
{	
	ArrayList<String> errors = new ArrayList<String>(); 
	
	RetroSynthRule curRule = null;
	
	
	
	ArrayList<String> getErrors()
	{
		return errors;
	}
	
	//------------ parsing parseRetroSynthRule -------------------
	
	public IRetroSynthRule parseRetroSynthRule(String ruleString)
	{	
		errors.clear();
		
		RetroSynthRule rule = new RetroSynthRule();
		rule.originalRuleString = ruleString;
		curRule = rule;
		
		int res = ruleString.indexOf(ReactionConst.KeyWordPrefix, 0);
		int curPos = res;
		
		if (res == -1)
		{
			errors.add("No key words found in the rule");
			return(null);
		}
		
		while (res != -1)
		{	
			res = ruleString.indexOf(ReactionConst.KeyWordPrefix, curPos+ReactionConst.KeyWordPrefix.length());
			String keyword;
			if (res == -1)
				keyword = ruleString.substring(curPos);
			else
			{	
				keyword = ruleString.substring(curPos,res);
				curPos = res;	
			}
			
			parseRetroSynthKeyWord(keyword);
		}	
		
		
		if (errors.isEmpty())
			return(rule);
		else
		{	
			return(null);
		}
		
		
	}
	
	void parseRetroSynthKeyWord(String keyWord)
	{	
		int sepPos = keyWord.indexOf(ReactionConst.KeyWordSeparator);		
		if (sepPos == -1)
		{	
			errors.add("Incorrect key word syntax: " + keyWord );
			return;
		}
		
		String key = keyWord.substring(ReactionConst.KeyWordPrefix.length(), sepPos).trim();		
		String keyValue = keyWord.substring(sepPos+1).trim();
		
		
		if (key.equals("NAME"))	
		{	
			parseRSRName(keyValue);
			return;
		}
		
		if (key.equals("INFO"))	
		{	
			parseRSRInfo(keyValue);
			return;
		}
		
		errors.add("Unknown key word: " + key);
		
	}
	
	void parseRSRName(String keyValue)
	{
		curRule.name = keyValue;
	}
	
	void parseRSRInfo(String keyValue)
	{
		curRule.info = keyValue;
	}
	
	
	
	
}
