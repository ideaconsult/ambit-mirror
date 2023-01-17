package ambit2.smarts.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import ambit2.smarts.SmartsManager;

public class TestSmartsSearchCLG extends TestCase
{
	public ILoggingTool logger;
	SmartsManager man = new SmartsManager(SilentChemObjectBuilder.getInstance());
	
	public TestSmartsSearchCLG() 
	{   
		logger = LoggingToolFactory.createLoggingTool(TestSmartsSearchCLG.class);
		//TestUtilities.man is used by function boolSearch()
		TestUtilities.man.setUseCDKIsomorphismTester(false); //by default it is true
	}
	
	public static Test suite() {
		return new TestSuite(TestSmartsSearchCLG.class);
	}
	
	/**
	 * Tests of Component Level Grouping with the examples from the
	 * Daylight SMARTS tutorial
	 */
	public void testCLG01()  throws Exception 
	{	
		assertEquals(1, TestUtilities.boolSearch("C.C","CCCC"));
	}
	
	public void testCLG02()   throws Exception
	{	
		assertEquals(1, TestUtilities.boolSearch("(C.C)","CCCC"));
	}
	
	public void testCLG03()  throws Exception
	{	
		assertEquals(0, TestUtilities.boolSearch("(C).(C)","CCCC"));
	}
	
	public void testCLG04()  throws Exception
	{	
		assertEquals(1, TestUtilities.boolSearch("(C).(C)","CCCC.CCCC"));
	}
	
	public void testCLG05()  throws Exception
	{	
		assertEquals(1, TestUtilities.boolSearch("(C).C","CCCC"));
	}
	
	public void testCLG06()  throws Exception
	{	
		assertEquals(1, TestUtilities.boolSearch("(C).(C).C","CCCC.CCCC"));
	}
	
	
	/**
	 * Additional tests of Component Level Grouping
	 */
	public void testCLG07()  throws Exception
	{	
		assertEquals(0, TestUtilities.boolSearch("(CCCC.CC.CCCN).N.C", "CCCCC.CCCN"));
		assertEquals(0,TestUtilities.boolSearch("(Cl.CCCC.CC.CCCCN).N.C", "CCCCC.CCCN"));
		assertEquals(1, TestUtilities.boolSearch("(CCCC.CC).(CCCN).N.C", "CCCCC.CCCN"));
		assertEquals(0, TestUtilities.boolSearch("(CCBr.CCN).(OCC)", "BrCCCCC.CCCN.OCCC"));
		assertEquals(1, TestUtilities.boolSearch("(CCBr).(CCN).(OCC)", "BrCCCCC.CCCN.OCCC"));
		assertEquals(1, TestUtilities.boolSearch("(CC[Br,Cl]).(CCN).(OCC)", "BrCCCCC.CCCN.OCCC"));
	}
	
	
	
	
	
}
