package ambit2.reactions;

public interface IRetroSynthRule 
{
	public String getName();
	
	public void setName(String newName);
	
	public String getInfo();
	
	public void setInfo(String newInfo);
	
	public String getOriginalRuleString();
	
	public boolean isRuleActive();
	
	public void setRuleActive(boolean isActive);
	
	public int getID();
	
	public void setID(int newID);
	
}
