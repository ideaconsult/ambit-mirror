package ambit2.tautomers.ranking;

import java.net.URL;
import java.util.List;

import ambit2.tautomers.rules.EnergyRule;
import ambit2.tautomers.rules.JsonRuleParser;

public class EnergyRanking 
{
	public List<EnergyRule> rules = null;
	
	
	public EnergyRanking() throws Exception
	{
		loadDefaultEnergyRules();
	}
	
	
	public EnergyRanking(String jsonRulesFile) throws Exception
	{
		loadEnergyRules(jsonRulesFile);
	}
	
	
	public void loadDefaultEnergyRules() throws Exception
	{
		JsonRuleParser jrp = new JsonRuleParser();
		URL resource = jrp.getClass().getClassLoader().getResource("ambit2/tautomers/energy-rules.json");
		rules =  JsonRuleParser.readRuleSetFromJSON(resource.getFile());
	}
	
	public void loadEnergyRules(String jsonRulesFile) throws Exception
	{
		//TODO
	}
	
	
}
