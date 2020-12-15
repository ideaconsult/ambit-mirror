package ambit2.sln.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.LoggingTool;

import ambit2.sln.SLNContainer;
import ambit2.sln.SLNHelper;
import ambit2.sln.SLNParser;
import ambit2.smarts.IsomorphismTester;
import ambit2.smarts.SmartsHelper;
import ambit2.smarts.SmartsParser;

public class TestSLNIsomorphism extends TestCase
{	
	public LoggingTool logger;
	public SLNParser slnParser = new SLNParser();
	public SLNHelper slnHelper = new SLNHelper();
	public IsomorphismTester isoTester = new IsomorphismTester();

	public TestSLNIsomorphism() 
	{   
		logger = new LoggingTool(this);
	}

	public static Test suite() {
		return new TestSuite(TestSLNIsomorphism.class);
	}

	public void slnMatch(String sln, String smiles, boolean expectedResult) throws Exception
	{	
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smiles);	
		SLNContainer query = slnParser.parse(sln);
		if (!slnParser.getErrorMessages().equals(""))
		{
			throw (new Exception("SLN " + sln + " parser errors: " + slnParser.getErrorMessages()));
		}
		
		isoTester.setQuery(query);
		SmartsParser.prepareTargetForSMARTSSearch(true, false, false, false, false, false, mol); //flags are set temporary
		 
		boolean result = isoTester.hasIsomorphism(mol);
		assertEquals(sln+" matched in " + smiles, expectedResult, result);
	}
	
	
	public void testSimpleSLNQueries() throws Exception 
	{
		slnMatch("C[1]CCN@1", "C1CCN1", true);
		slnMatch("CCCC", "C1CCC1", true);
		slnMatch("CCCN", "CCCCC", false);
		slnMatch("AnyN", "CCCCN", true);
		slnMatch("AnyN", "CCCC=N", false);
		slnMatch("Any=N", "CCCC=N", true);
		slnMatch("Any=Any-C", "CCCC=N", true);
		slnMatch("N=Any-Any", "CCCC=N", true);
		slnMatch("N=Any-Any=Any", "CCCC=N", false);
		
		slnMatch("N[charge<0]","CC(C)CC[N-]", true);
		slnMatch("N[charge<0]","CC(C)CC[N+]", false);
		slnMatch("N[charge<0]","CC(C)CCN", false);
		slnMatch("N[charge<=0]","CC(C)CCN", true);
		
		slnMatch("C[hc=0]","CC(C)CCC", false);
		slnMatch("C[hc=1]","CC(C)CCC", true);
		slnMatch("C[hc=4]","CC(C)CCC", false);
		slnMatch("C[hc=0|hc=0]","CC(C)CCC", false);
		slnMatch("C[hc=0|hc=1]","CC(C)CCC", true);
		
	}
	
	
}
