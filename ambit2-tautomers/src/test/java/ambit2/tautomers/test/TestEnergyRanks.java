package ambit2.tautomers.test;

import org.openscience.cdk.tools.LoggingTool;

import junit.framework.TestCase;

public class TestEnergyRanks extends TestCase 
{	
	TestTautomers tt;
	double eps = 0.0000001;
	boolean FlagPrintTautomers = false;
	public LoggingTool logger;

	public TestEnergyRanks() 
	{
		logger = new LoggingTool(this);

		// Initialization
		tt = new TestTautomers();
		tt.tman.getKnowledgeBase().use15ShiftRules(true);
		tt.tman.getKnowledgeBase().use17ShiftRules(false);
		tt.tman.FlagCheckNumOfRegistrationsForIncrementalAlgorithm = false;
		tt.tman.maxNumOfBackTracks = 100000;
	}
	
	public void test_00() throws Exception 
	{	
		int res = tt.testCaseEnergyRanks("O=CCC", 
									new double[] {0.0, 0.315}, 
									FlagPrintTautomers, eps);
		assertEquals(0, res);
	}
}
