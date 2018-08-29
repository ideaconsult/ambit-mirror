package ambit2.groupcontribution.test;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.LoggingTool;

import ambit2.smarts.IsomorphismTester;
import ambit2.smarts.SmartsHelper;
import ambit2.smarts.SmartsParser;
import ambit2.smarts.SmartsConst.SSM_MODE;
import ambit2.smarts.groups.GroupMatch;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestCorrectionFactors extends TestCase
{
	public LoggingTool logger;	
	public SmartsParser sp = new SmartsParser();
	public IsomorphismTester isoTester = new IsomorphismTester();
	
	public TestCorrectionFactors()
	{
		logger = new LoggingTool(this);
	}
	
	public static Test suite() 
	{
		return new TestSuite(TestCorrectionFactors.class);
	}
	
	public double getCorrectionFactor(String corFactor, String smiles) throws Exception 
	{
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smiles);
		//TODO
		return 0.0; 
	}

}
