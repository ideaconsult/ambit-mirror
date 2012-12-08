package ambit2.reactions;


public class RetroSynthRule implements IRetroSynthRule
{
	String originalRuleString = "";
	String name = "";
	String info = "";
	int id = 0;
	boolean FlagRuleActive = true;
	
	
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

}
