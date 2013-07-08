package ambit2.reactions;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import java.util.Stack;
import java.util.ArrayList;
import java.util.Vector;

import ambit2.smarts.SMIRKSManager;
import ambit2.smarts.SMIRKSReaction;

public class RetroSynthesis 
{
	private ReactionKnowledgeBase knowledgeBase; 
	private IAtomContainer molecule;
	private RetroSynthesisResult retroSynthResult;
	private Stack<RetroSynthNode> nodes = new Stack<RetroSynthNode>(); 
	private SMIRKSManager smrkMan = new SMIRKSManager(SilentChemObjectBuilder.getInstance());
		
	
	public RetroSynthesis() throws Exception 
	{	
		knowledgeBase = new ReactionKnowledgeBase();
	}
	
	public RetroSynthesis(String knowledgeBaseFile) throws Exception 
	{	
		knowledgeBase = new ReactionKnowledgeBase(knowledgeBaseFile);
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
		searchPaths();
		return retroSynthResult;
	}
	
	
	ArrayList<RetroSynthRuleInstance> findAllRuleInstances(IAtomContainer mol)
	{
		ArrayList<RetroSynthRuleInstance> ruleInstances = new ArrayList<RetroSynthRuleInstance>(); 
		for (RetroSynthRule rule : knowledgeBase.retroSynthRules)
		{
			 ArrayList<RetroSynthRuleInstance> inst = findRuleInstances(mol, rule);
			 for (int i = 0; i < inst.size(); i++)
				 ruleInstances.add(inst.get(i));
		}
		return ruleInstances;
	}
	
	
	/*
	 * Principle Strategy is based on depth first search algorithm
	 * There can be various criteria for stopping
	 * (1)impossible to apply more rules (transformations)
	 * (2)some of the products are starting materials
	 */
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
		ArrayList<RetroSynthRuleInstance> ruleInstances = findAllRuleInstances(molecule);
				
		//TODO
	}
	
	
	
		
	
	ArrayList<RetroSynthRuleInstance> findRuleInstances(IAtomContainer str, RetroSynthRule rule)
	{	
		ArrayList<RetroSynthRuleInstance> instances = new ArrayList<RetroSynthRuleInstance>();
		
		//smrkMan.applyTransformation(target, reaction);		
		Vector<Vector<IAtom>> rMaps = smrkMan.getNonOverlappingMappings(str);
		for (int i = 0; i < rMaps.size(); i++)
		{
			RetroSynthRuleInstance rInstance = new RetroSynthRuleInstance();
			rInstance.atoms = rMaps.get(i);
			instances.add(rInstance);
		}
		
		//TODO - Filtration eventually may be done here			
		return instances;
	}
	
	
	ArrayList<RetroSynthRuleInstance> filterAndSortInstances(ArrayList<RetroSynthRuleInstance> ruleInstances)
	{
		//currently does nothing		
		//TODO
		return ruleInstances;
	}
	
}
