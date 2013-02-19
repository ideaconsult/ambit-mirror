package ambit2.tautomers;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.Bond;

import java.util.HashMap;
import java.util.ArrayList;
import ambit2.smarts.ChemObjectFactory;
import ambit2.smarts.SmartsHelper;


/**
 * Utilities for generation of specific structures needed
 */


public class RuleStructureFactory 
{
	SmilesParser smilesParser;
	
	
	public RuleStructureFactory()
	{
		setUp();
	}
	
	public void setUp()
	{
		smilesParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
	}
	
	public void makeRuleStrcutures(String smi1, int pos1, String smi2, int pos2, 
							ArrayList<String> smiles, String outFile) throws Exception 
	{
		ChemObjectFactory cof = new ChemObjectFactory(SilentChemObjectBuilder.getInstance());
		ArrayList<ArrayList<String>> smiSets = new ArrayList<ArrayList<String>>(); 
				
		for (int i = 0; i < smiles.size(); i++)
		{	  
			String base = smiles.get(i);
			System.out.println(base);
			IAtomContainer mol1 = cof.connectStructures(smi1, pos1, base, 0, IBond.Order.SINGLE);
			IAtomContainer mol2 = cof.connectStructures(smi2, pos2, base, 0, IBond.Order.SINGLE);
			String target1 = SmartsHelper.moleculeToSMILES(new Molecule(mol1));
			String target2 = SmartsHelper.moleculeToSMILES(new Molecule(mol2));
			ArrayList<String> list = new ArrayList<String>();
			list.add(target1.trim());
			list.add(target2.trim());
			smiSets.add(list);
		}
		
		cof.saveSmilesTuplesToFile(smiSets, outFile);
	}
	
	
	public void makeRuleStrcutures(String smi1, int pos1, String smi2, int pos2, String inputSmilesFile, String outFile) throws Exception 
	{
		ChemObjectFactory cof = new ChemObjectFactory(SilentChemObjectBuilder.getInstance());
		ArrayList<String> smiles = cof.loadSmilesStringsFromFile(inputSmilesFile);		
		makeRuleStrcutures(smi1, pos1, smi2, pos2, smiles, outFile);
	}


	
}