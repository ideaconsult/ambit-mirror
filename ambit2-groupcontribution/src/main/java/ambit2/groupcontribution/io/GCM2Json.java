package ambit2.groupcontribution.io;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ambit2.groupcontribution.GroupContributionModel;
import ambit2.groupcontribution.GroupContributionModel.Type;
import ambit2.rules.json.JSONParsingUtils;


public class GCM2Json 
{
	private final static Logger logger = Logger.getLogger(GCM2Json.class.getName());
	
	public ArrayList<String> configErrors = new ArrayList<String>();
	public ArrayList<String> configWarnings = new ArrayList<String>();
	
	public GroupContributionModel loadFromJSON(File jsonConfig) throws Exception 
	{
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

		//JSONParsingUtils jsonUtils = new JSONParsingUtils();
		GroupContributionModel gcm = new GroupContributionModel();
		
		JsonNode curNode = root.path("MODEL_NAME");
		if (!curNode.isMissingNode())
		{	
			if (curNode.isTextual())
				gcm.setModelName(curNode.asText());
			else
				configErrors.add("MODEL_NAME is not textual!");
		}	
		
		curNode = root.path("MODEL_TYPE");
		if (!curNode.isMissingNode())
		{	
			if (curNode.isTextual())
			{
				String t = curNode.asText();
				try {
					Type type = Type.valueOf(t);
					gcm.setModelType(type);
				}
				catch (Exception x)
				{
					configErrors.add("MODEL_TYPE '" + t + "' is not correct!");
				}
			}	
			else
				configErrors.add("MODEL_TYPE is not textual!");
		}	
		
		return gcm;
	}	
	
}
