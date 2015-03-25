package ambit2.tautomers.rules;


import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

public class JsonRuleParser 
{
	private String error = null; //This is a work variable used by the functions: extractInt/Double/StringKeyword
	private List<String> errors = null; 
	
	public String getError(){
		return error;
	}
	
	void addError(String err)
	{
		if (errors == null)
			errors = new ArrayList<String>();
		errors.add(err);
	}
	
	public List<String> getErrors() {
		return errors;
	}
	
	public String getAllErrors() 
	{	
		if (errors == null)
			return "";
		
		StringBuffer sb = new StringBuffer();
		for (String err : errors)
			sb.append(err + "\n");
		return sb.toString();
	}
		
	
	/*
	    import java.io.File;
		import java.io.RandomAccessFile;
		import java.net.URL;

	public static void main(String agrs[]) throws Exception
	{
		System.out.println("*****************");
		JsonRuleParser jrp = new JsonRuleParser();
		URL resource = jrp.getClass().getClassLoader().getResource("ambit2/tautomers/energy-rules.json");
		File file = new File(resource.getFile());
		
		RandomAccessFile f = new RandomAccessFile(file,"r");			
		long length = f.length();
		int n = 0;
		while (f.getFilePointer() < length)
		{	
			n++;
			String line = f.readLine();
			System.out.println(line);
		}
		f.close();
	}
	*/
	
	
	public EnergyRule parseEnergyRule(JsonNode node)
	{
		EnergyRule rule = new EnergyRule();
		
		//RULE_NAME
		if (node.path("RULE_NAME").isMissingNode())
		{
			rule.addError("RULE_NAME is missing!");
		}
		else
		{
			String s = extractStringKeyword(node, "RULE_NAME", false);
			if (s == null)
				rule.addError(this.getError());
			else
				rule.ruleName = s;
		}
		
		//ID
		if (!node.path("RULE_ID").isMissingNode())
		{
			String s = extractStringKeyword(node, "RULE_ID", false);
			if (s == null)
				rule.addError(this.getError());
			else
				rule.id = s;
		}
		
		
		//STATE
		if (node.path("RULE_STATE").isMissingNode())
		{
			rule.addError("RULE_STATE is missing!");
		}
		else
		{
			Integer i = extractIntKeyword(node, "RULE_STATE", false);
			if (i == null)
				rule.addError(this.getError());
			else
				rule.state = i;
		}
		
		//RULE_STATE_INFO
		if (!node.path("RULE_STATE_INFO").isMissingNode())
		{
			String s = extractStringKeyword(node, "RULE_STATE_INFO", false);
			if (s == null)
				rule.addError(this.getError());
			else
				rule.stateInfo = s;
		}
		
		
		//STATE_ENERGY
		if (node.path("STATE_ENERGY").isMissingNode())
		{
			rule.addError("STATE_ENERGY is missing!");
		}
		else
		{
			Double d = extractDoubleKeyword(node, "STATE_ENERGY", false);
			if (d == null)
				rule.addError(this.getError());
			else
				rule.stateEnergy = d;
		}
		
		
		//Handle energy corrections
		JsonNode corrNode = node.path("ENERGY_CORRECTIONS");
		if (!corrNode.isMissingNode())
		{
			if (corrNode.isArray())
			{
				for (int i = 0 ; i < corrNode.size(); i++)
				{
					EnergyCorrection ecorr =  parseEnergyCorrection(corrNode.get(i));
					if (ecorr.errors == null)
						rule.energyCorrections.add(ecorr);
					else
					{
						for (String err : ecorr.errors)
							rule.addError("ENERGY_CORRECTIONS[" + (i+1) + "] : " + err);
					}
				}
			}
			else
			{
				rule.addError("Section ENERGY_CORRECTIONS is not an array");
			}
		}
		
		return rule;
	}
	
	public EnergyCorrection parseEnergyCorrection(JsonNode node)
	{
		EnergyCorrection enCorr = new EnergyCorrection();
		
		//CORRECTION_NAME
		if (node.path("CORRECTION_NAME").isMissingNode())
		{
			enCorr.addError("CORRECTION_NAME is missing!");
		}
		else
		{
			String s = extractStringKeyword(node, "CORRECTION_NAME", false);
			if (s == null)
				enCorr.addError(this.getError());
			else
				enCorr.correctionName = s;
		}
		
		//ENERGY
		if (node.path("ENERGY").isMissingNode())
		{
			enCorr.addError("ENERGY is missing!");
		}
		else
		{
			Double d = extractDoubleKeyword(node, "ENERGY", false);
			if (d == null)
				enCorr.addError(this.getError());
			else
				enCorr.energy = d;
		}
				
		//Handle atom conditions
		for (int i = 1; i <= 7; i++)
		{
			String atomCondStr = "ATOM" + i + "_CONDITIONS";
			JsonNode acNode = node.path(atomCondStr);
			if (!acNode.isMissingNode())
			{
				if (!acNode.isArray())
				{
					enCorr.addError(atomCondStr + " is not an array!");
					continue;
				}
				
				AtomCondition ac =  parseAtomCondition(acNode);
				if (ac.errors == null)
					enCorr.atomConditions.put(new Integer(i-1), ac); //1-base --> 0-base indexing
				else
				{
					for (String err : ac.errors)
						enCorr.addError(atomCondStr + " " + err);
				}
			}
		}
		
		return enCorr;
	}
	
	
	public AtomCondition parseAtomCondition(JsonNode node)
	{
		AtomCondition ac = new AtomCondition();
		
		//Currently all conditions are treated as smarts
		ac.smarts = new String[node.size()]; 
		
		for (int i = 0; i < node.size(); i++)
		{
			JsonNode elNode = node.get(i);
			if (elNode.isTextual())
			{
				ac.smarts[i] = elNode.asText();
			}
			else
			{	
				ac.addError("element " + (i+1) + " is not textual!");
				ac.smarts[i] = "";
			}	
		}
		
		return ac;
	}
	
	
	public List<EnergyRule> readEnergyRuleSet(JsonNode node) 
	{
		if (!node.isArray())
		{
			addError("Section ENERGY_RULES is not an array");
			return null;
		}
		
		List<EnergyRule> rules = new ArrayList<EnergyRule>();
		for (int i = 0; i< node.size(); i++)
		{
			EnergyRule rule =  parseEnergyRule(node.get(i));
			if (rule.errors != null)
			{
				for (String err : rule.errors)
					addError("ENERGY_RULES[" + (i+1)+"] error: " + err);
				continue;
			}
			
			//Make and check smarts queries from the atom conditions
			for (int k = 0; k < rule.energyCorrections.size(); k++)
			{	
				EnergyCorrection eCorrection = rule.energyCorrections.get(k);
				for (Entry<Integer, AtomCondition> entry : eCorrection.atomConditions.entrySet())
				{
					int atomIndex = entry.getKey();
					AtomCondition cond = entry.getValue();
					cond.makeSmartsQueries();
					
					if (cond.errors != null)
						for (String err: cond.errors)
							addError("ENERGY_RULES[" + (i+1)+"], " + 
									 "ENERGY_CORRECTION[" + (k+1) + "], " +
									 "ATOM" + (atomIndex+1) + "_CONDITIONS error: "+ err);
				}
			}	
					
			rules.add(rule);
		}
		
		return rules;
	}
	
	public static List<EnergyRule> readRuleSetFromJSON(String jsonFileName) throws Exception
	{
		FileInputStream fin = new FileInputStream(jsonFileName); 
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = null;
		
		try {
			rootNode = mapper.readTree(fin);
		} catch (Exception x) {
			throw x;
		} finally {
			try {fin.close();} catch (Exception x) {}	
		}
		
		
		JsonNode energyRulesNode = rootNode.path("ENERGY_RULES");
		if (!energyRulesNode.isMissingNode())
		{
			JsonRuleParser parser = new JsonRuleParser();
			List<EnergyRule> eRules = parser.readEnergyRuleSet(energyRulesNode);
			
			if (parser.getErrors() != null)
				throw (new Exception(parser.getAllErrors()));
			
			return eRules;
		}
		
		return null;
	}
	
	public static String toJSONString(EnergyRule eRule)
	{
		return toJSONString(eRule, "");
	}
	
	public static String toJSONString(EnergyRule eRule, String offset)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(offset + "{\n");
		int nFields = 0;
		
		if (eRule.ruleName != null)
		{	
			sb.append(offset + "\t\"RULE_NAME\" : \"" + eRule.ruleName + "\"");
			nFields++;
		}
		
		if (eRule.id != null)
		{
			if (nFields > 0)
				sb.append(",\n");
			
			sb.append(offset + "\t\"RULE_ID\" : \"" + eRule.id + "\"");
			nFields++;
		}
		
		//RULE_STATE
		{
			if (nFields > 0)
				sb.append(",\n");
			
			sb.append(offset + "\t\"RULE_STATE\" : " + eRule.state);
			nFields++;
		}
		
		if (eRule.stateInfo != null)
		{
			if (nFields > 0)
				sb.append(",\n");
			
			sb.append(offset + "\t\"RULE_STATE_INFO\" : \"" + eRule.stateInfo + "\"");
			nFields++;
		}
		
		//STATE_ENERGY
		{
			if (nFields > 0)
				sb.append(",\n");
			
			sb.append(offset + "\t\"STATE_ENERGY\" : " + eRule.stateEnergy);
			nFields++;
		}
		
		if (!eRule.energyCorrections.isEmpty())
		{
			if (nFields > 0)
				sb.append("\n");
			
			sb.append(offset + "\t\"ENERGY_CORRECTIONS\":\n");
			sb.append(offset + "\t[\n");
			for (int i = 0; i < eRule.energyCorrections.size(); i++)
			{
				sb.append(toJSONString(eRule.energyCorrections.get(i), offset+"\t\t"));
				if (i < (eRule.energyCorrections.size()-1))
					sb.append(",\n");
			}
			
			
			sb.append("\n" + offset + "\t]\n");
			nFields++;
		}
		
		if (nFields > 0)
			sb.append("\n");
		
		sb.append(offset + "}\n");
		return sb.toString();
	}
	
	
	public static String toJSONString(EnergyCorrection eCorr, String offset)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(offset + "{\n");
		int nFields = 0;
		
		if (eCorr.correctionName != null)
		{	
			sb.append(offset + "\t\"CORRECTION_NAME\" : \"" + eCorr.correctionName + "\"");
			nFields++;
		}
		
		for (Integer key : eCorr.atomConditions.keySet())
		{
			if (nFields > 0)
				sb.append(",\n");
			
			AtomCondition atCond = eCorr.atomConditions.get(key);
			sb.append(offset + "\t\"ATOM" + (key+1) + "_CONDITIONS\" : [");
			//Adding smarts conditions
			if (atCond.smarts != null)
				for (int i = 0; i < atCond.smarts.length; i++)
				{
					sb.append("\"" + atCond.smarts[i] +"\"");
					if (i < (atCond.smarts.length - 1))
						sb.append(",");
				}
			sb.append("]");
			nFields++;
		}
		
		//ENERGY
		{
			if (nFields > 0)
				sb.append(",\n");
			
			sb.append(offset + "\t\"ENERGY\" : " + eCorr.energy);
			nFields++;
		}
		
		if (nFields > 0)
			sb.append("\n");
		
		sb.append(offset + "}");
		return sb.toString();
	}	
		
	//Helper functions
	
	public String extractStringKeyword(JsonNode node, String keyword, boolean isRequired)
	{
		error = "";
		JsonNode keyNode = node.path(keyword);
		if(keyNode.isMissingNode())
		{
			if(isRequired)
			{	
				error = "Keyword " + keyword + " is missing!";
				return null;
			}
			return "";
		}
		
		if (keyNode.isTextual())
		{	
			return keyNode.asText();
		}
		else
		{	
			error = "Keyword " + keyword + " is not of type text!";
			return null;
		}			
	}
	
	public Double extractDoubleKeyword(JsonNode node, String keyword, boolean isRequired)
	{
		error = "";
		JsonNode keyNode = node.path(keyword);
		if(keyNode.isMissingNode())
		{
			if(isRequired)
			{	
				error = "Keyword " + keyword + " is missing!";
				return null;
			}
			return null;
		}
		
		if (keyNode.isDouble())
		{	
			return keyNode.asDouble();
		}
		else
		{	
			error = "Keyword " + keyword + " is not of type Int!";
			return null;
		}			
	}
	
	public Integer extractIntKeyword(JsonNode node, String keyword, boolean isRequired)
	{
		error = "";
		JsonNode keyNode = node.path(keyword);
		if(keyNode.isMissingNode())
		{
			if(isRequired)
			{	
				error = "Keyword " + keyword + " is missing!";
				return null;
			}
			return null;
		}
		
		if (keyNode.isInt())
		{	
			return keyNode.asInt();
		}
		else
		{	
			error = "Keyword " + keyword + " is not of type Int!";
			return null;
		}			
	}
	
}
