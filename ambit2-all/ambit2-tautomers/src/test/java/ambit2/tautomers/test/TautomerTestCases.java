package ambit2.tautomers.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.tools.LoggingTool;

public class TautomerTestCases extends TestCase {
    TestTautomers tt;
    boolean FlagPrintTautomers = false;
    public LoggingTool logger;

    public TautomerTestCases() {
	logger = new LoggingTool(this);

	// Initialization
	tt = new TestTautomers();
	tt.tman.getKnowledgeBase().use15ShiftRules(true);
	tt.tman.getKnowledgeBase().use17ShiftRules(false);
	tt.tman.FlagCheckNumOfRegistrationsForIncrementalAlgorithm = false;
	tt.tman.maxNumOfBackTracks = 100000;

    }

    public static Test suite() {
	return new TestSuite(TautomerTestCases.class);
    }

    public void test_Rule_KetoEnol_01() throws Exception {
	int res = tt.testCase("O=CCC", new String[] { "O=CCC", "OC=CC" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_Rule_KetoEnol_02() throws Exception {
	int res = tt.testCase("OC1=CC=CC=C1", new String[] { "Oc1=cc=cc=c1", "O=C1C=CC=CC1", "O=C1C=CCC=C1" },
		FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_Rule_KetoEnol_03() throws Exception {
	int res = tt.testCase("O1=CC=CN=C1", new String[] { "N=1C=OC=CC=1", "N1=CO=CC=C1" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_Rule_KetoEnol_04() throws Exception {
	int res = tt.testCase("O1=CC=CN=C1CC", new String[] { "O1=CC=CNC1=CC", "N1=CC=COC1=CC", "N1=CC=CO=C1CC",
		"N1=CCC=OC1=CC", "N=1C=CC=OC=1CC" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_Rule_KetoEnol_05() throws Exception {
	int res = tt.testCase("O=C(CCC)CC", new String[] { "O=C(CCC)CC", "OC(=CCC)CC", "OC(=CC)CCC" },
		FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_Rule_AminImin_01() throws Exception {
	int res = tt.testCase("N=CCC", new String[] { "N=CCC", "NC=CC" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_Rule_AminImin_02() throws Exception {
	int res = tt.testCase("NC1=CC=CC=C1", new String[] { "Nc1=cc=cc=c1", "N=C1C=CC=CC1", "N=C1C=CCC=C1" },
		FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_Rule_AminImid_01() throws Exception {
	int res = tt.testCase("O=CNCCC", new String[] { "O=CNCCC", "OC=NCCC" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_Rule_AminImid_02() throws Exception {
	int res = tt.testCase("OC1=NC=CC=C1", new String[] { "Oc=1n=cc=cc=1", "Oc1=nc=cc=c1", "O=C1C=CC=CN1",
		"O=C1N=CC=CC1", "O=C1N=CCC=C1" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_Rule_NitrosoOxime_01() throws Exception {
	int res = tt.testCase("O=NCCC", new String[] { "O=NCCC", "ON=CCC", "ONC=CC" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_Rule_AzoHydrazone_01() throws Exception {
	int res = tt.testCase("CN=NCC", new String[] { "CNNC=C", "CNN=CC", "C=NNCC", "CN=NCC" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_Rule_ThioketoThioenol_01() throws Exception {
	int res = tt.testCase("S=CCC", new String[] { "S=CCC", "SC=CC" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_Rule_ThioketoThioenol_02() throws Exception {
	int res = tt.testCase("SC1=CC=CC=C1", new String[] { "Sc1=cc=cc=c1", "S=C1C=CC=CC1", "S=C1C=CCC=C1" },
		FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_Rule_ThionitrosoThiooxime_01() throws Exception {
	int res = tt.testCase("S=NCCC", new String[] { "S=NCCC", "SN=CCC", "SNC=CC" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_Rule_AmidineImidine_01() throws Exception {
	int res = tt.testCase("N=CNCCCCC", new String[] { "NC=NCCCCC", "N=CNCCCCC" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_Rule_AmidineImidine_02() throws Exception {
	int res = tt.testCase("n1=cc=cn=c1", new String[] { "n1=cc=cn=c1" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_Rule_AmidineImidine_02_noIsoCheck() throws Exception {
	tt.tman.tautomerFilter.FlagApplyDuplicationCheckIsomorphism = false;
	int res = tt.testCase("n1=cc=cn=c1", new String[] { "n1=cc=cn=c1", "n1c=cc=nc=1" }, FlagPrintTautomers);

	tt.tman.tautomerFilter.FlagApplyDuplicationCheckIsomorphism = true; // default
									    // is
									    // restored

	assertEquals(0, res);
    }

    public void test_Rule_DiazoaminoDiazoamino_01() throws Exception {
	int res = tt.testCase("N=NNCCC", new String[] { "NNNC=CC", "NNN=CCC", "NN=NCCC", "N=NNCCC" },
		FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_Rule_DiazoaminoDiazoamino_02() throws Exception {
	int res = tt.testCase("N1=NC=CC=N1", new String[] { "n1=nc=cc=n1" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_Rule_DiazoaminoDiazoamino_02_noIsoCheck() throws Exception {
	tt.tman.tautomerFilter.FlagApplyDuplicationCheckIsomorphism = false;
	int res = tt.testCase("N1=NC=CC=N1", new String[] { "n1=nc=cc=n1", "n1n=cc=cn=1" }, FlagPrintTautomers);

	tt.tman.tautomerFilter.FlagApplyDuplicationCheckIsomorphism = true; // default
									    // is
									    // restored

	assertEquals(0, res);
    }

    public void test_Rule_ThioamideIminothiol_01() throws Exception {
	int res = tt.testCase("S=CNCC", new String[] { "S=CNCC", "SC=NCC" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_Rule_ThioamideIminothiol_02() throws Exception {
	int res = tt.testCase("N1=CCC=CC1=S", new String[] { "n1ccccc1S", "C=1C=CC(NC=1)=S", "n1ccccc1S",
		"N1=CC=CCC1=S", "N1=CCC=CC1=S" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_Rule_ThioamideIminothiol_03() throws Exception {
	int res = tt.testCase("S1=CN=CC=C1", new String[] { "N=1C=CC=SC=1", "N1=CC=CS=C1" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_Rule_NitrosamineDiazohydroxide_01() throws Exception {
	int res = tt.testCase("O=NNCC", new String[] { "ONNC=C", "ONN=CC", "ON=NCC", "O=NNCC" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    /*
     * [N+] ????? to check
     * 
     * public void test_Rule_NitrosamineDiazohydroxide_02() throws Exception {
     * int res = tt.testCase("O=N1N=CC=CC1", //new String[]{"O=N1N=CC=CC1",
     * "ON=1N=CC=CC=1"}, new String[]{"O=N1N=CC=CC1", "ON=1N=CC=CC=1",
     * "O=N1C=CC=CN1", "ON1=NC=CC=C1"}, FlagPrintTautomers);
     * 
     * assertEquals(0, res); }
     */

    public void test_Rule_AminImin_03() throws Exception {
	int res = tt.testCase("N=C(CCC)CC", new String[] { "N=C(CC)CCC", "NC(=CCC)CC", "NC(=CC)CCC" },
		FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_Rule_KetoEnol_AminImin_01() throws Exception {
	int res = tt.testCase("O=C(CN=CC)CC", new String[] { "O=C(CN=CC)CC", "OC(=CN=CC)CC", "OC(=CC)CN=CC",
		"O=C(CNC=C)CC", "OC(C=NC=C)CC", "OC(=CNC=C)CC", "OC(=CC)CNC=C" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_Rule_KetoEnol_AminImin_AmidImid_01() throws Exception {
	int res = tt.testCase("O=C1NC=CCC1", new String[] { "OC1=CCC=CN1", "O=C1NC=CCC1", "OC=1N=CCCC=1",
		"O=C1N=CCCC1", "OC1=NC=CCC1" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_Rule_KetoEnol_ThioketoThioEnol_01() throws Exception {
	int res = tt.testCase("O=C1S(=CC)C=CCC1", new String[] { "OC1=CCC=CS1(C=C)", "OC=1CCC=CS=1(C=C)",
		"O=C1CCC=CS1(C=C)", "OC1=CCCC=S1(C=C)", "O=C1CCCC=S1(C=C)", "OC1=CCC=CS1(=CC)", "O=C1CCC=CS1(=CC)" },
		FlagPrintTautomers);

	assertEquals(0, res);
    }

    /*
     * public void test_Rule_KetoEnol_AminImin_AzoHydrazone_01() throws
     * Exception { int res = tt.testCase("O=C1N(=N)CN=CC1", new
     * String[]{"O=C1C=CNC=N1(N)", "O=C1C=CN=CN1(N)", "OC=1C=CN=CN=1(N)",
     * "N=N1C=NC=CC1(O)", "OC1=CC=NC=N1(N)",
     * "O=C1N(N)=CN=CC1","O=C1C=CNCN1(=N)", "N=N1C(O)=CC=NC1",
     * "O=C1N(=N)CN=CC1"}, FlagPrintTautomers);
     * 
     * assertEquals(0, res); }
     */

    public void test_Rule_AminImin_AmidineImidine_01() throws Exception {
	int res = tt.testCase("CNC(=N)N=CCO", new String[] { "N=C(NC=CO)NC", "O=CCNC(=N)NC", "OC=CN=C(N)NC",
		"O=CCN=C(N)NC", "OC=CNC(=NC)N", "O=CCNC(=NC)N", "N=C(N=CCO)NC", "OCC=NC(=NC)N" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_Rule_AminImin_AmidineImidine_NitrosoOxime_01() throws Exception {
	int res = tt.testCase("CNC1N=CCC=N1O", new String[] { "ON1C=CC=NC1(NC)", "ON1=CC=CNC1(NC)", "ON1=CCC=NC1(NC)",
		"ON1=C(NC=CC1)NC", "ON1C(=NC=CC1)NC", "ON1C(=NC)NC=CC1", "ON1=C(N=CCC1)NC", "ON1C(N=CCC1)=NC",
		"O=N1CC=CNC1(NC)", "O=N1CCC=NC1(NC)" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_Rule_AminImin_AmidineImidine_ThioNitrosoThioOxime_01() throws Exception {
	int res = tt.testCase("CNC1N=CCC=N1S", new String[] { "SN1C=CC=NC1(NC)", "SN1=CC=CNC1(NC)", "SN1=CCC=NC1(NC)",
		"SN1=C(NC=CC1)NC", "SN1C(=NC=CC1)NC", "SN1C(=NC)NC=CC1", "SN1=C(N=CCC1)NC", "SN1C(N=CCC1)=NC",
		"S=N1CC=CNC1(NC)", "S=N1CCC=NC1(NC)" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_Rule_AminImin_AmidineImidine_AzoHydrazone_01() throws Exception {
	int res = tt.testCase("CNC1N=CCC=N1N", new String[] { "NN1C=CC=NC1(NC)", "NN1=CC=CNC1(NC)", "NN1=CCC=NC1(NC)",
		"NN1=C(NC=CC1)NC", "NN1C(=NC=CC1)NC", "NN1C(=NC)NC=CC1", "NN1=C(N=CCC1)NC", "NN1C(N=CCC1)=NC",
		"N=N1CC=CNC1(NC)", "N=N1CCC=NC1(NC)" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_Rule_AminImin_ThionitrosamineThioDiazoHydrazone_AzoHydrazone_01() throws Exception {
	int res = tt.testCase("SN=NCC", new String[] { "C=CNNS", "N(=CC)NS", "N(=NS)CC", "N(NCC)=S" },
		FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_Rule_CombinationOf5rules_01() throws Exception {
	int res = tt.testCase("C1CC(S)C(=O)NN1", new String[] { "OC=1NNCCC=1S", "O=C1NNCCC1S", "OC1NNCC=C1S",
		"OC1NNCCC1=S", "OC1=NNCCC1S", "OC1NNC=CC1S", "OC1NN=CCC1S", "OC1N=NCCC1S" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_Rule_CombinationOfRules_04() throws Exception {
	int res = tt.testCase("O=C1C=CNC=C1", new String[] { "Oc1ccncc1", "O=C1C=CN=CC1", "O=C1C=CNC=C1" },
		FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_Rule_CombinationOfRules_05() throws Exception {
	int res = tt.testCase("S=C1C=CNC=C1", new String[] { "Sc1ccncc1", "C1=CC(C=CN1)=S", "N=1C=CC(CC=1)=S" },
		FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_Rule_CombinationOfRules_06() throws Exception {
	int res = tt.testCase("N=C1C=CNC=C1", new String[] { "n1ccc(N)cc1", "N=C1C=CN=CC1", "N=C1C=CNC=C1" },
		FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_Rule_CombinationOfRules_08() throws Exception {
	int res = tt.testCase("OC(=CN)C", new String[] { "OC(=CN)C", "OC(=C)CN", "O=C(C)CN", "N=CC(O)C" },
		FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_Rule_CombinationOfRules_09() throws Exception {
	int res = tt.testCase("S=NC(N)C", new String[] { "C=C(N)NS", "N=C(NS)C", "N(=C(N)C)S", "N(C(N)C)=S" },
		FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_Rule_CombinationOfRules_10() throws Exception {
	int res = tt.testCase("C=C(N)S", new String[] { "C=C(N)S", "NC(C)=S", "N=C(C)S" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_Rule_CombinationOfRules_11() throws Exception {
	int res = tt.testCase("OC(=C)S", new String[] { "OC(=C)S", "O=C(C)S", "OC(C)=S" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_Rule_01() throws Exception {

	int res = tt.testCase("OC#CC", new String[] { "OC#CC", "O=C=CC" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_Rule_06() throws Exception {
	tt.tman.getKnowledgeBase().use17ShiftRules(true);
	int res = tt.testCase("O=CC=CC=CCCC", new String[] { "O=CC=CC=CCCC", "OC=CC=CC=CCC", "O=CCC=CC=CCC",
		"O=CC=CCC=CCC" }, FlagPrintTautomers);
	tt.tman.getKnowledgeBase().use17ShiftRules(false);

	assertEquals(0, res);
    }

    public void test_Rule_06_NoWarningFilter() throws Exception {
	tt.tman.tautomerFilter.FlagApplyWarningFilter = false;
	tt.tman.getKnowledgeBase().use17ShiftRules(true);
	int res = tt.testCase("O=CC=CC=CCCC", new String[] { "OC=CC=C=CCCC", "O=CCC=C=CCCC", "OC=C=CC=CCCC",
		"O=CC=CC=CCCC", "OC=CC=CC=CCC", "O=CCC=CC=CCC", "OC=C=CCC=CCC", "O=CC=CCC=CCC" }, FlagPrintTautomers);
	tt.tman.tautomerFilter.FlagApplyWarningFilter = true;
	tt.tman.getKnowledgeBase().use17ShiftRules(false);

	assertEquals(0, res);
    }

    // --

    public void test_Rule_02() throws Exception {

	int res = tt.testCase("NC#CC", new String[] { "NC#CC", "N=C=CC" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_Rule_03() throws Exception {

	int res = tt.testCase("SC#CC", new String[] { "SC#CC", "S=C=CC" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_Rule_Rule_OSC() throws Exception {
	int res = tt.testCase("O=SC", new String[] { "OS=C", "O=SC" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_Rule_Rule_OSC_01() throws Exception {
	int res = tt.testCase("O=SCC", new String[] { "OSC=C", "O=SCC", "OS=CC" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_Rule_CombinationOfRules_07() throws Exception {
	int res = tt.testCase("OS=C(CC)C", new String[] { "OS=C(CC)C", "OSC(=C)CC", "OS=C(C)CC", "O=SC(C)CC" },
		FlagPrintTautomers);

	assertEquals(0, res);
    }

    // -----------------

    public void test_acetaldehyde() throws Exception {
	int res = tt.testCase("O=CC", new String[] { "O=CC", "OC=C" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_acetaldoxime() throws Exception {
	int res = tt.testCase("CC=NO", new String[] { "ONC=C", "ON=CC", "O=NCC" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_acetamide() throws Exception {
	int res = tt.testCase("CC(N)=O", new String[] { "OC(=C)N", "O=C(N)C", "N=C(O)C" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_aceticAnhydride() throws Exception {
	int res = tt.testCase("CC(=O)OC(C)=O", new String[] { "OC(OC(O)=C)=C", "O=C(OC(O)=C)C", "O=C(OC(=O)C)C" },
		FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_acetohydrazide() throws Exception {
	int res = tt.testCase("CC(=O)NN", new String[] { "OC(=C)NN", "O=C(NN)C", "OC(=NN)C", "N=NC(O)C" },
		FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_acetone() throws Exception {
	int res = tt.testCase("CC(=O)C", new String[] { "OC(=C)C", "O=C(C)C" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_acetone_01() throws Exception {
	tt.tman.tautomerFilter.FlagApplyDuplicationCheckIsomorphism = false;
	int res = tt.testCase("CC(=O)C", new String[] { "OC(=C)C", "O=C(C)C", "OC(=C)C" }, FlagPrintTautomers);

	tt.tman.tautomerFilter.FlagApplyDuplicationCheckIsomorphism = true;
	assertEquals(0, res);
    }

    public void test_2acetoxypropanoicAcid() throws Exception {
	int res = tt.testCase("CC(OC(C)=O)C(C)=O", new String[] { "OC(OC(C(O)=C)C)=C", "OC(OC(=C(O)C)C)=C",
		"O=C(C)C(OC(O)=C)C", "O=C(OC(C(O)=C)C)C", "O=C(OC(=C(O)C)C)C", "O=C(OC(C(=O)C)C)C" },
		FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_acetylacetone() throws Exception {
	int res = tt.testCase("CC(=O)CC(C)=O", new String[] { "OC(=C)C=C(O)C", "O=C(C=C(O)C)C", "OC(=C)CC(O)=C",
		"O=C(C)CC(O)=C", "O=C(C)CC(=O)C" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_2acetylButyrolactone() throws Exception {
	int res = tt.testCase("CC(=O)C1CCOC1=O", new String[] { "OC=1OCCC=1C(O)=C", "O=C(C1=C(O)OCC1)C",
		"O=C1OCCC1C(O)=C", "O=C1OCCC1C(=O)C", "O=C1OCCC1=C(O)C" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_2acetylcyclopentanone() throws Exception {
	int res = tt.testCase("CC(=O)C1CCCC1=O", new String[] { "OC(=C)C1=C(O)CCC1", "O=C(C1=C(O)CCC1)C",
		"OC(=C)C1C(O)=CCC1", "O=C(C)C1C(O)=CCC1", "O=C1CCCC1(C(O)=C)", "O=C(C)C1C(=O)CCC1", "OC1=CCCC1=C(O)C",
		"O=C1C(=C(O)C)CCC1" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_1acetylthiourea() throws Exception {
	int res = tt.testCase("CC(=O)NC(N)=S", new String[] { "N=C(NC(O)=C)S", "O=C(NC(=N)S)C", "N=C(N=C(O)C)S",
		"OC(N=C(N)S)=C", "O=C(N=C(N)S)C", "OC(=C)NC(N)=S", "O=C(NC(N)=S)C", "OC(=NC(N)=S)C" },
		FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_adiamide() throws Exception {
	int res = tt.testCase("NC(=O)CCCCC(N)=O", new String[] { "OC(N)=CCCC=C(O)N", "O=C(N)CCCC=C(O)N",
		"N=C(O)CCCC=C(O)N", "O=C(N)CCCCC(=O)N", "O=C(N)CCCCC(=N)O", "N=C(O)CCCCC(=N)O" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_barbituricAcid() throws Exception {
	int res = tt.testCase("O=C1CC(=O)NC(=O)N1", new String[] { "O=C1N=C(O)NC(O)=C1", "O=C1C=C(O)N=C(O)N1",
		"O=C1N=C(O)NC(=O)C1", "Oc1nc(O)cc(O)n1", "O=C1N=C(O)N=C(O)C1", "O=C1C=C(O)NC(=O)N1",
		"O=C1NC(=O)CC(=O)N1", "O=C1N=C(O)C=C(O)N1", "O=C1N=C(O)CC(=O)N1", "O=C1N=C(O)CC(O)=N1" },
		FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_AsparagineMonohydrate() throws Exception {
	int res = tt.testCase("NC(CC(N)=O)C(O)=O", new String[] { "OC(O)=C(N)C=C(O)N", "O=C(O)C(N)C=C(O)N",
		"N=C(C=C(O)N)C(O)O", "O=C(N)C=C(N)C(O)O", "O=C(N)CC(N)=C(O)O", "O=C(O)C(N)CC(=O)N",
		"O=C(N)CC(=N)C(O)O", "N=C(O)C=C(N)C(O)O", "N=C(O)CC(N)=C(O)O", "O=C(O)C(N)CC(=N)O",
		"N=C(O)CC(=N)C(O)O", "O=C(O)C(N)=CC(O)N", "O=C(O)C(=N)CC(O)N" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_butanoxime() throws Exception {
	int res = tt.testCase("CCC(C)=NO", new String[] { "ONC(=C)CC", "ONC(=CC)C", "ON=C(C)CC", "O=NC(C)CC" },
		FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_AminoDimethyluracil() throws Exception {
	int res = tt.testCase("CN1C(N)=CC(=O)N(C)C1=O", new String[] { "O=C1C=C(N)N(C(=O)N1C)C",
		"O=C1N(C(=N)C=C(O)N1C)C", "O=C1N(C(=O)CC(=N)N1C)C" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_acetanilide() throws Exception {
	int res = tt.testCase("CC(=O)NC1=CC=CC=C1", new String[] { "OC(=C)Nc1ccccc1", "O=C(Nc1ccccc1)C",
		"OC(N=C1C=CC=CC1)=C", "O=C(N=C1C=CC=CC1)C", "OC(=Nc1ccccc1)C", "OC(N=C1C=CCC=C1)=C",
		"O=C(N=C1C=CCC=C1)C" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_acetanilideNoIsomorphismCheck() throws Exception {
	tt.tman.tautomerFilter.FlagApplyDuplicationCheckIsomorphism = false;

	int res = tt.testCase("CC(=O)NC1=CC=CC=C1", new String[] { "OC(=C)Nc1ccccc1", "O=C(Nc1ccccc1)C",
		"OC(N=C1C=CC=CC1)=C", "O=C(N=C1C=CC=CC1)C", "OC(=Nc1ccccc1)C", "OC(=C)Nc1ccccc1", "O=C(Nc1ccccc1)C",
		"OC(N=C1C=CC=CC1)=C", "O=C(N=C1C=CC=CC1)C", "OC(=Nc1ccccc1)C", "OC(N=C1C=CCC=C1)=C",
		"O=C(N=C1C=CCC=C1)C" }, FlagPrintTautomers);

	tt.tman.tautomerFilter.FlagApplyDuplicationCheckIsomorphism = true;

	assertEquals(0, res);
    }

    public void test_acetylenediurea() throws Exception {
	int res = tt.testCase("O=C1NC2NC(=O)NC2N1", new String[] { "OC1=NC2N=C(O)NC2(N1)", "OC1=NC2NC(O)=NC2(N1)",
		"O=C1NC2N=C(O)NC2(N1)", "O=C1NC2NC(=O)NC2(N1)" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_amitrole() throws Exception {
	int res = tt.testCase("NC1=NC=NN1", new String[] { "n1cnc(N)n1", "N1=NCN=C1N", "n1nc(N)nc1", "N=C1NN=CN1",
		"N=C1N=NCN1", "n1cnnc1N", "N=C1N=CNN1", "N1=NC(N=C1)N" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_thiotetronicAcid() throws Exception {
	int res = tt.testCase("O=C1CSC(=O)C1", new String[] { "Oc1cc(O)sc1", "O=C1C=C(O)SC1", "O=C1CC(O)=CS1",
		"O=C1C=C(O)CS1", "O=C1CC(=O)SC1" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_cytrazinicAcid() throws Exception {
	int res = tt.testCase("OC(=O)C1=CC(=O)NC(O)=C1", new String[] { "O=C(O)c1cc(O)nc(O)c1",
		"O=C(O)C1=CC(O)=NC(=O)C1", "O=C(O)C1=CC(=O)NC(O)=C1", "O=C(O)C1=CC(=O)N=C(O)C1",
		"O=C(O)C1=CC(=O)NC(=O)C1" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_ciclipirox() throws Exception {
	tt.tman.getKnowledgeBase().use17ShiftRules(true);

	int res = tt.testCase("CC1=CC(=O)N(O)C(=C1)C1CCCCC1", new String[] { "OC1=CC(=C)C=C(N1(O))C2CCCCC2",
		"O=C1N(O)C(=CC(=C)C1)C2CCCCC2", "O=C1C=C(C=C(N1(O))C2CCCCC2)C", "OC2=CC(=CC(=C1CCCCC1)N2(O))C",
		"O=C1N(O)C(C=C(C)C1)=C2CCCCC2", "OC2=CC(=C)CC(=C1CCCCC1)N2(O)", "O=C2N(O)C(=C1CCCCC1)CC(=C)C2",
		"O=C2C=C(C)CC(=C1CCCCC1)N2(O)" }, FlagPrintTautomers);

	tt.tman.getKnowledgeBase().use17ShiftRules(false);

	assertEquals(0, res);
    }

    public void test_urocanicAcid() throws Exception {
	int res = tt.testCase("OC(=O)C=CC1=CN=CN1", new String[] { "O=C(O)C=Cc1cncn1", "O=C(O)C=CC1=NC=NC1",
		"O=C(O)C=Cc1ncnc1", "OC(O)=CC=C1N=CN=C1", "O=C(O)CC=C1N=CN=C1", "O=C(O)C=CC1N=CN=C1" },
		FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_oxamycine() throws Exception {
	int res = tt.testCase("NC1CONC1=O", new String[] { "OC=1NOCC=1(N)", "O=C1NOCC1(N)", "OC1NOC=C1(N)",
		"N=C1CONC1(O)", "OC1=NOCC1(N)" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_cyclohexandiode() throws Exception {
	int res = tt.testCase("O=C1CCC(=O)CC1", new String[] { "OC1=CC=C(O)CC1", "OC=1C=CC(O)CC=1", "O=C1C=CC(O)CC1",
		"OC1=CCC(O)=CC1", "O=C1CC=C(O)CC1", "O=C1CCC(=O)CC1" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_4aminophenol() throws Exception {
	int res = tt.testCase("NC1=CC=C(O)C=C1", new String[] { "Oc1ccc(N)cc1", "O=C1C=CC(N)=CC1", "N=C1C=CC(O)=CC1",
		"O=C1C=CC(=N)CC1", "N=C1C=CC(O)C=C1", "O=C1C=CC(N)C=C1" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_3aminophenol() throws Exception {
	int res = tt.testCase("NC1=CC=CC(O)=C1", new String[] { "Oc1cccc(N)c1", "O=C1C=CC=C(N)C1", "Oc1cccc(N)c1",
		"O=C1C=CCC(N)=C1", "N=C1C=CC=C(O)C1", "N=C1C=C(O)C=CC1", "O=C1C=CCC(=N)C1", "O=C1C=C(N)C=CC1",
		"O=C1CC=CC(=N)C1", "N=C1C=CCC(O)=C1" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_2aminophenol() throws Exception {
	int res = tt.testCase("NC1=C(O)C=CC=C1", new String[] { "Oc1ccccc1(N)", "O=C1C=CC=CC1(N)", "N=C1C=CC=CC1(O)",
		"Oc1ccccc1(N)", "O=C1C(N)=CC=CC1", "N=C1C(O)=CC=CC1", "O=C1C(=N)CC=CC1", "N=C1C=CCC=C1(O)",
		"O=C1C(=N)C=CCC1", "O=C1C=CCC=C1(N)", "O=C1C=CCCC1(=N)" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_2pyridine() throws Exception {
	tt.tman.getKnowledgeBase().use17ShiftRules(true);

	int res = tt.testCase("NCC1=CC=CC=N1", new String[] { "N=CC1=CC=CCN1", "NC=C1C=CC=CN1", "N=CC1C=CC=CN1",
		"N=CC1=CCC=CN1", "n1ccccc1CN", "N=CC1=NCC=CC1", "N=CC1=NC=CCC1", "n1ccccc1CN", "N=CC1=NCCC=C1",
		"N1=CCC=CC1=CN", "N=CC1N=CCC=C1", "N1=CC=CCC1=CN", "N=CC1N=CC=CC1", "N=CC=1N=CCCC=1" },
		FlagPrintTautomers);

	tt.tman.getKnowledgeBase().use17ShiftRules(false);

	assertEquals(0, res);
    }

    public void test_2amino4methoxy6methylpyrimidine() throws Exception {
	int res = tt.testCase("COC1=CC(C)=NC(N)=N1", new String[] { "N=1C(OC)=CC(=C)NC=1N", "N=1C(=C)C=C(OC)NC=1N",
		"N=C1NC(OC)=CC(=C)N1", "N=1C(=NC(=C)CC=1OC)N", "N=C1N=C(OC)CC(=C)N1", "N=C1N=C(C=C(OC)N1)C",
		"Cc1cc(OC)nc(N)n1", "N=C1N=C(OC)C=C(N1)C", "N=C1N=C(OC)CC(=N1)C", "Cc1cc(OC)nc(N)n1" },
		FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_2amino3hydroxypyridine() throws Exception {
	int res = tt.testCase("NC1=C(O)C=CC=N1", new String[] { "Oc1cccnc1N", "N=C1NC=CC=C1(O)", "N=C1N=CC=CC1(O)",
		"O=C1C(=NC=CC1)N", "O=C1C(=N)NC=CC1", "O=C1C=CCN=C1(N)", "O=C1C=CCNC1(=N)", "Oc1cccnc1N",
		"O=C1C=CC=NC1(N)", "N=C1N=CCC=C1(O)", "O=C1C(=N)N=CCC1" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_mesalazine() throws Exception {
	int res = tt.testCase("NC1=CC=C(O)C(=C1)C(O)=O", new String[] { "OC(=O)c1cc(N)ccc1O",
		"O=C(O)C1=CC(N)=CCC1(=O)", "O=C(O)C1=CC(=N)CC=C1(O)", "O=C(O)C1=CC(=N)CCC1(=O)",
		"O=C1C=CC(N)=CC1(=C(O)O)", "O=C1C=CC(=N)C=C1C(O)O", "O=C1C=CC(=N)CC1=C(O)O", "OC(=O)c1cc(N)ccc1O",
		"O=C(O)C1=C(O)C=CC(=N)C1", "O=C(O)C1C=C(N)C=CC1(=O)", "O=C(O)C1C(=O)C=CC(=N)C1",
		"O=C(O)C1=CC(N)C=CC1(=O)", "O=C(O)C1=CC(=N)C=CC1(O)" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_2amino46dimthylpyrimidine() throws Exception {
	int res = tt.testCase("COC(=O)C1=CC=C(C(=O)OC)C(N)=C1", new String[] { "Nc1cc(ccc1C(=O)OC)C(=O)OC",
		"O=C(OC)C1=CC=C(C(=O)OC)CC1(=N)", "O=C(OC)C=1C=CC(C(=N)C=1)=C(O)OC", "Nc1cc(ccc1C(=O)OC)C(=O)OC",
		"O=C(OC)C=1C=CC(C(=O)OC)C(=N)C=1", "O=C(OC)C1=CCC(=CC1(=N))C(=O)OC" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_2aminobenzamide() throws Exception {
	int res = tt.testCase("NC(=O)C1=C(N)C=CC=C1", new String[] { "N=C1C=CC=CC1=C(O)N", "OC(=N)c1ccccc1N",
		"N=C(O)C1C=CC=CC1(=N)", "O=C(N)c1ccccc1N", "O=C(N)C1C=CC=CC1(=N)", "OC(=N)c1ccccc1N",
		"N=C(O)C1=CCC=CC1(=N)", "O=C(N)c1ccccc1N", "O=C(N)C1=CCC=CC1(=N)", "O=C(N)C1=CC=CCC1(=N)",
		"N=C(O)C1=CC=CCC1(=N)" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_4acetamidobenzaldehyde() throws Exception {
	int res = tt.testCase("CC(=O)NC1=CC=C(C=O)C=C1", new String[] { "OC=C1C=CC(=NC(O)=C)C=C1",
		"O=C(N=C1C=CC(=CO)C=C1)C", "OC(=C)Nc1ccc(cc1)C=O", "O=C(C)Nc1ccc(cc1)C=O", "O=CC=1C=CC(=NC(O)=C)CC=1",
		"O=CC=1C=CC(=NC(=O)C)CC=1", "OC(C)=Nc1ccc(cc1)C=O", "O=CC1C=CC(=NC(O)=C)C=C1",
		"O=CC1C=CC(=NC(=O)C)C=C1" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_thymine() throws Exception {
	int res = tt.testCase("CC1=CNC(=O)NC1=O", new String[] { "O=C1N=C(O)NC=C1C", "Cc1cnc(O)nc1O",
		"O=C1N=C(O)N=CC1C", "Cc1cnc(O)nc1O", "O=C1NC(O)=NC=C1C", "O=C1N=C(O)C(=CN1)C", "O=C1NC=C(C(=O)N1)C",
		"O=C1N=CC(=C(O)N1)C", "O=C1N=CC(C(O)=N1)C", "O=C1N=CC(C(=O)N1)C" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_2hydroxy34dimethoxy6methylbenzaldehyde() throws Exception {
	int res = tt.testCase("COC1=C(OC)C(O)=C(C=O)C(C)=C1", new String[] { "COc1c(O)c(C=O)c(C)cc1OC",
		"OC=C1C(O)=C(OC)C(OC)=CC1(=C)", "COc1c(O)c(C=O)c(C)cc1OC", "O=C1C(=CO)C(=C)C=C(OC)C1(OC)",
		"O=CC1=C(O)C(OC)C(OC)=CC1(=C)", "O=CC=1C(=O)C(OC)C(OC)=CC=1C", "O=C1C(OC)=C(OC)C=C(C1(=CO))C",
		"O=C1C(OC)=C(OC)CC(=C)C1(=CO)", "O=CC1=C(O)C(OC)=C(OC)CC1(=C)", "O=CC=1C(=O)C(OC)=C(OC)CC=1C",
		"O=CC1C(=O)C(OC)=C(OC)C=C1(C)", "O=CC1C(O)=C(OC)C(OC)=CC1(=C)", "O=CC1C(=O)C(OC)C(OC)=CC1(=C)",
		"O=CC1C(=O)C(OC)=C(OC)CC1(=C)" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_46diaminopyrimidine2ol() throws Exception {
	int res = tt.testCase("NC1=CC(=N)N=C(O)N1", new String[] { "N=C1N=C(O)NC(N)=C1", "N=C1C=C(N=C(O)N1)N",
		"N=C1N=C(O)NC(=N)C1", "Nc1cc(N)nc(O)n1", "N=C1N=C(O)N=C(N)C1", "O=C1NC(=N)C=C(N)N1",
		"O=C1N=C(N)C=C(N)N1", "O=C1N=C(N)CC(=N)N1", "O=C1N=C(N)CC(=N1)N", "O=C1NC(=N)CC(=N)N1" },
		FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_fluctosine() throws Exception {
	int res = tt.testCase("NC1=NC(=O)NC=C1F", new String[] { "Fc1cnc(O)nc1N", "O=C1N=C(N)C(F)=CN1",
		"Fc1cnc(O)nc1N", "FC1C=NC(O)=NC1(=N)", "O=C1N=CC(F)=C(N)N1", "O=C1N=CC(F)C(=N)N1",
		"O=C1N=CC(F)C(=N1)N", "FC1=CNC(O)=NC1(=N)", "FC1=CN=C(O)NC1(=N)", "O=C1NC=C(F)C(=N)N1" },
		FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_2thiouracil() throws Exception {
	int res = tt.testCase("O=C1CC=NC(=S)N1", new String[] { "O=C1N=C(NC=C1)S", "O=C1C=CN=C(N1)S", "Oc1ccnc(S)n1",
		"O=C1C=CNC(N1)=S", "OC1=NC(NC=C1)=S", "Oc1ccnc(S)n1", "O=C1N=C(N=CC1)S", "OC1=CC=NC(N1)=S",
		"O=C1NC(N=CC1)=S", "OC1=NC(N=CC1)=S" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_purpald() throws Exception {
	int res = tt.testCase("NNC1=NNC(=S)N1N", new String[] { "NNc1nnc(S)n1N", "N(N)=C1NN=C(N1(N))S",
		"N=NC1NN=C(N1(N))S", "N1=NC(N(N)C1=NN)S", "N=NC1=NNC(N1(N))S", "N=NC1N=NC(N1(N))S",
		"N=1NC(N(N)C=1NN)=S", "N(N)=C1NNC(N1(N))=S", "N=NC1NNC(N1(N))=S", "N1=NC(NN)N(N)C1=S" },
		FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_pemoline() throws Exception {
	tt.tman.getKnowledgeBase().use17ShiftRules(true);

	int res = tt.testCase("N=C1NC(=O)C(O1)C1=CC=CC=C1", new String[] { "N=C1N=C(O)C(O1)=C2C=CCC=C2",
		"OC=2NC(=N)OC=2c1ccccc1", "N=C1NC(=O)C(O1)c2ccccc2", "O=C1NC(=N)OC1=C2C=CCC=C2",
		"N=C1N=C(O)C(O1)=C2C=CC=CC2", "O=C1NC(=N)OC1=C2C=CC=CC2", "N=C1N=C(O)C(O1)c2ccccc2",
		"O=C1N=C(OC1=C2C=CCC=C2)N", "Oc2nc(N)oc2c1ccccc1", "O=C1N=C(OC1=C2C=CC=CC2)N",
		"NC1=NC(=O)C(O1)c2ccccc2" }, FlagPrintTautomers);

	tt.tman.getKnowledgeBase().use17ShiftRules(false);

	assertEquals(0, res);
    }

    public void test_iodothiouracil() throws Exception {
	int res = tt.testCase("SC1=NC=C(I)C(=O)N1", new String[] { "Ic1cnc(S)nc1O", "O=C1NC(=NC=C1I)S",
		"O=C1N=C(NC=C1I)S", "Ic1cnc(S)nc1O", "O=C1N=C(N=CC1I)S", "OC1=NC(NC=C1I)=S", "OC=1NC(N=CC=1I)=S",
		"O=C1NC(N=CC1I)=S", "OC1=NC(N=CC1I)=S", "O=C1NC(NC=C1I)=S" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_3aminoacetophenone() throws Exception {
	int res = tt.testCase("CC(=O)C1=CC=CC(N)=C1", new String[] { "OC(=C)c1cc(N)ccc1", "N=C1C=CC=C(C(O)=C)C1",
		"O=C(C)c1cc(N)ccc1", "O=C(C1=CC=CC(=N)C1)C", "OC(=C)c1cc(N)ccc1", "O=C(C)c1cc(N)ccc1",
		"N=C1C=C(C=CC1)C(O)=C", "O=C(C=1C=CCC(=N)C=1)C", "N=C1C=CCC(=C1)C(O)=C", "O=C(C1=CC(=N)C=CC1)C" },
		FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_4aminoacetophenone() throws Exception {
	int res = tt.testCase("CC(=O)C1=CC=C(N)C=C1", new String[] { "N=C1C=CC(C=C1)=C(O)C", "OC(=C)c1ccc(N)cc1",
		"N=C1C=CC(=CC1)C(O)=C", "N=C1C=CC(C=C1)C(O)=C", "O=C(C)c1ccc(N)cc1", "O=C(C=1C=CC(=N)CC=1)C",
		"O=C(C)C1C=CC(=N)C=C1" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_9aminoscridine() throws Exception {
	int res = tt.testCase("NC1=C2C=CC=CC2=NC2=CC=CC=C12", new String[] { "N=C1c3ccccc3N=C2CC=CC=C12",
		"N=C1c3ccccc3N=C2CC=CC=C12", "N=C1c3ccccc3Nc2ccccc12", "N=C1c3ccccc3N=C2C=CC=CC12",
		"N=C1c3ccccc3N=C2C=CCC=C12", "N=C1c3ccccc3Nc2ccccc12", "Nc1c3ccccc3nc2ccccc12",
		"N=C1c3ccccc3N=C2C=CC=CC12", "N=C1c3ccccc3N=C2C=CCC=C12", "Nc1c3ccccc3nc2ccccc12",
		"N=C1c3ccccc3Nc2ccccc12" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_1aminoanthraquinone() throws Exception {
	int res = tt.testCase("NC1=C2C(=O)C3=C(C=CC=C3)C(=O)C2=CC=C1", new String[] {
		"OC=3c1ccccc1C(=O)C2=CC=CC(=N)C2=3", "O=C3c1ccccc1C(=O)c2c3cccc2N", "N=C3C=CC=C2C(=O)c1ccccc1C(=O)C23",
		"O=C3c1ccccc1C(=O)c2c3cccc2N", "N=C1C=CCC3=C1C(=O)c2ccccc2C3=O", "O=C2C=1C=CCC(=N)C=1C(=O)c3ccccc23" },
		FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_2aminobenzimidazole() throws Exception {
	int res = tt.testCase("NC1=NC2=CC=CC=C2N1", new String[] { "Nc1nc2ccccc2n1", "N1=C(N=C2C1=CC=CC2)N",
		"N=C1Nc2ccccc2N1", "N=C2N=C1C(=CC=CC1)N2", "N=C1Nc2ccccc2N1", "N=C1N=C2C=CC=CC2(N1)",
		"N=C1N=C2C=CCC=C2(N1)", "N=C1N=C2C=CCCC2(=N1)", "N=C1N=C2C(=N1)CC=CC2", "Nc1nc2ccccc2n1",
		"N=1C(=NC2C=CC=CC=12)N", "N=1C(=NC2=CCC=CC=12)N" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_6amino14benzodioxaane() throws Exception {
	int res = tt.testCase("NC1=CC=C2OCCOC2=C1", new String[] { "Nc1ccc2OCCOc2c1", "N=C1C=C2OCCOC2(=CC1)",
		"Nc1ccc2OCCOc2c1", "N=C2C=CC=1OCCOC=1C2", "N=C1C=CC2OCCOC2(=C1)" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

    public void test_3aminobenzoicAcid() throws Exception {
	int res = tt.testCase("NC1=CC=C2OCCOC2=C1", new String[] { "Nc1ccc2OCCOc2c1", "N=C1C=C2OCCOC2(=CC1)",
		"Nc1ccc2OCCOc2c1", "N=C2C=CC=1OCCOC=1C2", "N=C1C=CC2OCCOC2(=C1)" }, FlagPrintTautomers);

	assertEquals(0, res);
    }

}
