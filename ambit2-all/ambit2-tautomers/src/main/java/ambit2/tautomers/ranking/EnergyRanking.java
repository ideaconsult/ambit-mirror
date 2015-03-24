package ambit2.tautomers.ranking;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.tautomers.RankingRule;
import ambit2.tautomers.RuleInstance;
import ambit2.tautomers.TautomerIncrementStep;
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
		rules =  JsonRuleParser.readRuleSetFromJSON(jsonRulesFile);
	}
	
	
	public void loadDefaultEnergyRules() throws Exception
	{
		JsonRuleParser jrp = new JsonRuleParser();
		URL resource = jrp.getClass().getClassLoader().getResource("ambit2/tautomers/energy-rules.json");
		rules =  JsonRuleParser.readRuleSetFromJSON(resource.getFile());
	}
	
	public double calculateRank(List<RuleInstance> usedRuleInstances, IAtomContainer tautomer)
	{
		double e_rank = 0.0;
		for (int i = 0; i < usedRuleInstances.size(); i++)
		{
			RuleInstance ri = usedRuleInstances.get(i);
			EnergyRule eRule = getEnergyRuleByName(ri.rule.name);
			
			if (ri.curState == eRule.state)
			{	
				e_rank += eRule.stateEnergy;
				//TODO add energy corrections
			}
			else
			{
				//This state is assigned energy 0.0
			}
		}
		
		return e_rank;
	}
	
	EnergyRule getEnergyRuleByName(String rname)
	{
		for (EnergyRule r : rules)
			if (r.ruleName.equals(rname))
				return r;
		return null;
	}
	
}
