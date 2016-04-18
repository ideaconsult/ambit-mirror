package ambit2.smarts.test;

import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.LoggingTool;

import ambit2.smarts.IsomorphismTester;
import ambit2.smarts.SmartsHelper;
import ambit2.smarts.SmartsManager;
import ambit2.smarts.SmartsParser;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestIsomorphismTesterStereo extends TestCase
{
	public LoggingTool logger;	
	public SmartsParser sp = new SmartsParser();
	public SmartsManager man = new SmartsManager(SilentChemObjectBuilder.getInstance());
	public IsomorphismTester isoTester = new IsomorphismTester();
		
	//Helper variables where the results from match function is stored
	boolean boolResult;
	int mappingPosCount;
	
	public TestIsomorphismTesterStereo() 
	{   
		logger = new LoggingTool(this);
		isoTester.setFlagCheckStereoElements(true);
	}
	
	public static Test suite() 
	{
		return new TestSuite(TestIsomorphismTester.class);
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
}
