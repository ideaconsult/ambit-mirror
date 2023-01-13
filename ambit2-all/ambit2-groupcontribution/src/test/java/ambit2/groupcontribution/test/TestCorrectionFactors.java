package ambit2.groupcontribution.test;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import ambit2.groupcontribution.GCMParser;
import ambit2.groupcontribution.correctionfactors.ICorrectionFactor;
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
	public ILoggingTool logger;	
	public SmartsParser sp = new SmartsParser();
	public IsomorphismTester isoTester = new IsomorphismTester();
	public GCMParser gcmParser = new GCMParser(sp, isoTester);
	
	public TestCorrectionFactors()
	{
		logger = LoggingToolFactory.createLoggingTool(TestCorrectionFactors.class);
	}
	
	public static Test suite() 
	{
		return new TestSuite(TestCorrectionFactors.class);
	}
	
	public double getCorrectionFactor(String corFactor, String smiles) throws Exception 
	{
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smiles);
		gcmParser.getErrors().clear();
		ICorrectionFactor cf = gcmParser.getCorrectionFactorFromString(corFactor);
		if (cf == null)
			throw new Exception(gcmParser.getAllErrorsAsString());
		return cf.calculateFor(mol); 
	}
	
	public void test01() throws Exception 
	{
		String corFactor = "G(CC)";
		String smi = "CCCN";
		double cfVal = getCorrectionFactor(corFactor, smi);
		assertEquals("Correction factor " + corFactor + " for " + smi, 2.0, cfVal);		
	}
	
	public void test02() throws Exception 
	{
		String corFactor = "G([C;!$(CN)][C;!$(CN)])";
		String smi = "CCCN";
		double cfVal = getCorrectionFactor(corFactor, smi);
		assertEquals("Correction factor " + corFactor + " for " + smi, 1.0, cfVal);		
	}
	

}
