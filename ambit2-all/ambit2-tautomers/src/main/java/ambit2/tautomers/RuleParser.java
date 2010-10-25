package ambit2.tautomers;

public class RuleParser 
{
	String errors = "";
	Rule curRule = null;
	
	public Rule parse(String ruleString)
	{	
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
		
		return(rule);
	}
	
	
	void parseKeyWord(String keyWord)
	{
		
	}
	
	
}
