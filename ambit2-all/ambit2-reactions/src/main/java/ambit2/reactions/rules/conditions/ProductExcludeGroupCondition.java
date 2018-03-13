package ambit2.reactions.rules.conditions;

import ambit2.rules.conditions.ICondition;
import ambit2.smarts.IsomorphismTester;
import ambit2.smarts.SmartsParser;
import ambit2.smarts.groups.GroupMatch;

public class ProductExcludeGroupCondition implements ICondition
{
	GroupMatch groupMatch = null;
	
	public ProductExcludeGroupCondition(String smarts, SmartsParser parser, IsomorphismTester isoTester)
	{
		groupMatch = new GroupMatch(smarts, parser, isoTester);
	}
	
	@Override
	public boolean isTrue(Object target) {
		//TODO
		return false;
	}

	@Override
	public boolean isNegated() {
		return false;
	}

	@Override
	public void setIsNegated(boolean isNeg) {
		//does not have any effect
	}
	
}
