package ambit2.tautomers.test;

import org.openscience.cdk.tools.LoggingTool;

import junit.framework.TestCase;

public class TestEnergyRanks extends TestCase 
{
	TestTautomers tt;
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
}
