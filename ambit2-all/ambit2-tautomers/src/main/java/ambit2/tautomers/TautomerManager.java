package ambit2.tautomers;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.smarts.SmartsHelper;
import ambit2.tautomers.ranking.EnergyRanking;


public class TautomerManager 
{
	protected static final Logger logger = Logger.getLogger(TautomerManager.class.getName());
	KnowledgeBase knowledgeBase = null; 
	IAtomContainer originalMolecule = null;
	IAtomContainer molecule = null;
	RuleSelector ruleSelector = null;
	List<IRuleInstance> extendedRuleInstances = new ArrayList<IRuleInstance>(); 
	List<IRuleInstance> extendedRuleInstances0; //The initial rules (pre-selection list)
	List<IRuleInstance> ruleInstances = new ArrayList<IRuleInstance>();
	List<List<IRuleInstance>> subCombinationsRI = new ArrayList<List<IRuleInstance>>();
	List<Rule> generatedRules = new ArrayList<Rule>(); 
	List<IAtomContainer> resultTautomers;	
	List<String> resultTatomerStringCodes = new ArrayList<String>(); 
	List<String> errors = new ArrayList<String>(); 
	int numOfRegistrations = 0;
	int status = TautomerConst.STATUS_NONE;
	private EnergyRanking energyRanking = null;
	
	
	public FilterTautomers tautomerFilter = new FilterTautomers(this);
	int originalValencySum;
	
	public int FlagEnergyRankingMethod = TautomerConst.ERM_OLD;
	public boolean FlagApplySimpleAromaticityRankCorrection = true;  //This flag is valid only for the old ranking method
	
	public boolean FlagSwitchToCombinatorialOnReachingRuleLimit = true;
	public boolean FlagRecurseBackResultTautomers = false;
	public boolean FlagCalculateCACTVSEnergyRank = false;
	
	public boolean FlagCheckDuplicationOnRegistering = true;
	public boolean FlagCheckValencyOnRegistering = false;       //it is not used yet
	public boolean FlagExcludeWarnFiltersOnRegistering = false; //it is not used yet
	
	
	//Some debug info flags
	public boolean FlagPrintTargetMoleculeInfo = false;
	public boolean FlagPrintExtendedRuleInstances = false;
	public boolean FlagPrintIcrementalStepDebugInfo = false;
	
	public int maxNumOfBackTracks = 5000; //Used only for the Incremental algorithm
	public int maxNumOfTautomerRegistrations = 1000;  //Used for the combinatorial and improved combinatorial algorithms
	public int maxNumOfSubCombinations = 10000; //Used only for the improved combinatorial algorithm
	public boolean FlagProcessRemainingStackIncSteps = true;   //Typically this flag should be true 
	public boolean FlagStopGenerationOnReachingRuleSelectorLimit = false; //Typically this flag should be false
	public boolean FlagCheckNumOfRegistrationsForIncrementalAlgorithm = true; 
	
	
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
		
		ruleSelector = RuleSelector.getDefaultSelectorRandom();
	}
	
	
	public void setStructure(IAtomContainer str) throws Exception {	
		molecule = str;
		originalMolecule = str;
		originalValencySum = FilterTautomers.getValencySum(str); 
		
		molecule = (IAtomContainer)originalMolecule.clone();
		status = TautomerConst.STATUS_SET_STRUCTURE;
	}
	
	public KnowledgeBase getKnowledgeBase()
	{
		return knowledgeBase;
	}
	
	public RuleSelector getRuleSelector()
	{
		return ruleSelector;
	}
	
	public void setRuleSelector(RuleSelector ruleSelector)
	{
		this.ruleSelector = ruleSelector;
	}
	
	
	/**
	 * This is pure combinatorial approach
	 * It is the initial approach 00 based on binary combinations. 
	 * n rule instances define 2^n combinations (binary number with n digits) 
	 */
	public List<IAtomContainer> generateTautomers() throws Exception
	{
		status = TautomerConst.STATUS_STARTED;
		numOfRegistrations = 0;
		
		searchAllRulePositions();
		
		//Overlapping instances are not handled. Incorrect tautomers are filtered out by FilterTautomers
		//ruleInstances.addAll(extendedRuleInstances);
		ruleInstances = ruleSelector.selectRules(this, extendedRuleInstances);      
		
		
		resultTautomers = new ArrayList<IAtomContainer>();
		if (ruleInstances.isEmpty())
		{	
			resultTautomers.add(molecule);
			status = TautomerConst.STATUS_FINISHED;
			return(resultTautomers);
		}	
		
		generateRuleInstanceCombinations();
		
		resultTautomers = tautomerFilter.filter(resultTautomers);
		
		if (FlagCalculateCACTVSEnergyRank)
			calcCACTVSEnergyRanks(resultTautomers);
		
		status = TautomerConst.STATUS_FINISHED;
		return(resultTautomers);
	}
	
	/**
	 * @return 
	 * @throws Exception
	 * Approach 01: improved combinatorial approach
	 */	 
	public List<IAtomContainer> generateTautomers_ImprovedCombApproach() throws Exception
	{
		status = TautomerConst.STATUS_STARTED;
		numOfRegistrations = 0;
		
		searchAllRulePositions();
		resultTautomers = new ArrayList<IAtomContainer>();		
		if (extendedRuleInstances.isEmpty())
		{	
			resultTautomers.add(molecule);
			status = TautomerConst.STATUS_FINISHED;
			return(resultTautomers);
		}
		
		//Rule selection (original extended list is kept in extendedRuleInstances0)
		extendedRuleInstances = ruleSelector.selectRules(this, extendedRuleInstances);
		
		//Generating sub combinations
		subCombinationsRI =  generateSubCombinations();
		
		
		//iterating all sub combinations
		for (List<IRuleInstance> subComb : subCombinationsRI)
		{
			//printRIGroup(subComb, "working with combination");
			
			//initialize molecule
			restoreMolecule(molecule, originalMolecule);
			ruleInstances = subComb;
			generateRuleInstanceCombinations();
		}
		
		resultTautomers = tautomerFilter.filter(resultTautomers);
		
		if (FlagCalculateCACTVSEnergyRank)
			calcCACTVSEnergyRanks(resultTautomers);
		
		status = TautomerConst.STATUS_FINISHED;
		return(resultTautomers);
	}
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 * This is the basic approach 
	 * Approach 02 (basic one) based on first depth search algorithm
	 */
	//
	public List<IAtomContainer> generateTautomersIncrementaly() throws Exception
	{
		//An approach for generation of tautomers
		//based on incremental steps of analysis with first-depth approach
		//In each Incremental steps one rule instance is added
		//and the other rule instances are revised and accordingly 
		//appropriate rule-instance sets are supported (derived from extendedRuleInstance)
		
		status = TautomerConst.STATUS_STARTED;
		numOfRegistrations = 0;
		resultTautomers = new ArrayList<IAtomContainer>();	
		resultTatomerStringCodes.clear();
		
		searchAllRulePositions();
		
		if (extendedRuleInstances.isEmpty())
		{	
			resultTautomers.add(molecule);
			status = TautomerConst.STATUS_FINISHED;
			return(resultTautomers);
		}
		
		//Rule selection (original extended list is kept in extendedRuleInstances0)
		extendedRuleInstances = ruleSelector.selectRules(this, extendedRuleInstances);
		
		//This is the combinatorial algorithm when rule number limit is reached.
		//This is needed because the incremental algorithms 'overcomes' the selection by finding   
		//and reusing again the excluded rules by the ruleSelector
		if (FlagSwitchToCombinatorialOnReachingRuleLimit)
			if (ruleSelector.switchToCombinatorial())
			{	
				if (FlagStopGenerationOnReachingRuleSelectorLimit)
				{
					status = TautomerConst.STATUS_STOPPED;
					return(resultTautomers);
				}
				return switchToCombinatorial();
			}
		
		//The incremental approach is performed here
		RuleManager rman = new RuleManager(this);
		rman.firstIncrementalStep();
		rman.iterateIncrementalSteps();	
		
		if (FlagRecurseBackResultTautomers)
		{
			//Apply special filter before recursing back the result tautomers
			List<IAtomContainer> filtered = preRecursionFiltration(resultTautomers);			
			List<IAtomContainer> res = generateTautomersFromMultipleTargets(filtered);
			resultTautomers = res;
		}
		else
			resultTautomers = tautomerFilter.filter(resultTautomers);
		
		if (FlagCalculateCACTVSEnergyRank)
			calcCACTVSEnergyRanks(resultTautomers);
		
		status = TautomerConst.STATUS_FINISHED;
		return(resultTautomers);
	}
	
	List<IAtomContainer> switchToCombinatorial() throws Exception
	{
		logger.info("******* Switching from Incremental to combinatorial!!");
		numOfRegistrations = 0;
		
		//Generating sub combinations
		subCombinationsRI =  generateSubCombinations();

		//iterating all sub combinations
		for (List<IRuleInstance> subComb : subCombinationsRI)
		{
			//initialize molecule
			restoreMolecule(molecule, originalMolecule);
			ruleInstances = subComb;
			generateRuleInstanceCombinations();
		}
		
		
		/*
		//pure combinatorial approach
		ruleInstances.addAll(extendedRuleInstances);
		generateRuleInstanceCombinations();
		*/
		
		resultTautomers = tautomerFilter.filter(resultTautomers);

		if (FlagCalculateCACTVSEnergyRank)
			calcCACTVSEnergyRanks(resultTautomers);
			
		status = TautomerConst.STATUS_FINISHED;
		return(resultTautomers);
	}
	
	
	//Combined approach (00, 01, 02) - not implemented!
	public List<IAtomContainer> generateTautomersCombinedApproach() throws Exception
	{
		resultTautomers = new ArrayList<IAtomContainer>();	
		resultTatomerStringCodes.clear();
		//TODO
		return(resultTautomers);
		
	}
	
	public void registerTautomer(IAtomContainer newTautomer) throws Exception
	{
		numOfRegistrations++;
		
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
				processTautomer(newTautomer);
			}	
		}
		else
		{	
			resultTautomers.add(newTautomer);
			processTautomer(newTautomer);
		}	
	}
	
	
	List<IAtomContainer> preRecursionFiltration(List<IAtomContainer> tautomers) throws Exception 
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
		List<IAtomContainer> res = tautomerFilter.filter(tautomers);
		
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
	
	List<IAtomContainer> generateTautomersFromMultipleTargets(List<IAtomContainer> targets) throws Exception 
	{
		IAtomContainer tmpOrgMolecule = originalMolecule;
		IAtomContainer tmpMolecule = molecule;
		
		List<IAtomContainer> summarizedResult = new ArrayList<IAtomContainer>();
		
		for (int i = 0; i < targets.size(); i++) 
		{
			setStructure(targets.get(i));
			resultTautomers = new ArrayList<IAtomContainer>();	
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
					List<IRuleInstance> instances = knowledgeBase.rules.get(i).applyRule(molecule); 
					if ((instances!=null) && (instances.size()>0))
						extendedRuleInstances.addAll(instances);
				} catch (Exception x) {
					logger.log(Level.WARNING,knowledgeBase.rules.get(i).name,x);
				}
			}
		
		extendedRuleInstances0 = extendedRuleInstances;
	}
	
		
	/*
	//This function is applied for approach 00 
	void handleOverlapedInstances()
	{			
		//Nothing special is done currently
		//Just all initial rules are added. 
		ruleInstances.addAll(extendedRuleInstances);
				
		//RuleManager rman = new RuleManager(this);
		//rman.handleOverlappingRuleInstances();
	}
	*/
	
	//This function is applied for approach 00 and as helper for approach 01
	void generateRuleInstanceCombinations() throws Exception 
	{
		for (int i = 0; i < ruleInstances.size(); i++)
			ruleInstances.get(i).firstState();
						
		int n;
		int instNumber; 
		
		do 	{
			
			if (numOfRegistrations > maxNumOfTautomerRegistrations)
				break;
			
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
		numOfRegistrations++;
		
		//if (numOfRegistrations%100 == 0) System.out.println("  "+ numOfRegistrations + " registered tautomers");
		
		//System.out.print("  tautomer: " + getTautomerCombination() +  "    " + SmartsHelper.moleculeToSMILES(molecule));		
		
		//Print H Atoms info
		//for (int i = 0; i < molecule.getAtomCount(); i++)
		//	System.out.print(" " + molecule.getAtom(i).getImplicitHydrogenCount());
		//System.out.println();
	}
	
	
	List<List<IRuleInstance>> generateSubCombinations()
	{
		//Determination of  groups (clusters) of overlapping rule instances 
		List<List<IRuleInstance>> riGroups = new ArrayList<List<IRuleInstance>>(); 
		for (IRuleInstance ri : extendedRuleInstances)
		{
			boolean FlagRIOverlaps = false;
			for (List<IRuleInstance> group : riGroups)
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
				List<IRuleInstance> group = new ArrayList<IRuleInstance>();
				group.add(ri);
				riGroups.add(group);
			}
		}
		
		 
		//The groups are sorted 
		List<IRuleInstance> defaultGroup = new ArrayList<IRuleInstance>();
		List<List<IRuleInstance>> bigGroups = new ArrayList<List<IRuleInstance>>();
		
		for (List<IRuleInstance> group: riGroups)
		{			
			//printRIGroup(group,"group"); System.out.println();
			if (group.size() == 1)
				defaultGroup.add(group.get(0));
			else
				bigGroups.add(group);
		}		
			
		
		//helper array contains the positions in each group
		int gpos[] = new int[bigGroups.size()]; 
		int gmax[] = new int[bigGroups.size()];
		long numOfSubCombinations = 1;
		
		//Initialization of the positions for each bigGroup 
		//plus calculation of the total number of the number of combinations 
		for (int i = 0; i < gpos.length; i++)
		{	
			gpos[i] = 0;
			gmax[i] = bigGroups.get(i).size();
			numOfSubCombinations = numOfSubCombinations * gmax[i]; 
		}
		
		logger.info("numOfSubCombinations = " + numOfSubCombinations);
		
		//Generation of all sub-combinations from clusters
		List<List<IRuleInstance>> subCombs = new ArrayList<List<IRuleInstance>>();	
		 
		long curSComb = 0;
		while ((curSComb < numOfSubCombinations) & (curSComb < maxNumOfSubCombinations))
		{	
			//Create a combination
			List<IRuleInstance> subCombination  = new ArrayList<IRuleInstance>();
			subCombination.addAll(defaultGroup);
			for (int i = 0; i < gpos.length; i++)
				subCombination.add(bigGroups.get(i).get(gpos[i]));
			
			subCombs.add(subCombination);
						
			//iterate to next combination
			for (int i = 0; i < gpos.length; i++)
			{
				gpos[i]++;
				if (gpos[i] < gmax[i])
					break; //algorithm will not go to the next group
				else
					gpos[i] = 0; //group position is set to zero and next group position is iterated
			}
			curSComb++;
		}
		
		return subCombs;
	}
	
	//small helper
	void printRIGroup(List<IRuleInstance> group, String info)
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
		if (FlagPrintTargetMoleculeInfo) {
			logger.log(Level.FINE,"Debug info - Target Atom atributes:");
			String s = SmartsHelper.getAtomsAttributes(molecule);
			logger.log(Level.FINE,s);
			logger.log(Level.FINE,"Debug info - Target Bond atributes:");
			String s2 = SmartsHelper.getBondAttributes(molecule);
			logger.log(Level.FINE,s2);
		}
		
		
		if (FlagPrintExtendedRuleInstances) 	{	
			logger.log(Level.FINE,"Debug info - extendedRuleInstances:");
			for (int i = 0; i < extendedRuleInstances.size(); i++)
				logger.log(Level.FINE,((RuleInstance)extendedRuleInstances.get(i)).debugInfo(molecule));
			if(extendedRuleInstances.isEmpty())
				logger.log(Level.FINE,"  NONE");
		}	
			
	}
	
	public static IAtomContainer getCanonicTautomer(List<IAtomContainer> tautomers)
	{	
		if (tautomers.size() == 1)
			return tautomers.get(0);
		
		IAtomContainer can_t = tautomers.get(0);
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
	
	public int getInitialRuleCount(){
		return extendedRuleInstances0.size();
	}
	
	public int getStatus(){
		return status;
	}
	
	public static void calcCACTVSEnergyRanks(List<IAtomContainer> tautomers)
	{
		for (IAtomContainer mol: tautomers)
		{
			double rank = CACTVSRanking.getEnergyRank(mol);
			mol.setProperty("CACTVS_ENERGY_RANK", new Double(rank));
		}
	}
	
	public static void processTautomer(IAtomContainer ac) throws Exception
	{
		if (ac == null)
			return;
		
		//Clear aromaticity flags
		for (int i = 0; i < ac.getAtomCount(); i++)
			ac.getAtom(i).setFlag(CDKConstants.ISAROMATIC, false);			
		for (int i = 0; i < ac.getBondCount(); i++)
			ac.getBond(i).setFlag(CDKConstants.ISAROMATIC, false);
		
		//AtomContainerManipulator.clearAtomConfigurations(ac);
		for (IAtom atom : ac.atoms()) {
            atom.setHybridization((IAtomType.Hybridization) CDKConstants.UNSET);
        }
		
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(ac);
		CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance());
		adder.addImplicitHydrogens(ac);
		//AtomContainerManipulator.convertImplicitToExplicitHydrogens(ac);

		CDKHueckelAromaticityDetector.detectAromaticity(ac);
	}

	public EnergyRanking getEnergyRanking() throws Exception 
	{
		if (energyRanking == null)
			energyRanking = new EnergyRanking();
		return energyRanking;
	}
	
	public void setEnergyRanking(EnergyRanking energyRanking)
	{
		this.energyRanking = energyRanking;
	}
	
}
