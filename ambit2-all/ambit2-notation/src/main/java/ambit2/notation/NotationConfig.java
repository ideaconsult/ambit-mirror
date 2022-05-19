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
	public String type = null;
	public String info = null;
	public String version = null;
	
	public List<NotationSection> sections = new ArrayList<NotationSection>();
		
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
		
		//SECTIONS
		curNode = root.path("SECTIONS");
		if (!curNode.isArray())
			notCfg.errors.add("SECTIONS is an array!");
		else {
			for (int i = 0; i < curNode.size(); i++)
			{
				NotationSection section = 
						NotationSection.extractNotationSection(curNode.get(i), i, notCfg.errors);
				notCfg.sections.add(section);
			}
		}
			
		//TODO
		
		return notCfg;
	}
	
	
	public String toJSONString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{\n");
		int nFields = 0;
		
		if (type != null) {
			if (nFields > 0)
				sb.append(",\n");
			sb.append("\t\"TYPE\" : \"" + info + "\"");
			nFields++;
		}		
		if (info != null) {
			if (nFields > 0)
				sb.append(",\n");
			sb.append("\t\"INFO\" : \"" + info + "\"");
			nFields++;
		}
		if (version != null) {
			if (nFields > 0)
				sb.append(",\n");
			sb.append("\t\"VERSION\" : \"" + info + "\"");
			nFields++;
		}
		
		sb.append("\n");
		
		sb.append("}\n"); // end of JSON
		return sb.toString();
	}
	
}
