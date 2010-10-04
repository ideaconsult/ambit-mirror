package ambit2.tautomers;

import org.openscience.cdk.interfaces.IAtomContainer;
import java.util.Vector;

public class TautomerManager 
{
	KnowledgeBase knowledgeBase; 
	IAtomContainer molecule;
	Vector<RuleInstance> extendedRuleInstances = new Vector<RuleInstance>(); 
	Vector<RuleInstance> ruleInstances = new Vector<RuleInstance>(); 
	
	
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
			return(v);
		
		generateRuleCombinations();
		
		return(v);
	}
	
	void searchAllRulePositions()
	{
		extendedRuleInstances.clear();
		ruleInstances.clear();
		
		//TODO
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
