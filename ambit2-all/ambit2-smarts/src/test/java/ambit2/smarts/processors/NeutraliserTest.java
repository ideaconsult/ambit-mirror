package ambit2.smarts.processors;

import java.io.InputStream;
import java.util.BitSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.fingerprint.CircularFingerprinter;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.core.data.MoleculeTools;
import ambit2.core.processors.FragmentProcessor;

public class NeutraliserTest {
	static Logger logger ;
	static FragmentProcessor fragmenter ;

	@BeforeClass
	public static void steup() {
		logger = Logger.getLogger("NeutraliserTest");
		logger.setLevel(Level.FINEST);
		
		fragmenter = new FragmentProcessor(logger);
	}
	@Test
	public void test() throws Exception {
		processsdf("ambit2/core/data/charge/input.sdf", "[O.minus]",-1,"CCCCCCCCCCCCCCOS(=O)(=O)O");
	}

	@Test
	public void testChembl() throws Exception {
		processsdf("ambit2/smirks/test/CHEMBL570345.sdf", "[N.plus.sp2, O.minus]",0,"C1(=NON(=O)=C1C(C=2SC=CC2)=O)C(=O)C=3SC=CC3");
	}

	public void processsdf(String resource, String atomtypesremoved,int originalcharge,String expectedsmiles)
			throws Exception {

		MDLV2000Reader reader = null;
		IAtomContainer mol = null;
		try {
			InputStream in = (getClass().getClassLoader()
					.getResourceAsStream(resource));
			Assert.assertNotNull(in);
			reader = new MDLV2000Reader(in);
			mol = reader.read(MoleculeTools.newMolecule(SilentChemObjectBuilder
					.getInstance()));
			AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
			CDKHydrogenAdder.getInstance(mol.getBuilder())
					.addImplicitHydrogens(mol);
		} catch (CDKException x) {
			throw x;
		} finally {
			if (reader != null)
				reader.close();
		}
		if (mol != null) {
			mol = fragmenter.process(mol);
			int charge = 0;
			for (IAtom atom : mol.atoms())
				charge += atom.getFormalCharge();
			Assert.assertEquals(originalcharge, charge);
			NeutraliseProcessor p = new NeutraliseProcessor(logger);
			p.setAtomtypeasproperties(true);
			p.setSparseproperties(true);
			mol = p.process(mol);
			charge = 0;
			for (IAtom atom : mol.atoms())
				charge += atom.getFormalCharge();
			
			SmilesGenerator g = SmilesGenerator.isomeric();
			Assert.assertEquals(expectedsmiles,g.create(mol));
			if (atomtypesremoved != null)
				Assert.assertEquals(atomtypesremoved,
						mol.getProperty("AtomTypes.removed").toString());
			Assert.assertEquals("[]", mol.getProperty("AtomTypes.added")
					.toString());
			Assert.assertEquals(0, charge);
			
			CircularFingerprinter fp = new CircularFingerprinter();
			BitSet bs = fp.getBitFingerprint(mol).asBitSet();
			Assert.assertNotNull(bs);
		}
	}
}
