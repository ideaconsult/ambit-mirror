package ambit2.structure2name.rules.parser;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ambit2.structure2name.components.FunctionalGroup;
import ambit2.structure2name.rules.CarbonData;
import ambit2.structure2name.rules.FunctionalGroupData;
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

		IUPACRuleDataBase irdb = new IUPACRuleDataBase();

		//Section CARBONS
		JsonNode carbonsNode = rootNode.path("CARBONS");
		if (carbonsNode.isMissingNode()) 
			addError("Section CARBONS is missing!");		 
		else if (!carbonsNode.isArray()) 
			addError("Section CARBONS is not array!");
		else { 
			CarbonData carbData[] = extractCarbonData(carbonsNode);
			irdb.carbonData = carbData;
		}
		
		//Section FUNCTIONAL_GROUPS
		JsonNode fgNode = rootNode.path("FUNCTIONAL_GROUPS");
		if (fgNode.isMissingNode()) 
			addError("Section FUNCTIONAL_GROUPS is missing!");		 
		else if (!fgNode.isArray()) 
			addError("Section FUNCTIONAL_GROUPS is not array!");
		else { 
			FunctionalGroupData fgData[] = extractFunctionalGroupData(fgNode);
			irdb.functionalGroups = fgData;
		}

		return irdb;
	}

	CarbonData[] extractCarbonData(JsonNode carbonsNode)
	{
		int n = carbonsNode.size();
		CarbonData cd[] = new CarbonData[n];
		for (int i = 0; i < n; i++)
		{
			cd[i] = new CarbonData();
			JsonNode node = carbonsNode.get(i);

			//number
			if (node.path("number").isMissingNode()) {
				addError("Keyword \"number\" is mising in CARBONS element " + i+1);
			} else {
				Integer iObj = extractIntKeyword(node, "number", false);
				if (iObj == null)
					addError("Error in keyword \"number\" in CARBONS element " + (i+1) + ": " + error);
				else
					cd[i].number = iObj;
			}

			//prefix
			if (!node.path("prefix").isMissingNode()) 
			{	
				String s =  extractStringKeyword(node, "prefix", false);
				if (s == null)
					addError("Error in keyword \"prefix\" in CARBONS element " + (i+1) + ": " + error);
				else
					cd[i].prefix = s;
			}
		}
		return cd;
	}

	FunctionalGroupData[] extractFunctionalGroupData(JsonNode funGrpNode)
	{
		int n = funGrpNode.size();
		FunctionalGroupData fgd[] = new FunctionalGroupData[n];
		for (int i = 0; i < n; i++)
		{
			fgd[i] = new FunctionalGroupData();
			JsonNode node = funGrpNode.get(i);

			//atom_symbol
			if (!node.path("atom_symbol").isMissingNode()) 
			{	
				String s =  extractStringKeyword(node, "atom_symbol", false);
				if (s == null)
					addError("Error in keyword \"atom_symbol\" in FUNCTIONAL_GROUPS element " + (i+1) + ": " + error);
				else
					fgd[i].atomSymbol = s;
			}
		}
		return fgd;
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
