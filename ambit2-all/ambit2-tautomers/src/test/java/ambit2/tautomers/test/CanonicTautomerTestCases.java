package ambit2.tautomers.test;

import java.util.List;

import org.openscience.cdk.inchi.InChIGenerator;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import ambit2.smarts.SmartsHelper;
import ambit2.tautomers.TautomerConst;
import ambit2.tautomers.TautomerManager;
import ambit2.tautomers.TautomerConst.CanonicTautomerMethod;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class CanonicTautomerTestCases extends TestCase 
{
	InChIGeneratorFactory igf;	
	TautomerManager tman;
	boolean FlagPrintTautomers = false;
	public ILoggingTool logger;
	
	public CanonicTautomerTestCases() throws Exception
	{
		logger = LoggingToolFactory.createLoggingTool(CanonicTautomerTestCases.class);
		igf = InChIGeneratorFactory.getInstance();

		// Initialization
		tman = new TautomerManager();
		
		tman.getKnowledgeBase().activateChlorineRules(false);
		tman.getKnowledgeBase().activateRingChainRules(false);
		tman.getKnowledgeBase().use15ShiftRules(true);
		tman.getKnowledgeBase().use17ShiftRules(false);
		
		tman.FlagCheckNumOfRegistrationsForIncrementalAlgorithm = false;
		tman.maxNumOfBackTracks = 100000;
		tman.FlagProcessRemainingStackIncSteps = true;
		tman.FlagCalculateCACTVSEnergyRank = true;
		tman.FlagRegisterOnlyBestRankTautomers = false;
		tman.FlagAddImplicitHAtomsOnTautomerProcess = false; //default value is true but probably it is not needed. 
		tman.setCanonicTautomerMethod(CanonicTautomerMethod.CACTVS_RANK_INCHI_KEY);
		//tman.setCanonicTautomerMethod(CanonicTautomerMethod.ENERGY_RANK_INCHI_KEY);
		
		tman.tautomerFilter.setFlagApplyWarningFilter(true);
		tman.tautomerFilter.setFlagApplyExcludeFilter(true);
		tman.tautomerFilter.setFlagApplyDuplicationFilter(true);
		tman.tautomerFilter.setFlagFilterIncorrectValencySumStructures(true);
		tman.tautomerFilter.FlagApplySimpleAromaticityRankCorrection = true;
		
		//The filtration is based on isomorphism check
		//Structures with equvalent atoms are excluded but different resonance aromatic forms are preserved
		tman.tautomerFilter.setFlagApplyDuplicationCheckIsomorphism(false);
		tman.tautomerFilter.setFlagApplyDuplicationCheckInChI(true);
	}
	
	public static Test suite() {
		return new TestSuite(CanonicTautomerTestCases.class);
	}
	
	int testCanonic(String targetSmi, String expectedCanTautSmi) throws Exception
	{
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(targetSmi);
		IAtomContainer expectedCanTaut = SmartsHelper.getMoleculeFromSmiles(expectedCanTautSmi);
		
		tman.FlagRegisterOnlyBestRankTautomers = false;
		tman.setStructure(mol);
		List<IAtomContainer> tautomers = tman.generateTautomersIncrementaly();
		IAtomContainer t1 = tman.getCanonicTautomer(tautomers);
		
		if(FlagPrintTautomers)
		{	
			InChIGenerator ig0 = igf.getInChIGenerator(expectedCanTaut, tman.tautomerFilter.getInchiOptions());
			String inchKey0 = ig0.getInchiKey();
			System.out.println("target = " + targetSmi);	
			System.out.println("eexpected can. tautomer inchi = " + inchKey0);
			for (int i = 0; i < tautomers.size(); i++)
			{	
				InChIGenerator ig = igf.getInChIGenerator(tautomers.get(i), tman.tautomerFilter.getInchiOptions());
				String inchKey = ig.getInchiKey();
				Double rank = tautomers.get(i).getProperty(TautomerConst.TAUTOMER_RANK);
				Double cactvs_rank = tautomers.get(i).getProperty(TautomerConst.CACTVS_ENERGY_RANK);
				System.out.println("   "+ SmartsHelper.moleculeToSMILES(tautomers.get(i),
								false) + "  " + rank + "  " + cactvs_rank+"  " + inchKey);
			}	
		}	
		
		tman.FlagRegisterOnlyBestRankTautomers = true;
		tman.setStructure(mol);  //This is needed again to reset the tautomer manager
		tautomers = tman.generateTautomersIncrementaly();
		IAtomContainer t2 = tman.getCanonicTautomer(tautomers);
		if(FlagPrintTautomers)
		{	
			System.out.println("best found");
			for (int i = 0; i < tautomers.size(); i++)
			{	
				InChIGenerator ig = igf.getInChIGenerator(tautomers.get(i), tman.tautomerFilter.getInchiOptions());
				String inchKey = ig.getInchiKey();
				Double rank = tautomers.get(i).getProperty(TautomerConst.TAUTOMER_RANK);
				Double cactvs_rank = tautomers.get(i).getProperty(TautomerConst.CACTVS_ENERGY_RANK);
				System.out.println("   "+ SmartsHelper.moleculeToSMILES(tautomers.get(i),
								false) + "  " + rank + "  " + cactvs_rank + "  " + inchKey);
			}
			System.out.println();
		}	
		
		
		
		
			
		//Checking canonical tauomers by inchi key
		InChIGenerator ig = igf.getInChIGenerator(expectedCanTaut, tman.tautomerFilter.getInchiOptions());
		String expInchiKey = ig.getInchiKey();
		
		ig = igf.getInChIGenerator(t1, tman.tautomerFilter.getInchiOptions());
		String key1 = ig.getInchiKey();
		
		ig = igf.getInChIGenerator(t2, tman.tautomerFilter.getInchiOptions());
		String key2 = ig.getInchiKey();
		
		assertEquals("Expected canonic tautomer (all registered): " +  expectedCanTautSmi, expInchiKey, key1);
		assertEquals("Expected canonic tautomer (best registered): " +  expectedCanTautSmi, expInchiKey, key2);
		
		return 0;
	}
	
	public void test_01() throws Exception {
		testCanonic("OC=CCC", "O=CCCC");
	}
	
	public void test_02() throws Exception {
		testCanonic("OC=CC=CN", "O=CCCC=N");
	}
	
	public void test_03() throws Exception {
		testCanonic("SC=C", "S=CC");
	}
	
	public void test_03B() throws Exception {
		tman.setCanonicTautomerMethod(CanonicTautomerMethod.ENERGY_RANK_INCHI_KEY);
		testCanonic("SC=C", "SC=C");  //ambit rank prefers SC=C
		tman.setCanonicTautomerMethod(CanonicTautomerMethod.CACTVS_RANK_INCHI_KEY);
	}
	
	public void test_04() throws Exception {
		testCanonic("C1CCCC=C1O", "C1CCCCC1=O");
	}
	
	public void test_05() throws Exception {
		testCanonic("OC1=CC=CCC1", "O=C1CCC=CC1");
	}
	
	public void test_06() throws Exception {
		testCanonic("O=C1CC=CC=C1", "OC1=CC=CC=C1");
	}
	
	public void test_07() throws Exception {
		testCanonic("OC(=C)CC(O)=C", "CC(=O)CC(C)=O");
	}
	
	public void test_08() throws Exception {
		testCanonic("CC1=CC(C)=CC(=O)C1", "CC1=CC(O)=CC(C)=C1");
	}
	
	public void test_09() throws Exception {
		testCanonic("CC(N)=C", "CC(=N)C");
	}
	
	public void test_10() throws Exception {
		testCanonic("C1CCCC=C1N", "C1CCCCC1=N");
	}
	
	public void test_11() throws Exception {
		testCanonic("NC1=CC=CCC1", "N=C1CCC=CC1");
	}
	
	public void test_12() throws Exception {
		testCanonic("N=C1CC=CC=C1", "NC1=CC=CC=C1");
	}
	
	public void test_13() throws Exception {
		testCanonic("NC(=C)CC(N)=C", "CC(=N)CC(C)=N");
	}
	
	public void test_14() throws Exception {
		testCanonic("CC1=CC(C)=CC(=N)C1", "CC1=CC(N)=CC(C)=C1");
	}
	
	public void test_15() throws Exception {
		testCanonic("SC=C", "S=CC");
	}

	public void test_15B() throws Exception {
		tman.setCanonicTautomerMethod(CanonicTautomerMethod.ENERGY_RANK_INCHI_KEY);
		testCanonic("S=CC", "SC=C");  //ambit rank prefers SC=C
		tman.setCanonicTautomerMethod(CanonicTautomerMethod.CACTVS_RANK_INCHI_KEY);
	}
	
	public void test_16() throws Exception {
		testCanonic("CC(S)=C", "CC(=S)C");
	}
	
	public void test_16B() throws Exception {
		tman.setCanonicTautomerMethod(CanonicTautomerMethod.ENERGY_RANK_INCHI_KEY);
		testCanonic("CC(=S)C", "CC(S)=C");  //ambit rank prefers CC(S)=C
		tman.setCanonicTautomerMethod(CanonicTautomerMethod.CACTVS_RANK_INCHI_KEY);
	}
	
	public void test_17() throws Exception {
		testCanonic("C1CCCC=C1S", "C1CCCCC1=S");
	}
	
	public void test_17B() throws Exception {
		tman.setCanonicTautomerMethod(CanonicTautomerMethod.ENERGY_RANK_INCHI_KEY);
		testCanonic("C1CCCCC1=S", "C1CCCC=C1S");  //ambit rank prefers C1CCCC=C1S
		tman.setCanonicTautomerMethod(CanonicTautomerMethod.CACTVS_RANK_INCHI_KEY);
	}
	
	public void test_18() throws Exception {
		testCanonic("SC1=CC=CCC1", "S=C1CCC=CC1");
	}
	
	public void test_18B() throws Exception {
		tman.setCanonicTautomerMethod(CanonicTautomerMethod.ENERGY_RANK_INCHI_KEY);
		testCanonic("S=C1CCC=CC1", "SC1=CC=CCC1");  //ambit rank prefers SC1=CC=CCC1
		tman.setCanonicTautomerMethod(CanonicTautomerMethod.CACTVS_RANK_INCHI_KEY);
	}
	
	public void test_19() throws Exception {
		testCanonic("S=C1CC=CC=C1", "SC1=CC=CC=C1");
	}
			
	public void test_20() throws Exception {
		testCanonic("SC(=C)CC(S)=C", "CC(=S)CC(C)=S");
	}
	
	//Two best tautomers found: C=C(S)C=C(C)S and C=C(S)CC(=C)S. 
	//Test case is OK only with C=C(S)C=C(C)S.
	//If tautomer C=C(S)CC(=C)S is set as expected caninical the TestCase brokes
	public void test_20B() throws Exception {
		tman.setCanonicTautomerMethod(CanonicTautomerMethod.ENERGY_RANK_INCHI_KEY);
		testCanonic("CC(=S)CC(C)=S", "C=C(S)C=C(C)S");  //ambit rank prefers C=C(S)C=C(C)S
		tman.setCanonicTautomerMethod(CanonicTautomerMethod.CACTVS_RANK_INCHI_KEY);
	}
	
	public void test_21() throws Exception {
		testCanonic("CC1=CC(C)=CC(=S)C1", "CC1=CC(S)=CC(C)=C1");
	}
			
	public void test_22() throws Exception {
		testCanonic("OC1=NCCCC1","O=C1CCCCN1");
	}
	
	public void test_23() throws Exception {
		testCanonic("CC(=C)NC(O)=C","CC(C)=NC(=O)C");
	}
	
	public void test_24() throws Exception {
		testCanonic("CC(=O)CC(N)=C","CC(=N)CC(C)=O");
	}

	public void test_25() throws Exception {
		testCanonic("CC(=O)CC(S)=C","CC(=S)CC(C)=O");
	}
	
	public void test_26() throws Exception {
		testCanonic("CC(=S)CC(S)=C","CC(=S)CC(C)=S");
	}
	
	public void test_27() throws Exception {
		testCanonic("SC1=NCCCC1","S=C1CCCCN1");
	}
	
	public void test_28() throws Exception {
		testCanonic("NC1=NCCCC1","N=C1CCCCN1");
	}
	
	public void test_29() throws Exception {
		testCanonic("CC(=N)CC(N)=C","CC(=N)CC(C)=N");
	}
	
	public void test_30() throws Exception {
		testCanonic("N=C1NC=CC=C1","NC1=NC=CC=C1");
	}
	
	public void test_31() throws Exception {
		testCanonic("S=C1NC=CC=C1","SC1=NC=CC=C1");
	}
	
	public void test_32() throws Exception {
		testCanonic("O=C1NC=CC=C1","OC1=NC=CC=C1");
	}
	
	public void test_33() throws Exception {
		testCanonic("NN=CC(=C)C","NN=CC(=C)C");
	}
	
	public void test_33B() throws Exception {
		tman.setCanonicTautomerMethod(CanonicTautomerMethod.ENERGY_RANK_INCHI_KEY);
		testCanonic("NN=CC(=C)C","N=NCC(=C)C");  //ambit rank prefers N=NCC(=C)C
		tman.setCanonicTautomerMethod(CanonicTautomerMethod.CACTVS_RANK_INCHI_KEY);
	}
	
	public void test_34() throws Exception {
		testCanonic("SN=CC(=C)C","SN=CC(=C)C");
	}
	
	public void test_34B() throws Exception {
		tman.setCanonicTautomerMethod(CanonicTautomerMethod.ENERGY_RANK_INCHI_KEY);
		testCanonic("SN=CC(=C)C","SN=CC(=C)C");  //ambit rank prefers SN=CC(=C)C
		tman.setCanonicTautomerMethod(CanonicTautomerMethod.CACTVS_RANK_INCHI_KEY);
	}
	
	public void test_35() throws Exception {
		testCanonic("NC1=C(O)CCCC1","NC1C(=O)CCCC1");
	}
	
	public void test_36() throws Exception {
		testCanonic("NC1=C(O)CCC=C1","N=C1C(=O)CCCC1");
	}
	
	public void test_37() throws Exception {
		testCanonic("OC1=C(O)CCCC1","OC1C(=O)CCCC1");
	}
	
	public void test_38() throws Exception {
		testCanonic("OC1=C(O)CCC=C1","O=C1C(=O)CCCC1");
	}
	
	public void test_39() throws Exception {
		testCanonic("NC1=C(N)CCCC1","NC1C(=N)CCCC1");
	}
	
	public void test_40() throws Exception {
		testCanonic("NC1=C(N)CCC=C1","N=C1C(=N)CCCC1");
	}
	
	public void test_41() throws Exception {
		testCanonic("SC1=C(S)CCCC1","SC1C(=S)CCCC1");
	}
	
	public void test_42() throws Exception {
		testCanonic("SC1=C(S)CCC=C1","S=C1C(=S)CCCC1");
	}
	
	public void test_43() throws Exception {
		testCanonic("NC1=C(S)CCCC1","NC1C(=S)CCCC1");
	}
	
	public void test_44() throws Exception {
		testCanonic("NC1=C(S)CCC=C1","N=C1C(=S)CCCC1");
	}
	
	public void test_45() throws Exception {
		testCanonic("NC=C(S)CCC=C","NCC(=S)CCC=C");
	}
	
	public void test_46() throws Exception {
		testCanonic("NC=C(S)CCCC","NCC(=S)CCCC");
	}
	
	public void test_47() throws Exception {
		testCanonic("SC=C(S)CCC=C","SCC(=S)CCC=C");
	}
	
	public void test_47B() throws Exception {
		tman.setCanonicTautomerMethod(CanonicTautomerMethod.ENERGY_RANK_INCHI_KEY);
		testCanonic("SC=C(S)CCC=C", "SC=C(S)CCC=C");  //ambit rank prefers SC=C(S)CCC=C
		tman.setCanonicTautomerMethod(CanonicTautomerMethod.CACTVS_RANK_INCHI_KEY);
	}
	
	public void test_48() throws Exception {
		testCanonic("OC=C(O)CCC=C","OCC(=O)CCC=C");
	}
	
	public void test_49() throws Exception {
		testCanonic("NC=C(N)CCC=C","NCC(=N)CCC=C");
	}
	
	public void test_50() throws Exception {
		testCanonic("O=C1CC=CN1","OC1=CC=CN1");
	}
	
}
