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
			mapRule(knowledgeBase.rules.get(i));
	}
	
	void mapRule(Rule rule)
	{
		for (int i = 0; i < rule.statePaterns.length; i++)
		{
			//TODO
		}
	}
	
	void handleOverlapedInstances()
	{
		//TODO
	}
	
	void generateRuleCombinations()
	{
		//TODO
	}
	
}
