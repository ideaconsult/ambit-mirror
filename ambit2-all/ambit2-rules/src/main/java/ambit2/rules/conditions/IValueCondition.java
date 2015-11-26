package ambit2.rules.conditions;

import ambit2.rules.conditions.value.IValue;

public interface IValueCondition extends ICondition
{
	public boolean isTrue(Double target);
	public IValue getValue();
	public void setValue(IValue value);
}
