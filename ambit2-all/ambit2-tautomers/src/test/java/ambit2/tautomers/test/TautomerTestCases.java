package ambit2.tautomers.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.LoggingTool;


public class TautomerTestCases extends TestCase
{
	TestTautomers tt;
	boolean FlagPrintTautomers = false;
	public LoggingTool logger;

	public TautomerTestCases() 
	{   
		logger = new LoggingTool(this);
		
		//Initialization
		tt = new TestTautomers();
		tt.tman.use15ShiftRules(true);
		tt.tman.use17ShiftRules(false);
		
	}

	public static Test suite() {
		return new TestSuite(TautomerTestCases.class);
	}


	public void test_Rule_KetoEnol_01()  throws Exception
	{	
		int res = tt.testCase("O=CCC", 
				new String[]{"O=CCC", "OC=CC"}, 
				FlagPrintTautomers);

		assertEquals(0, res);
	}
	

	public void test_Rule_KetoEnol_02() throws Exception      
	{	
		int res = tt.testCase("OC1=CC=CC=C1", 
				new String[]{"Oc1=cc=cc=c1" , "O=C1C=CC=CC1", "O=C1C=CCC=C1"}, 
				FlagPrintTautomers);

		assertEquals(0, res);
	}

	public void test_Rule_KetoEnol_03() throws Exception      
	{	
		int res = tt.testCase("O1=CC=CN=C1", 
				new String[]{"N=1C=OC=CC=1" , "N1=CO=CC=C1"}, 
				FlagPrintTautomers);

		assertEquals(0, res);
	}


	public void test_Rule_KetoEnol_04() throws Exception      
	{	
		int res = tt.testCase("O1=CC=CN=C1CC", 
				new String[]{"O1=CC=CNC1=CC" , "N1=CC=COC1=CC","N1=CC=CO=C1CC", "N1=CCC=OC1=CC", "N=1C=CC=OC=1CC"}, 
				FlagPrintTautomers);

		assertEquals(0, res);
	}


	public void test_Rule_KetoEnol_05()  throws Exception
	{	
		int res = tt.testCase("O=C(CCC)CC", 
				new String[]{"O=C(CCC)CC", "OC(=CCC)CC", "OC(=CC)CCC"}, 
				FlagPrintTautomers);

		assertEquals(0, res);
	}


	public void test_Rule_AminImin_01() throws Exception      
	{	
		int res = tt.testCase("N=CCC", 
				new String[]{"N=CCC", "NC=CC"}, 
				FlagPrintTautomers);

		assertEquals(0, res);
	}


	public void test_Rule_AminImin_02() throws Exception      
	{	
		int res = tt.testCase("NC1=CC=CC=C1", 
				new String[]{"Nc1=cc=cc=c1" , "N=C1C=CC=CC1", "N=C1C=CCC=C1"}, 
				FlagPrintTautomers);

		assertEquals(0, res);
	}


	public void test_Rule_AminImid_01() throws Exception      
	{	
		int res = tt.testCase("O=CNCCC", 
				new String[]{"O=CNCCC", "OC=NCCC"}, 
				FlagPrintTautomers);

		assertEquals(0, res);
	}


	public void test_Rule_AminImid_02() throws Exception      
	{	
		int res = tt.testCase("OC1=NC=CC=C1",
				new String[]{"Oc=1n=cc=cc=1","Oc1=nc=cc=c1","O=C1C=CC=CN1","O=C1N=CC=CC1","O=C1N=CCC=C1"}, 
				FlagPrintTautomers);

		assertEquals(0, res);
	}

	public void test_Rule_NitrosoOxime_01() throws Exception      
	{	
		int res = tt.testCase("O=NCCC", 
				new String[]{"O=NCCC", "ON=CCC", "ONC=CC"}, 
				FlagPrintTautomers);

		assertEquals(0, res);
	}
	

	public void test_Rule_AzoHydrazone_01() throws Exception      
	{	
		int res = tt.testCase("CN=NCC", 
				new String[]{"CNNC=C", "CNN=CC", "C=NNCC","CN=NCC"}, 
				FlagPrintTautomers);

		assertEquals(0, res);
	}

	public void test_Rule_ThioketoThioenol_01() throws Exception      
	{	
		int res = tt.testCase("S=CCC", 
				new String[]{"S=CCC", "SC=CC"}, 
				FlagPrintTautomers);

		assertEquals(0, res);
	}

	public void test_Rule_ThioketoThioenol_02() throws Exception      
	{	
		int res = tt.testCase("SC1=CC=CC=C1", 
				new String[]{"Sc1=cc=cc=c1" , "S=C1C=CC=CC1", "S=C1C=CCC=C1"}, 
				FlagPrintTautomers);

		assertEquals(0, res);
	}

	public void test_Rule_ThionitrosoThiooxime_01() throws Exception      
	{	
		int res = tt.testCase("S=NCCC", 
				new String[]{"S=NCCC", "SN=CCC", "SNC=CC"}, 
				FlagPrintTautomers);

		assertEquals(0, res);
	}

	

	public void test_Rule_AmidineImidine_01() throws Exception      
	{	
		int res = tt.testCase("N=CNCCCCC", 
				new String[]{"NC=NCCCCC", "N=CNCCCCC"}, 
				FlagPrintTautomers);

		assertEquals(0, res);
	}


	public void test_Rule_AmidineImidine_02() throws Exception      
	{	
		int res = tt.testCase("n1=cc=cn=c1", 
				new String[]{"n1=cc=cn=c1"}, 
				FlagPrintTautomers);

		assertEquals(0, res);
	}
	
	public void test_Rule_AmidineImidine_02_noIsoCheck() throws Exception      
	{	
		tt.tman.tautomerFilter.FlagApplyDuplicationCheckIsomorphism = false;
		int res = tt.testCase("n1=cc=cn=c1", 
				new String[]{"n1=cc=cn=c1", "n1c=cc=nc=1"}, 
				FlagPrintTautomers);
		
		tt.tman.tautomerFilter.FlagApplyDuplicationCheckIsomorphism = true; //default is restored
		
		assertEquals(0, res);
	}


	public void test_Rule_DiazoaminoDiazoamino_01() throws Exception      
	{	
		int res = tt.testCase("N=NNCCC", 
				new String[]{"NNNC=CC", "NNN=CCC", "NN=NCCC", "N=NNCCC"}, 
				FlagPrintTautomers);

		assertEquals(0, res);
	}
	
	public void test_Rule_DiazoaminoDiazoamino_02() throws Exception      
	{	
		int res = tt.testCase("N1=NC=CC=N1",
				new String[]{"n1=nc=cc=n1"},
				FlagPrintTautomers);
		
		assertEquals(0, res);
	}


	public void test_Rule_DiazoaminoDiazoamino_02_noIsoCheck() throws Exception      
	{	
		tt.tman.tautomerFilter.FlagApplyDuplicationCheckIsomorphism = false;
		int res = tt.testCase("N1=NC=CC=N1",
				new String[]{"n1=nc=cc=n1", "n1n=cc=cn=1"},
				FlagPrintTautomers);
		
		tt.tman.tautomerFilter.FlagApplyDuplicationCheckIsomorphism = true; //default is restored

		assertEquals(0, res);
	}

	public void test_Rule_ThioamideIminothiol_01() throws Exception      
	{	
		int res = tt.testCase("S=CNCC", 
				new String[]{"S=CNCC", "SC=NCC"}, 
				FlagPrintTautomers);

		assertEquals(0, res);
	}


	public void test_Rule_ThioamideIminothiol_02 () throws Exception      
	{	
		int res = tt.testCase("N1=CCC=CC1=S",
				new String[]{"n1ccccc1S", "C=1C=CC(NC=1)=S", "n1ccccc1S","N1=CC=CCC1=S","N1=CCC=CC1=S"}, 
				FlagPrintTautomers);

		assertEquals(0, res);
	}


	public void test_Rule_ThioamideIminothiol_03() throws Exception      
	{	
		int res = tt.testCase("S1=CN=CC=C1", 
				new String[]{"N=1C=CC=SC=1", "N1=CC=CS=C1"},				
				FlagPrintTautomers);

		assertEquals(0, res);
	}


	public void test_Rule_NitrosamineDiazohydroxide_01() throws Exception      
	{	
		int res = tt.testCase("O=NNCC", 
				new String[]{"ONNC=C", "ONN=CC", "ON=NCC", "O=NNCC"}, 
				FlagPrintTautomers);

		assertEquals(0, res);
	}


	/*
	 * [N+] ????? to check
	 * 
	public void test_Rule_NitrosamineDiazohydroxide_02() throws Exception      
	{	
		int res = tt.testCase("O=N1N=CC=CC1", 
				//new String[]{"O=N1N=CC=CC1", "ON=1N=CC=CC=1"}, 
				new String[]{"O=N1N=CC=CC1", "ON=1N=CC=CC=1", "O=N1C=CC=CN1", "ON1=NC=CC=C1"},
				FlagPrintTautomers);

		assertEquals(0, res);
	}
	*/
	
	public void test_Rule_AminImin_03() throws Exception      
	{	
		int res = tt.testCase("N=C(CCC)CC", 
				new String[]{"N=C(CC)CCC", "NC(=CCC)CC", "NC(=CC)CCC"}, 
				FlagPrintTautomers);

		assertEquals(0, res);
	}


	public void test_Rule_KetoEnol_AminImin_01()  throws Exception
	{	
		int res = tt.testCase("O=C(CN=CC)CC", 
				new String[]{"O=C(CN=CC)CC", "OC(=CN=CC)CC", "OC(=CC)CN=CC", "O=C(CNC=C)CC", "OC(C=NC=C)CC", "OC(=CNC=C)CC", "OC(=CC)CNC=C"}, 
				FlagPrintTautomers);

		assertEquals(0, res);
	}


	public void test_Rule_KetoEnol_AminImin_AmidImid_01()  throws Exception
	{	
		int res = tt.testCase("O=C1NC=CCC1", 
				new String[]{"OC1=CCC=CN1", "O=C1NC=CCC1", "OC=1N=CCCC=1", "O=C1N=CCCC1", "OC1=NC=CCC1"}, 
				FlagPrintTautomers);

		assertEquals(0, res);
	}


	public void test_Rule_KetoEnol_ThioketoThioEnol_01()  throws Exception
	{	
		int res = tt.testCase("O=C1S(=CC)C=CCC1", 
				new String[]{"OC1=CCC=CS1(C=C)", "OC=1CCC=CS=1(C=C)", "O=C1CCC=CS1(C=C)", "OC1=CCCC=S1(C=C)", "O=C1CCCC=S1(C=C)", "OC1=CCC=CS1(=CC)", "O=C1CCC=CS1(=CC)"}, 
				FlagPrintTautomers);

		assertEquals(0, res);
	}

	/*
	public void test_Rule_KetoEnol_AminImin_AzoHydrazone_01()  throws Exception
	{	
		int res = tt.testCase("O=C1N(=N)CN=CC1", 
				new String[]{"O=C1C=CNC=N1(N)", "O=C1C=CN=CN1(N)", "OC=1C=CN=CN=1(N)", "N=N1C=NC=CC1(O)", "OC1=CC=NC=N1(N)", "O=C1N(N)=CN=CC1","O=C1C=CNCN1(=N)", "N=N1C(O)=CC=NC1", "O=C1N(=N)CN=CC1"}, 
				FlagPrintTautomers);

		assertEquals(0, res);
	}
	*/

	public void test_Rule_AminImin_AmidineImidine_01()  throws Exception
	{	
		int res = tt.testCase("CNC(=N)N=CCO", 
				new String[]{"N=C(NC=CO)NC", "O=CCNC(=N)NC", "OC=CN=C(N)NC", "O=CCN=C(N)NC", "OC=CNC(=NC)N", "O=CCNC(=NC)N","N=C(N=CCO)NC", "OCC=NC(=NC)N"}, 
				FlagPrintTautomers);

		assertEquals(0, res);
	}

	public void test_Rule_AminImin_AmidineImidine_NitrosoOxime_01()  throws Exception
	{	
		int res = tt.testCase("CNC1N=CCC=N1O", 
				new String[]{"ON1C=CC=NC1(NC)", "ON1=CC=CNC1(NC)", "ON1=CCC=NC1(NC)", "ON1=C(NC=CC1)NC", "ON1C(=NC=CC1)NC", "ON1C(=NC)NC=CC1","ON1=C(N=CCC1)NC", "ON1C(N=CCC1)=NC", "O=N1CC=CNC1(NC)","O=N1CCC=NC1(NC)"}, 
				FlagPrintTautomers);

		assertEquals(0, res);
	}

	public void test_Rule_AminImin_AmidineImidine_ThioNitrosoThioOxime_01()  throws Exception
	{	
		int res = tt.testCase("CNC1N=CCC=N1S", 
				new String[]{"SN1C=CC=NC1(NC)", "SN1=CC=CNC1(NC)", "SN1=CCC=NC1(NC)", "SN1=C(NC=CC1)NC", "SN1C(=NC=CC1)NC", "SN1C(=NC)NC=CC1","SN1=C(N=CCC1)NC", "SN1C(N=CCC1)=NC", "S=N1CC=CNC1(NC)","S=N1CCC=NC1(NC)"}, 
				FlagPrintTautomers);

		assertEquals(0, res);
	}

	public void test_Rule_AminImin_AmidineImidine_AzoHydrazone_01()  throws Exception
	{	
		int res = tt.testCase("CNC1N=CCC=N1N", 
				new String[]{"NN1C=CC=NC1(NC)", "NN1=CC=CNC1(NC)", "NN1=CCC=NC1(NC)", "NN1=C(NC=CC1)NC", "NN1C(=NC=CC1)NC", "NN1C(=NC)NC=CC1","NN1=C(N=CCC1)NC", "NN1C(N=CCC1)=NC", "N=N1CC=CNC1(NC)","N=N1CCC=NC1(NC)"}, 
				FlagPrintTautomers);

		assertEquals(0, res);
	}


	public void test_Rule_AminImin_ThionitrosamineThioDiazoHydrazone_AzoHydrazone_01()  throws Exception
	{	
		int res = tt.testCase("SN=NCC", 
				new String[]{"C=CNNS", "N(=CC)NS", "N(=NS)CC", "N(NCC)=S"}, 
				FlagPrintTautomers);

		assertEquals(0, res);
	}

	public void test_Rule_CombinationOf5rules_01()  throws Exception
	{	
		int res = tt.testCase("C1CC(S)C(=O)NN1", 
				new String[]{"OC=1NNCCC=1S", "O=C1NNCCC1S", "OC1NNCC=C1S", "OC1NNCCC1=S","OC1=NNCCC1S","OC1NNC=CC1S","OC1NN=CCC1S", "OC1N=NCCC1S"}, 
				FlagPrintTautomers);

		assertEquals(0, res);
	}


	public void test_Rule_CombinationOfRules_04()  throws Exception  
	{	
		int res = tt.testCase("O=C1C=CNC=C1", 
				new String[]{"Oc1ccncc1", "O=C1C=CN=CC1", "O=C1C=CNC=C1"},  
				FlagPrintTautomers);

		assertEquals(0, res);
	}

	public void test_Rule_CombinationOfRules_05()  throws Exception 
	{	
		int res = tt.testCase("S=C1C=CNC=C1",				 
				new String[]{"Sc1ccncc1", "C1=CC(C=CN1)=S", "N=1C=CC(CC=1)=S"},
				FlagPrintTautomers);

		assertEquals(0, res);
	}

	public void test_Rule_CombinationOfRules_06()  throws Exception  
	{	
		int res = tt.testCase("N=C1C=CNC=C1",  
				new String[]{"n1ccc(N)cc1", "N=C1C=CN=CC1", "N=C1C=CNC=C1"},
				FlagPrintTautomers);

		assertEquals(0, res);
	}

	public void test_Rule_CombinationOfRules_08()  throws Exception  
	{	
		int res = tt.testCase("OC(=CN)C", 
				new String[]{"OC(=CN)C", "OC(=C)CN", "O=C(C)CN", "N=CC(O)C"}, 
				FlagPrintTautomers);

		assertEquals(0, res);
	}

	public void test_Rule_CombinationOfRules_09()  throws Exception  
	{	
		int res = tt.testCase("S=NC(N)C", 
				new String[]{"C=C(N)NS", "N=C(NS)C", "N(=C(N)C)S", "N(C(N)C)=S"}, 
				FlagPrintTautomers);

		assertEquals(0, res);
	}

	public void test_Rule_CombinationOfRules_10 ()  throws Exception  
	{	
		int res = tt.testCase("C=C(N)S", 
				new String[]{"C=C(N)S", "NC(C)=S", "N=C(C)S"}, 
				FlagPrintTautomers);

		assertEquals(0, res);
	}

	public void test_Rule_CombinationOfRules_11 ()  throws Exception  
	{	
		int res = tt.testCase("OC(=C)S", 
				new String[]{"OC(=C)S", "O=C(C)S", "OC(C)=S"}, 
				FlagPrintTautomers);

		assertEquals(0, res);
	}

	public void test_Rule_01 ()  throws Exception  
	{	

		int res = tt.testCase("OC#CC", 
				new String[]{"OC#CC", "O=C=CC"}, 
				FlagPrintTautomers);

		assertEquals(0, res);
	}

	public void test_Rule_06 ()  throws Exception  
	{	
		tt.tman.use17ShiftRules(true);
		int res = tt.testCase("O=CC=CC=CCCC", 
				new String[]{"O=CC=CC=CCCC", "OC=CC=CC=CCC", "O=CCC=CC=CCC", "O=CC=CCC=CCC"}, 
				FlagPrintTautomers);
		tt.tman.use17ShiftRules(false);
		
		assertEquals(0, res);
	}
	

	public void test_Rule_06_NoWarningFilter ()  throws Exception  
	{	
		tt.tman.tautomerFilter.FlagApplyWarningFilter = false;
		tt.tman.use17ShiftRules(true);
		int res = tt.testCase("O=CC=CC=CCCC", 
				new String[]{"OC=CC=C=CCCC", "O=CCC=C=CCCC", "OC=C=CC=CCCC", "O=CC=CC=CCCC", "OC=CC=CC=CCC", "O=CCC=CC=CCC", "OC=C=CCC=CCC", "O=CC=CCC=CCC"}, 
				FlagPrintTautomers);
		tt.tman.tautomerFilter.FlagApplyWarningFilter = true;
		tt.tman.use17ShiftRules(false);

		assertEquals(0, res);
	}
	
	
	
	//--
	
	public void test_Rule_02 ()  throws Exception  
	{	

		int res = tt.testCase("NC#CC", 
				new String[]{"NC#CC", "N=C=CC"}, 
				FlagPrintTautomers);

		assertEquals(0, res);
	}

	public void test_Rule_03 ()  throws Exception  
	{	

		int res = tt.testCase("SC#CC", 
				new String[]{"SC#CC", "S=C=CC"}, 
				FlagPrintTautomers);

		assertEquals(0, res);
	}

	public void test_Rule_Rule_OSC()  throws Exception  
	{	
		int res = tt.testCase("O=SC", 
				new String[]{"OS=C", "O=SC"}, 
				FlagPrintTautomers);

		assertEquals(0, res);
	}
	public void test_Rule_Rule_OSC_01()  throws Exception  
	{	
		int res = tt.testCase("O=SCC", 
				new String[]{"OSC=C", "O=SCC", "OS=CC"}, 
				FlagPrintTautomers);

		assertEquals(0, res);
	}

	public void test_Rule_CombinationOfRules_07()  throws Exception  
	{	
		int res = tt.testCase("OS=C(CC)C", 
				new String[]{"OS=C(CC)C", "OSC(=C)CC", "OS=C(C)CC", "O=SC(C)CC"}, 
				FlagPrintTautomers);

		assertEquals(0, res);
	}

}
