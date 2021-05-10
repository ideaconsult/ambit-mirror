package ambit2.notation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ambit2.rules.json.JSONParsingUtils;



public class NotationConfig 
{
	public String info = null;
	public String type = null;
	public List<NotationSection> dataElements = new ArrayList<NotationSection>();
		
	public List<String> errors = new ArrayList<String>();
	

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
		
		//JSONParsingUtils jsonUtils = new JSONParsingUtils();		
		NotationConfig notCfg = new NotationConfig(); 
		JsonNode curNode;
		
		// INFO
		curNode = root.path("INFO");
		if (!curNode.isMissingNode()) {
			try {
				String keyword = JSONParsingUtils.extractStringKeyword(root, "INFO", false);
				if (keyword != null)
					notCfg.info = keyword;
			}
			catch (Exception x) {
				notCfg.errors.add(x.getMessage());
			}
		}

		
		//TODO
		
		return notCfg;
	}	
}
