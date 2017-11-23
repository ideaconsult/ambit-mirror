package ambit2.tautomers.test;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.LoggingTool;

import ambit2.smarts.SmartsFlags;
import ambit2.smarts.SmartsHelper;
import ambit2.smarts.SmartsParser;
import ambit2.smarts.TopLayer;
import ambit2.tautomers.rules.CustomTautomerRegion;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestTautomerRegion extends TestCase
{
	public LoggingTool logger;
	
	public TestTautomerRegion()
	{
		logger = new LoggingTool(this);		
	}
	
	public static Test suite() {
		return new TestSuite(TestTautomerRegion.class);
	}
	
	int checkNitroGroupPositions(String smiles, List<int[]> expectedAtIndices) throws Exception
	{
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smiles);
		TopLayer.setAtomTopLayers(mol, TopLayer.TLProp);
		List<IAtom[]> positions =  CustomTautomerRegion.getNitroGroupPositions(mol);
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
	
	public void testNitroGroups() throws Exception
	{
		String smiles;
		List<int[]> expectedPos = new ArrayList<int[]>();
		int res;
		
		smiles = "CCCCN(=O)=O";
		expectedPos.clear();
		expectedPos.add(new int[] {4,5,6});
		res = checkNitroGroupPositions(smiles, expectedPos);
		assertEquals("Nitro group positions for " + smiles, expectedPos.size(), res);
		
	}
	
}
