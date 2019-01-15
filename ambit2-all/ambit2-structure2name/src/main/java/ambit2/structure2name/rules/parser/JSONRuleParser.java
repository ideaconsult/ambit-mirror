package ambit2.structure2name.rules.parser;

import java.io.FileInputStream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ambit2.structure2name.IUPACRuleDataBase;

public class JSONRuleParser 
{
	public IUPACRuleDataBase loadIUPACRuleDataBase (String jsonFileName) throws Exception 
	{
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = null;

		try (FileInputStream fin = new FileInputStream(jsonFileName)) {
			rootNode = mapper.readTree(fin);
		} catch (Exception x) {
			throw x;
		} finally {

		}
		
		//TODO
		
		return null;
	}	
}
