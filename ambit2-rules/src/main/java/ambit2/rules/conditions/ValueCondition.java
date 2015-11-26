package ambit2.rules.conditions;

import ambit2.rules.conditions.value.IValue;
import ambit2.rules.conditions.value.Value;

public class ValueCondition implements IValueCondition
{
	private IValue value = null;
	private boolean FlagNegated = false;
	
	public ValueCondition (IValue value)
	{
		setValue(value);
	}
		
	@Override
	public boolean isTrue(Object target) {
		if (target instanceof Double)
			return isTrue((Double) target);		
		if (target instanceof Integer)
			return isTrue(((Integer) target).doubleValue());
		return false;
	}

	@Override
	public boolean isTrue(Double target) {
		if (value == null)
			return false;
		
		if (this.isNegated())
			return !(value.getRelation().check(value.getValue(), target));
		else
			return value.getRelation().check(value.getValue(), target);
	}

	@Override
	public IValue getValue() {
		return value;
	}

	@Override
	public void setValue(IValue value) {
			this.value = value;
	}

	@Override
	public boolean isNegated() {
		return FlagNegated;
	}

	@Override
	public void setIsNegated(boolean isNeg) {
		FlagNegated = isNeg;	
	}
}
