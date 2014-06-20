package ambit2.groupcontribution.test;

import ambit2.smarts.SmartsHelper;
import ambit2.groupcontribution.descriptors.*;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;


public class TestUtils 
{

	
	public static void main(String[] args) throws Exception
	{
		testLD("CCCOCCCCNCC");
		
	}
	
	public static void testLD(String smiles) throws Exception
	{
		System.out.println("Testing LDs for " + smiles);		
		testLocalDescriptor(smiles, new LDAtomSymbol());
		testLocalDescriptor(smiles, new LDHNum());
		
	}
	
	public static void testLocalDescriptor(String smiles, ILocalDescriptor ld) throws Exception
	{
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smiles);
		
		System.out.print("  " + ld.getShortName() + ": ");
		for (IAtom at : mol.atoms())
		{	
			int descrVal = ld.calcForAtom(at, mol);
			System.out.print("  " + ld.getDesignation(descrVal));
		}	
		System.out.println();
	}

}
