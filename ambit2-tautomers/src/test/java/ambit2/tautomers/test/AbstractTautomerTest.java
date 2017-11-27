package ambit2.tautomers.test;

import ambit2.tautomers.TautomerConst.CanonicTautomerMethod;
import ambit2.tautomers.TautomerManager;

public class AbstractTautomerTest {

	TautomerManager tman;
	boolean FlagPrintTautomers = false;

	public AbstractTautomerTest() {

		// Initialization
		tman = getTautomerManager();
	}

	protected TautomerManager getTautomerManager() {
		return getDefaultTautomerManager();		
	}
	
	public static TautomerManager getDefaultTautomerManager() {
		TautomerManager tman = new TautomerManager();

		tman.getKnowledgeBase().activateChlorineRules(false);
		tman.getKnowledgeBase().activateRingChainRules(false);
		tman.getKnowledgeBase().use15ShiftRules(true);
		tman.getKnowledgeBase().use17ShiftRules(false);

		tman.FlagCheckNumOfRegistrationsForIncrementalAlgorithm = false;
		tman.maxNumOfBackTracks = 100000;
		tman.FlagProcessRemainingStackIncSteps = true;
		tman.FlagCalculateCACTVSEnergyRank = false;
		tman.FlagRegisterOnlyBestRankTautomers = false;
		tman.FlagAddImplicitHAtomsOnTautomerProcess = false; // default value is
																// true but
																// probably it
																// is not
																// needed.
		tman.setCanonicTautomerMethod(CanonicTautomerMethod.CACTVS_RANK_INCHI_KEY);

		tman.tautomerFilter.setFlagApplyWarningFilter(true);
		tman.tautomerFilter.setFlagApplyExcludeFilter(true);
		tman.tautomerFilter.setFlagApplyDuplicationFilter(true);
		tman.tautomerFilter.setFlagFilterIncorrectValencySumStructures(true);
		tman.tautomerFilter.FlagApplySimpleAromaticityRankCorrection = true;

		// The filtration is based on isomorphism check
		// Structures with equvalent atoms are excluded but different resonance
		// aromatic forms are preserved
		tman.tautomerFilter.setFlagApplyDuplicationCheckIsomorphism(true);
		tman.tautomerFilter.setFlagApplyDuplicationCheckInChI(false);
		return tman;
	}
}
