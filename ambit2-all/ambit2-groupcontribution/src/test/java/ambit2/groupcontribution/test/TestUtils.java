package ambit2.groupcontribution.test;

import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.groupcontribution.descriptors.CDKDescriptorInfo;
import ambit2.groupcontribution.descriptors.CDKDescriptorManager;
import ambit2.groupcontribution.descriptors.ILocalDescriptor;
import ambit2.groupcontribution.descriptors.LDAtomFormalCharge;
import ambit2.groupcontribution.descriptors.LDAtomHeavyNeighbours;
import ambit2.groupcontribution.descriptors.LDAtomHybridization;
import ambit2.groupcontribution.descriptors.LDAtomSymbol;
import ambit2.groupcontribution.descriptors.LDAtomValency;
import ambit2.groupcontribution.descriptors.LDHNum;
import ambit2.groupcontribution.transformations.IValueTransformation;
import ambit2.groupcontribution.transformations.TransformationUtils;
import ambit2.groupcontribution.utils.MoleculeUtils;
import ambit2.smarts.SmartsHelper;


public class TestUtils 
{

	public static void main(String[] args) throws Exception
	{
		//testLD("CC(NC=C)COC(S(=O)(=O)O)CCNC=O");
		//testLD("CN[H]");
		
		//testCDKDescriptors(new String[] {"W"});
		//testCDKDescriptorCalculation("CC(C)C", new String[] {"W"});
		
		testParseTransformation("LN>>POW(2)");
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
		testGetExplicitHCount(moleculeBuilder(smiles));
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
	
	public static void testCDKDescriptors(String[] descriptors)
	{
		CDKDescriptorManager descrMan = new CDKDescriptorManager(); 
		for (String d: descriptors)
			descrMan.parseDecriptor(d);
		
		if (!descrMan.errors.isEmpty())
		{	
			System.out.println("There are descriptor errors:");
			for (String err : descrMan.errors)
				System.out.println(err);
		}
		
		for (CDKDescriptorInfo di : descrMan.descriptors )
		{
			System.out.print(di.toString());
			System.out.print(descrMan.descriptorInstances.get(di.descrInstanceIndex).getClass().getName());
		}
	}
	
	public static void testCDKDescriptorCalculation(String smiles, String[] descriptors) throws Exception
	{
		CDKDescriptorManager descrMan = new CDKDescriptorManager(); 
		for (String d: descriptors)
			descrMan.parseDecriptor(d);
		
		if (!descrMan.errors.isEmpty())
		{	
			System.out.println("There are descriptor errors:");
			for (String err : descrMan.errors)
				System.out.println(err);
		}
		
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smiles);
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);

		System.out.println("Calculating descriptors for " + smiles);
		List<DescriptorValue> dValues = descrMan.calcDecriptorValuesForMolecule(mol, mol);
		for (int i = 0; i < dValues.size(); i++)
		{	
			double d = descrMan.getDecriptor(i, dValues);
			System.out.println(descrMan.descriptors.get(i).name + " = " + d);
		}
	}
	
	public static void testParseTransformation(String s) throws Exception
	{
		System.out.println("Testing transformatio: " + s);
		TransformationUtils tu = new TransformationUtils();
		IValueTransformation  trans = tu.parseTransformation(s);
		
		if (!tu.errors.isEmpty())
		{	
			System.out.println("There are transformation errors:");
			for (String err : tu.errors)
				System.out.println(err);
		}
		
		if (trans != null)
			System.out.println(trans.getTransformationName());
	}

}
