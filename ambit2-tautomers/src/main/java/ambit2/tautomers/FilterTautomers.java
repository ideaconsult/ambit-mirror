package ambit2.tautomers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.jniinchi.INCHI_OPTION;
import net.sf.jniinchi.INCHI_RET;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.inchi.InChIGenerator;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.base.data.Property;
import ambit2.core.data.MoleculeTools;
import ambit2.smarts.IsomorphismTester;
import ambit2.smarts.SmartsHelper;
import ambit2.smarts.SmartsParser;
import ambit2.tautomers.ranking.TautomerStringCode;

public class FilterTautomers {
	private List<INCHI_OPTION> options = new ArrayList<INCHI_OPTION>();

	public List<INCHI_OPTION> getInchiOptions() {
		return options;
	}

	public void setInchiOptions(List<INCHI_OPTION> options) {
		this.options = options;
	}

	public FilterTautomers(TautomerManager m) {
		tman = m;
		options.add(INCHI_OPTION.FixedH);
		options.add(INCHI_OPTION.SAbs);
		options.add(INCHI_OPTION.SAsXYZ);
		options.add(INCHI_OPTION.SPXYZ);
		options.add(INCHI_OPTION.FixSp3Bug);
		options.add(INCHI_OPTION.AuxNone);

	}

	protected boolean FlagApplyWarningFilter = true;

	public boolean isFlagApplyWarningFilter() {
		return FlagApplyWarningFilter;
	}

	public void setFlagApplyWarningFilter(boolean flagApplyWarningFilter) {
		FlagApplyWarningFilter = flagApplyWarningFilter;
	}

	protected boolean FlagApplyExcludeFilter = true;

	public boolean isFlagApplyExcludeFilter() {
		return FlagApplyExcludeFilter;
	}

	public void setFlagApplyExcludeFilter(boolean flagApplyExcludeFilter) {
		FlagApplyExcludeFilter = flagApplyExcludeFilter;
	}

	protected boolean FlagApplyDuplicationFilter = true;

	public boolean isFlagApplyDuplicationFilter() {
		return FlagApplyDuplicationFilter;
	}

	public void setFlagApplyDuplicationFilter(boolean flagApplyDuplicationFilter) {
		FlagApplyDuplicationFilter = flagApplyDuplicationFilter;
	}

	protected boolean FlagTreatAromaticBondsAsEquivalentFilter = false;
	protected boolean FlagApplyDuplicationCheckIsomorphism = true;

	public boolean isFlagApplyDuplicationCheckIsomorphism() {
		return FlagApplyDuplicationCheckIsomorphism;
	}

	public void setFlagApplyDuplicationCheckIsomorphism(
			boolean flagApplyDuplicationCheckIsomorphism) {
		FlagApplyDuplicationCheckIsomorphism = flagApplyDuplicationCheckIsomorphism;
	}

	public boolean FlagApplySimpleAromaticityRankCorrection = true;

	// This duplication check is suitable for any case
	// since equal InChI keys are given to the resonance Kekeule stuctures
	// By default this check is switched off
	protected boolean FlagApplyDuplicationCheckInChI = false;

	public boolean isFlagApplyDuplicationCheckInChI() {
		return FlagApplyDuplicationCheckInChI;
	}

	public void setFlagApplyDuplicationCheckInChI(
			boolean flagApplyDuplicationCheckInChI) {
		FlagApplyDuplicationCheckInChI = flagApplyDuplicationCheckInChI;
	}

	// protected boolean FlagINCHI_OPTION_FixedH = true;
	protected boolean FlagFilterIncorrectValencySumStructures = true;

	public boolean isFlagFilterIncorrectValencySumStructures() {
		return FlagFilterIncorrectValencySumStructures;
	}

	public void setFlagFilterIncorrectValencySumStructures(
			boolean flagFilterIncorrectValencySumStructures) {
		FlagFilterIncorrectValencySumStructures = flagFilterIncorrectValencySumStructures;
	}

	protected boolean FlagStoreRemovedByFilterTautomers = false; // by default
																	// it is not
																	// needed

	public boolean isFlagStoreRemovedByFilterTautomers() {
		return FlagStoreRemovedByFilterTautomers;
	}

	public void setFlagStoreRemovedByFilterTautomers(
			boolean flagStoreRemovedByFilterTautomers) {
		FlagStoreRemovedByFilterTautomers = flagStoreRemovedByFilterTautomers;
	}
	
	protected boolean FlagFilterIncorrectHAtomsStructures = false;

	public boolean isFlagFilterIncorrectHAtomsStructures() {
		return FlagFilterIncorrectHAtomsStructures;
	}

	public void setFlagFilterIncorrectHAtomsStructures(
			boolean flagFilterIncorrectHAtomsStructures) {
		FlagFilterIncorrectHAtomsStructures = flagFilterIncorrectHAtomsStructures;
	}


	public TautomerManager tman;
	public List<IAtomContainer> removedTautomers = new ArrayList<IAtomContainer>();
	public List<List<Integer>> warnFilters = new ArrayList<List<Integer>>(); // List<
																				// "<FilterNumber> <Number_of_positions> <Pos1> <Pos2> ..."
																				// x
																				// n
																				// >
	public List<List<Integer>> excludeFilters = new ArrayList<List<Integer>>();
	public List<List<Integer>> warnFiltersOriginalPos = new ArrayList<List<Integer>>();
	public List<List<Integer>> excludeFiltersOriginalPos = new ArrayList<List<Integer>>();

	private IsomorphismTester isoTester = new IsomorphismTester();

	protected boolean FlagExcludeWarningTautomers = true;

	public boolean isFlagExcludeWarningTautomers() {
		return FlagExcludeWarningTautomers;
	}

	public void setFlagExcludeWarningTautomers(
			boolean flagExcludeWarningTautomers) {
		FlagExcludeWarningTautomers = flagExcludeWarningTautomers;
	}

	public List<IAtomContainer> filter(List<IAtomContainer> tautomers)
			throws Exception {
		getOriginalPositions();
		// System.out.println("generated " + tautomers.size() + " strctures");
		if (FlagStoreRemovedByFilterTautomers) {
			removedTautomers.clear();
			warnFilters.clear();
			excludeFilters.clear();
		}

		List<IAtomContainer> filteredTautomers = new ArrayList<IAtomContainer>();
		List<IAtomContainer> uniqueTautomers;

		// Remove duplications based on double bond positions as expressed in
		// the tautomer string code
		if (FlagApplyDuplicationFilter) {
			if (tman.FlagCheckDuplicationOnRegistering)
				uniqueTautomers = tautomers; // duplication check is already
												// done on registration
			else
				uniqueTautomers = dubplicationFilter(tautomers);
		} else
			uniqueTautomers = tautomers;

		if (FlagFilterIncorrectValencySumStructures) {
			List<IAtomContainer> tempTautomers = filterIncorrectValencySumStructs(uniqueTautomers);
			uniqueTautomers = tempTautomers;
		}
		
		if (FlagFilterIncorrectHAtomsStructures) {
			List<IAtomContainer> tempTautomers =  filterIncorrectHAtomsStructs(uniqueTautomers);
			uniqueTautomers = tempTautomers;
		}
		
		/*
		 * //Pre-processing is done here for (int i = 0; i <
		 * uniqueTautomers.size(); i++) { try{
		 * //System.out.println("preprocess " + i);
		 * preProcessStructures(uniqueTautomers.get(i)); } catch(Exception e){
		 * throw e; //System.out.println(e.toString()); Please do not hide
		 * exceptions! } }
		 */

		// Filtration according to the filter rules
		for (int i = 0; i < uniqueTautomers.size(); i++) {
			// System.out.println("Tautomer #" + i + "  " +
			// SmartsHelper.moleculeToSMILES(uniqueTautomers.get(i)));

			List<Integer> vWarnF;
			if (FlagApplyWarningFilter)
				vWarnF = getWarnFilters(uniqueTautomers.get(i));
			else
				vWarnF = null;

			List<Integer> vExcludF;
			if (FlagApplyExcludeFilter)
				vExcludF = getExcludeFilters(uniqueTautomers.get(i));
			else
				vExcludF = null;

			if (vExcludF != null) {
				if (FlagStoreRemovedByFilterTautomers) {
					removedTautomers.add(uniqueTautomers.get(i));
					warnFilters.add(vWarnF);
					excludeFilters.add(vWarnF);
				}
				continue;
			}

			if ((vWarnF != null)) {
				if (FlagExcludeWarningTautomers) {
					if (FlagStoreRemovedByFilterTautomers) {
						removedTautomers.add(uniqueTautomers.get(i));
						warnFilters.add(vWarnF);
						excludeFilters.add(vWarnF);
					}
					continue;
				}
			}

			filteredTautomers.add(uniqueTautomers.get(i));
		}

		if (FlagTreatAromaticBondsAsEquivalentFilter) {
			// Filtration based on tautomer string code where aromatic bonds are
			// managed
			//
			// This filtration should not be applied before the allene atom
			// filters
			// since some side effect are observed

			// TODO
		}

		if (FlagApplyDuplicationCheckIsomorphism) {
			List<IAtomContainer> filteredTautomers2 = duplicationFilterBasedOnIsomorphism(filteredTautomers);
			filteredTautomers = filteredTautomers2;
		}

		// Remove duplications based on InChI representation
		// Generally this option is switched-off (false) since InChI based
		// filtering removes
		// leaves only one resonance aromatic structure.
		if (FlagApplyDuplicationCheckInChI) {
			try {
				List<IAtomContainer> filteredTautomers2 = duplicationFilterBasedOnInChI(filteredTautomers);
				filteredTautomers = filteredTautomers2;
			} catch (Exception e) {
				throw e;
				// System.out.println(e.toString());
			}
		}

		return filteredTautomers;
	}

	List<IAtomContainer> dubplicationFilter(List<IAtomContainer> tautomers) {
		List<IAtomContainer> uniqueTautomers0 = new ArrayList<IAtomContainer>();
		Set<String> tCodes = new TreeSet<String>();

		for (int i = 0; i < tautomers.size(); i++) {
			String tcode = TautomerStringCode.getCode(tautomers.get(i), false,
					tman.getCodeStrBondSequence());
			if (!tCodes.contains(tcode)) {
				tCodes.add(tcode);
				uniqueTautomers0.add(tautomers.get(i));
			}
		}

		return uniqueTautomers0;
	}

	List<IAtomContainer> filterIncorrectValencySumStructs(
			List<IAtomContainer> tautomers) {
		int vsum = getValencySum(tman.originalMolecule);
		List<IAtomContainer> filtrated = new ArrayList<IAtomContainer>();

		for (int i = 0; i < tautomers.size(); i++) {
			int v = getValencySum(tautomers.get(i));
			if (v == vsum)
				filtrated.add(tautomers.get(i));
		}

		return (filtrated);
	}

	List<IAtomContainer> duplicationFilterBasedOnInChI(
			List<IAtomContainer> tautomers) throws Exception {
		InChIGeneratorFactory igf = InChIGeneratorFactory.getInstance();
		List<IAtomContainer> uniqueTautomers = new ArrayList<IAtomContainer>();
		Set<String> tKeys = new TreeSet<String>();

		for (int i = 0; i < tautomers.size(); i++) {
			// IAtomContainer clone =
			// getCloneForInChIGeneration(tautomers.get(i)); //the clone
			// contains explicit H atoms

			IAtomContainer clone = tautomers.get(i);

			InChIGenerator ig = igf.getInChIGenerator(clone, options);
			INCHI_RET returnCode = ig.getReturnStatus();
			if (INCHI_RET.ERROR == returnCode) {
				Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,
						ig.getMessage());
			}
			String inchiKey = ig.getInchiKey();

			if (!tKeys.contains(inchiKey)) {
				tKeys.add(inchiKey);
				tautomers.get(i).setProperty(Property.opentox_InChI,
						ig.getInchi());
				tautomers.get(i).setProperty(Property.opentox_InChIKey,
						ig.getInchiKey());
				uniqueTautomers.add(tautomers.get(i));
			}
		}
		return uniqueTautomers;
	}

	List<IAtomContainer> duplicationFilterBasedOnIsomorphism(
			List<IAtomContainer> tautomers) throws Exception {

		List<IAtomContainer> filtered = new ArrayList<IAtomContainer>();

		// preparing each tautomer molecule for sss seaching
		for (int i = 0; i < tautomers.size(); i++) {
			SmartsParser.prepareTargetForSMARTSSearch(false, false, false,
					false, false, false, tautomers.get(i));
		}

		for (int i = 0; i < tautomers.size(); i++) {
			if (!checkIsomorphismEquivalence(tautomers.get(i), filtered))
				filtered.add(tautomers.get(i));
		}

		return (filtered);
	}
	
	List<IAtomContainer> filterIncorrectHAtomsStructs(List<IAtomContainer> tautomers) 
	{
		List<IAtomContainer> okTautomers = new ArrayList<IAtomContainer>();
		for (int i = 0; i < tautomers.size(); i++) {
			IAtomContainer t = tautomers.get(i);
			if (checkHAtoms(t))
				okTautomers.add(t);
		}
		return okTautomers;
	}

	boolean checkHAtoms(IAtomContainer target)
	{
		for (IAtom a : target.atoms())
		{
			Integer hAtoms = a.getImplicitHydrogenCount();
			if (hAtoms != null)
				if (hAtoms < 0)
					return false;
		}

		return true;
	}


	boolean checkIsomorphismEquivalence(IAtomContainer target,
			List<IAtomContainer> structs) {
		if (structs.isEmpty())
			return (false);

		IQueryAtomContainer query = SmartsHelper.getQueryAtomContainer(target,
				false);
		isoTester.setQuery(query);

		for (int i = 0; i < structs.size(); i++) {
			if (isoTester.hasIsomorphism(structs.get(i)))
				return (true);
		}

		return false;
	}

	IAtomContainer getCloneForInChIGeneration(IAtomContainer ac)
			throws Exception {
		IAtomContainer clone = ac.clone();
		// the clone already has all atom types in place
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(clone);
		CDKHydrogenAdder adder = CDKHydrogenAdder
				.getInstance(SilentChemObjectBuilder.getInstance());
		adder.addImplicitHydrogens(clone);

		MoleculeTools.convertImplicitToExplicitHydrogens(clone);
		return clone;

	}

	public void getOriginalPositions() throws Exception {
		warnFiltersOriginalPos.clear();

		for (int i = 0; i < tman.knowledgeBase.warningFilters.size(); i++) {
			Filter f = tman.knowledgeBase.warningFilters.get(i);
			RuleStateFlags flags = f.fragmentFlags;
			SmartsParser.prepareTargetForSMARTSSearch(flags.mNeedNeighbourData,
					flags.mNeedValenceData, flags.mNeedRingData,
					flags.mNeedRingData2, flags.mNeedExplicitHData,
					flags.mNeedParentMoleculeData, tman.molecule);

			isoTester.setQuery(f.fragmentQuery);
			warnFiltersOriginalPos.add(isoTester
					.getIsomorphismPositions(tman.molecule));
		}

		excludeFiltersOriginalPos.clear();

		for (int i = 0; i < tman.knowledgeBase.excludeFilters.size(); i++) {
			Filter f = tman.knowledgeBase.excludeFilters.get(i);
			RuleStateFlags flags = f.fragmentFlags;
			SmartsParser.prepareTargetForSMARTSSearch(flags.mNeedNeighbourData,
					flags.mNeedValenceData, flags.mNeedRingData,
					flags.mNeedRingData2, flags.mNeedExplicitHData,
					flags.mNeedParentMoleculeData, tman.molecule);

			isoTester.setQuery(f.fragmentQuery);
			excludeFiltersOriginalPos.add(isoTester
					.getIsomorphismPositions(tman.molecule));
		}
	}

	public List<Integer> getWarnFilters(IAtomContainer tautomer)
			throws Exception {
		List<Integer> v = new ArrayList<Integer>();
		for (int i = 0; i < tman.knowledgeBase.warningFilters.size(); i++) {
			Filter f = tman.knowledgeBase.warningFilters.get(i);
			RuleStateFlags flags = f.fragmentFlags;
			SmartsParser.prepareTargetForSMARTSSearch(flags.mNeedNeighbourData,
					flags.mNeedValenceData, flags.mNeedRingData,
					flags.mNeedRingData2, flags.mNeedExplicitHData,
					flags.mNeedParentMoleculeData, tautomer);

			isoTester.setQuery(f.fragmentQuery);
			List<Integer> pos = isoTester.getIsomorphismPositions(tautomer);
			List<Integer> orgPos = warnFiltersOriginalPos.get(i);
			List<Integer> notOriginalPos = new ArrayList<Integer>();

			for (int k = 0; k < pos.size(); k++) {
				Integer IOk = pos.get(k);
				boolean FlagOrgPos = false;
				for (int j = 0; j < orgPos.size(); j++) {
					if (IOk.intValue() == orgPos.get(j).intValue()) {
						FlagOrgPos = true;
						break;
					}
				}

				if (!FlagOrgPos)
					notOriginalPos.add(IOk);
			}

			// Format: <FilterNumber> <Number_of_positions> <Pos1> <Pos2> ...
			if (!notOriginalPos.isEmpty()) {
				v.add(new Integer(i));
				v.add(new Integer(notOriginalPos.size()));
				for (int k = 0; k < notOriginalPos.size(); k++)
					v.add(notOriginalPos.get(k));
			}
		}

		if (v.isEmpty())
			return null;
		else
			return (v);
	}

	public List<Integer> getExcludeFilters(IAtomContainer tautomer)
			throws Exception {
		// System.out.print("tautomer target: " +
		// SmartsHelper.moleculeToSMILES(tautomer));

		List<Integer> v = new ArrayList<Integer>();
		for (int i = 0; i < tman.knowledgeBase.excludeFilters.size(); i++) {
			Filter f = tman.knowledgeBase.excludeFilters.get(i);
			RuleStateFlags flags = f.fragmentFlags;

			SmartsParser.prepareTargetForSMARTSSearch(flags.mNeedNeighbourData,
					flags.mNeedValenceData, flags.mNeedRingData,
					flags.mNeedRingData2, flags.mNeedExplicitHData,
					flags.mNeedParentMoleculeData, tautomer);

			isoTester.setQuery(f.fragmentQuery);
			List<Integer> pos = isoTester.getIsomorphismPositions(tautomer);

			List<Integer> orgPos = excludeFiltersOriginalPos.get(i);
			List<Integer> notOriginalPos = new ArrayList<Integer>();
			// System.out.println("filter: " + f.fragmentSmarts + "  " +
			// pos.size());

			for (int k = 0; k < pos.size(); k++) {
				Integer IOk = pos.get(k);
				boolean FlagOrgPos = false;
				for (int j = 0; j < orgPos.size(); j++) {
					if (IOk.intValue() == orgPos.get(j).intValue()) {
						FlagOrgPos = true;
						break;
					}
				}

				if (!FlagOrgPos)
					notOriginalPos.add(IOk);
			}

			// Format: <FilterNumber> <Number_of_positions> <Pos1> <Pos2> ...
			if (!notOriginalPos.isEmpty()) {
				v.add(new Integer(i));
				v.add(new Integer(notOriginalPos.size()));
				for (int k = 0; k < notOriginalPos.size(); k++)
					v.add(notOriginalPos.get(k));
			}
		}

		if (v.isEmpty())
			return null;
		else
			return (v);
	}

	void clearAromaticityFlags(IAtomContainer ac) {
		for (int i = 0; i < ac.getAtomCount(); i++) {
			ac.getAtom(i).setFlag(CDKConstants.ISAROMATIC, false);
		}

		for (int i = 0; i < ac.getBondCount(); i++) {
			ac.getBond(i).setFlag(CDKConstants.ISAROMATIC, false);
		}

	}

	/*
	 * void preProcessStructures(IAtomContainer ac) throws Exception { if (ac ==
	 * null) return;
	 * 
	 * clearAromaticityFlags(ac);
	 * //AtomContainerManipulator.clearAtomConfigurations(ac); for (IAtom atom :
	 * ac.atoms()) { atom.setHybridization((IAtomType.Hybridization)
	 * CDKConstants.UNSET);
	 * 
	 * }
	 * 
	 * AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(ac);
	 * CDKHydrogenAdder adder =
	 * CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance());
	 * adder.addImplicitHydrogens(ac);
	 * //AtomContainerManipulator.convertImplicitToExplicitHydrogens(ac);
	 * 
	 * CDKHueckelAromaticityDetector.detectAromaticity(ac);
	 * 
	 * if (FlagApplySimpleAromaticityRankCorrection) { //Fix rank according to
	 * the aromaticity info double aromRank =
	 * RuleManager.getAdditionalAromaticityRank(ac); if (aromRank != 0.0) {
	 * Double rank = (Double)ac.getProperty("TAUTOMER_RANK"); if (rank == null)
	 * return; double newRank = rank.doubleValue(); newRank += aromRank;
	 * ac.setProperty("TAUTOMER_RANK", new Double(newRank)); } }
	 * 
	 * }
	 */

	public static int getValencySum(IAtomContainer ac) {
		int sum = 0;
		for (int i = 0; i < ac.getBondCount(); i++)
			sum += SmartsHelper.bondOrderToIntValue(ac.getBond(i));

		return sum;
	}

	public boolean checkMolecule(IAtomContainer mol) throws Exception {
		if (FlagFilterIncorrectValencySumStructures) {
			int vsum = getValencySum(mol);
			if (vsum != tman.originalValencySum)
				return false;
		}

		if (FlagApplyWarningFilter) {
			List<Integer> vWarnF;
			vWarnF = getWarnFilters(mol);
			if (vWarnF != null)
				return false;
		}

		if (FlagApplyExcludeFilter) {
			List<Integer> vExcludF = getExcludeFilters(mol);
			if (vExcludF != null)
				return false;
		}

		return true;
	}

}
