package ambit2.tautomers.ranking;

import java.net.URL;
import java.util.List;
import java.util.Map.Entry;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.smarts.IsomorphismTester;
import ambit2.tautomers.RankingRule;
import ambit2.tautomers.RuleInstance;
import ambit2.tautomers.TautomerIncrementStep;
import ambit2.tautomers.rules.AtomCondition;
import ambit2.tautomers.rules.EnergyCorrection;
import ambit2.tautomers.rules.EnergyRule;
import ambit2.tautomers.rules.JsonRuleParser;

public class EnergyRanking 
{
	public List<EnergyRule> rules = null;
	private IsomorphismTester isoTester = new IsomorphismTester();
	
	
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
	
	public double calculateRank(List<RuleInstance> usedRuleInstances, IAtomContainer tautomer) throws Exception
	{
		double e_rank = 0.0;
		
		for (int i = 0; i < usedRuleInstances.size(); i++)
		{
			RuleInstance ri = usedRuleInstances.get(i);			
			EnergyRule eRule = getEnergyRuleByName(ri.rule.name);
			if (eRule == null)
				continue;
			
			//System.out.println("Rule: " + ri.rule.name);
			
			if (ri.curState == eRule.state)
			{	
				e_rank += eRule.stateEnergy;
				
				//Checking the conditions for energy corrections
				for (EnergyCorrection eCorrection : eRule.energyCorrections)
				{	
					boolean FlagApplyCorrection = true;
					for (Entry<Integer, AtomCondition> entry : eCorrection.atomConditions.entrySet())
					{
						int atomIndex = entry.getKey();
						AtomCondition cond = entry.getValue();
						
						IAtom atom = ri.atoms.get(atomIndex);
						if (!cond.checkConditionForAtom(tautomer, atom, isoTester));
						{
							FlagApplyCorrection = false;
							break;
						}
					}
					
					if (FlagApplyCorrection)
					{	
						e_rank += eCorrection.energy;
						//System.out.println("correction: " + eCorrection.correctionName + "  " + eCorrection.energy);
					}	
				}
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
