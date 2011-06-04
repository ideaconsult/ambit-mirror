package ambit2.smarts.test;

import java.io.File;
import java.io.FileInputStream;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.io.iterator.IteratingMDLReader;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.core.processors.structure.AtomConfigurator;
import ambit2.core.processors.structure.HydrogenAdderProcessor;
import ambit2.smarts.IsomorphismTester;
import ambit2.smarts.SMIRKSManager;
import ambit2.smarts.SMIRKSReaction;
import ambit2.smarts.SmartsHelper;
import ambit2.smarts.SmartsParser;

public class TestSMIRKS 
{
	//All tests fail , if hydrogens are explicit!
	boolean explicitH = false;
	
	public static LoggingTool logger;
	SMIRKSManager smrkMan = new SMIRKSManager();
	SmartsParser smartsParser = new SmartsParser();
	IsomorphismTester isoTester = new IsomorphismTester();
	SmilesGenerator smigen = new SmilesGenerator();
	
	@BeforeClass
	public static void  init() {
		logger = new LoggingTool(TestSMIRKS.class);
	}
	/*
	public TestSMIRKS() 
	{   
		
	}
	
	public static Test suite() {
		return new TestSuite(TestSMIRKS.class);
	}
	
	*/
	/**
	 * Throws exception if anything is wrong
	 * @param result
	 * @param expectedResult
	 * @throws Exception
	 */
	void checkReactionResult(IAtomContainer result, String expectedResult[]) throws Exception
	{
		IMoleculeSet ms =  ConnectivityChecker.partitionIntoMolecules(result);
		
		if (ms.getAtomContainerCount() != expectedResult.length)
			throw new Exception(String.format("Found %d products, expected %d",ms.getAtomContainerCount(),expectedResult.length));
		
		for (int i = 0; i < expectedResult.length; i++)
		{
			QueryAtomContainer query = smartsParser.parse(expectedResult[i]);
			String error = smartsParser.getErrorMessages();
			if (!error.equals(""))
			{
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
				throw new Exception(String.format("The product does not match expected product %s",expectedResult[i]));
		}
		//all ok if coming here
	}
	
	IAtomContainer applySMIRKSReaction(String smirks, String targetSmiles) throws Exception {
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IAtomContainer target = sp.parseSmiles(targetSmiles);
		if (explicitH)
			AtomContainerManipulator.convertImplicitToExplicitHydrogens(target);
		//System.out.println(smigen.createSMILES(target));
		return applySMIRKSReaction(smirks, target);
	}

	IAtomContainer applySMIRKSReaction(String smirks,IAtomContainer target) throws Exception
	{
	
		SMIRKSReaction reaction = smrkMan.parse(smirks);
		if (!smrkMan.getErrors().equals(""))
		{
			throw(new Exception("Smirks Parser errors:\n" + smrkMan.getErrors()));
		}
		
		if (smrkMan.applyTransformation(target, reaction)) 
			return target; //all products inside the atomcontainer, could be disconnected
		else return null;
	}	
	/*
	IAtomContainer applySMIRKSReaction(String smirks, IAtomContainer target) throws Exception
	{

		SMIRKSReaction reaction = smrkMan.parse(smirks);
		if (!smrkMan.getErrors().equals(""))
		{
			//System.out.println(smrkMan.getErrors()); 
			throw(new Exception("Smirks Parser errors:\n" + smrkMan.getErrors()));
		}
		
		smrkMan.applyTransformation(target, reaction);
		
		return (target);
	}
	*/
	
	
	
	//-------------------------------------------------------------
	
	@org.junit.Test
	public void testSM0001() throws Exception {
		
		String smirks = "[N:1][C:2][C:3][C:4]>>[C:4]=[C:3].S[C:2][N-:1]Cl";
		String target = "NCCC";
		String expectedResult[] = new String[] {"C=C","SC[N-]Cl"};
		
		IAtomContainer result = applySMIRKSReaction(smirks, target);
		Assert.assertNotNull(result);
		String transformedSmiles = SmartsHelper.moleculeToSMILES(result);	
		//System.out.println("Reaction application: " + target + "  -->  " + transformedSmiles);
		
		checkReactionResult(result,expectedResult);
		
	}
	
	@org.junit.Test
	public void testN_dealkylation() throws Exception {
		
		String smirks = "[N:1][C:2]([H])>>[N:1][H].[C:2]=[O]";
		String target = "SNC(Cl)[H]";
		String expectedResult[] = new String[] {"SN[H]","ClC=O"};
		
		IAtomContainer result = applySMIRKSReaction(smirks, target);
		Assert.assertNotNull(result);
		checkReactionResult(result,expectedResult);
		
	}
	
	@org.junit.Test
	public void testN_oxidation() throws Exception {

		String smirks = "[N:1][C:2]([H])>>[N:1](-[O])[C:2]";
		String target = "CCNC[H]";
		String expectedResult[] = new String[] {"CCN([O])C"};

		IAtomContainer result = applySMIRKSReaction(smirks, target);
		Assert.assertNotNull(result);
		checkReactionResult(result,expectedResult);
		
	}
	
	@org.junit.Test
	public void testS_oxidation() throws Exception {

		String smirks = "[#16:1][#6:2]>>[#16:1](=[O])[#6:2]";
		String target = "SCNCCCS";
		String expectedResult[] = new String[] {"O=SCNCCCS=O"};

		IAtomContainer result = applySMIRKSReaction(smirks, target);
		Assert.assertNotNull(result);
		checkReactionResult(result,expectedResult);
		
	}
	
	@org.junit.Test
	public void testAromatic_hydroxylation() throws Exception {

		String smirks = "[c:1][H:2]>>[c:1][O][H:2]";
		String target = "[H]c1nnc([H])nn1";
		String expectedResult[] = new String[] {"[H]Oc1nnc(O[H])nn1"};

		IAtomContainer result = applySMIRKSReaction(smirks, target);
		Assert.assertNotNull(result);
		checkReactionResult(result,expectedResult);
		
	}
	
	@org.junit.Test
	public void testAliphatic_hydroxylation() throws Exception {

		String smirks = "[C;X4:1][H:2]>>[C:1][O][H:2]";
		String target = "CC([H])([H])[H]";
		String expectedResult[] = new String[] {"CC([H])([H])[O][H]"};

		IAtomContainer result = applySMIRKSReaction(smirks, target);
		Assert.assertNotNull(result);
		checkReactionResult(result,expectedResult);
		
	}
	
	@org.junit.Test
	public void testAliphatic_hydroxylation1() throws Exception {
		//this crashes if read from SDF
		String smirks = "[C;X4:1][H:2]>>[C:1][O][H:2]";
		String target = "c1cc(c(cc1Cl)Cl)Cl";
		IAtomContainer result = applySMIRKSReaction(smirks, target);
		Assert.assertNull(result);
		
	}	
	
	@org.junit.Test
	public void testO_dealkylation() throws Exception {

		String smirks = "[O:1][C:2]([H])>>[O:1][H].[C:2]=[O]";
		String target = "CCNCOC[H]";
		String expectedResult[] = new String[] {"CCNCO[H]","C=O"};

		IAtomContainer result = applySMIRKSReaction(smirks, target);
		Assert.assertNotNull(result);
		checkReactionResult(result,expectedResult);
		
	}
	

	@org.junit.Test
	public void testAldehyde_oxidation() throws Exception {
//NN_diethylformamide
		String smirks = "[C;H1:1]=[O:2]>>[C:1](O)=[O:2]";
		String target = "CCN(CC)C=O";
		String expectedResult[] = new String[] {"CCN(CC)[CH](=O)O"};

		IAtomContainer result = applySMIRKSReaction(smirks, target);
		Assert.assertNotNull(result);
		
		//System.out.println(smigen.createSMILES(result));
		checkReactionResult(result,expectedResult);
		
	}

	@org.junit.Test
	public void testAldehyde_oxidation_sdf() throws Exception {
//NN_diethylformamide
		String smirks = "[C;H1:1]=[O:2]>>[C:1](O)=[O:2]";
		String expectedResult[] = new String[] {"CCN(CC)[CH](=O)O"};
		IAtomContainer target;
		
		AtomConfigurator  cfg = new AtomConfigurator();
		HydrogenAdderProcessor hadder = new HydrogenAdderProcessor();
		hadder.setAddEexplicitHydrogens(explicitH); // does not work if hydrogens are explicit!
		
		File file = new File(getClass().getClassLoader().getResource("smirkstest.sdf").getFile());

		IteratingMDLReader reader = new IteratingMDLReader(new FileInputStream(file), NoNotificationChemObjectBuilder.getInstance());
		try {
			while (reader.hasNext()) {
				target = (IAtomContainer) reader.next();
				hadder.process((IAtomContainer)target);
				cfg.process((IAtomContainer)target);
				//System.out.println(smigen.createSMILES(target));
				CDKHueckelAromaticityDetector.detectAromaticity((IAtomContainer)target);			
				IAtomContainer result = applySMIRKSReaction(smirks, target);
				Assert.assertNotNull(result);
				//System.out.println(smigen.createSMILES(result));
				checkReactionResult(result,expectedResult);
							
			}
		} finally {
			reader.close();
		}

	}	
}
