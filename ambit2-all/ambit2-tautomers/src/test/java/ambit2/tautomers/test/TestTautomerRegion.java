package ambit2.tautomers.test;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import ambit2.smarts.SmartsFlags;
import ambit2.smarts.SmartsHelper;
import ambit2.smarts.SmartsParser;
import ambit2.smarts.TopLayer;
import ambit2.tautomers.TautomerManager;
import ambit2.tautomers.rules.CustomTautomerRegion;
import ambit2.tautomers.rules.TautomerRegion;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestTautomerRegion extends TestCase
{
	public ILoggingTool logger;
	public TautomerManager tman = null;
	public boolean FlagPrintTautomers = false;
	
	public TestTautomerRegion()
	{
		logger = LoggingToolFactory.createLoggingTool(TestTautomerRegion.class);		
		tman = AbstractTautomerTest.getDefaultTautomerManager();
	}
	
	public static Test suite() {
		return new TestSuite(TestTautomerRegion.class);
	}
	
	int checkPositions(List<IAtom[]> positions, List<int[]> expectedAtIndices, IAtomContainer mol)
	{
		if (positions.size() == 0 && expectedAtIndices.size() == 0)
			return -1; //special case
		
		int nMatchedPositions = 0;
		for (IAtom[] pos: positions)
		{
			int atInd[] = getAtomIndices(pos, mol);
			for (int[] expAtInd : expectedAtIndices)			
				if (checkEqualIndices(atInd, expAtInd))
				{
					nMatchedPositions++;
					break;
				}
		}
		return nMatchedPositions;
	}
	
	int checkNitroGroupPositions(String smiles, List<int[]> expectedAtIndices) throws Exception
	{
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smiles);
		TopLayer.setAtomTopLayers(mol);
		List<IAtom[]> positions =  CustomTautomerRegion.getNitroGroupPositions(mol);
		return checkPositions(positions, expectedAtIndices, mol);
	}
	
	int checkNitroxidePositions(String smiles, List<int[]> expectedAtIndices) throws Exception
	{
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smiles);
		TopLayer.setAtomTopLayers(mol);
		List<IAtom[]> positions =  CustomTautomerRegion.getNitroxidePositions(mol);		
		return checkPositions(positions, expectedAtIndices, mol);
	}
	
	int checkSulfonylGroupPositions(String smiles, List<int[]> expectedAtIndices) throws Exception
	{
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smiles);
		TopLayer.setAtomTopLayers(mol);
		List<IAtom[]> positions =  CustomTautomerRegion.getSulfonylGroupPositions(mol);		
		return checkPositions(positions, expectedAtIndices, mol);
	}
	
	int checkAzideGroupPositions(String smiles, List<int[]> expectedAtIndices) throws Exception
	{
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smiles);
		TopLayer.setAtomTopLayers(mol);
		List<IAtom[]> positions =  CustomTautomerRegion.getAzideGroupPositions(mol);		
		return checkPositions(positions, expectedAtIndices, mol);
	}
	
	
	boolean checkTautomerExcludeRegion(TautomerRegion tautoReg, String smiles, int[] expectedExcludeIndices) throws Exception
	{
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smiles);
		TopLayer.setAtomTopLayers(mol);
		tautoReg.calculateRegion(mol);
		List<Integer> excludeIndices = tautoReg.getExcludeAtomIndices();
		return checkEqualIndices(expectedExcludeIndices, excludeIndices);
	}
		
	int[] getAtomIndices(IAtom atoms[], IAtomContainer mol)
	{
		int indices[] = new int[atoms.length];
		for (int i = 0; i < atoms.length; i++)
			indices[i] = mol.getAtomNumber(atoms[i]);
		return indices;
	}
	
	boolean checkEqualIndices(int pos1[], int pos2[])
	{
		if (pos1.length != pos2.length)
			return false;
		int nMatches = 0;
		for (int i = 0; i < pos1.length; i++)
		{
			for (int k = 0; k < pos2.length; k++)
				if (pos1[i] == pos2[k])
				{
					nMatches++;
					continue;
				}
		}
		return (nMatches == pos1.length);
	}
	
	boolean checkEqualIndices(int pos1[], List<Integer> pos2)
	{
		if (pos1.length != pos2.size())
			return false;
		
		int nMatches = 0;
		for (int i = 0; i < pos1.length; i++)
		{
			for (int k = 0; k < pos2.size(); k++)
				if (pos1[i] == pos2.get(k).intValue())
				{
					nMatches++;
					continue;
				}
		}
		return (nMatches == pos1.length);
	}
	
	
	public void testNitroGroups() throws Exception
	{
		String smiles;
		List<int[]> expectedPos = new ArrayList<int[]>();
		int res;
		
		smiles = "CCCCN([O-])=O"; //Incorrect nitro group: expecting -1
		expectedPos.clear();
		res = checkNitroGroupPositions(smiles, expectedPos);
		assertEquals("Nitro group positions for " + smiles, -1, res);
		
		smiles = "CCCCN"; //No nitro groups: expecting -1
		expectedPos.clear();
		res = checkNitroGroupPositions(smiles, expectedPos);
		assertEquals("Nitro group positions for " + smiles, -1, res);
		
		smiles = "CCCCN(=O)=O";
		expectedPos.clear();
		expectedPos.add(new int[] {4,5,6});
		res = checkNitroGroupPositions(smiles, expectedPos);
		assertEquals("Nitro group positions for " + smiles, expectedPos.size(), res);
		
		smiles = "O=NCCCCN(=O)=O";
		expectedPos.clear();
		expectedPos.add(new int[] {6,7,8});
		res = checkNitroGroupPositions(smiles, expectedPos);
		assertEquals("Nitro group positions for " + smiles, expectedPos.size(), res);
		
		smiles = "CCCC[N+](=O)[O-]";
		expectedPos.clear();
		expectedPos.add(new int[] {4,5,6});
		res = checkNitroGroupPositions(smiles, expectedPos);
		assertEquals("Nitro group positions for " + smiles, expectedPos.size(), res);
		
		smiles = "O=N(=O)CCCC[N+](=O)[O-]";
		expectedPos.clear();
		expectedPos.add(new int[] {0,1,2});
		expectedPos.add(new int[] {7,8,9});
		res = checkNitroGroupPositions(smiles, expectedPos);
		assertEquals("Nitro group positions for " + smiles, expectedPos.size(), res);
		
	}
	
	public void testNitroxides() throws Exception
	{
		String smiles;
		List<int[]> expectedPos = new ArrayList<int[]>();
		int res;
		
		smiles = "CCCCN"; //Incorrect nitro group: expecting -1
		expectedPos.clear();
		res = checkNitroxidePositions(smiles, expectedPos);
		assertEquals("Nitroxide positions for " + smiles, -1, res);
		
		smiles = "CCCCN=O";
		expectedPos.clear();
		expectedPos.add(new int[] {4,5});
		res = checkNitroxidePositions(smiles, expectedPos);
		assertEquals("Nitrooxide positions for " + smiles, expectedPos.size(), res);
		
		smiles = "CCCC[N+][O-]";
		expectedPos.clear();
		expectedPos.add(new int[] {4,5});
		res = checkNitroxidePositions(smiles, expectedPos);
		assertEquals("Nitrooxide positions for " + smiles, expectedPos.size(), res);
		
		smiles = "O=NCCCC[N+][O-]";
		expectedPos.clear();
		expectedPos.add(new int[] {0,1});
		expectedPos.add(new int[] {6,7});
		res = checkNitroxidePositions(smiles, expectedPos);
		assertEquals("Nitrooxide positions for " + smiles, expectedPos.size(), res);
		
		//only one nitroxide is expected (nitro group is not considered as a nitroxide)
		smiles = "O=N(=O)CCCC[N+][O-]"; 
		expectedPos.clear();
		expectedPos.add(new int[] {7,8});
		res = checkNitroxidePositions(smiles, expectedPos);
		assertEquals("Nitrooxide positions for " + smiles, expectedPos.size(), res);
		
		//no nitroxide is expected (nitro group is not considered as a nitroxide)
		smiles = "O=N(=O)CCCC[N+](=O)[O-]"; 
		expectedPos.clear();
		res = checkNitroxidePositions(smiles, expectedPos);
		assertEquals("Nitrooxide positions for " + smiles, -1, res);
		
	}
	
	public void testSulfonylGroups() throws Exception
	{
		String smiles;
		List<int[]> expectedPos = new ArrayList<int[]>();
		int res;
		
		smiles = "CCCCS(C)=O"; //No sulfonyl groups
		expectedPos.clear();		
		res = checkSulfonylGroupPositions(smiles, expectedPos);
		assertEquals("Sulfonyl positions for " + smiles, -1, res);
		
		smiles = "CCCCS(C)(=O)=O";
		expectedPos.clear();
		expectedPos.add(new int[] {4,6,7});
		res = checkSulfonylGroupPositions(smiles, expectedPos);
		assertEquals("Sulfonyl positions for " + smiles, expectedPos.size(), res);
		
		smiles = "O=S(C)(=O)CCCCS(C)(=O)=O";
		expectedPos.clear();
		expectedPos.add(new int[] {0,1,3});
		expectedPos.add(new int[] {8,10,11});		
		res = checkSulfonylGroupPositions(smiles, expectedPos);
		assertEquals("Sulfonyl positions for " + smiles, expectedPos.size(), res);
	}
	
	public void testAzideGroups() throws Exception
	{
		String smiles;
		List<int[]> expectedPos = new ArrayList<int[]>();
		int res;
		
		smiles = "CCCCN=N"; //No azide groups
		expectedPos.clear();		
		res = checkAzideGroupPositions(smiles, expectedPos);
		assertEquals("Sulfonyl positions for " + smiles, -1, res);
		
		smiles = "CCCCN=N#N";
		expectedPos.clear();
		expectedPos.add(new int[] {4,5,6});
		res = checkAzideGroupPositions(smiles, expectedPos);
		assertEquals("Sulfonyl positions for " + smiles, expectedPos.size(), res);
		
		smiles = "O=CCCCN=[N+]=[N-]";
		expectedPos.clear();
		expectedPos.add(new int[] {5,6,7});
		res = checkAzideGroupPositions(smiles, expectedPos);
		assertEquals("Sulfonyl positions for " + smiles, expectedPos.size(), res);
				
		smiles = "N#N=NCCCCN=[N+]=[N-]";
		expectedPos.clear();
		expectedPos.add(new int[] {0,1,2});
		expectedPos.add(new int[] {7,8,9});
		res = checkAzideGroupPositions(smiles, expectedPos);
		assertEquals("Sulfonyl positions for " + smiles, expectedPos.size(), res);
		
		smiles = "N=N=NCCCCN=[N+]=[N-]"; //Only one azide group is expected
		expectedPos.clear();		
		expectedPos.add(new int[] {7,8,9});
		res = checkAzideGroupPositions(smiles, expectedPos);
		assertEquals("Sulfonyl positions for " + smiles, expectedPos.size(), res);	
	}	
	
	public void testTautomerRegion() throws Exception
	{
		String smiles;
		TautomerRegion tautoReg = new TautomerRegion();
		int expectedExclInd[]; 
		boolean res;
		
		//(1) Only nitro groups are excluded		
		tautoReg.setExcludeNitroGroup(true);
		
		smiles = "CCCCN(=O)=O";
		expectedExclInd = new int[] {4,5,6};
		res = checkTautomerExcludeRegion( tautoReg, smiles, expectedExclInd);
		assertEquals("Exclude tautomer region for " + smiles, true, res);
		
		smiles = "O=NCCCC[N+](=O)[O-]";
		expectedExclInd = new int[] {6,7,8};
		res = checkTautomerExcludeRegion( tautoReg, smiles, expectedExclInd);
		assertEquals("Exclude tautomer region for " + smiles, true, res);
		
		
		//(2) Nitro groups and nitroxides are excluded	
		tautoReg.setExcludeNitroxides(true);
		
		smiles = "CCCCN(=O)=O";
		expectedExclInd = new int[] {4,5,6};
		res = checkTautomerExcludeRegion( tautoReg, smiles, expectedExclInd);
		assertEquals("Exclude tautomer region for " + smiles, true, res);
		
		smiles = "O=NCCCC[N+](=O)[O-]";
		expectedExclInd = new int[] {0,1,6,7,8};
		res = checkTautomerExcludeRegion( tautoReg, smiles, expectedExclInd);
		assertEquals("Exclude tautomer region for " + smiles, true, res);
		
		smiles = "c1ccccc1CCN(=O)=O";
		expectedExclInd = new int[] {8,9,10};
		res = checkTautomerExcludeRegion( tautoReg, smiles, expectedExclInd);
		assertEquals("Exclude tautomer region for " + smiles, true, res);
		
		
		//(3) Nitro groups, nitroxides and aromatic systems are excluded	
		tautoReg.setExcludeAromaticSystems(true);
		
		smiles = "CCCCN(=O)=O";
		expectedExclInd = new int[] {4,5,6};
		res = checkTautomerExcludeRegion( tautoReg, smiles, expectedExclInd);
		assertEquals("Exclude tautomer region for " + smiles, true, res);
		
		smiles = "c1ccccc1CCN(=O)=O";
		expectedExclInd = new int[] {0,1,2,3,4,5,8,9,10};
		res = checkTautomerExcludeRegion( tautoReg, smiles, expectedExclInd);
		assertEquals("Exclude tautomer region for " + smiles, true, res);
		
		smiles = "c1ccccc1CCN=O";
		expectedExclInd = new int[] {0,1,2,3,4,5,8,9};
		res = checkTautomerExcludeRegion( tautoReg, smiles, expectedExclInd);
		assertEquals("Exclude tautomer region for " + smiles, true, res);
		
		//(4) Nitro groups, nitroxides, aromatic systems, sulfonyl and azide groups are excluded	
		tautoReg.setExcludeSulfonylGroups(true);
		tautoReg.setExcludeAzideGroups(true);
		
		smiles = "O=NCCCc1ccccc1CCS(C)(=O)=O";
		expectedExclInd = new int[] {0,1,5,6,7,8,9,10,13,15,16};
		res = checkTautomerExcludeRegion( tautoReg, smiles, expectedExclInd);
		assertEquals("Exclude tautomer region for " + smiles, true, res);
		
		smiles = "N#N=NCCCCS(C)(=O)=O";
		expectedExclInd = new int[] {0,1,2,7,9,10};
		res = checkTautomerExcludeRegion( tautoReg, smiles, expectedExclInd);
		assertEquals("Exclude tautomer region for " + smiles, true, res);
		
		smiles = "[N-]=[N+]=NCCCC[N+][O-]";
		expectedExclInd = new int[] {0,1,2,7,8};
		res = checkTautomerExcludeRegion( tautoReg, smiles, expectedExclInd);
		assertEquals("Exclude tautomer region for " + smiles, true, res);
		
	}
	
		
	public void test_TautomerRegionGeneration_01() throws Exception {
		
		//Empty region testing
		tman.tautomerRegion.setUseRegion(true);		
		int res = TestTautomers.testCase("OC1=CC=CC=C1",
				new String[] { "Oc1=cc=cc=c1", "O=C1C=CC=CC1", "O=C1C=CCC=C1" }, FlagPrintTautomers, tman);
		Assert.assertEquals(0, res);
		
		res = TestTautomers.testCase("O=CCCCCN=O", 
				new String[] { "O=CCCCCN=O", "OC=CCCCN=O", 
							"O=CCCCC=NO", "OC=CCCC=NO", "O=CCCC=CNO", "OC=CCC=CNO",}, FlagPrintTautomers, tman);		
		Assert.assertEquals(0, res);
	}

	public void test_TautomerRegionGeneration_02() throws Exception {

		//Exclude nitroxides region testing
		tman.tautomerRegion.setUseRegion(true);
		tman.tautomerRegion.setExcludeNitroxides(true);

		int res = TestTautomers.testCase("O=CCCCN=O", 
				new String[] { "O=CCCCN=O", "OC=CCCN=O" }, FlagPrintTautomers, tman);		
		Assert.assertEquals(0, res);
		
		//Exclude nitroxides groups alternatively by deactivating rule nitroso/oxime
		tman.tautomerRegion.setUseRegion(false);
		tman.getKnowledgeBase().activateRule("nitroso/oxime", false);
		res = TestTautomers.testCase("O=CCCCN=O", 
				new String[] { "O=CCCCN=O", "OC=CCCN=O" }, FlagPrintTautomers, tman);		
		Assert.assertEquals(0, res);
		
	}
	
	public void test_TautomerRegionGeneration_03() throws Exception {

		//Exclude nitro groups testing
		tman.tautomerRegion.setUseRegion(true);
		tman.tautomerRegion.setExcludeNitroGroup(true);

		int res = TestTautomers.testCase("O=CCCCN(=O)=O", 
				new String[] { "O=CCCCN(=O)=O", "OC=CCCN(=O)=O" }, FlagPrintTautomers, tman);		
		Assert.assertEquals(0, res);
		
		//Exclude nitro groups alternatively by deactivating rule nitroso/oxime
		tman.tautomerRegion.setUseRegion(false);
		tman.getKnowledgeBase().activateRule("nitroso/oxime", false);
		
		res = TestTautomers.testCase("O=CCCCN(=O)=O", 
				new String[] { "O=CCCCN(=O)=O", "OC=CCCN(=O)=O" }, FlagPrintTautomers, tman);		
		Assert.assertEquals(0, res);
		
	}
	
	public void test_TautomerRegionGeneration_04() throws Exception {

		//Exclude custom regions
		tman.tautomerRegion.setUseRegion(true);
		List<String> regionSmarts = new ArrayList<String>();
		regionSmarts.add("N=O");
		tman.tautomerRegion.setCustomExcludeRegionsSmarts(regionSmarts);

		int res = TestTautomers.testCase("O=CCCCN(=O)=O", 
				new String[] { "O=CCCCN(=O)=O", "OC=CCCN(=O)=O" }, FlagPrintTautomers, tman);		
		Assert.assertEquals(0, res);
				
		regionSmarts.clear();
		regionSmarts.add("C[Br,Cl,F]");
		tman.tautomerRegion.setCustomExcludeRegionsSmarts(regionSmarts);

		res = TestTautomers.testCase("O=CCC(Cl)C=O", 
				new String[] { "O=CCC(Cl)C=O", "OC=CC(Cl)C=O" }, FlagPrintTautomers, tman);		
		Assert.assertEquals(0, res);
	}
}
