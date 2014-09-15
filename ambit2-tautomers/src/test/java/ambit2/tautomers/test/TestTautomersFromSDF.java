package ambit2.tautomers.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.FixBondOrdersTool;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.core.io.FileInputState;
import ambit2.tautomers.TautomerManager;

/**
 * Test for tautomer generation. Different number of tautomers obtained , depending on the starting structure 
 * (SDF vs SMILES and different tautomers as a starting point)
 * @author nina
 *
 */
public class TestTautomersFromSDF {
	protected FixBondOrdersTool kekulizer = new FixBondOrdersTool();
	TautomerManager tman = new TautomerManager();
	SmilesGenerator g = new SmilesGenerator();

	@Test
	public void testWarfarin() throws Exception {
		URL uri = TestTautomersFromSDF.class.getResource("/ambit2/tautomers/test/warfarin_aromatic.sdf");
		Assert.assertNotNull(uri);
		
		InputStream in = new FileInputStream(new File(uri.getFile()));
		Assert.assertNotNull(in);
		
		List<IAtomContainer> tautomersSDF = null;
		try {
			IIteratingChemObjectReader<IAtomContainer> reader = FileInputState.getReader(in,".sdf");
			while (reader.hasNext()) {
				IAtomContainer mol = reader.next();
				g.setUseAromaticityFlag(false);
				tautomersSDF = testTautomerGeneration(mol);
			}
		} catch (Exception x) {
			throw x;
		} finally {
			if (in !=null) in.close();
		}
		//3 tautomers from SDF
		Assert.assertNotNull(tautomersSDF);
		g.setUseAromaticityFlag(true);
		/*
		for (IAtomContainer tautomer: tautomersSDF) {
			System.out.println(g.createSMILES(tautomer));
		}
		*/
		//two valid smiles for warfarin 
		String warfarin1 = "O=C1OC3=CC=CC=C3(C(O)C1C(C=2C=CC=CC=2)CC(=O)C)";
		String warfarin2 = "C3=C(C(C1=C(OC2=C(C1=O)C=CC=C2)O)CC(=O)C)C=CC=C3";
		SmilesParser parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
		
		IMolecule mol = parser.parseSmiles(warfarin1);
		List<IAtomContainer> tautomers1 = testTautomerGeneration(mol);
		//6 tautomers
		mol = parser.parseSmiles(warfarin2);
		List<IAtomContainer> tautomers2 = testTautomerGeneration(mol);
		//18 tautomers
		Assert.assertEquals(tautomers1.size(),tautomersSDF.size());
		Assert.assertEquals(tautomers2.size(),tautomersSDF.size());
		Assert.assertEquals(tautomers1.size(),tautomers2.size());
		
	}
	

	public List<IAtomContainer>  testTautomerGeneration(IAtomContainer mol) throws Exception {
		boolean aromatic = false;
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
		for (IBond bond : mol.bonds()) if (bond.getFlag(CDKConstants.ISAROMATIC)) {aromatic = true; break;}
		if (!aromatic)	CDKHueckelAromaticityDetector.detectAromaticity(mol);
		aromatic = false;
		for (IBond bond : mol.bonds()) if (bond.getFlag(CDKConstants.ISAROMATIC)) {aromatic = true; break;}
		//implicit H count is NULL if read from InChI ...
		mol = AtomContainerManipulator.removeHydrogens(mol);
		CDKHydrogenAdder.getInstance(mol.getBuilder()).addImplicitHydrogens(mol);
		
		if (aromatic) 
			mol = kekulizer.kekuliseAromaticRings((IMolecule)mol);
		
		//System.out.println(g.createSMILES(mol));
		tman.setStructure(mol);
		
		return tman.generateTautomersIncrementaly();
	}
}
