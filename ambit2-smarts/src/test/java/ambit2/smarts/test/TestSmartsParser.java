package ambit2.smarts.test;

import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.LoggingTool;

import ambit2.smarts.IsomorphismTester;
import ambit2.smarts.SmartsHelper;
import ambit2.smarts.SmartsParser;
import ambit2.smarts.SmartsParserError;

public class TestSmartsParser extends TestCase
{
	public SmartsParser smartsParser = new SmartsParser();
	public IsomorphismTester isoTester = new IsomorphismTester();
	public LoggingTool logger;
	public IQueryAtomContainer mQuery;
	public IAtomContainer mTarget;
	SmartsHelper helper = new SmartsHelper(SilentChemObjectBuilder.getInstance());
	
	public TestSmartsParser() 
	{   
		logger = new LoggingTool(this);
		smartsParser.mSupportSmirksSyntax = true;
		smartsParser.setComponentLevelGrouping(true);
	}
	
	
	public static Test suite() {
		return new TestSuite(TestSmartsParser.class);
	}
		
	public boolean matchBooleanResult(String smarts, String smiles) throws Exception 
	{
		IQueryAtomContainer query = smartsParser.parse(smarts);
		mQuery = query;
		String error = smartsParser.getErrorMessages();
		if (!error.equals(""))
		{
			System.out.println("Smarts Parser errors:\n" + error);
			throw(new Exception("Smarts Parser errors:\n" + error));
		}
		
		SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
		IAtomContainer atomContainer = sp.parseSmiles(smiles);
		mTarget = atomContainer;
		smartsParser.setSMARTSData(atomContainer);
		isoTester.setQuery(query);	
		return isoTester.hasIsomorphism(atomContainer);
	}
	
	public void match(String smarts, String smiles, boolean expResult) throws Exception 
	{
		assertEquals("Matching " + smarts + " against " + smiles, expResult, matchBooleanResult(smarts, smiles));
	}
	
	public void parserError(String smarts, String[] expErrors) throws Exception 
	{
		IQueryAtomContainer query = smartsParser.parse(smarts);
		Vector<SmartsParserError> errors = smartsParser.getErrors();
		if (expErrors == null)				
			assertEquals("Parisng " + smarts + " no errors expected!", 0, errors.size());
		else
		{
			assertEquals("Parisng " + smarts + " expected number of errors ", expErrors.length, errors.size());
			for (int i = 0; i < expErrors.length; i++)
			{	
				assertEquals("Parisng " + smarts + " error # " + (i+1), 
							expErrors[i].trim(), errors.get(i).getError().trim());
			}	
		}
	}
	
	public String smartsToQueryToSmarts(String smarts) throws Exception
	{	 
		IQueryAtomContainer qac = smartsParser.parse(smarts);
		if (!smartsParser.getErrorMessages().equals(""))
		{
			System.out.println("Smarts Parser errors:\n" + smartsParser.getErrorMessages());
			throw(new Exception("Smarts Parser errors:\n" + smartsParser.getErrorMessages()));
		}
		
		return helper.toSmarts(qac);
	}
	
	
	public void testSmartsRingClosures() throws Exception 
	{
		match("C1CCCCC1","C1CCCCC1", true); 
		match("C%001CCCCC%01","C1CCCCC1", true);
		match("C-,:1CCCCC1","C1CCCCC1", true);
		match("C-,:1CCCCC-,:1","C1CCCCC1", true);
		match("C%124CCCCC%124","C1CCCCC1", true);
		
		match("C-,=0CC2CCCCC2CC0","C1CC2CCCCC2CC1", true);
		match("C-,=0CC~2CCCCC2CC-,=0","C1CC2CCCCC2CC1", true);
		
		match("C5CCCC-,:52CCCC2","C1CCCC12CCCC2", true);
		match("C5CCCC-,:55CCCC5","C1CCCC12CCCC2", true);  //this is a strange smarts but it must work
		match("C5CCCC-,:5=5CCCC5","C1CCCC1=2CCCC2", true);  //the structure in this case has a 5 valent spiro carbon
		match("C5CCCC-,:5=5CCCC=5","C1CCCC12CCCC=2", true);  //the structure in this case has a 5 valent spiro carbon
	}
	
	public void testSmartsParserErrors() throws Exception 
	{	
		parserError("C5CCCC-,:52CCCC2", null);
		parserError("C5CCCC-,:52CCCC2", new String[]{});  //this another form of the test for the cases with no errors
		
		parserError("C1C",new String[]{
				"There are unclosed ring indices", 
				"Ring index 1 is unclosed"});
		
		parserError("C12C0", new String[]{
				"There are unclosed ring indices", 
				"Ring index 0 is unclosed",
				"Ring index 1 is unclosed",
				"Ring index 2 is unclosed"});
		
		parserError("C-2CCC=2",new String[]{
				"Ring closure index 2 is associated with two different bond types"});
		
		parserError("C-2CCC-,=2",new String[]{
				"Ring closure index 2 is associated with a bond type and a bond expression"});
		
		parserError("C-,=3CCC=3",new String[]{
				"Ring closure index 3 is associated with a bond expression and a bond type"});
		
		parserError("C-,=3CCC-,:3",new String[]{
				"Ring closure index 3 is associated with two different bond expressions"});
		
	}
	
	public void testSmartsToSmarts01() throws Exception 
	{
		String smarts = "C1CCCC1[#8;+;!$(O-N)]";
		String converted_smarts = smartsToQueryToSmarts(smarts);
		assertEquals("Smarts2Smarts: " , smarts, converted_smarts);
	}
	
	public void testSmartsToSmarts02() throws Exception 
	{
		String smarts = "C1CCCC1[#8;+;!$(O-N)]";
		String converted_smarts = smartsToQueryToSmarts(smarts);
		assertEquals("Smarts2Smarts: " , smarts, converted_smarts);
	}
	
	public void testSmartsToSmarts03() throws Exception 
	{
		String smarts = "[C:1][C:2][C:3]";  //This is just a test for the SMIRKS mapping
		String converted_smarts = smartsToQueryToSmarts(smarts);
		assertEquals("Smarts2Smarts: " , smarts, converted_smarts);
	}
	
	public void testSmartsToSmarts04() throws Exception 
	{
		String smarts = "C1C[C&-;!R5]CC1CCC2CCCC2CCCNCCCC3=CC=CC3";
		String converted_smarts = smartsToQueryToSmarts(smarts);
		assertEquals("Smarts2Smarts: " , smarts, converted_smarts);
	}
	
	public void testSmartsToSmarts05() throws Exception 
	{
		String smarts = "CC.CCC.CCN[C;!R5]";  
		String converted_smarts = smartsToQueryToSmarts(smarts);
		assertEquals("Smarts2Smarts: " , smarts, converted_smarts);
	}
	
}
