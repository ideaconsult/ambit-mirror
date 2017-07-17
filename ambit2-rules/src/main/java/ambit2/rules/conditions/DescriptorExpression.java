package ambit2.rules.conditions;

import java.util.ArrayList;
import java.util.List;

public class DescriptorExpression implements ICondition
{
	public static enum LogicalOperation {
		LEFT_BRACKET, RIGHT_BRACKET, OR, AND
	}
	
	public List<Object> tokens = new ArrayList<Object>();
	
	
	public static DescriptorExpression generateDescriptorExpression(List<Object> tokens) throws Exception
	{
		int nOpenBrackets = 0;
		//TODO
		return null;
	}
	
	public boolean calculateExprValue(Object target)
	{
		//TODO
		return false;
	}

	@Override
	public boolean isTrue(Object target) {
		return calculateExprValue(target);
	}

	@Override
	public boolean isNegated() {		
		return false;
	}

	@Override
	public void setIsNegated(boolean isNeg) {
	}
	
}
