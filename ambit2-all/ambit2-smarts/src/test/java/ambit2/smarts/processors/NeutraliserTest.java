package ambit2.smarts.processors;

import java.io.InputStream;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.core.data.MoleculeTools;
import ambit2.core.processors.FragmentProcessor;

public class NeutraliserTest {
	FragmentProcessor fragmenter = new FragmentProcessor();

	@Test
	public void test() throws Exception {

		MDLReader reader = null;
		IAtomContainer mol = null;
		try {
			InputStream in = (getClass().getClassLoader()
					.getResourceAsStream("ambit2/core/data/charge/input.sdf"));
			Assert.assertNotNull(in);
			reader = new MDLReader(in);
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
			Assert.assertFalse(charge == 0);
			NeutraliseProcessor p = new NeutraliseProcessor();
			p.setAtomtypeasproperties(true);
			p.setSparseproperties(true);
			mol = p.process(mol);
			charge = 0;
			for (IAtom atom : mol.atoms())
				charge += atom.getFormalCharge();
			Assert.assertEquals(0, charge);
			Assert.assertEquals("[O.minus]",
					mol.getProperty("AtomTypes.removed").toString());
			Assert.assertEquals("[]", mol.getProperty("AtomTypes.added")
					.toString());
		}
	}
}
