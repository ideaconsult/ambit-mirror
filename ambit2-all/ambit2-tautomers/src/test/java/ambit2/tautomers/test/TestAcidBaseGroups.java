package ambit2.tautomers.test;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.LoggingTool;

import ambit2.smarts.SmartsHelper;
import ambit2.tautomers.zwitterion.AminoGroup;
import ambit2.tautomers.zwitterion.IBasicCenter;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestAcidBaseGroups extends TestCase
{
	public LoggingTool logger;
	
	public TestAcidBaseGroups() 
	{   
		logger = new LoggingTool(this);		
	}	
	
	public static Test suite() {
		return new TestSuite(TestAcidBaseGroups.class);
	}
	
	void check(String smi, IAtomContainer mol, 
			List<AminoGroup> centerList, List<int[]> expectedPositions )
	{
		assertEquals("Amino group positions ", expectedPositions.size(), centerList.size());
		
		for (AminoGroup g : centerList)
		{
			IAtom atoms[] = g.getAtoms();
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
		check(smi, mol, centerList, expectedPositions);
	}
}
