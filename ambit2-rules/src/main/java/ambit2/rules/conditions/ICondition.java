package ambit2.rules.conditions;

public interface ICondition
{	
	public boolean isTrue(Object target);
	public boolean isNegated();
	public void setIsNegated(boolean isNeg);
}
