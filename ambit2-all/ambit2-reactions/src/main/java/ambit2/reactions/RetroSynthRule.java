package ambit2.reactions;


public class RetroSynthRule implements IRetroSynthRule
{
	String originalRuleString = "";
	String name = "";
	boolean FlagRuleActive = true;
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String newName)
	{
		name = newName;
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
	
	

}
