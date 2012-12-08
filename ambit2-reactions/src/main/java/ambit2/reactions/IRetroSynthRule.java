package ambit2.reactions;

public interface IRetroSynthRule 
{
	public String getName();
	public void setName(String newName);
	public String getOriginalRuleString();
	public boolean isRuleActive();
	public void setRuleActive(boolean isActive);
	
}
