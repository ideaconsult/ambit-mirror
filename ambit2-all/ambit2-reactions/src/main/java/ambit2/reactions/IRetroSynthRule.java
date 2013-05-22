package ambit2.reactions;

import java.util.ArrayList;
import ambit2.reactions.ReactionConst.RetroSynthRuleType;

public interface IRetroSynthRule 
{
	public ArrayList<String>  postProcessRule();
	
	public String getName();
	
	public void setName(String newName);
	
	public String getInfo();
	
	public void setInfo(String newInfo);
	
	public String getOriginalRuleString();
	
	public boolean isRuleActive();
	
	public void setRuleActive(boolean isActive);
	
	public int getID();
	
	public void setID(int newID);
	
	public RetroSynthRuleType getType();
	
	public void setType(RetroSynthRuleType newType);	
	
}
