package ambit2.test.smarts;

import junit.framework.*;
//import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.mcss.RMap;

import ambit2.smarts.SmartsConst;
import ambit2.smarts.SmartsHelper;
import ambit2.smarts.SmartsManager;
import ambit2.smarts.SmartsParser;

import java.util.*;

public class TestSmartsSearchCLG extends TestCase
{
	public LoggingTool logger;
	SmartsManager man = new SmartsManager();
	
	public TestSmartsSearchCLG() 
	{   
		logger = new LoggingTool(this);
	}
	
	public static Test suite() {
		return new TestSuite(TestSmartsSearchCLG.class);
	}
	
	/**
	 * Tests of Component Level Grouping with the examples from the
	 * Daylight SMARTS tutorial
	 */
	public void testCLG01()  
	{	
		assertEquals(1, TestUtilities.boolSearch("C.C","CCCC"));
	}
	
	public void testCLG02()  
	{	
		assertEquals(1, TestUtilities.boolSearch("(C.C)","CCCC"));
	}
	
	public void testCLG03()  
	{	
		assertEquals(0, TestUtilities.boolSearch("(C).(C)","CCCC"));
	}
	
	public void testCLG04()  
	{	
		assertEquals(1, TestUtilities.boolSearch("(C).(C)","CCCC.CCCC"));
	}
	
	public void testCLG05()  
	{	
		assertEquals(1, TestUtilities.boolSearch("(C).C","CCCC"));
	}
	
	public void testCLG06()  
	{	
		assertEquals(1, TestUtilities.boolSearch("(C).(C).C","CCCC.CCCC"));
	}
	
	
	/**
	 * Additional tests of Component Level Grouping
	 */
	public void testCLG07()  
	{	
		assertEquals(0, TestUtilities.boolSearch("(CCCC.CC.CCCN).N.C", "CCCCC.CCCN"));
		assertEquals(0,TestUtilities.boolSearch("(Cl.CCCC.CC.CCCCN).N.C", "CCCCC.CCCN"));
		assertEquals(1, TestUtilities.boolSearch("(CCCC.CC).(CCCN).N.C", "CCCCC.CCCN"));
		assertEquals(0, TestUtilities.boolSearch("(CCBr.CCN).(OCC)", "BrCCCCC.CCCN.OCCC"));
		assertEquals(1, TestUtilities.boolSearch("(CCBr).(CCN).(OCC)", "BrCCCCC.CCCN.OCCC"));
		assertEquals(1, TestUtilities.boolSearch("(CC[Br,Cl]).(CCN).(OCC)", "BrCCCCC.CCCN.OCCC"));
	}
	
	
	
	
	
}
