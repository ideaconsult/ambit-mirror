package ambit2.structure2name.rules.parser;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ambit2.structure2name.rules.IUPACRuleDataBase;

public class JSONRuleParser 
{
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
}
