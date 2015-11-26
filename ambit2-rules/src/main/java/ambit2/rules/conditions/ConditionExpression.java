package ambit2.rules.conditions;

import java.util.ArrayList;

public class ConditionExpression implements ICondition
{
	private ArrayList<ICondition> conditions = new ArrayList<ICondition>();
	private boolean FlagANDOperation = true;
	
	@Override
	public boolean isTrue(Object target) 
	{
		if (FlagANDOperation)
		{
			for (ICondition c: conditions)
			{
				boolean res = c.isTrue(target);
				if (c.isNegated())
					res = ! res;
				
				if (!res)
					return false;
			}
			
			return true;
		}
		else //OR operation
		{
			for (ICondition c: conditions)
			{
				boolean res = c.isTrue(target);
				if (c.isNegated())
					res = ! res;
				
				if (res)
					return true;
			}
			return true;
		}
	}

	@Override
	public boolean isNegated() {
		return false;
	}
	
	@Override
	public void setIsNegated(boolean isNeg) {
		// Nothing is done. Currently ConditionExpression is not negated
	}


	public ArrayList<ICondition> getConditions() {
		return conditions;
	}

	public void setConditions(ArrayList<ICondition> conditions) {
		this.conditions = conditions;
	}
	
	public void addCondition(ICondition cond) {
		conditions.add(cond);
	}

	public boolean isANDOperation() {
		return FlagANDOperation;
	}

	public void setANDOperation(boolean isANDOperation) {
		this.FlagANDOperation = isANDOperation;
	}

	
	
}
