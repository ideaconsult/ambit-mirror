package ambit2.reactions;

import org.openscience.cdk.interfaces.IAtomContainer;
import java.util.Stack;
import java.util.ArrayList;

public class RetroSynthesis 
{
	ReactionKnowledgeBase knowledgeBase; 
	IAtomContainer molecule;
	RetroSynthesisResult retroSynthResult;
	Stack<RetroSynthNode> nodes = new Stack<RetroSynthNode>(); 
	ArrayList<IRetroSynthRuleInstance>  ruleInstances = new ArrayList<IRetroSynthRuleInstance>(); 
	
	
	public RetroSynthesis() throws Exception 
	{	
		knowledgeBase = new ReactionKnowledgeBase();
	}
	
	public ReactionKnowledgeBase getReactionKnowledgeBase()
	{
		return knowledgeBase;
	}
	
	public void setStructure(IAtomContainer str) 
	{	
		molecule = str;
	}
	
	
	public RetroSynthesisResult run()
	{
		retroSynthResult = new RetroSynthesisResult();
		
		ruleInstances = findRuleInstances(molecule);		
		searchPaths();
		
		return retroSynthResult;
	}
	
	
	public static ArrayList<IRetroSynthRuleInstance> findRuleInstances(IAtomContainer mol)
	{
		//TODO
		return null;
	}
	
	
	//Strategy 1 - based on depth first search algorithm
	//There can be various criteria for stopping
	// (1)impossible to apply more rules (transformations)
	// (2)some of the products are starting materials
	
	
	void searchPaths()
	{	
		nodes.clear();
		generateInitialNodes();
		
		
		//Iterate stack
		while (!nodes.isEmpty())
		{
			//TODO
		}
	}
	
	void generateInitialNodes()
	{
		//TODO
	}
	
	
	
	
	
	
	
}
