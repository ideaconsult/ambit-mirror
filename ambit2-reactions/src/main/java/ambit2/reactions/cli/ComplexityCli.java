package ambit2.reactions.cli;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

//import ambit2.core.data.MoleculeTools;
import ambit2.reactions.rules.scores.AtomComplexity;
import ambit2.smarts.SmartsHelper;
import ambit2.smarts.TopLayer;

public class ComplexityCli {

	public static void main(String[] args) throws Exception
	{
		if (args.length > 0)
			calcAtomComplexity(args[0], true);
	}
	
	public static void calcAtomComplexity(String smiles, boolean includeImplicitHAtoms) throws Exception
	{
		System.out.println("Atom complexities for molecule: " + smiles);
		NumberFormat formatter = new DecimalFormat("#0.00"); 
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smiles);
		
		//if (FlagExcplicitHAtoms)
			//MoleculeTools.convertImplicitToExplicitHydrogens(mol);
		TopLayer.setAtomTopLayers(mol);
		AtomComplexity atomCompl = new AtomComplexity();
		atomCompl.setIncludeImplicitHAtoms(includeImplicitHAtoms);
		for (int i = 0; i < mol.getAtomCount(); i++)
		{
			IAtom atom = mol.getAtom(i);
			double c = atomCompl.calcAtomComplexity(atom, mol);
			System.out.println("" + (i+1) + "  " + atom.getSymbol() + "  " + formatter.format(c));
		}
		
		/*
		System.out.println("\nAtom paths details:");
		for (int i = 0; i < mol.getAtomCount(); i++)
		{
			IAtom atom = mol.getAtom(i);
			double c = atomCompl.calcAtomComplexity(atom, mol);
			System.out.println("" + (i+1) + "  " + atom.getSymbol() + "  " + c);
			System.out.println(atomCompl.getAtomPathsAsString(atom, mol));
		}
		*/
		
	}

}
