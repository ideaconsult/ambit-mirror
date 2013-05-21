package ambit2.reactions;


public class RetroSynthRule implements IRetroSynthRule
{
	String originalRuleString = "";
	String name = "";
	String smirks = "";
	String info = "";
	
	int id = 0;
	boolean FlagRuleActive = true;
	
	public void postProcessRule()
	{
		
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String newName)
	{
		name = newName;
	}
	
	public String getSmirks()
	{
		return name;
	}
	
	public void setSmirks(String newName)
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
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(ReactionConst.KeyWordPrefix + "NAME = " + name + "\n");
		sb.append(ReactionConst.KeyWordPrefix + "SMIRKS = " + smirks + "\n");
		sb.append(ReactionConst.KeyWordPrefix + "INFO = " + info + "\n");
		return sb.toString();
	}

}
