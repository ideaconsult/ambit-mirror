package ambit2.smarts.test;

import java.util.Vector;

import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.CDKHydrogenAdder;

import ambit2.smarts.IsomorphismTester;
import ambit2.smarts.SMIRKSManager;
import ambit2.smarts.SMIRKSReaction;
import ambit2.smarts.SmartsHelper;
import ambit2.smarts.SmartsParser;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestSMIRKS extends TestCase
{
	public LoggingTool logger;
	SMIRKSManager smrkMan = new SMIRKSManager();
	SmartsParser smartsParser = new SmartsParser();
	IsomorphismTester isoTester = new IsomorphismTester();
	
	public TestSMIRKS() 
	{   
		logger = new LoggingTool(this);
	}
	
	public static Test suite() {
		return new TestSuite(TestSMIRKS.class);
	}
	
	int checkReactionResult(IAtomContainer result, String expectedResult[]) throws Exception
	{
		IMoleculeSet ms =  ConnectivityChecker.partitionIntoMolecules(result);
		
		if (ms.getAtomContainerCount() != expectedResult.length)
			return(-1);
		
		for (int i = 0; i < expectedResult.length; i++)
		{
			QueryAtomContainer query = smartsParser.parse(expectedResult[i]);
			String error = smartsParser.getErrorMessages();
			if (!error.equals(""))
			{
				System.out.println("Smarts Parser errors:\n" + error);
				throw(new Exception("Smarts Parser errors:\n" + error));
				//return (100 + i);
			}
			
			boolean FlagMatch = false;
			
			for (int k = 0; k < ms.getAtomContainerCount(); k++)
			{
				IAtomContainer ac = ms.getMolecule(k);
				if (ac.getAtomCount() == query.getAtomCount())
				{
					isoTester.setQuery(query);
					smartsParser.setSMARTSData(ac);
					boolean res = isoTester.hasIsomorphism(ac);
					if (res)
					{
						FlagMatch = true;
						break;
					}
				}
			}
			
			if (!FlagMatch)
				return(10000 + i);
			
		}
			
		return(0);
	}
	
	
	IAtomContainer applySMIRKSReaction(String smirks, String targetSmiles) throws Exception
	{
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IAtomContainer target = sp.parseSmiles(targetSmiles);
		
		/*
		CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(
				DefaultChemObjectBuilder.getInstance()
		);
		*/
		
		//System.out.println("No Impl H Atoms = " + target.getAtom(0).getImplicitHydrogenCount().intValue());
				
		SMIRKSReaction reaction = smrkMan.parse(smirks);
		if (!smrkMan.getErrors().equals(""))
		{
			System.out.println(smrkMan.getErrors());
			throw(new Exception("Smirks Parser errors:\n" + smrkMan.getErrors()));
		}
		
		smrkMan.applyTransformation(target, reaction);
		
		return (target);
	}
	
	
	
	//-------------------------------------------------------------
	
	
	public void testSM0001() throws Exception {
		
		String smirks = "[N:1][C:2][C:3][C:4]>>[C:4]=[C:3].S[C:2][N-:1]Cl";
		String target = "NCCC";
		String expectedResult[] = new String[] {"C=C","SC[N-]Cl"};
		
		IAtomContainer result = applySMIRKSReaction(smirks, target);
		String transformedSmiles = SmartsHelper.moleculeToSMILES(result);	
		//System.out.println("Reaction application: " + target + "  -->  " + transformedSmiles);
		
		int checkRes = checkReactionResult(result,expectedResult);
		assertEquals(0, checkRes);
	}
	
	
	public void testN_dealkylation() throws Exception {
		
		String smirks = "[N:1][C:2]([H])>>[N:1][H].[C:2]=[O]";
		String target = "SNC(Cl)[H]";
		String expectedResult[] = new String[] {"SN[H]","ClC=O"};
		
		IAtomContainer result = applySMIRKSReaction(smirks, target);
		int checkRes = checkReactionResult(result,expectedResult);
		assertEquals(0, checkRes);
	}
	
	public void testN_oxidation() throws Exception {

		String smirks = "[N:1][C:2]([H])>>[N:1](-[O])[C:2]";
		String target = "CCNC[H]";
		String expectedResult[] = new String[] {"CCN([O])C"};

		IAtomContainer result = applySMIRKSReaction(smirks, target);
		int checkRes = checkReactionResult(result,expectedResult);
		assertEquals(0, checkRes);
	}
	
	public void testS_oxidation() throws Exception {

		String smirks = "[#16:1][#6:2]>>[#16:1](=[O])[#6:2]";
		String target = "SCNCCCS";
		String expectedResult[] = new String[] {"O=SCNCCCS=O"};

		IAtomContainer result = applySMIRKSReaction(smirks, target);
		int checkRes = checkReactionResult(result,expectedResult);
		assertEquals(0, checkRes);
	}
	
	public void testAromatic_hydroxylation() throws Exception {

		String smirks = "[c:1][H:2]>>[c:1][O][H:2]";
		String target = "[H]c1nnc([H])nn1";
		String expectedResult[] = new String[] {"[H]Oc1nnc(O[H])nn1"};

		IAtomContainer result = applySMIRKSReaction(smirks, target);
		int checkRes = checkReactionResult(result,expectedResult);
		assertEquals(0, checkRes);
	}
	
	public void testAliphatic_hydroxylation() throws Exception {

		String smirks = "[C;X4:1][H:2]>>[C:1][O][H:2]";
		String target = "CC([H])([H])[H]";
		String expectedResult[] = new String[] {"CC([H])([H])[O][H]"};

		IAtomContainer result = applySMIRKSReaction(smirks, target);
		int checkRes = checkReactionResult(result,expectedResult);
		assertEquals(0, checkRes);
	}
	
	public void testO_dealkylation() throws Exception {

		String smirks = "[O:1][C:2]([H])>>[O:1][H].[C:2]=[O]";
		String target = "CCNCOC[H]";
		String expectedResult[] = new String[] {"CCNCO[H]","C=O"};

		IAtomContainer result = applySMIRKSReaction(smirks, target);
		int checkRes = checkReactionResult(result,expectedResult);
		assertEquals(0, checkRes);
	}
	
	
	
}
