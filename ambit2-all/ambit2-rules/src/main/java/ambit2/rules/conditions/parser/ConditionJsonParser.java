package ambit2.rules.conditions.parser;


import ambit2.rules.conditions.DescriptorValueCondition;
import ambit2.rules.conditions.IDescriptorValueCondition;
import ambit2.rules.conditions.value.IValue;
import ambit2.rules.conditions.value.IValue.Relation;
import ambit2.rules.conditions.value.Value;
import ambit2.rules.json.JSONParsingUtils;

import com.fasterxml.jackson.databind.JsonNode;

public class ConditionJsonParser 
{
	public static IDescriptorValueCondition getDescriptorValueCondition(JsonNode node) throws Exception
	{
		StringBuffer errors = new StringBuffer();
		String descrName = JSONParsingUtils.extractStringKeyword(node, "DESCRIPTOR", true);
		String descrRelationStr = JSONParsingUtils.extractStringKeyword(node, "RELATION", true);
		IValue.Relation relation = Relation.getRelationFromString(descrRelationStr);
		Double descrValue = JSONParsingUtils.extractDoubleKeyword(node, "VALUE", true);
		
		//TODO check for errors
		
		if (errors.toString().isEmpty())
		{	
			IValue val = new Value();
			//TODO set value
			IDescriptorValueCondition dvc = new DescriptorValueCondition(val, null, descrName);
			return dvc;
		}
		else
			throw new Exception("Errors in DescriptorValueCondition: " + errors.toString());
	}
}
