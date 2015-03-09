package ambit2.tautomers.rules;


import java.util.List;
import org.codehaus.jackson.JsonNode;

public class JsonRuleParser 
{
	private String error = null;
	
	public String getError() {
		return error;
	}
	
	/*
	    import java.io.File;
		import java.io.RandomAccessFile;
		import java.net.URL;

	public static void main(String agrs[]) throws Exception
	{
		System.out.println("*****************");
		JsonRuleParser jrp = new JsonRuleParser();
		URL resource = jrp.getClass().getClassLoader().getResource("ambit2/tautomers/energy-rules.json");
		File file = new File(resource.getFile());
		
		RandomAccessFile f = new RandomAccessFile(file,"r");			
		long length = f.length();
		int n = 0;
		while (f.getFilePointer() < length)
		{	
			n++;
			String line = f.readLine();
			System.out.println(line);
		}
		f.close();
	}
	*/
	
	
	public EnergyRule parseEnergyRule(JsonNode node)
	{
		//TODO
		return null;
	}
	
	
	public static List<EnergyRule> readRuleSetFromJSON(String jsonFileName) throws Exception
	{
		//TODO
		return null;
	}
	
	
	
	//Helper functions
	
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
	
}
