package ambit2.tautomers.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.LoggingTool;


public class TautomerTestCases extends TestCase
{
	TestTautomers tt = new TestTautomers();
	boolean FlagPrintTautomers = false;
	public LoggingTool logger;

	public TautomerTestCases() 
	{   
		logger = new LoggingTool(this);
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

	public void test_Rule_NitrosoOxime_02() throws Exception      
	{	
		int res = tt.testCase("O=N1CC=CC=C1", 
				new String[]{"O=N1CC=CC=C1", "ON1=CC=CC=C1"}, 
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

	public void test_Rule_ThionitrosoThiooxime_02() throws Exception      
	{	
		int res = tt.testCase("S=N1CC=CC=C1", 
				new String[]{"S=N1CC=CC=C1", "SN1=CC=CC=C1"}, 
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
				new String[]{"N=1C=CC=CC=1S", "C=1C=CC(NC=1)=S", "N1=CC=CC=C1S","N1=CC=CCC1=S","N1=CCC=CC1=S"}, 
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
	


}
