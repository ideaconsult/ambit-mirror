package ambit2.structure2name.rules;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import ambit2.structure2name.rules.parser.JSONRuleParser;

public class IUPACRuleDataBase 
{
	public String alkaneSufix = "ane";
	public String substituentSufix = "yl";
		
	public CarbonData carbonData[] = null;
	public FunctionalGroupData functionalGroups[] = null;
	//public Map<String,FunctionalGroupData> functionalGroups = new HashMap<String,FunctionalGroupData>();
	
	
	
	public static IUPACRuleDataBase getDefaultRuleDataBase() throws Exception
	{
		JSONRuleParser jrp = new JSONRuleParser();		
		URL resource = jrp.getClass().getClassLoader().getResource("ambit2/structure2name/rules/BasicRules.json");
		IUPACRuleDataBase iupacRuleDB = jrp.loadIUPACRuleDataBase(resource.getFile());
		
		String errors = jrp.getAllErrors();
		if (!errors.equals(""))
			throw (new Exception(errors));
		
		return iupacRuleDB;
	}
}
