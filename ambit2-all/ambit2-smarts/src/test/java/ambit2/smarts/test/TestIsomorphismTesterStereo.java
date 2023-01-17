package ambit2.smarts.test;

import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import ambit2.smarts.IsomorphismTester;
import ambit2.smarts.SmartsHelper;
import ambit2.smarts.SmartsManager;
import ambit2.smarts.SmartsParser;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestIsomorphismTesterStereo extends TestCase
{
	public ILoggingTool logger;	
	public SmartsParser sp = new SmartsParser();
	public SmartsManager man = new SmartsManager(SilentChemObjectBuilder.getInstance());
	public IsomorphismTester isoTester = new IsomorphismTester();
		
	//Helper variables where the results from match function is stored
	boolean boolResult;
	int mappingPosCount;
	
	public TestIsomorphismTesterStereo() 
	{   
		logger = LoggingToolFactory.createLoggingTool(TestIsomorphismTesterStereo.class);
		isoTester.setFlagCheckStereoElements(true);
	}
	
	public static Test suite() 
	{
		return new TestSuite(TestIsomorphismTesterStereo.class);
	}
	
	public void match(String smarts, String smiles) throws Exception 
	{
		//Testing the algorithm via SmartsManager		
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smiles);
		man.setUseCDKIsomorphismTester(false);
		man.setIsomorphismTesterFlagCheckStereoElements(true);
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
	
	public void testDBStereo01A() throws Exception {
		match("C/C=C/N", "CC/C=C/NCC");
		assertEquals(true, boolResult);
		assertEquals(1, mappingPosCount);
	}
	
	public void testDBStereo01B() throws Exception {
		match("C/C=C/N", "CC\\C=C\\NCC");
		assertEquals(true, boolResult);
		assertEquals(1, mappingPosCount);
	}
	
	public void testDBStereo01C() throws Exception {
		match("C\\C=C/N", "CC/C=C/NCC");
		assertEquals(false, boolResult);
		assertEquals(0, mappingPosCount);
	}
	
	public void testDBStereo02() throws Exception {
		match("C/C=C/CC", "CC/C=C(\\CC)/CC");
		assertEquals(true, boolResult);
		assertEquals(2, mappingPosCount);
	}
	
	public void testDBStereo03() throws Exception {
		match("C/C=C/C", "CC/C(C)=C(C)/CC");
		assertEquals(true, boolResult);
		assertEquals(4, mappingPosCount);
	}
	
	public void testDBStereo04() throws Exception {
		match("C/C=C/C", "O/C(C)=C(C)/CC");
		assertEquals(true, boolResult);
		assertEquals(2, mappingPosCount);
	}
	
	public void testDBStereo05() throws Exception {
		match("C/C=C/C", "O/C(C)=C(C)/CC");
		assertEquals(true, boolResult);
		assertEquals(2, mappingPosCount);
	}
	
	public void testDBStereo06A() throws Exception {
		match("N/C=C/C", "N/C=C(O)/CC/C=C/N");
		assertEquals(true, boolResult);
		assertEquals(2, mappingPosCount);
	}
	
	public void testDBStereo06B() throws Exception {
		match("N/C=C/C", "N/C=C(O)/CC/C=C\\N");
		assertEquals(true, boolResult);
		assertEquals(1, mappingPosCount);
	}
	
	public void testDBStereo06C() throws Exception {
		match("N/C=C/C", "N\\C=C(O)/CC/C=C\\N");
		assertEquals(false, boolResult);
		assertEquals(0, mappingPosCount);
	}
	
	public void testDBStereo06D() throws Exception {
		match("N/C=C/C", "NC=C(O)CCC=CN");
		assertEquals(false, boolResult);
		assertEquals(0, mappingPosCount);
	}
	
	public void testDBStereo06E() throws Exception {
		match("N/?C=C/?C", "NC=C(O)CCC=CN");
		assertEquals(true, boolResult);
		assertEquals(2, mappingPosCount);
	}
	
		
	public void testDBStereo06F() throws Exception {
		match("N/?C=C/?C", "N/C=C(O)/CC/C=C\\N");
		assertEquals(true, boolResult);
		assertEquals(1, mappingPosCount);
	}
	
	public void testDBStereo06G() throws Exception {
		match("N/?C=C/C", "N/C=C(O)/CC/C=C/N");
		assertEquals(true, boolResult);
		assertEquals(2, mappingPosCount);
	}
	
	public void testDBStereo06H() throws Exception {
		match("N/?C=C/?C", "N/C=C(O)/CCC=CN");
		assertEquals(true, boolResult);
		assertEquals(2, mappingPosCount);
	}
	
	public void testDBStereo07A() throws Exception {
		match("N/C=C/CCC\\C=C\\O", "ClCN/C=C/CCC/C=C/OCC");
		assertEquals(true, boolResult);
		assertEquals(1, mappingPosCount);
	}
	
	public void testChiralAtom01A() throws Exception {
		match("N[C@](C)(O)CC", "N[C@@](CC)(O)C");
		assertEquals(true, boolResult);
		assertEquals(1, mappingPosCount);
	}
	
	public void testChiralAtom01B() throws Exception {
		match("N[C@](C)(O)CC", "N[C@](CC)(O)C");
		assertEquals(false, boolResult);
		assertEquals(0, mappingPosCount);
	}
	
	public void testChiralAtom01C() throws Exception {
		match("N[C](C)(O)CC", "N[C@](CC)(O)C");
		assertEquals(true, boolResult);
		assertEquals(1, mappingPosCount);
	}
	
	public void testChiralAtom01D() throws Exception {
		match("N[C@](C)(O)*", "N[C@@](CC)(O)Cl");
		assertEquals(false, boolResult);
		assertEquals(0, mappingPosCount);
	}
	
	public void testChiralAtom01E() throws Exception {
		match("N[C@](C)(*)*", "N[C@@](CC)(O)Cl");
		assertEquals(true, boolResult);
		assertEquals(1, mappingPosCount);
	}
	
	public void testChiralAtom01F() throws Exception {
		match("N[C@](C)([!C])*", "N[C@@](CC)(O)Cl");
		assertEquals(true, boolResult);
		assertEquals(1, mappingPosCount);
	}
	
	public void testChiralAtom01G() throws Exception {
		match("N[C@](C)(O)*", "N[C@](CC)(O)Cl");
		assertEquals(true, boolResult);
		assertEquals(1, mappingPosCount);
	}
	
	public void testChiralAtom01H() throws Exception {
		match("N[C@,C+](C)(O)*", "N[C+](CC)(O)Cl");
		assertEquals(true, boolResult);
		assertEquals(1, mappingPosCount);
	}
	
	public void testChiralAtom01I() throws Exception {
		match("N[*@](C)(O)*", "N[C@](CC)(O)Cl");
		assertEquals(true, boolResult);
		assertEquals(1, mappingPosCount);
	}
	
	public void testExtendedChirality01A() throws Exception {
		match("NC(Cl)=[C@]=C(C)(O)", "NC(Cl)=[C@@]=C(OC)(C)");
		assertEquals(true, boolResult);
		assertEquals(1, mappingPosCount);
	}
	
	
}
