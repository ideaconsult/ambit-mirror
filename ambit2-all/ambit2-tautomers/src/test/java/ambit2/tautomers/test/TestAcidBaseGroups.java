package ambit2.tautomers.test;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import ambit2.smarts.SmartsHelper;
import ambit2.tautomers.zwitterion.AminoGroup;
import ambit2.tautomers.zwitterion.CarboxylicGroup;
import ambit2.tautomers.zwitterion.IAcidicCenter;
import ambit2.tautomers.zwitterion.IBasicCenter;
import ambit2.tautomers.zwitterion.PhosphoricGroup;
import ambit2.tautomers.zwitterion.SulfGroup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestAcidBaseGroups extends TestCase
{
	public ILoggingTool logger;
	
	public TestAcidBaseGroups() 
	{   
		logger = LoggingToolFactory.createLoggingTool(TestAcidBaseGroups.class);		
	}	
	
	public static Test suite() {
		return new TestSuite(TestAcidBaseGroups.class);
	}
	
	void checkAminoGroup(String smi, IAtomContainer mol, 
			List<AminoGroup> centerList, List<int[]> expectedPositions )
	{
		assertEquals("Amino group positions ", expectedPositions.size(), centerList.size());
		
		for (AminoGroup g : centerList)
		{
			IAtom atoms[] = g.getAtoms();
			chechGroupPositions(smi, mol, atoms, expectedPositions);			
		}
	}
	
	void checkCarboxylicGroup(String smi, IAtomContainer mol, 
			List<CarboxylicGroup> centerList, List<int[]> expectedPositions )
	{
		assertEquals("Amino group positions ", expectedPositions.size(), centerList.size());
		
		for (CarboxylicGroup g : centerList)
		{
			IAtom atoms[] = g.getAtoms();
			chechGroupPositions(smi, mol, atoms, expectedPositions);
		}
	}
	
	void checkPhosphoricGroup(String smi, IAtomContainer mol, 
			List<PhosphoricGroup> centerList, List<int[]> expectedPositions )
	{
		assertEquals("Phosphoric group positions ", expectedPositions.size(), centerList.size());
		
		for (PhosphoricGroup g : centerList)
		{
			IAtom atoms[] = g.getAtoms();
			chechGroupPositions(smi, mol, atoms, expectedPositions);
		}
	}
	
	void checkSulfGroup(String smi, IAtomContainer mol, 
			List<SulfGroup> centerList, List<int[]> expectedPositions )
	{
		assertEquals("Sulf group positions ", expectedPositions.size(), centerList.size());
		
		for (SulfGroup g : centerList)
		{
			IAtom atoms[] = g.getAtoms();
			chechGroupPositions(smi, mol, atoms, expectedPositions);
		}
	}
	
	void chechGroupPositions(String smi, IAtomContainer mol, 
			IAtom atoms[], List<int[]> expectedPositions)
	{	
		int atInd[] = new int[atoms.length];
		String s = "";
		for (int i = 0; i < atoms.length; i++) {
			int ind = mol.getAtomNumber(atoms[i]);
			atInd[i] = ind;
			s += (" " + atoms[i].getSymbol()+ind); 
		}
		
		boolean res = findAtoms(atInd, expectedPositions);
		assertEquals("Group position " + s + " in " + smi, true, res);
	}
	
	boolean findAtoms(int atomIndices[], List<int[]> expectedPositions)
	{
		for (int p[] : expectedPositions)
		{
			boolean flagFound = true;
			for (int i = 0; i < p.length; i++)
			{
				if (atomIndices[i] != p[i])
				{	
					flagFound = false;
					break;
				}	
			}
			if (flagFound)
				return true;	
		}
		return false;
	}
	
	public void test01() throws Exception 
	{
		String smi = "NCCCCN";
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smi);
		List<AminoGroup> centerList = AminoGroup.findAllCenters(mol);
		List<int[]> expectedPositions = new ArrayList<int[]>();
		expectedPositions.add(new int[] {0});
		expectedPositions.add(new int[] {5});
		checkAminoGroup(smi, mol, centerList, expectedPositions);
	}
	
	public void test02() throws Exception 
	{
		String smi = "NCCCC[N+][H]";
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smi);
		List<AminoGroup> centerList = AminoGroup.findAllCenters(mol);
		List<int[]> expectedPositions = new ArrayList<int[]>();
		expectedPositions.add(new int[] {0});
		expectedPositions.add(new int[] {5,6});
		checkAminoGroup(smi, mol, centerList, expectedPositions);
	}
	
	public void test03() throws Exception 
	{
		String smi = "N=CCCCN";
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smi);
		List<AminoGroup> centerList = AminoGroup.findAllCenters(mol);
		List<int[]> expectedPositions = new ArrayList<int[]>();
		//imino group (atom 0) is excluded 
		expectedPositions.add(new int[] {5});
		checkAminoGroup(smi, mol, centerList, expectedPositions);
	}
	
	public void test11() throws Exception 
	{
		String smi = "CCCC(O)=O";
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smi);
		List<CarboxylicGroup> centerList = CarboxylicGroup.findAllCenters(mol);
		List<int[]> expectedPositions = new ArrayList<int[]>();
		expectedPositions.add(new int[] {3,4,5});
		checkCarboxylicGroup(smi, mol, centerList, expectedPositions);
	}
	
	public void test12() throws Exception 
	{
		String smi = "CCCC([O-])=O";
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smi);
		List<CarboxylicGroup> centerList = CarboxylicGroup.findAllCenters(mol);
		List<int[]> expectedPositions = new ArrayList<int[]>();
		expectedPositions.add(new int[] {3,4,5});
		checkCarboxylicGroup(smi, mol, centerList, expectedPositions);
	}
	
	public void test31() throws Exception 
	{
		String smi = "S(=O)(=O)(O)CCCS(O)=O";
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smi);
		List<SulfGroup> centerList = SulfGroup.findAllCenters(mol);
		List<int[]> expectedPositions = new ArrayList<int[]>();
		expectedPositions.add(new int[] {0,3,1});
		expectedPositions.add(new int[] {7,8,9});
		checkSulfGroup(smi, mol, centerList, expectedPositions);
	}
	
	public void test32() throws Exception 
	{
		String smi = "S(=O)(=O)(O)CCCS([O-])=O";
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smi);
		List<SulfGroup> centerList = SulfGroup.findAllCenters(mol);
		List<int[]> expectedPositions = new ArrayList<int[]>();
		expectedPositions.add(new int[] {0,3,1});
		expectedPositions.add(new int[] {7,8,9});
		checkSulfGroup(smi, mol, centerList, expectedPositions);
	}
	
	public void test33() throws Exception 
	{
		String smi = "S(=O)(=O)(O)CCCS(O[H])=O";
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smi);
		List<SulfGroup> centerList = SulfGroup.findAllCenters(mol);
		List<int[]> expectedPositions = new ArrayList<int[]>();
		expectedPositions.add(new int[] {0,3,1});
		expectedPositions.add(new int[] {7,8,10,9}); //9 is the explicit H atom
		checkSulfGroup(smi, mol, centerList, expectedPositions);
	}
	
	public void test41() throws Exception 
	{
		String smi = "P(=O)(O)(O)CCCP(C)(O)=O";
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smi);
		List<PhosphoricGroup> centerList = PhosphoricGroup.findAllCenters(mol);
		List<int[]> expectedPositions = new ArrayList<int[]>();
		expectedPositions.add(new int[] {0,3,1});
		expectedPositions.add(new int[] {7,9,10});
		checkPhosphoricGroup(smi, mol, centerList, expectedPositions);
	}
	
	public void test101() throws Exception 
	{	
		String smi = "CCCCN";
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smi);
		List<AminoGroup> centerList = AminoGroup.findAllCenters(mol);
		AminoGroup g = centerList.get(0);
		g.shiftState();
		String newSmi = SmartsHelper.moleculeToSMILES(mol, true);
		g.shiftState();
		String newSmi2 = SmartsHelper.moleculeToSMILES(mol, true);
		
		assertEquals("Testing amino group state shift", "CCCC[NH3+]", newSmi);
		assertEquals("Testing amino group double state shift", smi, newSmi2);
		//System.out.println(smi + " --> " + newSmi + " --> " + newSmi2);		
	}
	
	public void test102() throws Exception 
	{	
		String smi = "CCCCNC";
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smi);
		List<AminoGroup> centerList = AminoGroup.findAllCenters(mol);
		AminoGroup g = centerList.get(0);
		g.shiftState();
		String newSmi = SmartsHelper.moleculeToSMILES(mol, true);
		g.shiftState();
		String newSmi2 = SmartsHelper.moleculeToSMILES(mol, true);
		
		assertEquals("Testing amino group state shift", "CCCC[NH2+]C", newSmi);
		assertEquals("Testing amino group double state shift", smi, newSmi2);
		//System.out.println(smi + " --> " + newSmi + " --> " + newSmi2);		
	}
	
	public void test103() throws Exception 
	{	
		String smi = "CCCCN(C)C";
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smi);
		List<AminoGroup> centerList = AminoGroup.findAllCenters(mol);
		AminoGroup g = centerList.get(0);
		g.shiftState();
		String newSmi = SmartsHelper.moleculeToSMILES(mol, true);
		g.shiftState();
		String newSmi2 = SmartsHelper.moleculeToSMILES(mol, true);
		
		assertEquals("Testing amino group state shift", "CCCC[NH+](C)C", newSmi);
		assertEquals("Testing amino group double state shift", smi, newSmi2);
		//System.out.println(smi + " --> " + newSmi + " --> " + newSmi2);		
	}
	
	public void test201() throws Exception 
	{	
		String smi = "CCCC(=O)O";
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smi);
		List<CarboxylicGroup> centerList = CarboxylicGroup.findAllCenters(mol);
		CarboxylicGroup g = centerList.get(0);
		g.shiftState();
		String newSmi = SmartsHelper.moleculeToSMILES(mol, true);
		g.shiftState();
		String newSmi2 = SmartsHelper.moleculeToSMILES(mol, true);
		
		assertEquals("Testing carboxylic group state shift", "CCCC(=O)[O-]", newSmi);
		assertEquals("Testing carboxylic group double state shift", smi, newSmi2);
		//System.out.println(smi + " --> " + newSmi + " --> " + newSmi2);		
	}
	
	public void test301() throws Exception 
	{	
		String smi = "CCCS(=O)O";
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smi);
		List<SulfGroup> centerList = SulfGroup.findAllCenters(mol);
		SulfGroup g = centerList.get(0);
		g.shiftState();
		String newSmi = SmartsHelper.moleculeToSMILES(mol, true);
		g.shiftState();
		String newSmi2 = SmartsHelper.moleculeToSMILES(mol, true);
		
		assertEquals("Testing sulf group state shift", "CCCS(=O)[O-]", newSmi);
		assertEquals("Testing sulf group double state shift", smi, newSmi2);
		//System.out.println(smi + " --> " + newSmi + " --> " + newSmi2);		
	}
	
	public void test302() throws Exception 
	{	
		String smi = "CCCS(=O)(=O)O";
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smi);
		List<SulfGroup> centerList = SulfGroup.findAllCenters(mol);
		SulfGroup g = centerList.get(0);
		g.shiftState();
		String newSmi = SmartsHelper.moleculeToSMILES(mol, true);
		g.shiftState();
		String newSmi2 = SmartsHelper.moleculeToSMILES(mol, true);
		
		assertEquals("Testing sulf group state shift", "CCCS(=O)(=O)[O-]", newSmi);
		assertEquals("Testing sulf group double state shift", smi, newSmi2);
		//System.out.println(smi + " --> " + newSmi + " --> " + newSmi2);		
	}
	
	public void test401() throws Exception 
	{	
		String smi = "CCCP(=O)(C)O";
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smi);
		List<PhosphoricGroup> centerList = PhosphoricGroup.findAllCenters(mol);
		PhosphoricGroup g = centerList.get(0);
		g.shiftState();
		String newSmi = SmartsHelper.moleculeToSMILES(mol, true);
		g.shiftState();
		String newSmi2 = SmartsHelper.moleculeToSMILES(mol, true);
		
		assertEquals("Testing Phosphoric group state shift", "CCCP(=O)(C)[O-]", newSmi);
		assertEquals("Testing Phosphoric group double state shift", smi, newSmi2);
		//System.out.println(smi + " --> " + newSmi + " --> " + newSmi2);		
	}
	
	public void test402() throws Exception 
	{	
		String smi = "CCCP(=O)(Cl)O";
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smi);
		List<PhosphoricGroup> centerList = PhosphoricGroup.findAllCenters(mol);
		PhosphoricGroup g = centerList.get(0);
		g.shiftState();
		String newSmi = SmartsHelper.moleculeToSMILES(mol, true);
		g.shiftState();
		String newSmi2 = SmartsHelper.moleculeToSMILES(mol, true);
		
		assertEquals("Testing Phosphoric group state shift", "CCCP(=O)(Cl)[O-]", newSmi);
		assertEquals("Testing Phosphoric group double state shift", smi, newSmi2);
		//System.out.println(smi + " --> " + newSmi + " --> " + newSmi2);		
	}
	
	public void test403() throws Exception 
	{	
		String smi = "CCCP(=O)(O)O";
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smi);
		List<PhosphoricGroup> centerList = PhosphoricGroup.findAllCenters(mol);
		PhosphoricGroup g = centerList.get(0);
		g.shiftState();
		String newSmi = SmartsHelper.moleculeToSMILES(mol, true);
		g.shiftState();
		String newSmi2 = SmartsHelper.moleculeToSMILES(mol, true);
		
		assertEquals("Testing Phosphoric group state shift", "CCCP(=O)(O)[O-]", newSmi);
		assertEquals("Testing Phosphoric group double state shift", smi, newSmi2);
		//System.out.println(smi + " --> " + newSmi + " --> " + newSmi2);		
	}
	
	
}
