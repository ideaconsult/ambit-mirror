package ambit2.reactions;

import java.util.ArrayList;



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
	
	public Type getType();
	
	public void setType(Type newType);	
	
	//public ArrayList<IRetroSynthRuleInstance> getRuleInstances(IAtomContainer str);
	
	/*
	 * Retro Synthesis Rule Types 
	 */
	public enum Type {
		TRANSFORM, 
		UNKNOWN;

		public static Type getFromString(String stringType)
		{
			if (stringType.equals("TRANSFORM"))
				return Type.TRANSFORM;

			return Type.UNKNOWN;
		}
	}

}
