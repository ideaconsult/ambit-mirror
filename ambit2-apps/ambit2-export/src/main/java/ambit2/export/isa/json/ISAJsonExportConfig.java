package ambit2.export.isa.json;

import java.io.File;
import java.io.FileInputStream;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import ambit2.export.isa.ISAExportConfig;
import ambit2.rules.json.JSONParsingUtils;

public class ISAJsonExportConfig extends ISAExportConfig
{
	
	public boolean singleJSONFile = false; 
		
	
	
	
	public void parseJSONConfig(JsonNode rootNode) throws Exception
	{	
		//Handle stuff on ISA level
		//super.parseJSONConfig(rootNode);
		
		JsonNode curNode;
		
		//Handling section EXPORT_INFO
		curNode = rootNode.path("EXPORT_INFO");
		if (curNode.isMissingNode())
			throw new Exception ("Section \"EXPORT_INFO\" is missing!");
		else
		{
			//ISA_FORMAT
			//ISA_VERSION
			
			//TODO
			
		}
		
		//Handling section BASIC
		curNode = rootNode.path("BASIC");
		if (curNode.isMissingNode())
			throw new Exception ("Section \"BASIC\" is missing!");
		else
		{
			//SINGLE_JSON_FILE
			Boolean b = JSONParsingUtils.extractBooleanKeyword(rootNode, "SINGLE_JSON_FILE", false);
			if (b != null)
				singleJSONFile = b;
			
			//TODO

		}
		
		
		Boolean b = JSONParsingUtils.extractBooleanKeyword(rootNode, "test", true);
		//TODO
	}
	
	
	public static ISAJsonExportConfig loadFromJSON(File jsonConfig) throws Exception
	{
		FileInputStream fin = new FileInputStream(jsonConfig); 
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = null;
		
		try {
			root = mapper.readTree(fin);
		} catch (Exception x) {
			throw x;
		} finally {
			try {fin.close();} catch (Exception x) {}	
		}
		
		ISAJsonExportConfig conf = new ISAJsonExportConfig();
		conf.parseJSONConfig(root);
		return conf;
		
	}
}
