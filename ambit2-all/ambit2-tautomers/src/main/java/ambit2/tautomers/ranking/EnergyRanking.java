package ambit2.tautomers.ranking;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.smarts.IsomorphismTester;
import ambit2.tautomers.IRuleInstance;
import ambit2.tautomers.KnowledgeBase;
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
	
	public double calculateRank(TautomerIncrementStep incStep , IAtomContainer tautomer) throws Exception
	{
		double e_rank = 0.0;
		
		for (int i = 0; i < incStep.usedRuleInstances.size(); i++)
		{
			RuleInstance ri = incStep.usedRuleInstances.get(i);	
			
			int stateCheck  = ri.checkCurStateInstanceValidity();
			if (stateCheck != 0)
				continue;
			
			EnergyRule eRule = getEnergyRuleByName(ri.rule.name);
			if (eRule == null)
				continue;
			
			//System.out.println("Rule: " + ri.rule.name);
			
			if (ri.curState == eRule.state)
			{	
				boolean FlagBaseEnergy = true;
				
				//Checking the conditions for energy corrections
				for (EnergyCorrection eCorrection : eRule.energyCorrections)
				{	
					boolean FlagApplyCorrection = true;
					for (Entry<Integer, AtomCondition> entry : eCorrection.atomConditions.entrySet())
					{
						int ruleAtomIndex = entry.getKey();
						AtomCondition cond = entry.getValue();
						
						IAtom atom = ri.atoms.get(ruleAtomIndex);  //This atom corresponds to incStep.struct (not to the tautomer which is a clone of incStep.struct) 
						int tautomerAtomIndex = incStep.struct.getAtomNumber(atom);  //The correct index is taken from incStep.struct
						
						if (!cond.checkConditionForAtom(tautomer, tautomerAtomIndex, isoTester))
						{
							FlagApplyCorrection = false;
							break; //no more conditions are checked
						}
					}
					
					if (FlagApplyCorrection)
					{	
						e_rank += eCorrection.energy;
						FlagBaseEnergy = false;  //correction energy is used instead of the base rule energy
						break;   //no more corrections are checked
						//System.out.println("correction: " + eCorrection.correctionName + "  " + eCorrection.energy);
					}	
				}
				
				if (FlagBaseEnergy)
					e_rank += eRule.stateEnergy;
			}
			else
			{
				//This state is assigned energy 0.0
			}
		}
		
		return e_rank;
	}
	
	
	public double calculateRank(IAtomContainer tautomer, KnowledgeBase knowledgeBase) throws Exception
	{
		double e_rank = 0.0;
		List<IRuleInstance> ruleInstances = new ArrayList<IRuleInstance>();
		
		//A new search for the rule instances is performed 
		for (int i = 0; i < knowledgeBase.rules.size(); i++)
			if (knowledgeBase.rules.get(i).isRuleActive) {
				try {
					List<IRuleInstance> instances = knowledgeBase.rules.get(i).applyRule(tautomer); 
					if ((instances!=null) && (instances.size()>0))
						ruleInstances.addAll(instances);
				} catch (Exception x) {
					//logger.log(Level.WARNING,knowledgeBase.rules.get(i).name,x);
				}
			}
		
		
		for (int i = 0; i < ruleInstances.size(); i++)
		{
			RuleInstance ri = (RuleInstance) ruleInstances.get(i);	
			
			EnergyRule eRule = getEnergyRuleByName(ri.rule.name);
			if (eRule == null)
				continue;
			
			//System.out.println("Rule: " + ri.rule.name);
			
			if (ri.curState == eRule.state)
			{	
				boolean FlagBaseEnergy = true;
				
				//Checking the conditions for energy corrections
				for (EnergyCorrection eCorrection : eRule.energyCorrections)
				{	
					boolean FlagApplyCorrection = true;
					for (Entry<Integer, AtomCondition> entry : eCorrection.atomConditions.entrySet())
					{
						int ruleAtomIndex = entry.getKey();
						AtomCondition cond = entry.getValue();
						
						IAtom atom = ri.atoms.get(ruleAtomIndex);   
						int tautomerAtomIndex = tautomer.getAtomNumber(atom);  
						
						if (!cond.checkConditionForAtom(tautomer, tautomerAtomIndex, isoTester))
						{
							FlagApplyCorrection = false;
							break; //no more conditions are checked
						}
					}
					
					if (FlagApplyCorrection)
					{	
						e_rank += eCorrection.energy;
						FlagBaseEnergy = false;  //correction energy is used instead of the base rule energy
						break;   //no more corrections are checked
						//System.out.println("correction: " + eCorrection.correctionName + "  " + eCorrection.energy);
					}	
				}
				
				if (FlagBaseEnergy)
					e_rank += eRule.stateEnergy;
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
