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
	
	
	public void test_Rule_KetoEnol_02_AromSys() throws Exception      
	{	
		int res = tt.testCase("OC1=CC=CC=C1", 
				new String[]{"Oc1=cc=cc=c1" , "O=C1C=CC=CC1"}, 
				FlagPrintTautomers);
		
		assertEquals(0, res);
	}


}
