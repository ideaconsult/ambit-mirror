package ambit2.reactions.rules;

import java.util.ArrayList;



public class ReactionParser 
{	
	ArrayList<String> errors = new ArrayList<String>(); 
	RetroSynthRule curRule = null;
	GenericParserUtils genericParserUtils = new GenericParserUtils(); 
	
	public ArrayList<String> getErrors()
	{
		return errors;
	}
	
	/*
	 * Defining the meta-info for the RetroSynthRule
	 */
	public static GenericRuleMetaInfo getRetroSynthRuleMetaInfo()
	{
		GenericRuleMetaInfo mi = new GenericRuleMetaInfo();
		mi.keyWord.add("NAME");
		mi.objectFieldName.add("name");
		mi.keyWordRequired.add(new Boolean(true));
		
		mi.keyWord.add("TYPE");
		mi.objectFieldName.add("stringRuleType");
		mi.keyWordRequired.add(new Boolean(true));
				
		mi.keyWord.add("SMIRKS");
		mi.objectFieldName.add("smirks");
		mi.keyWordRequired.add(new Boolean(false));		
		
		mi.keyWord.add("INFO");
		mi.objectFieldName.add("info");
		mi.keyWordRequired.add(new Boolean(false));		
		
		return(mi);
	}
	
	public void setParserMetaInfoForRetroSynthRule()
	{
		genericParserUtils.metaInfo = ReactionParser.getRetroSynthRuleMetaInfo();
	}
	
	public RetroSynthRule parseRetroSynthRule(String ruleString) 
	{	
		errors.clear();
		
		RetroSynthRule rule = new RetroSynthRule();
		rule.originalRuleString = ruleString;
		curRule = rule;
		
		try
		{
			genericParserUtils.parseRule(ruleString, rule);
			if (genericParserUtils.getErrors().size() > 0)
			{	
				errors.addAll(genericParserUtils.getErrors());
				return null;
			}	
		}
		catch (Exception e)
		{
			errors.add(e.toString());
		}
		
		if (errors.isEmpty())
			return(rule);
		else
		{	
			return(null);
		}		
	}
	
}
