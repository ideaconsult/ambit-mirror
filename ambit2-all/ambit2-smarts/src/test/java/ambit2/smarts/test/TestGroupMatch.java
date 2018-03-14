package ambit2.smarts.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.LoggingTool;

import ambit2.smarts.IsomorphismTester;
import ambit2.smarts.SmartsHelper;
import ambit2.smarts.SmartsParser;
import ambit2.smarts.groups.GroupMatch;

public class TestGroupMatch extends TestCase
{
	public LoggingTool logger;	
	public SmartsParser sp = new SmartsParser();
	public IsomorphismTester isoTester = new IsomorphismTester();
	
	public TestGroupMatch()
	{
		logger = new LoggingTool(this);
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
	}
	
	public void test10()  throws Exception
	{
		boolean res = matchGroup("[a]", "C1CCCC1");
		assertEquals(false, res);
	}
	

}
