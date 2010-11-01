package ambit2.tautomers;

public class RuleParser 
{
	String errors = "";
	Rule curRule = null;
	
	public Rule parse(String ruleString)
	{	
		System.out.println("rule: " + ruleString);
		errors = "";
		Rule rule = new Rule();
		curRule = rule;
		int curPos = 0;
		int res = ruleString.indexOf(TautomerConst.KeyWordPrefix, curPos);
		
		while (res != -1)
		{	
			res = ruleString.indexOf(TautomerConst.KeyWordPrefix, curPos+TautomerConst.KeyWordPrefix.length());
			String keyword;
			if (res == -1)
				keyword = ruleString.substring(curPos);
			else
			{	
				keyword = ruleString.substring(curPos,res);
				curPos = res;	
			}
			
			parseKeyWord(keyword);
		}			
		
		
		postProcessing();
		
		//System.out.println("errors: " + errors);		
		if (errors.equals(""))
			return(rule);
		else
			return(null);
	}
	
	
	void postProcessing()
	{
		
	}
	
	void parseKeyWord(String keyWord)
	{		
		//System.out.println("   keyword: " + keyWord);
		int sepPos = keyWord.indexOf(TautomerConst.KeyWordSeparator);		
		if (sepPos == -1)
		{	
			errors += "Incorrect key word syntax: " + keyWord + "\n";
			return;
		}
		
		String key = keyWord.substring(TautomerConst.KeyWordPrefix.length(), sepPos).trim();		
		String keyValue = keyWord.substring(sepPos+1).trim();
		//System.out.println(">"+keyValue+"<");
		
		if (key.equals("NAME"))	
		{	
			parseName(keyValue);
			return;
		}
		
				
		if (key.equals("TYPE"))
		{	
			parseType(keyValue);
			return;
		}
		
		if (key.equals("GROUP"))	
		{	
			parseGroup(keyValue);
			return;
		}
		
		if (key.equals("STATES"))	
		{	
			parseStates(keyValue);
			return;
		}
		
		if (key.equals("GROUP_POS"))	
		{	
			parseGroup_Pos(keyValue);
			return;
		}
		
		if (key.equals("INFO"))	
		{	
			parseInfo(keyValue);
			return;
		}
		
		errors += "Unknow key word: " + key + "\n";
		
	}
	
	void parseName(String keyValue)
	{
		curRule.name = keyValue;
	}
	
	void parseType(String keyValue)
	{
		if (keyValue.equals("MOBILE_GROUP"))
		{	
			curRule.type = TautomerConst.RT_MobileGroup;
			return;
		}	
		
		errors += "Unknow rule type: " + keyValue + "\n";
	}
	
	void parseGroup(String keyValue)
	{
		curRule.mobileGroup = keyValue;
	}
	
	
	void parseStates(String keyValue)
	{
		
	}
	
	void parseGroup_Pos(String keyValue)
	{	
	}
	
	void parseInfo(String keyValue)
	{	
	}
	
	
	
	
	
	
}
