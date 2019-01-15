package ambit2.structure2name.rules.parser;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ambit2.structure2name.rules.IUPACRuleDataBase;

public class JSONRuleParser 
{
	private String error = null;
	private List<String> errors = null;

	void addError(String err) {
		if (errors == null)
			errors = new ArrayList<String>();
		errors.add(err);
	}

	public List<String> getErrors() {
		return errors;
	}

	public String getAllErrors() {
		if (errors == null)
			return "";

		StringBuffer sb = new StringBuffer();
		for (String err : errors)
			sb.append(err + "\n");
		return sb.toString();
	}
	
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
	
	// Helper functions

		public String extractStringKeyword(JsonNode node, String keyword, boolean isRequired) {
			error = "";
			JsonNode keyNode = node.path(keyword);
			if (keyNode.isMissingNode()) {
				if (isRequired) {
					error = "Keyword " + keyword + " is missing!";
					return null;
				}
				return "";
			}

			if (keyNode.isTextual()) {
				return keyNode.asText();
			} else {
				error = "Keyword " + keyword + " is not of type text!";
				return null;
			}
		}

		public Double extractDoubleKeyword(JsonNode node, String keyword, boolean isRequired) {
			error = "";
			JsonNode keyNode = node.path(keyword);
			if (keyNode.isMissingNode()) {
				if (isRequired) {
					error = "Keyword " + keyword + " is missing!";
					return null;
				}
				return null;
			}

			if (keyNode.isDouble()) {
				return keyNode.asDouble();
			} else {
				error = "Keyword " + keyword + " is not of type Int!";
				return null;
			}
		}

		public Integer extractIntKeyword(JsonNode node, String keyword, boolean isRequired) {
			error = "";
			JsonNode keyNode = node.path(keyword);
			if (keyNode.isMissingNode()) {
				if (isRequired) {
					error = "Keyword " + keyword + " is missing!";
					return null;
				}
				return null;
			}

			if (keyNode.isInt()) {
				return keyNode.asInt();
			} else {
				error = "Keyword " + keyword + " is not of type Int!";
				return null;
			}
		}
}
