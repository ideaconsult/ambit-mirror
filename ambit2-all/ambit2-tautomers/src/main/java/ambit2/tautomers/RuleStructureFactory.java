package ambit2.tautomers;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;

import java.util.HashMap;


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
	
	public void makeRuleStrcutureFileFor(String smi1, String smi2, int atachPoint, String outFile)
	{
		
	}
	
	
	
	
	
}