package ambit2.reactions.rules.conditions;

import ambit2.rules.conditions.ICondition;
import ambit2.smarts.IsomorphismTester;
import ambit2.smarts.SmartsParser;
import ambit2.smarts.groups.GroupMatch;

public class ReactionGroupCondition implements ICondition
{	
	public static enum RGConditionType {
		PRODUCT_EXCLUDE_GROUP, PRODUCT_INCLUDE_GROUP, 
		REACTANT_EXCLUDE_GROUP, REACTANT_INCLUDE_GROUP
	}
	
	GroupMatch groupMatch = null;
	RGConditionType conditionType = null;
	
	public ReactionGroupCondition(
			RGConditionType conditionType, 
			String smarts, 
			SmartsParser parser, 
			IsomorphismTester isoTester)
	{
		groupMatch = new GroupMatch(smarts, parser, isoTester);
		this.conditionType = conditionType;
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
