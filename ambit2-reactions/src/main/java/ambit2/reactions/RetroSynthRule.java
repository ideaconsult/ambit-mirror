package ambit2.reactions;

import java.util.ArrayList;
import ambit2.reactions.ReactionConst.RetroSynthRuleType;

public class RetroSynthRule implements IRetroSynthRule
{
	//String info
	String originalRuleString = "";
	String name = "";
	String smirks = "";
	String info = "";
	String stringRuleType = "";
	RetroSynthRuleType ruleType;
			
	
	int id = 0;
	boolean FlagRuleActive = true;
	ArrayList<String> postErrors = new ArrayList<String>(); 
	
	//---------- interface implementation -------------
	
	public ArrayList<String>  postProcessRule()
	{	
		postErrors.clear();
		processTypeInfo();
		processSmirksInfo();
		return postErrors;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String newName)
	{
		name = newName;
	}
	
	public String getInfo()
	{
		return info;
	}
	
	public void setInfo(String newInfo)
	{
		info = newInfo;
	}
	
	public String getOriginalRuleString()
	{
		return originalRuleString;
	}
	
	public boolean isRuleActive()
	{
		return FlagRuleActive;
	}
	
	public void setRuleActive(boolean isActive)
	{
		FlagRuleActive = isActive;
	}
	
	public int getID()
	{
		return id;
	}
	
	public void setID(int newID)
	{
		id = newID;
	}
	
	public RetroSynthRuleType getType()
	{
		return ruleType;
	}
	
	public void setType(RetroSynthRuleType newType)
	{
		ruleType = newType;
	}
	
	public String getSmirks()
	{
		return name;
	}
	
	public void setSmirks(String newName)
	{
		name = newName;
	}
	
	
	//---------- specific function implementation -------------
	
	
	void processTypeInfo()
	{
		//TODO
	}
	
	void processSmirksInfo()
	{
		//TODO	
	}
	
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(ReactionConst.KeyWordPrefix + "NAME = " + name + "\n");
		sb.append(ReactionConst.KeyWordPrefix + "TYPE = " + ruleType + "\n");
		sb.append(ReactionConst.KeyWordPrefix + "SMIRKS = " + smirks + "\n");
		sb.append(ReactionConst.KeyWordPrefix + "INFO = " + info + "\n");
		return sb.toString();
	}

}
