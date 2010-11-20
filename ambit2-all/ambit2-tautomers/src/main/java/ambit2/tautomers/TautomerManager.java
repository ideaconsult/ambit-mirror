package ambit2.tautomers;

import org.openscience.cdk.interfaces.IAtomContainer;
import java.util.Vector;

public class TautomerManager 
{
	KnowledgeBase knowledgeBase; 
	IAtomContainer molecule;
	Vector<IRuleInstance> extendedRuleInstances = new Vector<IRuleInstance>(); 
	Vector<IRuleInstance> ruleInstances = new Vector<IRuleInstance>(); 
	
	
	TautomerManager()
	{
		knowledgeBase = new KnowledgeBase();
		if (knowledgeBase.errors.size() > 0)
		{	
			System.out.println(knowledgeBase.getAllErrors());
		}	
	}
	
	
	public void setStructure(IAtomContainer str)
	{	
		molecule = str;
	}
	
	
	public Vector<IAtomContainer> generateTautomers()
	{
		searchAllRulePositions();
		handleOverlapedInstances();
		
		Vector<IAtomContainer> v = new Vector<IAtomContainer>();
		if (ruleInstances.isEmpty())
		{	
			v.add(molecule);
			return(v);
		}	
		
		generateRuleCombinations();
		
		return(v);
	}
	
	void searchAllRulePositions()
	{
		extendedRuleInstances.clear();
		ruleInstances.clear();
		
		for (int i = 0; i < knowledgeBase.rules.size(); i++)
		{	
			Vector<IRuleInstance> instances = knowledgeBase.rules.get(i).applyRule(molecule); 
			extendedRuleInstances.addAll(instances);
		}	
	}
	
	void handleOverlapedInstances()
	{
		//Currently no special treatment is performed		
		ruleInstances.addAll(extendedRuleInstances);
	}
	
	void generateRuleCombinations()
	{
		for (int i = 0; i < ruleInstances.size(); i++)
			ruleInstances.get(i).firstState();
		
		//TODO
		
	}
	
}
