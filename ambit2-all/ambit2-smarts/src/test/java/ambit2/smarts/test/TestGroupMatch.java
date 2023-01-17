package ambit2.smarts.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import ambit2.smarts.IsomorphismTester;
import ambit2.smarts.SmartsHelper;
import ambit2.smarts.SmartsParser;
import ambit2.smarts.SmartsConst.SSM_MODE;
import ambit2.smarts.groups.GroupMatch;

public class TestGroupMatch extends TestCase
{
	public ILoggingTool logger;	
	public SmartsParser sp = new SmartsParser();
	public IsomorphismTester isoTester = new IsomorphismTester();
	
	public TestGroupMatch()
	{
		logger = LoggingToolFactory.createLoggingTool(TestGroupMatch.class);
	}
	
	public static Test suite() 
	{
		return new TestSuite(TestGroupMatch.class);
	}
	
	public boolean matchGroup(String smarts, String smiles) throws Exception 
	{
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smiles);
		GroupMatch grpMatch = new GroupMatch(smarts, sp, isoTester);
		return grpMatch.match(mol); 
	}
	
	public boolean matchGroupAtPosition(String smarts, String smiles, int atNum) throws Exception 
	{
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smiles);
		GroupMatch grpMatch = new GroupMatch(smarts, sp, isoTester);
		return grpMatch.matchAtPosition(mol, atNum); 
	}
	
	public int getGroupMatchCount(String smarts, String smiles, SSM_MODE flagSSMode) throws Exception 
	{
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smiles);
		GroupMatch grpMatch = new GroupMatch(smarts, sp, isoTester);
		grpMatch.setFlagSSMode(flagSSMode);
		return grpMatch.matchCount(mol); 
	}
	
	public void test01()  throws Exception
	{
		boolean res = matchGroup("CCCN", "CCCCN");
		assertEquals(true, res);
	}
	
	public void test02()  throws Exception
	{
		boolean res = matchGroup("CCCO", "CCCCN");
		assertEquals(false, res);
	}
	
	public void test03()  throws Exception
	{
		boolean res = matchGroup("[*;R]", "C1CCC1N");
		assertEquals(true, res);
	}
	
	public void test04()  throws Exception
	{
		boolean res = matchGroup("[*;R]", "CCCCN");
		assertEquals(false, res);
	}
	
	public void test05()  throws Exception
	{
		boolean res = matchGroup("[*;r5]", "C1CCCC1N");
		assertEquals(true, res);
		
		res = matchGroupAtPosition("[*;r5]", "C1CCCC1N", 0);
		assertEquals(true, res);
		res = matchGroupAtPosition("[*;r5]", "C1CCCC1N", 1);
		assertEquals(true, res);
		res = matchGroupAtPosition("[*;r5]", "C1CCCC1N", 2);
		assertEquals(true, res);
		res = matchGroupAtPosition("[*;r5]", "C1CCCC1N", 3);
		assertEquals(true, res);
		res = matchGroupAtPosition("[*;r5]", "C1CCCC1N", 4);
		assertEquals(true, res);
		res = matchGroupAtPosition("[*;r5]", "C1CCCC1N", 5);
		assertEquals(false, res);
	}
	
	public void test06()  throws Exception
	{
		boolean res = matchGroup("[*;r5]", "C1CCC1N");
		assertEquals(false, res);
	}
	
	public void test07()  throws Exception
	{
		boolean res = matchGroup("[*+]", "CCCC");
		assertEquals(false, res);
	}
	
	public void test08()  throws Exception
	{
		boolean res = matchGroup("[*+]", "CCC[C+]");
		assertEquals(true, res);
	}
	
	public void test09()  throws Exception
	{
		boolean res = matchGroup("[a]", "c1cc(C)c(N)cc1");
		assertEquals(true, res);
		
		res = matchGroupAtPosition("[a]", "c1cc(C)c(N)cc1", 0);
		assertEquals(true, res);
		res = matchGroupAtPosition("[a]", "c1cc(C)c(N)cc1", 1);
		assertEquals(true, res);
		res = matchGroupAtPosition("[a]", "c1cc(C)c(N)cc1", 2);
		assertEquals(true, res);
		res = matchGroupAtPosition("[a]", "c1cc(C)c(N)cc1", 3);
		assertEquals(false, res);
	}
	
	public void test10()  throws Exception
	{
		boolean res = matchGroup("[a]", "C1CCCC1");
		assertEquals(false, res);
	}
	
	//Recursive SMARTS
	
	public void test100()  throws Exception
	{
		boolean res = matchGroup("[C;$(CO)]", "CCCCCO");
		assertEquals(true, res);
	}
	
	public void test101()  throws Exception
	{
		boolean res = matchGroup("[C;!$(CO)]", "CCCCCN");
		assertEquals(true, res);
	}
	
	//Test group counts
	
	public void test501() throws Exception
	{
		String smarts = "CC";
		String target = "CCCN";		
		
		int res = getGroupMatchCount(smarts, target, SSM_MODE.SSM_NON_IDENTICAL);
		assertEquals("Matching " + smarts + " against " + target + " in mode NON_IDENTICAL: ",
				2, res);
		
		res = getGroupMatchCount(smarts, target, SSM_MODE.SSM_NON_OVERLAPPING);
		assertEquals("Matching " + smarts + " against " + target + " in mode NON_OVERLAPPING: ",
				1, res);
		
		res = getGroupMatchCount(smarts, target, SSM_MODE.SSM_ALL);
		assertEquals("Matching " + smarts + " against " + target + " in mode ALL: ",
				4, res);		
	}
	
	public void test502() throws Exception
	{
		String smarts = "ccn";
		String target = "c1ccccn1";		
		
		int res = getGroupMatchCount(smarts, target, SSM_MODE.SSM_NON_IDENTICAL);
		assertEquals("Matching " + smarts + " against " + target + " in mode NON_IDENTICAL: ",
				2, res);
		
		res = getGroupMatchCount(smarts, target, SSM_MODE.SSM_NON_OVERLAPPING);
		assertEquals("Matching " + smarts + " against " + target + " in mode NON_OVERLAPPING: ",
				1, res);
		
		res = getGroupMatchCount(smarts, target, SSM_MODE.SSM_ALL);
		assertEquals("Matching " + smarts + " against " + target + " in mode ALL: ",
				2, res);		
	}
	

}
