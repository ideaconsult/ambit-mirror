package ambit2.mopac;


import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.core.smiles.SmilesParserWrapper;
import ambit2.core.smiles.SmilesParserWrapper.SMILES_PARSER;

/**
 * Some utilities and helper functions for the work with MOPAC
 */

public class MopacUtilities 
{
	protected AbstractMopacShell shell;
	protected SmilesParserWrapper parser;
	
	
	public MopacUtilities() throws Exception
	{
		setUp();
	}
	
	public void setUp() throws Exception {
		shell = new MopacShell();
		parser =  SmilesParserWrapper.getInstance(SMILES_PARSER.CDK);	
	}
	

	public double getTotalEnergy(String smiles) throws Exception
	{
		IAtomContainer mol = parser.parseSmiles(smiles); 
		mol.setProperty("SMILES",smiles);
		mol.setProperty("TITLE",smiles);
		return getTotalEnergy(mol);
	}
	
	public double getTotalEnergy(IAtomContainer mol) throws Exception
	{	
		IAtomContainer newmol = shell.runShell(mol);
		Double energy = Double.parseDouble((String)newmol.getProperty("TOTAL ENERGY"));
		return energy;
	}
	
}
