package ambit2.sln.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import ambit2.sln.SLNContainer;
import ambit2.sln.SLNHelper;
import ambit2.sln.SLNParser;

public class TestSLNParser extends TestCase
{
	public ILoggingTool logger;
	public SLNParser slnParser = new SLNParser();
	public SLNHelper slnHelper = new SLNHelper();
	
	public TestSLNParser() 
	{   
		logger = LoggingToolFactory.createLoggingTool(TestSLNParser.class);
	}
	
	public static Test suite() {
		return new TestSuite(TestSLNParser.class);
	}
	
	public void sln2sln(String sln) throws Exception
	{
		SLNContainer container = slnParser.parse(sln);
		String error = slnParser.getErrorMessages(); 
		if (!error.equals(""))
		{	 
			System.out.println("SLN Parser errors:\n" + error);
			throw(new Exception("SLN Parser errors:\n" + error));
		}
		
		String sln1 = slnHelper.toSLN(container);
		assertEquals(sln, sln1);
	}
	
	public void slnParserError(String sln, String expectedError) throws Exception
	{
		SLNContainer container = slnParser.parse(sln);
		String error = slnParser.getErrorMessages();
		if (expectedError.equals(""))
			assertEquals("",error);
		else
			assertEquals(error, true, error.startsWith(expectedError));
	}
	
	
	public void testSLN2SLN() throws Exception 
	{
		//This flag is set TRUE in order to generate SLNs which are identical to the input SLNs
		slnHelper.FlagPreserveOriginalAtomID = true;
		
		sln2sln("CCCCC");
		sln2sln("C[1]CCCC@1");
		sln2sln("C[1]CCCC@1");
		sln2sln("CH2[5]CCCCH2@5");
		sln2sln("CC(C)(C)C");
		sln2sln("CC(=O[a=b])CC[a1=b1]");
		sln2sln("CC-[a=b]CCOCCCN");
		sln2sln("AnyCC");
		sln2sln("C=Any=C[a=b]C");
		sln2sln("AnyCC[!src=3]");
		sln2sln("AnyCC[!r]");
	}
	
	
	public void testSLNParserErrors() throws Exception 
	{
		slnParserError("CCC","");
		slnParserError("CCC<","Incorrect molecule attributes section at the end");		
	}
	
}
