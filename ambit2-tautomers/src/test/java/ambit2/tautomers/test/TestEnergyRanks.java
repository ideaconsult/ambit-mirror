package ambit2.tautomers.test;

import org.openscience.cdk.tools.LoggingTool;

import junit.framework.TestCase;

public class TestEnergyRanks extends TestCase 
{	
	TestTautomers tt;
	double eps = 0.0001;
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
		//The following tests are perfomed for the old energy ranking method
		
		int res = tt.testCaseEnergyRanks("O=CCC", 
									new double[] {0.0, 0.315}, 
									FlagPrintTautomers, eps);
		assertEquals(0, res);
		
		res = tt.testCaseEnergyRanks("N=CCC", 
				new double[] {0.0, 0.037}, 
				FlagPrintTautomers, eps);
		assertEquals(0, res);
		
		
		res = tt.testCaseEnergyRanks("O=CNCC", 
				new double[] {0.0, 0.673}, 
				FlagPrintTautomers, eps);
		assertEquals(0, res);
		
		
		res = tt.testCaseEnergyRanks("O=NC", 
				new double[] {0.025, 0.0}, 
				FlagPrintTautomers, eps);
		assertEquals(0, res);
		
		res = tt.testCaseEnergyRanks("O=NCCC", 
				new double[] {0.025, 0.0, 0.037}, 
				FlagPrintTautomers, eps);
		assertEquals(0, res);
		
		
		res = tt.testCaseEnergyRanks("N=NCCC", 
				new double[] {0.0, 0.137, 0.037}, 
				FlagPrintTautomers, eps);
		assertEquals(0, res);
		
		
		res = tt.testCaseEnergyRanks("S=CCCC", 
				new double[] {0.246, 0.0}, 
				FlagPrintTautomers, eps);
		assertEquals(0, res);
		
		
		res = tt.testCaseEnergyRanks("S=NCCC", 
				new double[] {0.983, 0.0, 0.037}, 
				FlagPrintTautomers, eps);
		assertEquals(0, res);
		
		
		res = tt.testCaseEnergyRanks("N=CNCC", 
				new double[] {0.0, 0.137}, 
				FlagPrintTautomers, eps);
		assertEquals(0, res);
		
		
		res = tt.testCaseEnergyRanks("N=NNCCC", 
				new double[] {0.0, 0.173, 0.137, 0.037}, 
				FlagPrintTautomers, eps);
		assertEquals(0, res);
		
		
		res = tt.testCaseEnergyRanks("S=CNCCC", 
				new double[] {0.0, 0.068}, 
				FlagPrintTautomers, eps);
		assertEquals(0, res);
		
		
		res = tt.testCaseEnergyRanks("O=NNCCC", 
				new double[] {0.0, 0.639, 0.137, 0.037}, 
				FlagPrintTautomers, eps);
		assertEquals(0, res);
		
		
		res = tt.testCaseEnergyRanks("O=CCCCC=N", 
				new double[] {0.0, 0.315, 0.352, 0.037}, 
				FlagPrintTautomers, eps);
		assertEquals(0, res);
		
		
		res = tt.testCaseEnergyRanks("S=CCCCC=N", 
				new double[] {0.246, 0.0, 0.037, 0.283}, 
				FlagPrintTautomers, eps);
		assertEquals(0, res);
		
		
		res = tt.testCaseEnergyRanks("O=CCCCC=O", 
				new double[] {0.0, 0.315, 0.630}, 
				FlagPrintTautomers, eps);
		assertEquals(0, res);
		
		
		res = tt.testCaseEnergyRanks("S=CCCCC=O", 
				new double[] {0.246, 0.0, 0.315, 0.561}, 
				FlagPrintTautomers, eps);
		assertEquals(0, res);
		
		
		res = tt.testCaseEnergyRanks("O=CNCCC=O", 
				new double[] {0.0, 0.315, 0.988, 0.673}, 
				FlagPrintTautomers, eps);
		assertEquals(0, res);
		
		
		res = tt.testCaseEnergyRanks("O=CNCCC=N", 
				new double[] {0.710, 0.673, 0.037, 0.0}, 
				FlagPrintTautomers, eps);
		assertEquals(0, res);
		
		
		res = tt.testCaseEnergyRanks("O=CNCCCN=O", 
				new double[] {0.710, 0.037, 0.673, 0.0, 0.698, 0.025}, 
				FlagPrintTautomers, eps);
		assertEquals(0, res);
		
		
		res = tt.testCaseEnergyRanks("O=CNCCC=S", 
				new double[] {0.0, 0.673, 0.919, 0.246}, 
				FlagPrintTautomers, eps);
		assertEquals(0, res);
		
		
		res = tt.testCaseEnergyRanks("O=CCCCCN=N", 
				new double[] {0.352, 0.037, 0.452, 0.137, 0.315, 0.0}, 
				FlagPrintTautomers, eps);
		assertEquals(0, res);
		
		
		res = tt.testCaseEnergyRanks("O=CCCCCN=S", 
				new double[] {0.352, 0.037, 0.315, 0.0, 1.298, 0.983}, 
				FlagPrintTautomers, eps);
		assertEquals(0, res);
		
		
		res = tt.testCaseEnergyRanks("O=CCCCCNC=N", 
				new double[] {0.0, 0.452, 0.315, 0.137}, 
				FlagPrintTautomers, eps);
		assertEquals(0, res);
		
		
		res = tt.testCaseEnergyRanks("O=CCCCCNN=N", 
				new double[] {0.0, 0.315, 0.137, 0.452, 0.037, 0.352, 0.173, 0.488}, 
				FlagPrintTautomers, eps);
		assertEquals(0, res);
		
		
		res = tt.testCaseEnergyRanks("O=CCCCCNC=S", 
				new double[] {0.383, 0.068, 0.315, 0.0}, 
				FlagPrintTautomers, eps);
		assertEquals(0, res);
		
				
		res = tt.testCaseEnergyRanks("O=CCCCCNN=O", 
				new double[] {0.352, 0.037, 0.452, 0.137, 0.954, 0.639, 0.315, 0.0}, 
				FlagPrintTautomers, eps);
		assertEquals(0, res);
		
		
		res = tt.testCaseEnergyRanks("N=CCCCCN=N", 
				new double[] {0.074, 0.037, 0.174, 0.137, 0.037, 0.0}, 
				FlagPrintTautomers, eps);
		assertEquals(0, res);
		
		
		res = tt.testCaseEnergyRanks("N=CCCCCN=S", 
				new double[] {0.074, 0.037, 0.037, 0.0, 1.02, 0.983}, 
				FlagPrintTautomers, eps);
		assertEquals(0, res);
		
		
		res = tt.testCaseEnergyRanks("N=CCCCCNC=N", 
				new double[] {0.174, 0.137, 0.037, 0.0}, 
				FlagPrintTautomers, eps);
		assertEquals(0, res);
		
		
		res = tt.testCaseEnergyRanks("N=CCCCCNN=N", 
				new double[] {0.21, 0.173, 0.074, 0.037, 0.174, 0.137, 0.037, 0.0}, 
				FlagPrintTautomers, eps);
		assertEquals(0, res);
		
		
		res = tt.testCaseEnergyRanks("N=CCCCCNC=S", 
				new double[] {0.105, 0.068, 0.037, 0.0}, 
				FlagPrintTautomers, eps);
		assertEquals(0, res);
		
		
		res = tt.testCaseEnergyRanks("N=CCCCCNN=O", 
				new double[] {0.074, 0.037, 0.174, 0.137, 0.676, 0.639, 0.037, 0.0}, 
				FlagPrintTautomers, eps);
		assertEquals(0, res);
	}
}
