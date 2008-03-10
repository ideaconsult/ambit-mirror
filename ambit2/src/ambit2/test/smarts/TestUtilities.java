package ambit2.test.smarts;

import ambit2.smarts.*;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.exception.InvalidSmilesException;


public class TestUtilities 
{
	public static void main(String[] args)
	{
		TestUtilities tu = new TestUtilities();
		tu.testSmartsManager("[!$([OH1,SH1])]C(=O)[Br,Cl,F,I]","CN(C)C(=O)Cl");
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
	
	public void testSmartsManager(String smarts, String smiles)
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
}
