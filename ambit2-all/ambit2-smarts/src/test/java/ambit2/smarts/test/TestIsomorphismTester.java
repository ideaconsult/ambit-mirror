package ambit2.smarts.test;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.LoggingTool;

import ambit2.smarts.IsomorphismTester;
import ambit2.smarts.SmartsHelper;
import ambit2.smarts.SmartsManager;
import ambit2.smarts.SmartsParser;


public class TestIsomorphismTester extends TestCase
{
	public LoggingTool logger;	
	public SmartsParser sp = new SmartsParser();
	public SmartsManager man = new SmartsManager(SilentChemObjectBuilder.getInstance());
	public IsomorphismTester isoTester = new IsomorphismTester();
		
	//Helper variable where the results from match function is stored
	boolean boolResult;
	int mappingPosCount;
	
	
	public TestIsomorphismTester() 
	{   
		logger = new LoggingTool(this);
	}
	
	public static Test suite() 
	{
		return new TestSuite(TestIsomorphismTester.class);
	}
	
	public void match(String smarts, String smiles) throws Exception 
	{
		//Testing the algorithm via SmartsManager		
		IMolecule mol = SmartsHelper.getMoleculeFromSmiles(smiles);
		man.setUseCDKIsomorphismTester(false);
		man.setQuery(smarts);
		boolResult = man.searchIn(mol);
		
		
		//Direct test of class IsomorphismTester
		IQueryAtomContainer query  = sp.parse(smarts);
		sp.setNeededDataFlags();
		String errorMsg = sp.getErrorMessages();
		if (!errorMsg.equals(""))
		{
			System.out.println("Smarts Parser errors:\n" + errorMsg);			
			return;
		}
		
		isoTester.setQuery(query);
		sp.setSMARTSData(mol);
		List<Integer> pos = isoTester.getIsomorphismPositions(mol);
		mappingPosCount = pos.size();
	}
	
	
	//---------------------------------------------------------------------------------
	
	
	/**
	 * Tests from
	 * From http://www.daylight.com/dayhtml_tutorials/languages/smarts/index.html
	 */
	public void testPropertyCharge1() throws Exception {
		match("[+1]", "[OH-].[Mg+2]");
		assertEquals(false, boolResult);
		assertEquals(0, mappingPosCount);
	}
	
	public void testPropertyCharge2() throws Exception {
		match("[+1]", "COCC(O)Cn1ccnc1[N+](=O)[O-]");		
		assertEquals(true, boolResult);
		assertEquals(1, mappingPosCount);
	}
	
	public void testPropertyCharge3() throws Exception {
		match("[+1]", "[NH4+]");		
		assertEquals(true, boolResult);
		assertEquals(1, mappingPosCount);
	}
	
	public void testPropertyCharge4() throws Exception {
		match("[+1]", "CN1C(=O)N(C)C(=O)C(N(C)C=N2)=C12");		
		assertEquals(false, boolResult);
		assertEquals(0, mappingPosCount);
	}
	
	public void testPropertyCharge5() throws Exception {
		match("[+1]", "[Cl-].[Cl-].NC(=O)c2cc[n+](COC[n+]1ccccc1C=NO)cc2");		
		assertEquals(true, boolResult);
		assertEquals(2, mappingPosCount);
	}
	
	public void testPropertyAromatic1() throws Exception {
		match("[a]", "c1cc(C)c(N)cc1");		
		assertEquals(true, boolResult);
		assertEquals(6, mappingPosCount);
	}
	
	public void testPropertyAromatic2() throws Exception {
		match("[a]", "c1c(C)c(N)cnc1");		
		assertEquals(true, boolResult);
		assertEquals(6, mappingPosCount);
	}
	
	public void testPropertyAromatic3() throws Exception {
		match("[a]", "c1(C)c(N)cco1");		
		assertEquals(true, boolResult);
		assertEquals(5, mappingPosCount);
	}
	
	public void testPropertyAromatic4() throws Exception {
		match("[a]", "c1c(C)c(N)c[nH]1");		
		assertEquals(true, boolResult);
		assertEquals(5, mappingPosCount);
	}
	
	public void testPropertyAromatic5() throws Exception {
		match("[a]", "n1cc(O)ccc1");       //On1ccccc1 is not recognized as aromatic
		assertEquals(true, boolResult);
		assertEquals(6, mappingPosCount);
	}
	
	public void testPropertyAromatic6() throws Exception {
		match("[a]", "[O-][n+]1ccccc1");		
		assertEquals(true, boolResult);
		assertEquals(6, mappingPosCount);
	}
	
	public void testPropertyAromatic7() throws Exception {
		match("[a]", "c1ncccc1C1CCCN1C");		
		assertEquals(true, boolResult);
		assertEquals(6, mappingPosCount);
	}
	
	public void testPropertyAromatic8() throws Exception {
		match("[a]", "c1ccccc1C(=O)OC2CC(N3C)CCC3C2C(=O)OC");		
		assertEquals(true, boolResult);
		assertEquals(6, mappingPosCount);
	}
	
	public void testPropertyRing3() throws Exception {
		match("CC=1CC=1", "CC=1CC=1");		
		assertEquals(true, boolResult);
		assertEquals(1, mappingPosCount);
	}
	
	public void testDoubleNonAromaticBond1() throws Exception {
		match("cc=c", "C1=CC=CC=C1C2=CC=CC=C2");		
		assertEquals(false, boolResult);
	}
	
	public void testDoubleNonAromaticBond2() throws Exception {
		match("cc=c", "c1ccccc1c2ccccc2");
		assertEquals(false, boolResult);
	}
	
	//----------------  Test recursive SMARTS -------------------
	
	
	public void testRecursiveSmarts4() throws Exception {
		match("[$(*O);$(*CC)]", "c1ncccc1C1CCCN1C");
		assertEquals(false, boolResult);
		//assertEquals(0, mappingPosCount);
	}
	
	public void testRecursiveSmarts5() throws Exception {
		match("[$(*O);$(*CC)]", "N12CCC36C1CC(C(C2)=CCOC4CC5=O)C4C3N5c7ccccc76");		
		assertEquals(true, boolResult);
		//assertEquals(1, mappingPosCount);
	}
	
	public void testRecursiveSmarts6() throws Exception {
		match("[$([CX3]=[CX1]),$([CX3+]-[CX1-])]", "CN1C(=O)N(C)C(=O)C(N(C)C=N2)=C12");		
		assertEquals(false, boolResult);
		//assertEquals(0, mappingPosCount);
	}
	
	
	
}
