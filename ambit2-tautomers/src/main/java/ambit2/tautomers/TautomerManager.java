package ambit2.tautomers;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import ambit2.smarts.SmartsHelper;


public class TautomerManager 
{
	protected static Logger logger = Logger.getLogger(TautomerManager.class.getName());
	KnowledgeBase knowledgeBase; 
	IAtomContainer originalMolecule;
	IAtomContainer molecule;
	Vector<IRuleInstance> extendedRuleInstances = new Vector<IRuleInstance>(); 
	Vector<IRuleInstance> ruleInstances = new Vector<IRuleInstance>();
	Vector<Vector<IRuleInstance>> subCombinationsRI = new Vector<Vector<IRuleInstance>>();
	Vector<Rule> generatedRules = new Vector<Rule>(); 
	Vector<IAtomContainer> resultTautomers;	
	Vector<String> resultTatomerStringCodes = new Vector<String>(); 
	Vector<String> errors = new Vector<String>(); 
	public FilterTautomers tautomerFilter = new FilterTautomers(this);
	int originalValencySum;
		
	public boolean FlagRecurseBackResultTautomers = false;
	
	
	public boolean FlagCheckDuplicationOnRegistering = true;
	public boolean FlagCheckValencyOnRegistering = false;       //it is not used yet
	public boolean FlagExcludeWarnFiltersOnRegistering = false; //it is not used yet
	
	
	//Some debug info flags
	public boolean FlagPrintTargetMoleculeInfo = false;
	public boolean FlagPrintExtendedRuleInstances = false;
	public boolean FlagPrintIcrementalStepDebugInfo = false;
	
	public int maxNumOfBackTracks = 100000;
	public int maxNumOfTautomerRegistrations = 100000;
				
	
	public TautomerManager()
	{
		knowledgeBase = new KnowledgeBase();
		if (knowledgeBase.errors.size() > 0)
		{	
			logger.warning("There are errors in the knowledge base:");
			logger.info(knowledgeBase.getAllErrors());
		}
		
		knowledgeBase.activateRingChainRules(knowledgeBase.FlagUseRingChainRules);
		knowledgeBase.activateChlorineRules(knowledgeBase.FlagUseChlorineRules);
	}
	
	
	public void setStructure(IAtomContainer str) throws Exception {	
		molecule = str;
		originalMolecule = str;
		originalValencySum = FilterTautomers.getValencySum(str); 
		
		molecule = (IAtomContainer)originalMolecule.clone();
	}
	
	public KnowledgeBase getKnowledgeBase()
	{
		return knowledgeBase;
	}
	
	
	//Approach 00: this is the initial (pure combinatorial)  approach 
	public Vector<IAtomContainer> generateTautomers() throws Exception
	{
		searchAllRulePositions();
		handleOverlapedInstances();
		
		resultTautomers = new Vector<IAtomContainer>();
		if (ruleInstances.isEmpty())
		{	
			resultTautomers.add(molecule);
			return(resultTautomers);
		}	
		
		generateRuleInstanceCombinations();
		
		resultTautomers = tautomerFilter.filter(resultTautomers);
		
		return(resultTautomers);
	}
	
	//Approach 01: improved combinatorial approach 
	public Vector<IAtomContainer> generateTautomers_ImprovedCombApproach() throws Exception
	{
		searchAllRulePositions();
		resultTautomers = new Vector<IAtomContainer>();		
		if (extendedRuleInstances.isEmpty())
		{	
			resultTautomers.add(molecule);
			return(resultTautomers);
		}
		
		//Generating sub combinations
		subCombinationsRI =  generateSubCombinations();
		
		
		//iterating all sub combinations
		for (Vector<IRuleInstance> subComb : subCombinationsRI)
		{
			//printRIGroup(subComb, "working with combination");
			
			//initialize molecule
			restoreMolecule(molecule, originalMolecule);
			ruleInstances = subComb;
			generateRuleInstanceCombinations();
		}
		
		resultTautomers = tautomerFilter.filter(resultTautomers);
		
		return(resultTautomers);
	}
	
	//Approach 02 (basic one) based on first depth search algorithm
	public Vector<IAtomContainer> generateTautomersIncrementaly() throws Exception
	{
		//An approach for generation of tautomers
		//based on incremental steps of analysis with first-depth approach
		//In each Incremental steps one rule instance is added
		//and the other rule instances are revised and accordingly 
		//appropriate rule-instance sets are supported (derived from extendedRuleInstance)
		
		resultTautomers = new Vector<IAtomContainer>();	
		resultTatomerStringCodes.clear();
		
		searchAllRulePositions();
		
		if (extendedRuleInstances.isEmpty())
		{	
			resultTautomers.add(molecule);
			return(resultTautomers);
		}
		
		RuleManager rman = new RuleManager(this);
		rman.firstIncrementalStep();
		rman.iterateIncrementalSteps();	
		
		if (FlagRecurseBackResultTautomers)
		{
			//Apply special filter before recursing back the result tautomers
			Vector<IAtomContainer> filtered = preRecursionFiltration(resultTautomers);			
			Vector<IAtomContainer> res = generateTautomersFromMultipleTargets(filtered);
			resultTautomers = res;
		}
		else
			resultTautomers = tautomerFilter.filter(resultTautomers);
		
		return(resultTautomers);
	}
	
	//Combined approach (00, 01, 02)
	public Vector<IAtomContainer> generateTautomersCombinedApproach() throws Exception
	{
		resultTautomers = new Vector<IAtomContainer>();	
		resultTatomerStringCodes.clear();
		//TODO
		
		return(resultTautomers);
		
	}
	
	public void registerTautomer(IAtomContainer newTautomer)
	{
		if (FlagCheckDuplicationOnRegistering)
		{
			//FlagTreatAromaticBondsAsEquivalent should always be false here  
			//since aromaticity is not correct during the tautomer generation
			
			String newCode = getTautomerCodeString(newTautomer, false);   
			
			//System.out.println("---> " + newCode);
			
			if (!resultTatomerStringCodes.contains(newCode))
			{	
				resultTatomerStringCodes.add(newCode);
				resultTautomers.add(newTautomer);
			}	
		}
		else
			resultTautomers.add(newTautomer);
	}
	
	
	Vector<IAtomContainer> preRecursionFiltration(Vector<IAtomContainer> tautomers) throws Exception 
	{
		//Save original filtration flags
		boolean F_ApplyDuplicationCheckInChI = tautomerFilter.FlagApplyDuplicationCheckInChI;
		boolean F_ApplyDuplicationCheckIsomorphism = tautomerFilter.FlagApplyDuplicationCheckIsomorphism;
		boolean F_ApplyDuplicationFilter = tautomerFilter.FlagApplyDuplicationFilter;
		boolean F_ApplyExcludeFilter = tautomerFilter.FlagApplyExcludeFilter;
		boolean F_ApplyWarningFilter = tautomerFilter.FlagApplyWarningFilter;
		boolean F_ExcludeWarningTautomers = tautomerFilter.FlagExcludeWarningTautomers;
		boolean F_FilterIncorrectValencySumStructures = tautomerFilter.FlagFilterIncorrectValencySumStructures;
		
		//Setting the flags for filtration
		tautomerFilter.FlagApplyDuplicationCheckInChI = false;
		tautomerFilter.FlagApplyDuplicationCheckIsomorphism = true;
		tautomerFilter.FlagApplyDuplicationFilter = true;
		tautomerFilter.FlagApplyExcludeFilter = true;
		tautomerFilter.FlagApplyWarningFilter = true;
		tautomerFilter.FlagExcludeWarningTautomers = true;
		tautomerFilter.FlagFilterIncorrectValencySumStructures = true;
		
		//Performing filtration
		Vector<IAtomContainer> res = tautomerFilter.filter(tautomers);
		
		//Restore original filtration flags
		tautomerFilter.FlagApplyDuplicationCheckInChI = F_ApplyDuplicationCheckInChI;
		tautomerFilter.FlagApplyDuplicationCheckIsomorphism = F_ApplyDuplicationCheckIsomorphism; 
		tautomerFilter.FlagApplyDuplicationFilter = F_ApplyDuplicationFilter;
		tautomerFilter.FlagApplyExcludeFilter = F_ApplyExcludeFilter;
		tautomerFilter.FlagApplyWarningFilter = F_ApplyWarningFilter; 
		tautomerFilter.FlagExcludeWarningTautomers = F_ExcludeWarningTautomers;
		tautomerFilter.FlagFilterIncorrectValencySumStructures = F_FilterIncorrectValencySumStructures;
				
		return res;
	}
	
	Vector<IAtomContainer> generateTautomersFromMultipleTargets(Vector<IAtomContainer> targets) throws Exception 
	{
		IAtomContainer tmpOrgMolecule = originalMolecule;
		IAtomContainer tmpMolecule = molecule;
		
		Vector<IAtomContainer> summarizedResult = new Vector<IAtomContainer>();
		
		for (int i = 0; i < targets.size(); i++) 
		{
			setStructure(targets.get(i));
			resultTautomers = new Vector<IAtomContainer>();	
			searchAllRulePositions();
			
			if (extendedRuleInstances.isEmpty())
			{	
				summarizedResult.add(molecule);
				continue;
			}
			
			RuleManager rman = new RuleManager(this);
			rman.firstIncrementalStep();
			rman.iterateIncrementalSteps();	
			
			summarizedResult.addAll(resultTautomers);
		}
		
		//Restore the original molecule variables
		originalMolecule = tmpOrgMolecule;
		molecule = tmpMolecule; 
		
		return tautomerFilter.filter(summarizedResult);
	}
	
	
	void searchAllRulePositions() throws Exception
	{
		generatedRules.clear();
		extendedRuleInstances.clear();
		ruleInstances.clear();
		
		for (int i = 0; i < knowledgeBase.rules.size(); i++)
			if (knowledgeBase.rules.get(i).isRuleActive) {
				try {
					Vector<IRuleInstance> instances = knowledgeBase.rules.get(i).applyRule(molecule); 
					if ((instances!=null) && (instances.size()>0))
						extendedRuleInstances.addAll(instances);
				} catch (Exception x) {
					logger.log(Level.WARNING,knowledgeBase.rules.get(i).name,x);
				}
			}	
	}
	
	
	
	
		
	
	//This function is applied for approach 00 
	void handleOverlapedInstances()
	{			
		//Nothing special is done currently
		//Just all initial rules are added. 
		ruleInstances.addAll(extendedRuleInstances);
				
		//RuleManager rman = new RuleManager(this);
		//rman.handleOverlappingRuleInstances();
	}
	
	//This function is applied for approach 00 and as helper for approach 01
	void generateRuleInstanceCombinations() throws Exception 
	{
		for (int i = 0; i < ruleInstances.size(); i++)
			ruleInstances.get(i).firstState();
						
		int n;
		int instNumber; 
		
		do 	{
			registerTautomer00();
			
			n = ruleInstances.get(0).nextState();
			instNumber = 0;
			
			while(n == 0)
			{
				instNumber++;
				if (instNumber == ruleInstances.size())
					break;
				n = ruleInstances.get(instNumber).nextState();
			}
		} while (instNumber < ruleInstances.size()); 
	}
	
	
	//This function is applied for approach 00 and 01
	void registerTautomer00() throws Exception 
	{	
		IAtomContainer newTautomer = (IAtomContainer)molecule.clone();
		resultTautomers.add(newTautomer);
		
		//System.out.print("  tautomer: " + getTautomerCombination() +  "    " + SmartsHelper.moleculeToSMILES(molecule));		
		
		//Print H Atoms info
		//for (int i = 0; i < molecule.getAtomCount(); i++)
		//	System.out.print(" " + molecule.getAtom(i).getImplicitHydrogenCount());
		//System.out.println();
	}
	
	
	Vector<Vector<IRuleInstance>> generateSubCombinations()
	{
		//Determination of  groups (clusters) of overlapping rule instances 
		Vector<Vector<IRuleInstance>> riGroups = new Vector<Vector<IRuleInstance>>(); 
		for (IRuleInstance ri : extendedRuleInstances)
		{
			boolean FlagRIOverlaps = false;
			for (Vector<IRuleInstance> group : riGroups)
			{
				if (RuleManager.overlaps((RuleInstance)ri, group))
				{	
					group.add(ri);
					FlagRIOverlaps = true;
					break;
				}
			}
			
			if (!FlagRIOverlaps)
			{
				//a new group is created
				Vector<IRuleInstance> group = new Vector<IRuleInstance>();
				group.add(ri);
				riGroups.add(group);
			}
		}
		
		 
		//The groups are sorted 
		Vector<IRuleInstance> defaultGroup = new Vector<IRuleInstance>();
		Vector<Vector<IRuleInstance>> bigGroups = new Vector<Vector<IRuleInstance>>();
		
		for (Vector<IRuleInstance> group: riGroups)
		{			
			//printRIGroup(group,"group"); System.out.println();
			if (group.size() == 1)
				defaultGroup.add(group.firstElement());
			else
				bigGroups.add(group);
		}		
			
		
		//helper array contains the positions in each group
		int gpos[] = new int[bigGroups.size()]; 
		int gmax[] = new int[bigGroups.size()];
		long numOfCombinations = 1;
		
		//Initialization of the positions for each bigGroup 
		//plus calculation of the total number of the number of combinations 
		for (int i = 0; i < gpos.length; i++)
		{	
			gpos[i] = 0;
			gmax[i] = bigGroups.get(i).size();
			numOfCombinations = numOfCombinations * gmax[i]; 
		}
		
		//Generation of all sub-combinations from clusters
		Vector<Vector<IRuleInstance>> subCombs = new Vector<Vector<IRuleInstance>>();	
		 
		long curComb = 0;
		while (curComb < numOfCombinations)
		{	
			//Create a combination
			Vector<IRuleInstance> combination  = new Vector<IRuleInstance>();
			combination.addAll(defaultGroup);
			for (int i = 0; i < gpos.length; i++)
				combination.add(bigGroups.get(i).get(gpos[i]));
			
			subCombs.add(combination);
						
			//iterate to next combination
			for (int i = 0; i < gpos.length; i++)
			{
				gpos[i]++;
				if (gpos[i] < gmax[i])
					break; //algorithm will not go to the next group
				else
					gpos[i] = 0; //group position is set to zero and next group position is iterated
			}
			curComb++;
		}
		
		return subCombs;
	}
	
	//small helper
	void printRIGroup(Vector<IRuleInstance> group, String info)
	{
		logger.info(info);
		for (IRuleInstance ri : group)
			logger.info(((RuleInstance)ri).debugInfo(molecule));
	}
	
	
	
	public static String getTautomerCodeString(IAtomContainer tautomer, boolean treatAromBondsAsEquivalent)
	{
		StringBuffer sb = new StringBuffer();
		
		//All bonds are described in canonical numbering (i < j) 
		for (int i = 0; i < tautomer.getAtomCount(); i++)
		{
			for (int j = i+1; j < tautomer.getAtomCount(); j++)
			{
				IBond bo = tautomer.getBond(tautomer.getAtom(i), tautomer.getAtom(j));
				if (bo != null)
				{	
					if (bo.getFlag(CDKConstants.ISAROMATIC))
					{	
						if (treatAromBondsAsEquivalent)
							sb.append("a");
						else
							sb.append(bo.getOrder().ordinal());
					}		
					else							
						sb.append(bo.getOrder().ordinal());
				}	
			}
		}
		
		return(sb.toString());
	}
	
	
	String getTautomerCombination()
	{
		StringBuffer sb = new StringBuffer();
		for (int i = ruleInstances.size()-1; i >= 0; i--)
			sb.append(""+ruleInstances.get(i).getCurrentState() + " ");
		return (sb.toString());
	}
	
	/**
	 * TODO: Rewrite with logger and proper logger levels
	 */
	public void printDebugInfo()
	{
		if (FlagPrintTargetMoleculeInfo)
		{
			System.out.println("Debug info - Target Atom atributes:");
			String s = SmartsHelper.getAtomsAttributes(molecule);
			System.out.println(s + "\n");
			System.out.println("Debug info - Target Bond atributes:");
			String s2 = SmartsHelper.getBondAttributes(molecule);
			System.out.println(s2 + "\n");
		}
		
		
		if (FlagPrintExtendedRuleInstances)
		{	
			System.out.println("Debug info - extendedRuleInstances:");
			for (int i = 0; i < extendedRuleInstances.size(); i++)
				System.out.println(((RuleInstance)extendedRuleInstances.get(i)).debugInfo(molecule));
			if(extendedRuleInstances.isEmpty())
				System.out.println("  NONE");
		}	
			
	}
	
	public static IAtomContainer getCanonicTautomer(Vector<IAtomContainer> tautomers)
	{	
		if (tautomers.size() == 1)
			return tautomers.firstElement();
		
		IAtomContainer can_t = tautomers.firstElement();
		double rank = ((Double)can_t.getProperty("TAUTOMER_RANK")).doubleValue();
		
		for (IAtomContainer t : tautomers)
		{
			double newRank = ((Double)t.getProperty("TAUTOMER_RANK")).doubleValue();
			if (newRank < rank)
			{
				rank = newRank;
				can_t = t;
			}
		}
		
		return can_t;
	}
	
	void restoreMolecule(IAtomContainer mol, IAtomContainer origMol)
	{
		//restoring bond orders
		for (int i = 0; i < mol.getBondCount(); i++)
		{
			IBond b = mol.getBond(i);
			b.setOrder(origMol.getBond(i).getOrder());
		}
		
		//restoring implicit H atoms
		for (int i = 0; i < mol.getAtomCount(); i++)
		{	
			int nH = origMol.getAtom(i).getImplicitHydrogenCount();
			mol.getAtom(i).setImplicitHydrogenCount(nH);
		}	
			
	}
	
}
