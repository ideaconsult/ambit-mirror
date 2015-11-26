package ambit2.rules.conditions.parser;

import ambit2.rules.conditions.ConditionExpression;
import ambit2.rules.conditions.DescriptorValueCondition;
import ambit2.rules.conditions.ICondition;
import ambit2.rules.conditions.value.IValue.Relation;
import ambit2.rules.conditions.value.Value;


public class ConditionParsingUtils 
{
	/**
	 * Parses token in the format: <NEGATION> <DESCR_NAME> <RELATION> <VALUE>
	 * 
	 * @param token
	 * @return
	 * @throws Exception
	 */	
	public static DescriptorValueCondition getDescriptorValueConditionFromToken(String token) throws Exception 
	{
		if (token == null)
			throw new Exception("null token");
		
		String s = token.trim();
		
		if (s.isEmpty())
			throw new Exception("Empty token");
		
		DescriptorValueCondition dvc = new DescriptorValueCondition(null, null, null);
		int pos = 0;
		int n = s.length();
		
		//Handle negation in the beginning of the token
		boolean FlagNegation = false;
		while (pos < n)
		{	
			if (s.charAt(pos) == '!')
			{	
				pos++;
				FlagNegation = !FlagNegation;
			}	
			else
			{	
				break;
			}	
		}		
		dvc.setIsNegated(FlagNegation);
				
		//Omit empty spaces
		while (pos < n)
		{	
			if (s.charAt(pos) == ' ')
				pos++;
			else
				break;
		}	

		if (pos >= n)
			throw new Exception("Incorrect token " + token);
				
		//Handle descriptor name
		int pos0 = pos;
		if (Character.isLetter(s.charAt(pos)) || s.charAt(pos)== '_')
			pos++;
		
		while (pos < n)
		{	
			if (Character.isLetterOrDigit(s.charAt(pos)) || s.charAt(pos)== '_')
				pos++;
			else
				break;
		}	
		
		String descrName = s.substring(pos0, pos);
		dvc.setDescriptorName(descrName);
		
		//Omit empty spaces
		while (pos < n)
		{	
			if (s.charAt(pos) == ' ')
				pos++;
			else
				break;
		}	
		
		if (pos >= n)
			throw new Exception("Incorrect token " + token);
		
		
		
		//Handling relation		
		Relation relation = null;
		char c = s.charAt(pos); 
		
		switch (c)
		{		
		case '=':
			pos++;
			if (pos < n)
			{	
				if (s.charAt(pos) == '=' )
				{	
					relation = Relation.EQUALS;
					pos++;
				}
				else
					relation = Relation.EQUALS;
			}
			break;
		
		case '!':
			pos++;
			if (pos < n)
				if (s.charAt(pos) == '=' )
				{	
					relation = Relation.DIFFERENT;
					pos++;
				}	
			//This is an incorrect relation;
			break;
		
		case '>':
			pos++;
			if (pos < n)
			{	
				if (s.charAt(pos) == '=' )
				{	
					relation = Relation.GREATER_THAN_OR_EQUALS;
					pos++;
				}
				else
					relation = Relation.GREATER_THAN;
					
			}
			else
				relation = Relation.GREATER_THAN;
			break;

		case '<':
			pos++;
			if (pos < n)
			{	
				if (s.charAt(pos) == '=' )
				{	
					relation = Relation.LESS_THAN_OR_EQUALS;
					pos++;
				}
				else
					if (s.charAt(pos) == '>' )
					{	
						relation = Relation.DIFFERENT;
						pos++;
					}
					else
						relation = Relation.LESS_THAN;
			}
			else
				relation = Relation.LESS_THAN;
			break;
		}
		
		//Omit empty spaces
		while (pos < n)
		{	
			if (s.charAt(pos) == ' ')
				pos++;
			else
				break;
		}
		
		if (relation == null)
			throw new Exception("Incorrect token " + token);
		
		if (pos >= n)
			throw new Exception("Incorrect token " + token);
		
		
		//Handle value
		Double d = null;
		
		String vStr = s.substring(pos);
		try{
			d =  Double.parseDouble(vStr);
		}
		catch (Exception x){
			throw new Exception("Incorrect token " + token);
		}
		
		Value value = new Value(d, relation);
		dvc.setValue(value);
				
		return dvc;
	}
	
	
	public static ICondition parseConditionExpression(String str) throws Exception 
	{
		//TODO
		return null;
	}
}
