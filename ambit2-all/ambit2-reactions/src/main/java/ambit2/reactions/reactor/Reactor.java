package ambit2.reactions.reactor;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.TreeSet;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import net.sf.jniinchi.INCHI_OPTION;

import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.inchi.InChIGenerator;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.base.data.StructureRecord;
import ambit2.base.exceptions.EmptyMoleculeException;
import ambit2.core.data.MoleculeTools;
import ambit2.reactions.GenericReaction;
//import ambit2.reactions.Reaction;
import ambit2.reactions.ReactionDataBase;
import ambit2.rules.conditions.DescriptorSolverSet;
import ambit2.rules.conditions.DescriptorValueCondition;
import ambit2.rules.conditions.ICondition;
import ambit2.rules.conditions.IDescriptorSolver;
import ambit2.rules.conditions.INeedsDescriptorSolver;
import ambit2.smarts.SMIRKSManager;
import ambit2.smarts.SmartsHelper;

public class Reactor 
{	
	protected static Logger logger = Logger.getLogger(Reactor.class.getName());
	public static final String PropertyInchiKey = "INCHI_KEY";
	public static final String PropertyReactionIds = "REACTION_IDS";
	
	protected ReactionDataBase reactionDataBase = null;
	protected ReactorStrategy strategy = null;
	protected SMIRKSManager smrkMan = new SMIRKSManager(SilentChemObjectBuilder.getInstance());
	protected Stack<ReactorNode> reactorNodes = new Stack<ReactorNode>();
	protected TreeSet<String> nodeHashCodes = new TreeSet<String>();
	protected InChIGeneratorFactory igf = null;
	//List<INCHI_OPTION> igf_options = null;
	String igf_options_string = null;
	
	
	protected IDescriptorSolver descriptorSolver = null;
	

	//Status variables
	protected ReactorResult reactorResult = null;
	protected boolean FlagReactRecursively = false;
	
	public Reactor() throws Exception
	{
		setupInchiGenerator();
		setupDescriptoSolver();
		setupLogger(logger);
	}
	
	protected void setupInchiGenerator() throws Exception
	{
		igf_options_string = "FixedH";
		/*
		igf_options = new ArrayList<INCHI_OPTION>();
		igf_options.add(INCHI_OPTION.FixedH);
		igf_options.add(INCHI_OPTION.SAbs);
		igf_options.add(INCHI_OPTION.SAsXYZ);
		igf_options.add(INCHI_OPTION.SPXYZ);
		igf_options.add(INCHI_OPTION.FixSp3Bug);
		*/
		igf = InChIGeneratorFactory.getInstance();
		
	}
	
	protected void setupDescriptoSolver()
	{
		DescriptorSolverSet solverSet = new DescriptorSolverSet();
		IDescriptorSolver rds = new ReactorDescriptorSolver();
		solverSet.addSolver(rds);
		descriptorSolver = solverSet;
	}
	
	
	public SMIRKSManager getSMIRKSManager() {
		return smrkMan;
	}
	
	public boolean isFlagReactRecursively() {
		return FlagReactRecursively;
	}

	public void setFlagReactRecursively(boolean flagReactRecursively) {
		FlagReactRecursively = flagReactRecursively;
	}

	public ReactionDataBase getReactionDataBase() {
		return reactionDataBase;
	}

	public void setReactionDataBase(ReactionDataBase reactionDataBase) {
		this.reactionDataBase = reactionDataBase;
		setDescriptoSolverForReactDB();
	}
	
	public ReactorStrategy getStrategy() {
		return strategy;
	}

	public void setStrategy(ReactorStrategy strategy) {
		this.strategy = strategy;
	}
	
	public IDescriptorSolver getDescriptoSolver() {
		return descriptorSolver;
	}
	
	protected void setDescriptoSolverForReactDB()
	{	
		for (GenericReaction reaction: reactionDataBase.genericReactions)
		{	
			if (reaction.getConditions() != null)
				for (ICondition cond : reaction.getConditions())
				{
					//System.out.println("---- seting solver for " + cond.toString());
					if (cond instanceof INeedsDescriptorSolver)
						((DescriptorValueCondition)cond).setDescriptorSolver(descriptorSolver);
				}	
		}	
	}
	
	public int initializeReactor (IAtomContainer target)
	{
		if (target == null)
			return 1;		
		if (reactionDataBase == null)
			return 2;		
		if (reactionDataBase.genericReactions == null)
			return 3;		
		if (reactionDataBase.genericReactions.isEmpty())
			return 4;
		
		reactorResult = new ReactorResult();
		reactorNodes.clear();
		nodeHashCodes.clear();
		
		generateInitialNodes(target);
		
		
		return 0;
	}
	
	public ReactorResult react(IAtomContainer target) throws Exception
	{	
		int initRes = initializeReactor(target);
		if (initRes != 0)
			return null;
		
		/*
		if (target == null)
			return null;		
		if (reactionDataBase == null)
			return null;		
		if (reactionDataBase.reactions == null)
			return null;		
		if (reactionDataBase.reactions.isEmpty())
			return null;
		
		reactorResult = new ReactorResult();
		reactorNodes.clear();
		nodeHashCodes.clear();
		
		generateInitialNodes(target);
		*/
		
		
		//Iterate stack
		while (!reactorNodes.isEmpty() &&  checkStrategy())  
		{
			ReactorNode node = reactorNodes.pop();
			processNode(node);
		}
		
		if (strategy.FlagProcessRemainingStackNodes)
			processRemainingStackNodes();
		
		if (strategy.FlagLogMainReactionFlow)
			logger.info("\nFinal status: " + reactorResult.getStatusInfo());
		
		return reactorResult;
	}
	
	public List<ReactorNode> reactNext(int numNodes) throws Exception
	{	
		List<ReactorNode> nodes = new ArrayList<ReactorNode>();
		//Iterate number of nodes from the stack stack
		int n = 0;
		while (!reactorNodes.isEmpty() && checkStrategy() && (n < numNodes) )  
		{	
			ReactorNode node = reactorNodes.pop();
			nodes.add(node);
			processNode(node);
			n++;
		}
		
		return nodes;
	}
	
	void generateInitialNodes(IAtomContainer mol)
	{	
		ReactorNode node0 = new ReactorNode();
		addReagent(node0, mol);
		//updateNodeState(node0);
		reactorNodes.push(node0);
		
		if (strategy.FlagCheckNodeDuplicationOnPush)
			nodeHashCodes.add(node0.calcNodeHash());
	}
	
	void processNode(ReactorNode node) throws EmptyMoleculeException
	{
		ReactorNode.State state = getNodeState(node);
		
		if (strategy.FlagLogMainReactionFlow)
			logger.info("Reactor stack = " + reactorNodes.size() + " nodes"
				+"\nCurrent status: " + reactorResult.getStatusInfo()
				+"\nProcessing " + node.toString(this));
			else		
				if (strategy.FlagLogNumberOfProcessedNodes)
				{	
					if (reactorResult.numReactorNodes > reactorResult.logStep*strategy.NodeLogingFrequency)
					{	
						logger.info("Reactor processed nodes: " + reactorResult.numReactorNodes);
						reactorResult.logStep++;
					}		
				}		

		//Result is updated on node processing (not on node creating/pushing in the stack)
		updateResult(node, state);
		
		if (node.reagents.isEmpty())
			return;  //no reagents present - do nothing further
		
		if (strategy.FlagReactOneReagentOnly)
		{	
			//Only one reagent is used to generate children
			IAtomContainer reagent = node.reagents.getAtomContainer(node.reagents.getAtomContainerCount()-1);
			node.reagents.removeAtomContainer(reagent);
			int nNodes = generateChildrenNodes(node, reagent);
			
			if (strategy.FlagLogMainReactionFlow)
				logger.info("Generated " + nNodes + " nodes\n");
		}
		else
		{
			//generateChildrenNodesUsingAllReagents(node);
			//NOT implemented (unclear for now)!!!!
		}
	}
	
	int generateChildrenNodes(ReactorNode node, IAtomContainer reagent) throws EmptyMoleculeException
	{	
		int numOfReactionInstances = 0;
		for (int i = 0; i < reactionDataBase.genericReactions.size(); i++)
		{	
			GenericReaction reaction = reactionDataBase.genericReactions.get(i);
			List<List<IAtom>> instances = reaction.findReactionInstances(reagent, smrkMan);
			
			//Check reaction conditions
			if (strategy.FlagCheckReactionConditions)
				if (reaction.getConditions() != null)
				{	
					ReactorInfoPack ri = new ReactorInfoPack();
					ri.reactor = this;
					ri.reaction = reaction;
					ri.reagent = reagent;
					if (!reaction.checkConditionsForTarget(ri))
						continue;
				}
			
			numOfReactionInstances += instances.size();
			
			for (int k = 0; k < instances.size(); k++)
			{	
				List<IAtom> instance = instances.get(k);
				IAtomContainer products = null;
				try
				{
					products = reaction.applyAtInstance(reagent, instance, smrkMan, true);
				}
				catch (Exception x)
				{
					logger.severe("Reaction application error: " 
							+ reaction.getName() + "  " + reaction.getSmirks() 
							+ "  applied for reagent " + molToSmiles(reagent) + "   " + x.getMessage());
				}
				
				if (products == null)
					continue;
				
				if (smrkMan.isFlagProcessResultStructures())
				{	
					try{
						smrkMan.processProduct(products);
					}
					catch(Exception e){}
				}
					
				//System.out.println("Reaction: " + reaction.getSmirks()+"\n" + "Products: " + molToSmiles(products));
				
				IAtomContainerSet productsSet = ConnectivityChecker.partitionIntoMolecules(products);
				ReactorNode newNode = node.clone();
				
				//Update reactorResult status
				reactorResult.numReactions++;
				reactorResult.numReactorNodes++;
				//newNode.level = ...
				if (strategy.FlagTraceParentNodes)
					newNode.parentNode = node;
				
				if (strategy.FlagTraceReactionPath)
					setReactionPath(productsSet, reagent, reaction);
				
				addReagents(newNode, productsSet);
				//updateNodeState(newNode);
				
				if (strategy.FlagCheckNodeDuplicationOnPush)
				{
					String hash = newNode.calcNodeHash();
					if (!nodeHashCodes.contains(hash))
					{
						nodeHashCodes.add(hash);
						reactorNodes.push(newNode);
					}
				}
				else
					reactorNodes.push(newNode);
			}
		}
		
		if (numOfReactionInstances == 0)
		{
			//No reaction was applied then moving the reagent to finalized/forbidden/allowed products
			ReactorNode newNode = node.clone();
			finalizeProduct(newNode, reagent);
			//updateNodeState(newNode);
			reactorNodes.push(newNode);
			reactorResult.numReactorNodes++;
		}
		
		return numOfReactionInstances;
	}
	
	void finalizeProduct(ReactorNode node, IAtomContainer mol)
	{
		String reagentInchiKey  =  mol.getProperty(PropertyInchiKey);
		if (isForbiddenProduct(reagentInchiKey))
		{
			if (strategy.FlagStoreProducts)
				node.forbiddenProducts.addAtomContainer(mol);
			node.numForbiddenProducts++;
		}
		else
		{	
			if (isAllowedProduct(reagentInchiKey))
			{	
				if (strategy.FlagStoreProducts)
					node.allowedProducts.addAtomContainer(mol);
				node.numAllowedProducts++;
			}	
			else
			{	
				//It is neither forbidden nor allowed i.e. (undefined) finalized 
				if (strategy.FlagStoreProducts)
					node.finalizedProducts.addAtomContainer(mol);
				node.numFinilizedProducts++;
			}	
		}
	}
	
	void addReagents(ReactorNode node, IAtomContainerSet molSet)
	{	
		for (IAtomContainer mol : molSet.atomContainers())
			addReagent(node, mol);
	}
	
	void addReagent(ReactorNode node, IAtomContainer mol)
	{
		if (strategy.FlagCalcProductInchiKey)
		{
			try{
				System.out.println("Mol nAtoms: " + mol.getAtomCount() + "  " + SmartsHelper.moleculeToSMILES(mol, true));
				//MoleculeTools.convertExplicitHAtomsToImplicit(mol);
				//AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
				//System.out.println(SmartsHelper.getAtomsAttributes(mol));
				//System.out.println(InChIGeneratorFactory.getInstance().getInChIGenerator(mol, "FixedH").getInchi());
				
				InChIGenerator ig = igf.getInChIGenerator(mol, igf_options_string);
				//System.out.println("--->" + ig.getInchiKey());
								
				mol.setProperty(PropertyInchiKey, ig.getInchiKey());
				//mol.setProperty(PropertyInchiKey, InChIGeneratorFactory.getInstance().getInChIGenerator(mol, "FixedH").getInchi());
			}
			catch(Exception e){};
		}
		
		 		
		if (strategy.FlagRemoveReagentIfAllowedProduct)
		{
			String inchiKey = mol.getProperty(PropertyInchiKey);
			if (isAllowedProduct(inchiKey))
			{
				node.allowedProducts.addAtomContainer(mol);
				return;
			}
		}
		
		if (strategy.FlagRemoveReagentIfForbiddenProduct)
		{
			String inchiKey = mol.getProperty(PropertyInchiKey);
			if (isForbiddenProduct(inchiKey))
			{
				node.forbiddenProducts.addAtomContainer(mol);
				return;
			}
		}
		
		node.reagents.addAtomContainer(mol);
	}
	
	/*
	void generateChildrenNodesUsingAllReagents(ReactorNode node)
	{
		//TODO
	}
	*/
	
	
	boolean checkStrategy()
	{
		if (strategy.maxLevel >= 0)
			if (strategy.FlagStopOnMaxLevel)
				if (reactorResult.curMaxLevel > strategy.maxLevel)
					return false;
		
		if (strategy.maxNumOfReactions >= 0)
			if (reactorResult.numReactions > strategy.maxNumOfReactions)
				return false;
		
		if (strategy.maxNumOfNodes >= 0)
			if (reactorResult.numReactorNodes > strategy.maxNumOfNodes)
				return false;
			
		if (strategy.maxNumOfFailedNodes >= 0)
			if (reactorResult.numFailedNodes > strategy.maxNumOfFailedNodes)
				return false;
		
		if (strategy.maxNumOfSuccessNodes >= 0)
			if (reactorResult.numSuccessNodes > strategy.maxNumOfSuccessNodes)
				return false;
		
		//TODO check other conditions ...
		
		return true;
	}
	
	ReactorNode.State getNodeState(ReactorNode node)
	{
		if (strategy.FlagFailedNodeOnOneForbiddenProduct)
			if (node.numForbiddenProducts > 0)
				return  ReactorNode.State.FAILED;
		
		if (node.reagents.isEmpty())
		{	
			if (strategy.FlagSuccessNodeOnReachingAllowedProducts)
			{	
				if (node.numForbiddenProducts == 0)
					if (node.numFinilizedProducts == 0)
						return  ReactorNode.State.SUCCESS; //all products are allowed
			}
			
			if (strategy.FlagSuccessNodeOnZeroForbiddenProducts)
				if (node.numForbiddenProducts == 0)
					return  ReactorNode.State.SUCCESS;
			
			return  ReactorNode.State.FINISHED;
		}	
			
		return ReactorNode.State.UNFINISHED;
		
		//TODO check other conditions/parameters of the state
	}
	
	
	/*
	void updateNodeState(ReactorNode node)
	{	
		//Check for forbidden reagents and products...
		
	}
	*/
	
	void updateResult(ReactorNode node, ReactorNode.State nodeState)
	{
		if (reactorResult.curMaxLevel < node.level)
			reactorResult.curMaxLevel = node.level;
		
		reactorResult.numReactorNodes++;
		
		switch (nodeState)
		{
		case SUCCESS:	
			reactorResult.numSuccessNodes++;
			if (strategy.FlagStoreSuccessNodes) 
				reactorResult.successNodes.add(node);
		break;

		case FAILED:		
			reactorResult.numFailedNodes++;
			if (strategy.FlagStoreFailedNodes) 
				reactorResult.failedNodes.add(node);
		break;
		}
	}
	
	void processRemainingStackNodes()
	{
		//TODO
	}
	
	boolean isAllowedProduct(String inchiKey)
	{
		//if (inchiKey == null)
		//	return false;
		
		if (strategy.allowedProducts != null)
		{	
			for (StructureRecord sr : strategy.allowedProducts)
				if (inchiKey.equals(sr.getInchiKey()))
					return true;
		}		
		return false;
	}
	
	boolean isForbiddenProduct(String inchiKey)
	{
		//if (inchiKey == null)
		//	return false;
		
		if (strategy.forbiddenProducts != null)
		{	
			for (StructureRecord sr : strategy.forbiddenProducts)
				if (inchiKey.equals(sr.getInchiKey()))
					return true;
		}		
		return false;
	}
	
	void setReactionPath(IAtomContainerSet productsSet, IAtomContainer reagent, GenericReaction reaction)
	{
		Object reagent_rids = reagent.getProperty(PropertyReactionIds);
		int rids[] = null;
		if (reagent_rids == null)
		{	
			rids = new int[1];
			rids[0] = reaction.getId();
		}
		else
		{
			int i_reagent_rids[] = (int[]) reagent_rids;
			int n = i_reagent_rids.length;
			rids = new int[n+1];
			
			for (int k = 0; k < n; k++)
				rids[k] = i_reagent_rids[k];
			
			rids[n] = reaction.getId();
		}
		
		for (IAtomContainer ac : productsSet.atomContainers())
			ac.setProperty(PropertyReactionIds, rids);
	};
	
	
	//debug helper
	public String molToSmiles(IAtomContainer mol)
	{
		String smi = null;
		try {
			if (strategy.FlagLogExplicitHToImplicit)
			{
				IAtomContainer mol1 = mol.clone();
				MoleculeTools.convertExplicitHAtomsToImplicit(mol1);
				smi = SmartsHelper.moleculeToSMILES(mol1, true);
			}
			else
				smi = SmartsHelper.moleculeToSMILES(mol, true);
		} 
		catch (Exception e){
			logger.info(e.getMessage());
		};
		return smi;
	}
	
	public String reactionPathToString(IAtomContainer mol, boolean printName)
	{
		StringBuffer sb = new StringBuffer();
		Object obj = mol.getProperty(PropertyReactionIds);
		if (obj != null)
		{
			int rids[] = (int[]) obj;
			for (int i = 0; i < rids.length; i++)
			{	
				sb.append(" " + rids[i]);
				if (printName)
				{
					GenericReaction r = reactionDataBase.getGenericReactionByID(rids[i]);
					if (r != null)
						sb.append(" " + r.getName());
				}
			}	
		}
		return sb.toString();
	}
	
	private void setupLogger(Logger log)
	{
		log.setUseParentHandlers(false);
		Handler conHdlr = new ConsoleHandler();
		//TODO this is better to be done via logging.properties configuration
		conHdlr.setFormatter(new Formatter() {
			public String format(LogRecord record) {
				return
						/*
                    record.getLevel() + "  :  "
                	+ record.getSourceClassName() + " -:- "
                	+ record.getSourceMethodName() + " -:- "
						 */
				record.getMessage() + "\n";
			}
		});

		log.addHandler(conHdlr);
	}
	
	
	
	
	
	
	/*
	 * Reactor functionality:
	 * 1. Apply a set of reactions to the target in various modes (manage the reaction/ reaction sets to be applied)
	 * 		1b) Apply particular reaction to many targets
	 * 2. Organize various sets of reactions (sort of reaction DB functionality)
	 * 		2b) Organize metabolicc path ways
	 * 		3b) Organize synthetic paths
	 * 3. Search for a specific reaction in particular set of reactions
	 * 4. Filter reactions to be applied on the target (various criteria ...) 
	 * 5. Filter target sites for the reactions: Yes-list, No-list, SMARTS based, property based, ...
	 * 6. Estimate reaction sites (rule based, property based, charges, ...). 
	 *      Here can be used some of the strategy rules for retro- synthesis 
	 *   
	 * 
	 * Application possible functionalities:
	 * - draw/edit reactions
	 * - convert reactions to various formats (io)
	 * - define / edit rules
	 **/
}
