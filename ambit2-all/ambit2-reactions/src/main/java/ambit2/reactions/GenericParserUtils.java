package ambit2.reactions;

import java.util.ArrayList;
import java.lang.reflect.Field;

public class GenericParserUtils 
{
	public String KeyWordPrefix = ReactionConst.KeyWordPrefix;
	public String KeyWordSeparator = ReactionConst.KeyWordSeparator;
	public String KeyWordElementSeparator = ReactionConst.KeyWordElementSeparator;
	
	ArrayList<String> errors = new ArrayList<String>();
	
	Object targetObj = null;
	GenericRuleMetaInfo metaInfo = null;
	
	ArrayList<String> getErrors()
	{
		return errors;
	}
	
	public void setMetaInfo(GenericRuleMetaInfo mInfo)
	{
		metaInfo = mInfo;
	}
	
	public void parseRule(String ruleString, Object o) throws Exception
	{	
		targetObj = o;
		errors.clear();
		
		RetroSynthRule rule = new RetroSynthRule();
		rule.originalRuleString = ruleString;
				
		int res = ruleString.indexOf(KeyWordPrefix, 0);
		int curPos = res;
		
		if (res == -1)
		{
			errors.add("No key words found in the rule");
			return;
		}
		
		while (res != -1)
		{	
			res = ruleString.indexOf(KeyWordPrefix, curPos + KeyWordPrefix.length());
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
	}
	
	void parseKeyWord(String keyWord) throws Exception
	{	
		int sepPos = keyWord.indexOf(KeyWordSeparator);		
		if (sepPos == -1)
		{	
			errors.add("Incorrect key word syntax: " + keyWord );
			return;
		}
		
		String key = keyWord.substring(KeyWordPrefix.length(), sepPos).trim();		
		String keyValue = keyWord.substring(sepPos+1).trim();
		
		for (int i = 0; i < metaInfo.keyWord.size(); i++)
		{
			if (metaInfo.keyWord.get(i).equals(key))
			{
				setKeyValue(keyValue, i);
				return;
			}
		}
		
		errors.add("Unknown key word: " + key);
	}
	
	void setKeyValue(String keyValue, int keyIndex) throws Exception
	{
		Class cls = targetObj.getClass();
		Field f = cls.getDeclaredField(metaInfo.objectFieldName.get(keyIndex));
		//This approach is applied only for String fields
		f.set(targetObj, keyValue);
	}
	
	
}
