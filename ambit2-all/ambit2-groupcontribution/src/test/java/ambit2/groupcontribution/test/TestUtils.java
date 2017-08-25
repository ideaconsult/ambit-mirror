package ambit2.groupcontribution.test;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.groupcontribution.descriptors.ILocalDescriptor;
import ambit2.groupcontribution.descriptors.LDAtomFormalCharge;
import ambit2.groupcontribution.descriptors.LDAtomHeavyNeighbours;
import ambit2.groupcontribution.descriptors.LDAtomHybridization;
import ambit2.groupcontribution.descriptors.LDAtomSymbol;
import ambit2.groupcontribution.descriptors.LDAtomValency;
import ambit2.groupcontribution.descriptors.LDHNum;
import ambit2.groupcontribution.utils.MoleculeUtils;
import ambit2.smarts.SmartsHelper;


public class TestUtils 
{

	public static void main(String[] args) throws Exception
	{
		//testLD("CC(NC=C)COC(S(=O)(=O)O)CCNC=O");
		testLD("CN[H]");
	}

	public static void testLD(String smiles) throws Exception
	{
		//System.out.println("Testing LDs for " + smiles);		
		testLocalDescriptor(smiles, new LDAtomSymbol());
		testLocalDescriptor(smiles, new LDHNum());
		testLocalDescriptor(smiles, new LDAtomFormalCharge());			
		testLocalDescriptor(smiles, new LDAtomHybridization());		
		testLocalDescriptor(smiles, new LDAtomValency());
		testLocalDescriptor(smiles, new LDAtomHeavyNeighbours());
		//testGetExplicitHCount(moleculeBuilder(smiles));
		//testGetHCount(moleculeBuilder(smiles));
	}

	public static void testLocalDescriptor(String smiles, ILocalDescriptor ld) throws Exception
	{
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smiles);
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);

		System.out.print("  " + ld.getShortName() + ":");
		for (IAtom at : mol.atoms())
		{	
			int descrVal = ld.calcForAtom(at, mol);
			System.out.print("\t" + ld.getDesignation(descrVal));
		}	
		System.out.println();
	}

	public static void testGetExplicitHCount(IAtomContainer ac){
		System.out.println("Testing getExplicitHCount: ");
		for (IAtom at : ac.atoms()){
			if(!at.getSymbol().equals("H")){
				int explicitHCount = MoleculeUtils.getExplicitHCount(at, ac);
				System.out.println(at.getSymbol() + ": " + explicitHCount);
			}
		}
	}
	
	public static void testGetHCount(IAtomContainer ac){
		System.out.println("Testing getHCount: ");
		for (IAtom at : ac.atoms()){
			if(!at.getSymbol().equals("H")){
				int hCount = MoleculeUtils.getHCount(at, ac);
				System.out.println(at.getSymbol() + ": " + hCount);
			}
		}
	}
	
	public static IAtomContainer moleculeBuilder(String smiles) throws Exception{
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smiles);
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
		return mol;
	}

}
