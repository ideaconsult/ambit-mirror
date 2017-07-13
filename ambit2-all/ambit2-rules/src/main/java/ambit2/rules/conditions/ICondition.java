package ambit2.rules.conditions;

public interface ICondition
{	
	public static enum CondType { 
		DESCRIPTOR_VALUE, UNDEFINED
	}
	
	public boolean isTrue(Object target);
	public boolean isNegated();
	public void setIsNegated(boolean isNeg);
}
