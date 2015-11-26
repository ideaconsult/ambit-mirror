package ambit2.export.isa.tab;

import java.io.File;
import java.io.FileInputStream;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import ambit2.export.isa.ISAExportConfig;
import ambit2.rules.json.JSONParsingUtils;

public class ISATABExportConfig extends ISAExportConfig
{
	
	
	public static ISATABExportConfig loadFromJSON(File jsonConfig) throws Exception
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
		
		ISATABExportConfig conf = new ISATABExportConfig();
		conf.parseJSONConfig(root);
		return conf;
		
	}
	
	public void parseJSONConfig(JsonNode rootNode) throws Exception
	{	
		//Handle stuff on ISA level
		//super.parseJSONConfig(rootNode);
		
		//TODO
	}
	
	
}
