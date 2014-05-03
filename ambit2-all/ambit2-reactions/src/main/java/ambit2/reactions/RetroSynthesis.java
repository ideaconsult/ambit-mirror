package ambit2.reactions;

import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import java.util.Stack;
import java.util.ArrayList;
import java.util.Vector;

import ambit2.smarts.SMIRKSManager;
import ambit2.smarts.SMIRKSReaction;
import ambit2.smarts.SmartsParser;

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
		
		while (!nodes.isEmpty())  //Iterate stack
		{
			RetroSynthNode node = nodes.pop();
			processNode(node);
		}
	}
	
	void generateInitialNodes()
	{
		//Generated "zero" node (tree root of Depth-First Search algorithm);
		RetroSynthNode node0 = new RetroSynthNode();
		node0.components = molecule; //may be clone would be better ???
		checkComponents(node0);
		nodes.push(node0);
	}
	
	void processNode(RetroSynthNode node)
	{
		//Check if terminal 
		if (node.components.getAtomCount() == 0)		
		{	
			generateResult(node);
			return;
		}
		
		ArrayList<RetroSynthNode> children = generateChildrenNodes(node);
		if (children != null)
			for (RetroSynthNode child : children)
				nodes.push(child);
	}
	
	ArrayList<IRetroSynthRuleInstance> findAllRuleInstances(IAtomContainer mol)
	{
		ArrayList<IRetroSynthRuleInstance> ruleInstances = new ArrayList<IRetroSynthRuleInstance>(); 
		for (RetroSynthRule rule : knowledgeBase.retroSynthRules)
		{
			 ArrayList<IRetroSynthRuleInstance> inst = findRuleInstances(mol, rule);
			 for (int i = 0; i < inst.size(); i++)
				 ruleInstances.add(inst.get(i));
		}
		return ruleInstances;
	}
	
	void generateResult(RetroSynthNode node)
	{
		//TODO
	}
		
	public ArrayList<RetroSynthNode> generateChildrenNodes(RetroSynthNode node)
	{	
		ArrayList<RetroSynthNode> children = new ArrayList<RetroSynthNode>();		
		IAtomContainerSet compSet =  ConnectivityChecker.partitionIntoMolecules(node.components);	
		
		//Handle the first component		
		IAtomContainer comp = compSet.getAtomContainer(0);			
		ArrayList<IRetroSynthRuleInstance> ruleInstances = findAllRuleInstances(comp);
				
					
		//TODO
		
		//1.Check components status		
		//2.Prepare initial set of components for processing		
		//3.Prioritize the components/instances		
		//4.generated children one by one for each component (or with a more complicated combinations)
		
		
		
		for (RetroSynthNode childNode : children)
			checkComponents(childNode);
		
		return  children;
	}
	
		
	
	ArrayList<IRetroSynthRuleInstance> findRuleInstances(IAtomContainer str, RetroSynthRule rule)
	{	
		ArrayList<IRetroSynthRuleInstance> instances = new ArrayList<IRetroSynthRuleInstance>();
				
		smrkMan.getIsomorphismTester().setQuery(rule.reaction.reactant);
		SmartsParser.prepareTargetForSMARTSSearch(rule.reaction.reactantFlags, str);
		Vector<Vector<IAtom>> rMaps = smrkMan.getNonOverlappingMappings(str);		
		
		for (int i = 0; i < rMaps.size(); i++)
		{
			RetroSynthRuleInstance rInstance = new RetroSynthRuleInstance();
			rInstance.atoms = rMaps.get(i);
			rInstance.rule = rule;
			instances.add(rInstance);			
			System.out.print("rule: " + rule.smirks+"  "); smrkMan.printSSMap(str, rInstance.atoms);			
		}
		
		//TODO - Filtration eventually may be done here			
		return instances;
	}
	
	
	boolean isComponentResolved(IAtomContainer container)
	{
		//TODO
		return false;
	}
	
	//All resolved components are moved to resolved container
	void checkComponents(RetroSynthNode node)
	{
		IAtomContainerSet compSet =  ConnectivityChecker.partitionIntoMolecules(node.components);
		boolean isCompResolved[] = new boolean[compSet.getAtomContainerCount()];
		for (int i =0; i < compSet.getAtomContainerCount(); i++)
		{	
			IAtomContainer comp = compSet.getAtomContainer(i);
			isCompResolved[i] = isComponentResolved(comp);
		}
		
		//TODO
	}
	
	
	/*
	ArrayList<RetroSynthRuleInstance> filterAndSortInstances(ArrayList<RetroSynthRuleInstance> ruleInstances)
	{
		//currently does nothing		
		//TODO
		return ruleInstances;
	}
	*/
	
}
