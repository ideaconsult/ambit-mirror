package ambit2.smarts.test;

import java.util.logging.Logger;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.core.data.MoleculeTools;
import ambit2.core.helper.CDKHueckelAromaticityDetector;
import ambit2.smarts.SMIRKSManager;
import ambit2.smarts.SMIRKSReaction;

public class TestSMIRKSStereo extends TestCase
{
	protected static Logger logger = Logger.getLogger(TestSMIRKSStereo.class
			.getName());
	
	boolean FlagApplyStereoTransformation = true;

	SMIRKSManager smrkMan = new SMIRKSManager(SilentChemObjectBuilder.getInstance());
	SmilesParser smiParser = new SmilesParser(SilentChemObjectBuilder.getInstance());
	
	
	public TestSMIRKSStereo() {
		smrkMan.setFlagApplyStereoTransformation(FlagApplyStereoTransformation);
		smrkMan.setFlagProcessResultStructures(true);
		smrkMan.setFlagClearHybridizationBeforeResultProcess(true);
		smrkMan.setFlagClearImplicitHAtomsBeforeResultProcess(true);
		smrkMan.setFlagClearAromaticityBeforeResultProcess(true);
		smrkMan.setFlagAddImplicitHAtomsOnResultProcess(true);
		smrkMan.setFlagConvertAddedImplicitHToExplicitOnResultProcess(false);
		smrkMan.setFlagConvertExplicitHToImplicitOnResultProcess(true);
		smrkMan.getSmartsParser().mSupportDoubleBondAromaticityNotSpecified = false;
	}
	
	public static Test suite() {
		return new TestSuite(TestSMIRKSStereo.class);
	}
	
	IAtomContainer applySMIRKSReaction(String smirks, IAtomContainer target)
			throws Exception {

		
		SMIRKSReaction reaction = smrkMan.parse(smirks);
		if (!smrkMan.getErrors().equals("")) {
			throw (new Exception("Smirks Parser errors:\n"
					+ smrkMan.getErrors()));
		}		

		if (smrkMan.applyTransformation(target, reaction))
			return target; // all products inside the atomcontainer, could be
							// disconnected
		else
			return null;
	}
	
	IAtomContainer applySMIRKSReaction(String smirks, String targetSmiles, boolean explicitH)
			throws Exception {
		SmilesParser sp = new SmilesParser(
				SilentChemObjectBuilder.getInstance());
		IAtomContainer target = sp.parseSmiles(targetSmiles);
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(target);
		CDKHueckelAromaticityDetector.detectAromaticity(target);
		CDKHydrogenAdder h = CDKHydrogenAdder.getInstance(target.getBuilder());
		h.addImplicitHydrogens(target);
		if (explicitH)
			MoleculeTools.convertImplicitToExplicitHydrogens(target);
		
		return applySMIRKSReaction(smirks, target);
	}
	
	void checkReactionResult(IAtomContainer resultProduct, String expectedProductsSmiles[]) throws Exception
	{
		String expAbsSmi[] = new String[expectedProductsSmiles.length];
		for (int i = 0; i < expAbsSmi.length; i++)
			expAbsSmi[i] = getAbsoluteSmiles(expectedProductsSmiles[i]);
							
		IAtomContainerSet products = ConnectivityChecker.partitionIntoMolecules(resultProduct);
		for (int i = 0; i < products.getAtomContainerCount(); i++)
		{
			IAtomContainer product = products.getAtomContainer(i);
			String prodSmi = SmilesGenerator.absolute().create(product);
			boolean expectedProd = false;
			for (int k = 0; k < expAbsSmi.length; k++)
				if (prodSmi.equals(expAbsSmi[k]))
				{
					expectedProd = true;
					break;
				}
			
			assertEquals("Searching result product: " + prodSmi , true, expectedProd);
		}
	}
	
	String getAbsoluteSmiles(String smi) throws Exception
	{
		IAtomContainer mol = smiParser.parseSmiles(smi);
		return SmilesGenerator.absolute().create(mol);
	}
	
	//Transformations with SMIRKS containing stereo
	
	public void test01A() throws Exception 
	{
		String smirks = "[O:3][C:1]=[C:2][C:4]>>[O:3]/[C:1]=[C:2]/[C:4]";
		String target = "OC=CC";
		String expectedProducts[] = new String[] {"O/C=C/C"};
		boolean FlagExplicitH = false;
		
		IAtomContainer resultProduct = applySMIRKSReaction(smirks, target, FlagExplicitH);
		checkReactionResult(resultProduct, expectedProducts);
	}
	
	public void test01B() throws Exception 
	{
		String smirks = "[O:3][C:1]=[C:2][C:4]>>[O:3]/[C:1]=[C:2]/[C:4]";
		String target = "O/C=C\\C";
		String expectedProducts[] = new String[] {"O/C=C/C"};
		boolean FlagExplicitH = false;
		
		IAtomContainer resultProduct = applySMIRKSReaction(smirks, target, FlagExplicitH);
		checkReactionResult(resultProduct, expectedProducts);
	}
	
	
	
	//Transformation on chemical objects with stereo
	
	public void test101() throws Exception 
	{
		String smirks = "O[C:1]>>N[C:1]";
		String target = "C[C@](O)(CC)Cl";
		String expectedProducts[] = new String[] {"C[C@](N)(CC)Cl"};
		boolean FlagExplicitH = false;
		
		IAtomContainer resultProduct = applySMIRKSReaction(smirks, target, FlagExplicitH);
		checkReactionResult(resultProduct, expectedProducts);
	}
	
	
			//tu.testSMIRKS("O[C:1]>>N[C:1]", "O/C=C/C");		
			//tu.testSMIRKS("O[C:1]Cl>>N[C:1]Br", "C[C@](O)(CC)Cl"); 
			//tu.testSMIRKS("O[C:1]>>N[C:1]", "C[C@H](O)Cl");
			//tu.testSMIRKS("[C:1]=[C:2]>>[C:1].[C:2]", "O/C=C/C");
	
}
