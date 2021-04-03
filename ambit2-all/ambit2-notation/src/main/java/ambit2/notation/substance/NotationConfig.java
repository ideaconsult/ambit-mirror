package ambit2.notation.substance;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ambit2.rules.json.JSONParsingUtils;



public class NotationConfig 
{

	public static NotationConfig loadFromJSON(File jsonConfig) throws FileNotFoundException, IOException, JsonProcessingException {
		FileInputStream fin = new FileInputStream(jsonConfig);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = null;

		try {
			root = mapper.readTree(fin);
		} catch (JsonProcessingException x) {
			throw x;			
		} catch (IOException x) {
			throw x;
		} finally {
			try {
				fin.close();
			} catch (Exception x) {
			}
		}
		
		JSONParsingUtils jsonUtils = new JSONParsingUtils(); 
		NotationConfig notCfg = new NotationConfig(); 
		
		//TODO
		
		return notCfg;
	}	
}
