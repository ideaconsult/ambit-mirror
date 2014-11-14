package ambit2.groupcontribution.test;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.groupcontribution.descriptors.ILocalDescriptor;
import ambit2.groupcontribution.descriptors.LDAtomFormalCharge;
import ambit2.groupcontribution.descriptors.LDAtomHybridization;
import ambit2.groupcontribution.descriptors.LDAtomSymbol;
import ambit2.groupcontribution.descriptors.LDAtomValency;
import ambit2.groupcontribution.descriptors.LDHNum;
import ambit2.smarts.SmartsHelper;


public class TestUtils 
{

	
	public static void main(String[] args) throws Exception
	{
		testLD("CC(NC=C)COC(S(=O)(=O)O)CCNC=O");
		//testLD("c1ccccc1");
	}
	
	public static void testLD(String smiles) throws Exception
	{
		System.out.println("Testing LDs for " + smiles);		
		testLocalDescriptor(smiles, new LDAtomSymbol());
		testLocalDescriptor(smiles, new LDHNum());
		testLocalDescriptor(smiles, new LDAtomFormalCharge());			
		testLocalDescriptor(smiles, new LDAtomHybridization());		
		testLocalDescriptor(smiles, new LDAtomValency());
	}
	
	public static void testLocalDescriptor(String smiles, ILocalDescriptor ld) throws Exception
	{
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smiles);
		
		System.out.print("  " + ld.getShortName() + ": ");
		for (IAtom at : mol.atoms())
		{	
			int descrVal = ld.calcForAtom(at, mol);
			System.out.print("\t" + ld.getDesignation(descrVal));
		}	
		System.out.println();
	}

}
