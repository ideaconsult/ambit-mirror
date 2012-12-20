package ambit2.tautomers;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;


/**
 * Utilities for generation of specific structures needed
 */


public class RuleStructureFactory 
{
	SmilesParser smilesParser;
	
	public void setUp()
	{
		smilesParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
	}
	
	
	
	
	public IAtomContainer connectStructures(IAtomContainer str1,int numAt1, 
												   IAtomContainer str2, int numAt2, IBond.Order order) throws Exception
	{
		//TODO
		return null;
	}
	
	public IAtomContainer connectStructures(String smiles1, int numAt1, String smiles2, int numAt2, IBond.Order order) throws Exception 
	{
		IAtomContainer mol1 = smilesParser.parseSmiles(smiles1);
		IAtomContainer mol2 = smilesParser.parseSmiles(smiles2);
		
		return connectStructures(mol1, numAt1, mol2, numAt2, order);
	}
}
