package ambit2.core.json;

import org.codehaus.jackson.JsonNode;

public class JSONParserUtils 
{
	private String error = "";
	
	public String getError() {
		return error;
	}
	
	public String extractStringKeyword(JsonNode node, String keyword, boolean isRequired)
	{
		error = "";
		JsonNode keyNode = node.path(keyword);
		if(keyNode.isMissingNode())
		{
			if(isRequired)
			{	
				error = "Keyword " + keyword + " is missing!";
				return null;
			}
			return "";
		}
		
		if (keyNode.isTextual())
		{	
			return keyNode.asText();
		}
		else
		{	
			error = "Keyword " + keyword + " is not of type text!";
			return null;
		}			
	}
	
	public Double extractDoubleKeyword(JsonNode node, String keyword, boolean isRequired)
	{
		error = "";
		JsonNode keyNode = node.path(keyword);
		if(keyNode.isMissingNode())
		{
			if(isRequired)
			{	
				error = "Keyword " + keyword + " is missing!";
				return null;
			}
			return null;
		}
		
		if (keyNode.isDouble())
		{	
			return keyNode.asDouble();
		}
		else
		{	
			error = "Keyword " + keyword + " is not of type Int!";
			return null;
		}			
	}
	
	public Integer extractIntKeyword(JsonNode node, String keyword, boolean isRequired)
	{
		error = "";
		JsonNode keyNode = node.path(keyword);
		if(keyNode.isMissingNode())
		{
			if(isRequired)
			{	
				error = "Keyword " + keyword + " is missing!";
				return null;
			}
			return null;
		}
		
		if (keyNode.isInt())
		{	
			return keyNode.asInt();
		}
		else
		{	
			error = "Keyword " + keyword + " is not of type Int!";
			return null;
		}			
	}
	
	public Boolean extractBooleanKeyword(JsonNode node, String keyword, boolean isRequired)
	{
		error = "";
		JsonNode keyNode = node.path(keyword);
		if(keyNode.isMissingNode())
		{
			if(isRequired)
			{	
				error = "Keyword " + keyword + " is missing!";
				return null;
			}
			return null;
		}
		
		if (keyNode.isBoolean())
		{	
			return keyNode.asBoolean();
		}
		else
		{	
			error = "Keyword " + keyword + " is not of type Boolean!";
			return null;
		}			
	}
	
	public static String getNodeTypeAsString(JsonNode node)
	{
		if (node.isArray())
			return ("Array");
		
		if (node.isTextual())
			return ("Text");
		
		if (node.isObject())
			return ("Object");
		
		if (node.isInt())
			return ("Int");
		
		if (node.isLong())
			return ("Long");
		
		if (node.isDouble())
			return ("Double");
		
		if (node.isBoolean())
			return ("Boolean");
		
		if (node.isBigInteger())
			return ("BigInteger");
		
		
		/*
		if (node.isMissingNode())
			return ("MissingNode");
		*/
		return "0";
	}
	
}

