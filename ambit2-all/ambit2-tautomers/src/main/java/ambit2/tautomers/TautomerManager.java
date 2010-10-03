package ambit2.tautomers;

import org.openscience.cdk.interfaces.IAtomContainer;
import java.util.Vector;

public class TautomerManager 
{
	KnowledgeBase knowledgeBase = new KnowledgeBase(); 
	IAtomContainer molecule;
	Vector<RuleInstance> ruleInstances = new Vector<RuleInstance>(); 
	
	public void setStructure(IAtomContainer str)
	{	
		molecule = str;
	}
	
	void searchRulePositions()
	{
		ruleInstances.clear();
	}
	
	public Vector<IAtomContainer> generateTautomers()
	{
		searchRulePositions();
		
		Vector<IAtomContainer> v = new Vector<IAtomContainer>();
		if (ruleInstances.isEmpty())
			return(v);
		
		//Generate rule combinations
		//TODO
		
		return(v);
	}
	
}
