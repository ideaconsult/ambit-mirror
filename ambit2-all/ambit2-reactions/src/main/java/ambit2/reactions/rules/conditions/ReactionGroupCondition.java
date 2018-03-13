package ambit2.reactions.rules.conditions;

import ambit2.reactions.GenericReactionInstance;
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
	
	public GroupMatch getGroupMatch() {
		return groupMatch;
	}

	public void setGroupMatch(GroupMatch groupMatch) {
		this.groupMatch = groupMatch;
	}

	public RGConditionType getConditionType() {
		return conditionType;
	}

	public void setConditionType(RGConditionType conditionType) {
		this.conditionType = conditionType;
	}

	@Override
	public boolean isTrue(Object target) {
		if (target instanceof GenericReactionInstance)
			return checkReactionInstance((GenericReactionInstance)target);
		
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
	
	boolean checkReactionInstance(GenericReactionInstance gri) 
	{
		switch (conditionType)
		{
		case REACTANT_EXCLUDE_GROUP:
			return !groupMatch.match(gri.target);
		case REACTANT_INCLUDE_GROUP:
			return groupMatch.match(gri.target);
		case PRODUCT_EXCLUDE_GROUP:
			return !groupMatch.match(gri.products);
		case PRODUCT_INCLUDE_GROUP:
			return groupMatch.match(gri.products);	
		}
		return false;
	}
}
