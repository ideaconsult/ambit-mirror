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
		GroupContributionModel.GCMConfigInfo addConfigInfo = gcm.getAdditionalConfig();
		
		JsonNode curNode = root.path("MODEL_NAME");
		if (!curNode.isMissingNode())
		{	
			if (curNode.isTextual())
				gcm.setModelName(curNode.asText());
			else
				configErrors.add("MODEL_NAME is not textual!");
		}
		
		curNode = root.path("MODEL_DESCRIPTION");
		if (!curNode.isMissingNode())
		{	
			if (curNode.isTextual())
				gcm.setModelDescription(curNode.asText());
			else
				configErrors.add("MODEL_DESCRIPTION is not textual!");
		}
		
		curNode = root.path("MODEL_TYPE");
		if (!curNode.isMissingNode())
		{	
			if (curNode.isTextual())
				addConfigInfo.gcmTypeString = curNode.asText();				
			else
				configErrors.add("MODEL_TYPE is not textual!");
		}
		
		curNode = root.path("LOCAL_DESCRIPTORS");
		if (!curNode.isMissingNode())
		{	
			if (curNode.isTextual())			
				addConfigInfo.localDescriptorsString = curNode.asText();
			else
				configErrors.add("LOCAL_DESCRIPTORS is not textual!");
		}
		
		curNode = root.path("GLOBAL_DESCRIPTORS");
		if (!curNode.isMissingNode())
		{	
			if (curNode.isTextual())			
				addConfigInfo.globalDescriptorsString = curNode.asText();
			else
				configErrors.add("GLOBAL_DESCRIPTORS is not textual!");
		}
		
		curNode = root.path("CORRECTION_FACTORS");
		if (!curNode.isMissingNode())
		{	
			if (curNode.isTextual())			
				addConfigInfo.corFactorsString = curNode.asText();
			else
				configErrors.add("CORRECTION_FACTORS is not textual!");
			//TODO handle non textual json node
		}
		
		try {
			Double d = JSONParsingUtils.extractDoubleKeyword(root, "COLUMN_FILTRATION_THRESHOLD", false);
			if (d != null)
				addConfigInfo.columnFiltrationthreshold = d;	
		}
		catch (Exception e){
			configErrors.add(e.getMessage());		
		}
		
		try {
			Integer i = JSONParsingUtils.extractIntKeyword(root, "FRACTION_DIGITS", false);
			if (i != null)
				addConfigInfo.fractionDigits = i;	
		}
		catch (Exception e){
			configErrors.add(e.getMessage());		
		}
		
		curNode = root.path("TRAINING_SET_FILE");
		if (!curNode.isMissingNode())
		{	
			if (curNode.isTextual())			
				addConfigInfo.trainingSetFile = curNode.asText();
			else
				configErrors.add("TRAINING_SET_FILE is not textual!");			
		}
		
		curNode = root.path("EXTERNAL_SET_FILE");
		if (!curNode.isMissingNode())
		{	
			if (curNode.isTextual())			
				addConfigInfo.externalSetFile = curNode.asText();
			else
				configErrors.add("EXTERNAL_SET_FILE is not textual!");			
		}
		
		
		return gcm;
	}
	
	public String getAllErrorsAsString()
	{
		StringBuffer sb = new StringBuffer();
		for (String err : configErrors)
			sb.append(err + "\n");
		return sb.toString();
	}
	
	public String getAllWarningAsString()
	{
		StringBuffer sb = new StringBuffer();
		for (String w : configWarnings)
			sb.append(w + "\n");
		return sb.toString();
	}
	
}
