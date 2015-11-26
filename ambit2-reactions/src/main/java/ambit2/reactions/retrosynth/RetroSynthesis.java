package ambit2.reactions.retrosynth;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;


import ambit2.reactions.rules.IRetroSynthRuleInstance;
import ambit2.reactions.rules.RetroSynthRule;
import ambit2.reactions.rules.RetroSynthRuleInstance;
import ambit2.smarts.SMIRKSManager;
import ambit2.smarts.SmartsParser;

public class RetroSynthesis 
{
	private ReactionKnowledgeBase knowledgeBase;
	private StartingMaterialsDataBase startMatDatabase;
	private SyntheticStrategy synthStrategy;
	private IAtomContainer molecule;
	private RetroSynthesisResult retroSynthResult;
	private Stack<RetroSynthNode> nodes = new Stack<RetroSynthNode>(); 
	private SMIRKSManager smrkMan = new SMIRKSManager(SilentChemObjectBuilder.getInstance());
		
	
	public RetroSynthesis() throws Exception 
	{	
		knowledgeBase = new ReactionKnowledgeBase();
		synthStrategy = new SyntheticStrategy();
		startMatDatabase = new StartingMaterialsDataBase();
	}
	
	public RetroSynthesis(String knowledgeBaseFile) throws Exception 
	{	
		knowledgeBase = new ReactionKnowledgeBase(knowledgeBaseFile);
		synthStrategy = new SyntheticStrategy();
		startMatDatabase = new StartingMaterialsDataBase();
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
	 * Principle algorithm is based on depth first search algorithm
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
		node0.components.push(molecule); //use a clone of molecule ???
				
		if (isComponentResolved(molecule))
		{
			//No node is pushed in the stack
			generateResult(node0);
		}
		else
			nodes.push(node0);
	}
	
	void processNode(RetroSynthNode node)
	{
		//Check if terminal node 
		if (node.components.isEmpty())		
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
		ArrayList<IRetroSynthRuleInstance> ruleInstances0 = null;
		IAtomContainer comp = null;
		
		//Find one component which is resolvable		
		while (!node.components.isEmpty())
		{	
			comp = node.components.pop();
			ruleInstances0 = findAllRuleInstances(comp);
			if (ruleInstances0.isEmpty())
			{
				//The component can not be resolved 
				node.unresolved.addAtomContainer(comp);
				continue;
			}
			else
				break;
		}
		
		if (comp == null)
			return children;
		
		//Apply strategy. Prioritise the instances
		ArrayList<IRetroSynthRuleInstance> ruleInstances = synthStrategy.applyStrategy(comp, ruleInstances0);
		
		//Generate a child node one by one for each instance
		for (IRetroSynthRuleInstance instance : ruleInstances)
		{	
			RetroSynthNode newNode = node.clone();
			ArrayList<IAtomContainer> products = applyRuleInstance(comp, instance);
		
			//Check the result components (the products from working component - comp)  
			for (IAtomContainer product : products)
			{
				if (isComponentResolved(product))
					newNode.resolved.addAtomContainer(product);
				else
					newNode.components.push(product);
			}
			
			children.add(newNode);
		}	
		
		return  children;
	}
	
		
	
	ArrayList<IRetroSynthRuleInstance> findRuleInstances(IAtomContainer str, RetroSynthRule rule)
	{	
		ArrayList<IRetroSynthRuleInstance> instances = new ArrayList<IRetroSynthRuleInstance>();
				
		smrkMan.getIsomorphismTester().setQuery(rule.reaction.reactant);
		SmartsParser.prepareTargetForSMARTSSearch(rule.reaction.reactantFlags, str);
		List<List<IAtom>> rMaps = smrkMan.getNonOverlappingMappings(str);		
		
		for (int i = 0; i < rMaps.size(); i++)
		{
			RetroSynthRuleInstance rInstance = new RetroSynthRuleInstance();
			rInstance.setAtoms (rMaps.get(i));
			rInstance.setRule(rule);
			instances.add(rInstance);			
			System.out.print("rule: " + rule.smirks+"  "); smrkMan.printSSMap(str, rInstance.getAtoms());			
		}
		
		return instances;
	}
	
	
	ArrayList<IAtomContainer> applyRuleInstance(IAtomContainer target, IRetroSynthRuleInstance instance)
	{
		//The reaction is applied with cloning of the target
		
		//TODO 
		
		ArrayList<IAtomContainer> products = new ArrayList<IAtomContainer>();
		return products;
	}
	
		
	boolean isComponentResolved(IAtomContainer container)
	{
		startMatDatabase.isStartingMaterial(container);
		return false;
	}
	
	
	/*
	//All resolved components are moved to resolved container
	void checkComponents(RetroSynthNode node)
	{
		
		boolean isCompResolved[] = new boolean[compSet.getAtomContainerCount()];
		for (int i =0; i < compSet.getAtomContainerCount(); i++)
		{	
			IAtomContainer comp = compSet.getAtomContainer(i);
			isCompResolved[i] = isComponentResolved(comp);
		}
		
		
		//TODO
	}
	*/
	
	
}
