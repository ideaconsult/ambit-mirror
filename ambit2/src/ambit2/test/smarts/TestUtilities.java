package ambit2.test.smarts;

import java.util.Vector;

import ambit2.smarts.*;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.exception.InvalidSmilesException;


public class TestUtilities 
{
	public static void main(String[] args)
	{
		TestUtilities tu = new TestUtilities();
		//tu.testSmartsManagerBoolSearch("[!$([OH1,SH1])]C(=O)[Br,Cl,F,I]","CN(C)C(=O)Cl");
		//tu.testSmartsManagerBoolSearch("[x1;C]", "CCCC");
		tu.testSmartsManagerAtomMapping("N", "CCNCCNCC");
		tu.testSmartsManagerAtomMapping("[x3]", "C1CCC12CCC2");
	}
	
	SmartsParser sp = new SmartsParser();
	SmilesParser smilesparser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
	SmartsManager man = new SmartsManager();
	
	public IMolecule getMoleculeFromSmiles(String smi) 
	{	
		IMolecule mol = null;
		try {
			SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());			
			mol = sp.parseSmiles(smi);
		}
		catch (InvalidSmilesException e) {
			System.out.println(e.toString());
	 	}
		return (mol);
	}
	
	public void testSmartsManagerBoolSearch(String smarts, String smiles)
	{	
		IMolecule mol = getMoleculeFromSmiles(smiles);	
		man.setQuery(smarts);
		if (!man.getErrors().equals(""))
		{
			System.out.println(man.getErrors());
			return;
		}
		boolean res = man.searchIn(mol);
		System.out.println("Man_search " + smarts + " in " + smiles + "   --> " + res);
	}
	
	public void testSmartsManagerAtomMapping(String smarts, String smiles)
	{	
		IMolecule mol = getMoleculeFromSmiles(smiles);	
		man.setQuery(smarts);
		if (!man.getErrors().equals(""))
		{
			System.out.println(man.getErrors());
			return;
		}
		Vector<IAtom> atoms = man.getAtomMappings(mol);
		System.out.println(smarts + " mapped against " + smiles + 
						" gave " + atoms.size()+" atoms:");
		for (int i = 0; i < atoms.size(); i++)
		{
			System.out.print(" "+mol.getAtomNumber(atoms.get(i)));
		}
		System.out.println();
	}
	
	
}
