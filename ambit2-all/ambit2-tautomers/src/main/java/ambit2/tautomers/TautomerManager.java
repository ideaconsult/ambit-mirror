package ambit2.tautomers;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.stereo.StereoElementFactory;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.core.helper.CDKHueckelAromaticityDetector;
import ambit2.smarts.SmartsHelper;
import ambit2.smarts.StereoChemUtils;
import ambit2.tautomers.TautomerConst.CanonicTautomerMethod;
import ambit2.tautomers.ranking.CanonicTautomer;
import ambit2.tautomers.ranking.EnergyRanking;
import ambit2.tautomers.ranking.TautomerStringCode;

public class TautomerManager {

	protected static final Logger logger = Logger
			.getLogger(TautomerManager.class.getName());
	KnowledgeBase knowledgeBase = null;
	IAtomContainer originalMolecule = null;
	IAtomContainer molecule = null;
	RuleSelector ruleSelector = null;
	List<IRuleInstance> extendedRuleInstances = new ArrayList<IRuleInstance>();
	List<IRuleInstance> extendedRuleInstances0; // The initial rules
												// (pre-selection list)
	List<IRuleInstance> ruleInstances = new ArrayList<IRuleInstance>();
	List<List<IRuleInstance>> subCombinationsRI = new ArrayList<List<IRuleInstance>>();
	List<Rule> generatedRules = new ArrayList<Rule>();
	List<IAtomContainer> resultTautomers;
	List<String> resultTatomerStringCodes = new ArrayList<String>();
	double bestRank = 0.0;
	List<String> errors = new ArrayList<String>();
	int numOfRegistrations = 0;
	int status = TautomerConst.STATUS_NONE;
	private EnergyRanking energyRanking = null;
	private CanonicTautomerMethod canonicTautomerMethod = CanonicTautomerMethod.CACTVS_RANK_INCHI_KEY;
	private TautomerIncrementStep curIncrStep = null;
	private int codeStrBondSequence[] = null;

	public FilterTautomers tautomerFilter = new FilterTautomers(this);
	int originalValencySum;

	public int FlagEnergyRankingMethod = TautomerConst.ERM_OLD;
	public boolean FlagNewRuleInstanceSearchOnEnergyRanking = false;
	public boolean FlagApplySimpleAromaticityRankCorrection = true; // This flag
																	// is valid
																	// only for
																	// the old
																	// ranking
																	// method

	public boolean FlagSwitchToCombinatorialOnReachingRuleLimit = true;
	public boolean FlagRecurseBackResultTautomers = false;
	public boolean FlagCalculateCACTVSEnergyRank = false;

	public boolean FlagCheckDuplicationOnRegistering = true;
	public boolean FlagCheckValencyOnRegistering = false; // it is not used yet
	public boolean FlagExcludeWarnFiltersOnRegistering = false; // it is not
																// used yet

	public boolean FlagRegisterOnlyBestRankTautomers = false;

	// Some debug info flags
	public boolean FlagPrintTargetMoleculeInfo = false;
	public boolean FlagPrintExtendedRuleInstances = false;
	public boolean FlagPrintIcrementalStepDebugInfo = false;

	public int maxNumOfBackTracks = 5000; // Used only for the Incremental
											// algorithm
	public int maxNumOfTautomerRegistrations = 1000; // Used for the
														// combinatorial and
														// improved
														// combinatorial
														// algorithms
	public int maxNumOfSubCombinations = 10000; // Used only for the improved
												// combinatorial algorithm
	public boolean FlagProcessRemainingStackIncSteps = true; // Typically this
																// flag should
																// be true
	public boolean FlagGenerateStereoBasedOn2D = false;
	public boolean FlagSetStereoElementsOnTautomerProcess = true;
	public boolean FlagAddImplicitHAtomsOnTautomerProcess = true;
	public boolean FlagStopGenerationOnReachingRuleSelectorLimit = false; // Typically
																			// this
																			// flag
																			// should
																			// be
																			// false
	public boolean FlagCheckNumOfRegistrationsForIncrementalAlgorithm = true;

	public TautomerManager() {
		knowledgeBase = new KnowledgeBase();
		if (knowledgeBase.errors.size() > 0) {
			logger.warning("There are errors in the knowledge base:");
			logger.warning(knowledgeBase.getAllErrors());
		}

		knowledgeBase
				.activateRingChainRules(knowledgeBase.FlagUseRingChainRules);
		knowledgeBase.activateChlorineRules(knowledgeBase.FlagUseChlorineRules);

		ruleSelector = RuleSelector.getDefaultSelectorRandom();
	}

	public void setStructure(IAtomContainer str) throws Exception {
		molecule = str;
		originalMolecule = str;
		originalValencySum = FilterTautomers.getValencySum(str);

		molecule = (IAtomContainer) originalMolecule.clone();
		status = TautomerConst.STATUS_SET_STRUCTURE;
		codeStrBondSequence = TautomerStringCode.getBondIndexSequence(molecule);

		if (FlagRegisterOnlyBestRankTautomers)
			tautomerFilter.getOriginalPositions();
	}

	public KnowledgeBase getKnowledgeBase() {
		return knowledgeBase;
	}

	public RuleSelector getRuleSelector() {
		return ruleSelector;
	}

	public void setRuleSelector(RuleSelector ruleSelector) {
		this.ruleSelector = ruleSelector;
	}

	/**
	 * This is pure combinatorial approach It is the initial approach 00 based
	 * on binary combinations. n rule instances define 2^n combinations (binary
	 * number with n digits)
	 */
	public List<IAtomContainer> generateTautomers() throws Exception {
		status = TautomerConst.STATUS_STARTED;
		numOfRegistrations = 0;

		searchAllRulePositions();

		// Overlapping instances are not handled. Incorrect tautomers are
		// filtered out by FilterTautomers
		// ruleInstances.addAll(extendedRuleInstances);
		ruleInstances = ruleSelector.selectRules(this, extendedRuleInstances);

		resultTautomers = new ArrayList<IAtomContainer>();
		if (ruleInstances.isEmpty()) {
			resultTautomers.add(molecule);
			status = TautomerConst.STATUS_FINISHED;
			return (resultTautomers);
		}

		generateRuleInstanceCombinations();

		resultTautomers = tautomerFilter.filter(resultTautomers);

		if (FlagCalculateCACTVSEnergyRank)
			calcCACTVSEnergyRanks(resultTautomers);

		status = TautomerConst.STATUS_FINISHED;
		return (resultTautomers);
	}

	/**
	 * @return
	 * @throws Exception
	 *             Approach 01: improved combinatorial approach
	 */
	public List<IAtomContainer> generateTautomers_ImprovedCombApproach()
			throws Exception {
		status = TautomerConst.STATUS_STARTED;
		numOfRegistrations = 0;

		searchAllRulePositions();
		resultTautomers = new ArrayList<IAtomContainer>();
		if (extendedRuleInstances.isEmpty()) {
			resultTautomers.add(molecule);
			status = TautomerConst.STATUS_FINISHED;
			return (resultTautomers);
		}

		// Rule selection (original extended list is kept in
		// extendedRuleInstances0)
		extendedRuleInstances = ruleSelector.selectRules(this,
				extendedRuleInstances);

		// Generating sub combinations
		subCombinationsRI = generateSubCombinations();

		// iterating all sub combinations
		for (List<IRuleInstance> subComb : subCombinationsRI) {
			// printRIGroup(subComb, "working with combination");

			// initialize molecule
			restoreMolecule(molecule, originalMolecule);
			ruleInstances = subComb;
			generateRuleInstanceCombinations();
		}

		resultTautomers = tautomerFilter.filter(resultTautomers);

		if (FlagCalculateCACTVSEnergyRank)
			calcCACTVSEnergyRanks(resultTautomers);

		status = TautomerConst.STATUS_FINISHED;
		return (resultTautomers);
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 *             This is the basic approach Approach 02 (basic one) based on
	 *             first depth search algorithm
	 */
	//
	public List<IAtomContainer> generateTautomersIncrementaly()
			throws Exception {
		// An approach for generation of tautomers
		// based on incremental steps of analysis with first-depth approach
		// In each Incremental steps one rule instance is added
		// and the other rule instances are revised and accordingly
		// appropriate rule-instance sets are supported (derived from
		// extendedRuleInstance)

		status = TautomerConst.STATUS_STARTED;
		numOfRegistrations = 0;
		resultTautomers = new ArrayList<IAtomContainer>();
		resultTatomerStringCodes.clear();

		searchAllRulePositions();

		if (extendedRuleInstances.isEmpty()) {
			resultTautomers.add(molecule);
			status = TautomerConst.STATUS_FINISHED;
			return (resultTautomers);
		}

		// Rule selection (original extended list is kept in
		// extendedRuleInstances0)
		extendedRuleInstances = ruleSelector.selectRules(this,
				extendedRuleInstances);

		// This is the combinatorial algorithm when rule number limit is
		// reached.
		// This is needed because the incremental algorithms 'overcomes' the
		// selection by finding
		// and reusing again the excluded rules by the ruleSelector
		if (FlagSwitchToCombinatorialOnReachingRuleLimit)
			if (ruleSelector.switchToCombinatorial()) {
				if (FlagStopGenerationOnReachingRuleSelectorLimit) {
					status = TautomerConst.STATUS_STOPPED;
					return (resultTautomers);
				}
				return switchToCombinatorial();
			}

		// The incremental approach is performed here
		RuleManager rman = new RuleManager(this);
		rman.firstIncrementalStep();
		rman.iterateIncrementalSteps();

		if (FlagRecurseBackResultTautomers) {
			// Apply special filter before recursing back the result tautomers
			List<IAtomContainer> filtered = preRecursionFiltration(resultTautomers);
			List<IAtomContainer> res = generateTautomersFromMultipleTargets(filtered);
			resultTautomers = res;
		} else
			resultTautomers = tautomerFilter.filter(resultTautomers);

		if (FlagCalculateCACTVSEnergyRank)
			calcCACTVSEnergyRanks(resultTautomers);

		status = TautomerConst.STATUS_FINISHED;
		return (resultTautomers);
	}

	List<IAtomContainer> switchToCombinatorial() throws Exception {
		logger.fine("******* Switching from Incremental to combinatorial!!");
		numOfRegistrations = 0;

		// Generating sub combinations
		subCombinationsRI = generateSubCombinations();

		// iterating all sub combinations
		for (List<IRuleInstance> subComb : subCombinationsRI) {
			// initialize molecule
			restoreMolecule(molecule, originalMolecule);
			ruleInstances = subComb;
			generateRuleInstanceCombinations();
		}

		/*
		 * //pure combinatorial approach
		 * ruleInstances.addAll(extendedRuleInstances);
		 * generateRuleInstanceCombinations();
		 */

		resultTautomers = tautomerFilter.filter(resultTautomers);

		if (FlagCalculateCACTVSEnergyRank)
			calcCACTVSEnergyRanks(resultTautomers);

		status = TautomerConst.STATUS_FINISHED;
		return (resultTautomers);
	}

	/*
	 * //Combined approach (00, 01, 02) - not implemented! public
	 * List<IAtomContainer> generateTautomersCombinedApproach() throws Exception
	 * { resultTautomers = new ArrayList<IAtomContainer>();
	 * resultTatomerStringCodes.clear(); //... return(resultTautomers);
	 * 
	 * }
	 */

	public void registerBestRankTautomer(IAtomContainer newTautomer)
			throws Exception {
		double rank = 999999.0;
		switch (canonicTautomerMethod) {
		case ENERGY_RANK_INCHI_KEY:
			Double rankProp = RuleManager.calculateRank(curIncrStep,
					newTautomer, this);
			if (rankProp != null) {
				rank = rankProp;
				newTautomer.setProperty(TautomerConst.TAUTOMER_RANK, rankProp);
			}
			break;

		case CACTVS_RANK_INCHI_KEY:
			rank = CACTVSRanking.getEnergyRank(newTautomer);
			break;
		}

		// System.out.println("rank " + rank);

		if (resultTautomers.isEmpty()) {
			// this is the first tautomer hence it is considered as best rank
			bestRank = rank;
			resultTautomers.add(newTautomer);
			return;
		}

		if (rank == bestRank) {
			// the current rank is equal to the best rank
			resultTautomers.add(newTautomer);
			return;
		}

		if (rank < bestRank) {
			// new best rank value is found
			resultTautomers.clear();
			resultTautomers.add(newTautomer);
			bestRank = rank;
			return;
		}

		// if rank > bestRank nothing is done!
	}

	public boolean registerTautomer(IAtomContainer newTautomer)
			throws Exception {
		numOfRegistrations++;

		if (FlagRegisterOnlyBestRankTautomers) {
			if (tautomerFilter.checkMolecule(newTautomer)) {
				processTautomer(newTautomer, originalMolecule,
						FlagSetStereoElementsOnTautomerProcess, FlagGenerateStereoBasedOn2D, 
						FlagAddImplicitHAtomsOnTautomerProcess);
				registerBestRankTautomer(newTautomer);
			}
			return false; // In this case false is return
		}

		if (FlagCheckDuplicationOnRegistering) {
			// FlagTreatAromaticBondsAsEquivalent should always be false here
			// since aromaticity is not correct during the tautomer generation

			String newCode = TautomerStringCode.getCode(newTautomer, false,
					codeStrBondSequence);

			// System.out.println("---> " + newCode);

			if (!resultTatomerStringCodes.contains(newCode)) {
				resultTatomerStringCodes.add(newCode);
				resultTautomers.add(newTautomer);
				processTautomer(newTautomer, originalMolecule,
						FlagSetStereoElementsOnTautomerProcess, FlagGenerateStereoBasedOn2D,
						FlagAddImplicitHAtomsOnTautomerProcess);
				return true;
			} else
				return false;
		} else {
			resultTautomers.add(newTautomer);
			processTautomer(newTautomer, originalMolecule, FlagSetStereoElementsOnTautomerProcess, 
						FlagGenerateStereoBasedOn2D, FlagAddImplicitHAtomsOnTautomerProcess);
			return true;
		}
	}

	List<IAtomContainer> preRecursionFiltration(List<IAtomContainer> tautomers)
			throws Exception {
		// Save original filtration flags
		boolean F_ApplyDuplicationCheckInChI = tautomerFilter.FlagApplyDuplicationCheckInChI;
		boolean F_ApplyDuplicationCheckIsomorphism = tautomerFilter.FlagApplyDuplicationCheckIsomorphism;
		boolean F_ApplyDuplicationFilter = tautomerFilter.FlagApplyDuplicationFilter;
		boolean F_ApplyExcludeFilter = tautomerFilter.FlagApplyExcludeFilter;
		boolean F_ApplyWarningFilter = tautomerFilter.FlagApplyWarningFilter;
		boolean F_ExcludeWarningTautomers = tautomerFilter.FlagExcludeWarningTautomers;
		boolean F_FilterIncorrectValencySumStructures = tautomerFilter.FlagFilterIncorrectValencySumStructures;

		// Setting the flags for filtration
		tautomerFilter.FlagApplyDuplicationCheckInChI = false;
		tautomerFilter.FlagApplyDuplicationCheckIsomorphism = true;
		tautomerFilter.FlagApplyDuplicationFilter = true;
		tautomerFilter.FlagApplyExcludeFilter = true;
		tautomerFilter.FlagApplyWarningFilter = true;
		tautomerFilter.FlagExcludeWarningTautomers = true;
		tautomerFilter.FlagFilterIncorrectValencySumStructures = true;

		// Performing filtration
		List<IAtomContainer> res = tautomerFilter.filter(tautomers);

		// Restore original filtration flags
		tautomerFilter.FlagApplyDuplicationCheckInChI = F_ApplyDuplicationCheckInChI;
		tautomerFilter.FlagApplyDuplicationCheckIsomorphism = F_ApplyDuplicationCheckIsomorphism;
		tautomerFilter.FlagApplyDuplicationFilter = F_ApplyDuplicationFilter;
		tautomerFilter.FlagApplyExcludeFilter = F_ApplyExcludeFilter;
		tautomerFilter.FlagApplyWarningFilter = F_ApplyWarningFilter;
		tautomerFilter.FlagExcludeWarningTautomers = F_ExcludeWarningTautomers;
		tautomerFilter.FlagFilterIncorrectValencySumStructures = F_FilterIncorrectValencySumStructures;

		return res;
	}

	List<IAtomContainer> generateTautomersFromMultipleTargets(
			List<IAtomContainer> targets) throws Exception {
		IAtomContainer tmpOrgMolecule = originalMolecule;
		IAtomContainer tmpMolecule = molecule;

		List<IAtomContainer> summarizedResult = new ArrayList<IAtomContainer>();

		for (int i = 0; i < targets.size(); i++) {
			setStructure(targets.get(i));
			resultTautomers = new ArrayList<IAtomContainer>();
			searchAllRulePositions();

			if (extendedRuleInstances.isEmpty()) {
				summarizedResult.add(molecule);
				continue;
			}

			RuleManager rman = new RuleManager(this);
			rman.firstIncrementalStep();
			rman.iterateIncrementalSteps();

			summarizedResult.addAll(resultTautomers);
		}

		// Restore the original molecule variables
		originalMolecule = tmpOrgMolecule;
		molecule = tmpMolecule;

		return tautomerFilter.filter(summarizedResult);
	}

	void searchAllRulePositions() throws Exception {
		generatedRules.clear();
		extendedRuleInstances.clear();
		ruleInstances.clear();

		for (int i = 0; i < knowledgeBase.rules.size(); i++)
			if (knowledgeBase.rules.get(i).isRuleActive) {
				try {
					List<IRuleInstance> instances = knowledgeBase.rules.get(i)
							.applyRule(molecule);
					if ((instances != null) && (instances.size() > 0))
						extendedRuleInstances.addAll(instances);
				} catch (Exception x) {
					logger.log(Level.WARNING, knowledgeBase.rules.get(i).name,
							x);
				}
			}

		extendedRuleInstances0 = extendedRuleInstances;
	}

	/*
	 * //This function is applied for approach 00 void
	 * handleOverlapedInstances() { //Nothing special is done currently //Just
	 * all initial rules are added. ruleInstances.addAll(extendedRuleInstances);
	 * 
	 * //RuleManager rman = new RuleManager(this);
	 * //rman.handleOverlappingRuleInstances(); }
	 */

	// This function is applied for approach 00 and as helper for approach 01
	void generateRuleInstanceCombinations() throws Exception {
		for (int i = 0; i < ruleInstances.size(); i++)
			ruleInstances.get(i).firstState();

		int n;
		int instNumber;

		do {

			if (numOfRegistrations > maxNumOfTautomerRegistrations)
				break;

			registerTautomer00();

			n = ruleInstances.get(0).nextState();
			instNumber = 0;

			while (n == 0) {
				instNumber++;
				if (instNumber == ruleInstances.size())
					break;
				n = ruleInstances.get(instNumber).nextState();
			}

		} while (instNumber < ruleInstances.size());

	}

	// This function is applied for approach 00 and 01
	void registerTautomer00() throws Exception {
		// TODO why clone?
		IAtomContainer newTautomer = molecule.clone();

		if (FlagRegisterOnlyBestRankTautomers) {
			processTautomer(newTautomer, originalMolecule, FlagSetStereoElementsOnTautomerProcess, FlagGenerateStereoBasedOn2D, FlagAddImplicitHAtomsOnTautomerProcess);
			registerBestRankTautomer(newTautomer);
			return;
		}

		// TODO the may be needed here?? processTautomer(newTautomer);
		resultTautomers.add(newTautomer);
		numOfRegistrations++;

		// if (numOfRegistrations%100 == 0) System.out.println("  "+
		// numOfRegistrations + " registered tautomers");

		// System.out.print("  tautomer: " + getTautomerCombination() + "    " +
		// SmartsHelper.moleculeToSMILES(molecule));

		// Print H Atoms info
		// for (int i = 0; i < molecule.getAtomCount(); i++)
		// System.out.print(" " +
		// molecule.getAtom(i).getImplicitHydrogenCount());
		// System.out.println();
	}

	List<List<IRuleInstance>> generateSubCombinations() {
		// Determination of groups (clusters) of overlapping rule instances
		List<List<IRuleInstance>> riGroups = new ArrayList<List<IRuleInstance>>();
		for (IRuleInstance ri : extendedRuleInstances) {
			boolean FlagRIOverlaps = false;
			for (List<IRuleInstance> group : riGroups) {
				if (RuleManager.overlaps((RuleInstance) ri, group)) {
					group.add(ri);
					FlagRIOverlaps = true;
					break;
				}
			}

			if (!FlagRIOverlaps) {
				// a new group is created
				List<IRuleInstance> group = new ArrayList<IRuleInstance>();
				group.add(ri);
				riGroups.add(group);
			}
		}

		// The groups are sorted
		List<IRuleInstance> defaultGroup = new ArrayList<IRuleInstance>();
		List<List<IRuleInstance>> bigGroups = new ArrayList<List<IRuleInstance>>();

		for (List<IRuleInstance> group : riGroups) {
			// printRIGroup(group,"group"); System.out.println();
			if (group.size() == 1)
				defaultGroup.add(group.get(0));
			else
				bigGroups.add(group);
		}

		// helper array contains the positions in each group
		int gpos[] = new int[bigGroups.size()];
		int gmax[] = new int[bigGroups.size()];
		long numOfSubCombinations = 1;

		// Initialization of the positions for each bigGroup
		// plus calculation of the total number of the number of combinations
		for (int i = 0; i < gpos.length; i++) {
			gpos[i] = 0;
			gmax[i] = bigGroups.get(i).size();
			numOfSubCombinations = numOfSubCombinations * gmax[i];
		}

		logger.fine("numOfSubCombinations = " + numOfSubCombinations);

		// Generation of all sub-combinations from clusters
		List<List<IRuleInstance>> subCombs = new ArrayList<List<IRuleInstance>>();

		long curSComb = 0;
		while ((curSComb < numOfSubCombinations)
				& (curSComb < maxNumOfSubCombinations)) {
			// Create a combination
			List<IRuleInstance> subCombination = new ArrayList<IRuleInstance>();
			subCombination.addAll(defaultGroup);
			for (int i = 0; i < gpos.length; i++)
				subCombination.add(bigGroups.get(i).get(gpos[i]));

			subCombs.add(subCombination);

			// iterate to next combination
			for (int i = 0; i < gpos.length; i++) {
				gpos[i]++;
				if (gpos[i] < gmax[i])
					break; // algorithm will not go to the next group
				else
					gpos[i] = 0; // group position is set to zero and next group
									// position is iterated
			}
			curSComb++;
		}

		return subCombs;
	}

	// small helper
	void printRIGroup(List<IRuleInstance> group, String info) {
		logger.fine(info);
		for (IRuleInstance ri : group)
			logger.finest(((RuleInstance) ri).debugInfo(molecule));
	}

	String getTautomerCombination() {
		StringBuffer sb = new StringBuffer();
		for (int i = ruleInstances.size() - 1; i >= 0; i--)
			sb.append("" + ruleInstances.get(i).getCurrentState() + " ");
		return (sb.toString());
	}

	/**
	 * TODO: Rewrite with logger and proper logger levels
	 */
	public void printDebugInfo() {
		if (FlagPrintTargetMoleculeInfo) {
			logger.log(Level.FINE, "Debug info - Target Atom atributes:");
			String s = SmartsHelper.getAtomsAttributes(molecule);
			logger.log(Level.FINE, s);
			logger.log(Level.FINE, "Debug info - Target Bond atributes:");
			String s2 = SmartsHelper.getBondAttributes(molecule);
			logger.log(Level.FINE, s2);
		}

		if (FlagPrintExtendedRuleInstances) {
			logger.log(Level.FINE, "Debug info - extendedRuleInstances:");
			for (int i = 0; i < extendedRuleInstances.size(); i++)
				logger.log(Level.FINE, ((RuleInstance) extendedRuleInstances
						.get(i)).debugInfo(molecule));
			if (extendedRuleInstances.isEmpty())
				logger.log(Level.FINE, "  NONE");
		}

	}

	public IAtomContainer getCanonicTautomer0(List<IAtomContainer> tautomers) {
		if (tautomers.size() == 1)
			return tautomers.get(0);

		IAtomContainer can_t = tautomers.get(0);
		double rank = ((Double) can_t.getProperty(TautomerConst.TAUTOMER_RANK))
				.doubleValue();

		for (IAtomContainer t : tautomers) {
			double newRank = ((Double) t
					.getProperty(TautomerConst.TAUTOMER_RANK)).doubleValue();
			if (newRank < rank) {
				rank = newRank;
				can_t = t;
			}
		}

		return can_t;
	}

	public IAtomContainer getCanonicTautomer(List<IAtomContainer> tautomers) {
		CanonicTautomer canTautFinder = new CanonicTautomer(this);
		try {
			return canTautFinder.getCanonicTautomer(tautomers);
		} catch (Exception e) {
		}

		return null;
	}

	void restoreMolecule(IAtomContainer mol, IAtomContainer origMol) {
		// restoring bond orders
		for (int i = 0; i < mol.getBondCount(); i++) {
			IBond b = mol.getBond(i);
			b.setOrder(origMol.getBond(i).getOrder());
		}

		// restoring implicit H atoms
		for (int i = 0; i < mol.getAtomCount(); i++) {
			int nH = origMol.getAtom(i).getImplicitHydrogenCount();
			mol.getAtom(i).setImplicitHydrogenCount(nH);
		}

	}

	public int getInitialRuleCount() {
		return extendedRuleInstances0.size();
	}

	public int getStatus() {
		return status;
	}

	public static void calcCACTVSEnergyRanks(List<IAtomContainer> tautomers) {
		for (IAtomContainer mol : tautomers) {
			double rank = CACTVSRanking.getEnergyRank(mol);
			mol.setProperty(TautomerConst.CACTVS_ENERGY_RANK, new Double(rank));
		}
	}
	
	
	public static void processTautomer(IAtomContainer tautomer, IAtomContainer originalMol, 
					boolean FlagSetStereo, 
					boolean FlagGenerateStereoFrom2D,
					boolean FlagAddImplicitHAtoms) throws Exception 
	{
		if (tautomer == null)
			return;

		// Clear aromaticity flags
		for (int i = 0; i < tautomer.getAtomCount(); i++)
			tautomer.getAtom(i).setFlag(CDKConstants.ISAROMATIC, false);
		for (int i = 0; i < tautomer.getBondCount(); i++)
			tautomer.getBond(i).setFlag(CDKConstants.ISAROMATIC, false);

		// AtomContainerManipulator.clearAtomConfigurations(ac);
		for (IAtom atom : tautomer.atoms()) {
			atom.setHybridization((IAtomType.Hybridization) CDKConstants.UNSET);
		}

		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(tautomer);
		
		if (FlagAddImplicitHAtoms)
		{	
			CDKHydrogenAdder adder = CDKHydrogenAdder
					.getInstance(SilentChemObjectBuilder.getInstance());
			adder.addImplicitHydrogens(tautomer);
		}	
		// AtomContainerManipulator.convertImplicitToExplicitHydrogens(ac);

		CDKHueckelAromaticityDetector.detectAromaticity(tautomer);
		
		if (FlagSetStereo)
		{	
			//empty the stereo info which came from the IAtomContainer.clone() function
			tautomer.setStereoElements(new ArrayList<IStereoElement>()); 
			StereoChemUtils.cloneAndCheckStereo(tautomer, originalMol);
		}	
		
		//System.out.println(StereoChemUtils.getStereoElementsStatus(tautomer));
		
		if (FlagGenerateStereoFrom2D)
		{	
			try {
				StereoElementFactory stereo = StereoElementFactory
						.using2DCoordinates(tautomer);
				tautomer.setStereoElements(stereo.createAll());
			} catch (Exception x) {
				tautomer.setProperty("ERROR.tautomers.stereo", x.getMessage());
				logger.log(Level.WARNING, x.getMessage());
			}
		}	
	}

	public EnergyRanking getEnergyRanking() throws Exception {
		if (energyRanking == null)
			energyRanking = new EnergyRanking();
		return energyRanking;
	}

	public void setEnergyRanking(EnergyRanking energyRanking) {
		this.energyRanking = energyRanking;
	}

	public CanonicTautomerMethod getCanonicTautomerMethod() {
		return canonicTautomerMethod;
	}

	public void setCanonicTautomerMethod(
			CanonicTautomerMethod canonicTautomerMethod) {
		this.canonicTautomerMethod = canonicTautomerMethod;
	}

	public void setCurIncrStep(TautomerIncrementStep cis) {
		curIncrStep = cis;
	}

	public int[] getCodeStrBondSequence() {
		return codeStrBondSequence;
	}

}
