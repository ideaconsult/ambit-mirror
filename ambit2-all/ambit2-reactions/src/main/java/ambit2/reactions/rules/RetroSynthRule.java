package ambit2.reactions.rules;

import java.util.ArrayList;

import ambit2.reactions.ReactionConst;
import ambit2.smarts.SMIRKSManager;
import ambit2.smarts.SMIRKSReaction;

public class RetroSynthRule implements IRetroSynthRule
{
	//String info
	String originalRuleString = "";
	String name = "";
	public String smirks = "";
	String info = "";
	String stringRuleType = "";
	IRetroSynthRule.Type ruleType;
	public SMIRKSReaction reaction;
			
	
	int id = 0;
	boolean FlagRuleActive = true;
	ArrayList<String> postErrors = new ArrayList<String>(); 
	
	//---------- interface implementation -------------
	@Override
	public ArrayList<String>  postProcessRule()
	{	
		postErrors.clear();
		processTypeInfo();
		processSmirksInfo(SMIRKSManager.getDefaultSMIRKSManager());
		return postErrors;
	}
	
	@Override
	public String getName()
	{
		return name;
	}
	
	@Override
	public void setName(String newName)
	{
		name = newName;
	}
	
	@Override
	public String getInfo()
	{
		return info;
	}
	
	@Override
	public void setInfo(String newInfo)
	{
		info = newInfo;
	}
	
	@Override
	public String getOriginalRuleString()
	{
		return originalRuleString;
	}
	
	@Override
	public boolean isRuleActive()
	{
		return FlagRuleActive;
	}
	
	@Override
	public void setRuleActive(boolean isActive)
	{
		FlagRuleActive = isActive;
	}
	
	@Override
	public int getID()
	{
		return id;
	}
	
	@Override
	public void setID(int newID)
	{
		id = newID;
	}
	
	@Override
	public IRetroSynthRule.Type getType()
	{
		return ruleType;
	}
	
	@Override
	public void setType(IRetroSynthRule.Type newType)
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
	
	/*
	public ArrayList<IRetroSynthRuleInstance> getRuleInstances(IAtomContainer str)
	{
		//TODO
		return null;
	}
	*/
	
	//---------- specific function implementation -------------
	
	
	void processTypeInfo()
	{
		ruleType =  IRetroSynthRule.Type.getFromString(stringRuleType);
		if (ruleType == IRetroSynthRule.Type.UNKNOWN)
			postErrors.add("Incorrect rule type: " + stringRuleType);
	}
	
	void processSmirksInfo(SMIRKSManager smrkMan)
	{
		reaction = smrkMan.parse(smirks);
		if (!smrkMan.getErrors().equals(""))		
			postErrors.add("There are errors in SMIRKS: " + smirks + "  " + smrkMan.getErrors());
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
