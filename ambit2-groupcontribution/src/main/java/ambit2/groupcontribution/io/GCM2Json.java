package ambit2.groupcontribution.io;

import java.io.File;
import java.io.FileInputStream;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ambit2.groupcontribution.GroupContributionModel;
import ambit2.rules.json.JSONParsingUtils;


public class GCM2Json 
{
	private final static Logger logger = Logger.getLogger(GCM2Json.class.getName());
	
	public GroupContributionModel loadFromJSON(File jsonConfig) throws Exception {
		FileInputStream fin = new FileInputStream(jsonConfig);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = null;

		try {
			root = mapper.readTree(fin);
		} catch (Exception x) {
			throw x;
		} finally {
			try {
				fin.close();
			} catch (Exception x) {
			}
		}

		JSONParsingUtils jsonUtils = new JSONParsingUtils();
		//ExcelParserConfigurator conf = new ExcelParserConfigurator();
		
		return null;
	}	
	
}
