package ambit2.rules;


import ambit2.rules.conditions.ICondition;

public interface IBasicRule 
{
	public String getName();
	public ICondition getCondition();
}
