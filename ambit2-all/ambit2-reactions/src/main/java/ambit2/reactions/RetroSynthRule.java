package ambit2.reactions;

import java.util.ArrayList;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.reactions.ReactionConst.RetroSynthRuleType;
import ambit2.smarts.SMIRKSManager;
import ambit2.smarts.SMIRKSReaction;

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
		processSmirksInfo(SMIRKSManager.getDefaultSMIRKSManager());
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
	
	/*
	public ArrayList<IRetroSynthRuleInstance> getInstances(IAtomContainer str)
	{
		//TODO
		return null;
	}
	*/
	
	
	//---------- specific function implementation -------------
	
	
	void processTypeInfo()
	{
		ruleType =  RetroSynthRuleType.getFromString(stringRuleType);
		if (ruleType == RetroSynthRuleType.UNKNOWN)
			postErrors.add("Incorrect rule type: " + stringRuleType);
	}
	
	void processSmirksInfo(SMIRKSManager smrkMan)
	{
		SMIRKSReaction reaction = smrkMan.parse(smirks);
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
