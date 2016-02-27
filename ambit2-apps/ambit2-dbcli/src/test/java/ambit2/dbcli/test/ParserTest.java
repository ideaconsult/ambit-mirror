package ambit2.dbcli.test;

import java.io.InputStream;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.inchi.InChIGenerator;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.core.io.InteractiveIteratingMDLReader;
import ambit2.tautomers.processor.StructureStandardizer;

public class ParserTest {

	@Test
	public void testStereo_isomeric() throws Exception {
		testStereo(new SmilesGenerator().isomeric());
	}

	public void testStereo(SmilesGenerator g) throws Exception {

		SmilesParser p = new SmilesParser(SilentChemObjectBuilder.getInstance());

		InChIGeneratorFactory factory = InChIGeneratorFactory.getInstance();

		IIteratingChemObjectReader<IAtomContainer> reader = null;
		int error = 0;
		try {
			InputStream in = ParserTest.class.getClassLoader()
					.getResourceAsStream("ambit2/dbcli/test/stereo.sdf");
			Assert.assertNotNull(in);
			reader = new InteractiveIteratingMDLReader(in,
					SilentChemObjectBuilder.getInstance());
			Assert.assertNotNull(reader);

			StructureStandardizer standardizer = new StructureStandardizer();
			standardizer.setGenerateSMILES(true);
			standardizer.setGenerateSMILES_Canonical(false);
			standardizer.setNeutralise(true);
			standardizer.setSplitFragments(true);
			standardizer.setGenerateTautomers(false);
			standardizer.setImplicitHydrogens(true);
			standardizer.setClearIsotopes(true);

			while (reader.hasNext()) {
				IAtomContainer m1 = reader.next();

				AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(m1);
				try {
					String smi = g.create(m1);

					IAtomContainer m2 = p.parseSmiles(smi);
					AtomContainerManipulator
							.percieveAtomTypesAndConfigureAtoms(m2);
					String newsmiles = g.create(m2);
					Assert.assertEquals(smi, newsmiles);

					InChIGenerator gen1 = factory.getInChIGenerator(m1);
					InChIGenerator gen2 = factory.getInChIGenerator(m2);
					Assert.assertEquals(gen1.getInchi(), gen2.getInchi());
					Assert.assertEquals(gen1.getInchiKey(), gen2.getInchiKey());

					Assert.assertEquals(g.create(m1), g.create(m2));

					System.out.println(smi);
					printstereo(m1);
					System.out.println(newsmiles);
					printstereo(m2);

					IAtomContainer m1_transformed = standardizer.process(m1);
					AtomContainerManipulator
							.percieveAtomTypesAndConfigureAtoms(m1_transformed);
					IAtomContainer m2_transformed = standardizer.process(m2);
					AtomContainerManipulator
							.percieveAtomTypesAndConfigureAtoms(m2_transformed);

					System.out.println(g.create(m1_transformed));
					printstereo(m1_transformed);
					System.out.println(g.create(m2_transformed));
					printstereo(m2_transformed);

					gen1 = factory.getInChIGenerator(m1_transformed);
					gen2 = factory.getInChIGenerator(m2_transformed);
					Assert.assertEquals(gen1.getInchi(), gen2.getInchi());
					Assert.assertEquals(gen1.getInchiKey(), gen2.getInchiKey());
				} catch (Exception x) {
					error++;
					System.out.println(x.getMessage());
				}

			}

		} finally {
			if (reader != null)
				reader.close();
		}
		Assert.assertEquals(0, error);

	}

	protected void printstereo(IAtomContainer m) {
		for (IBond b : m.bonds()) {
			System.out.print(b.getStereo());
			System.out.print('\t');
		}
		System.out.println();
		for (IStereoElement se : m.stereoElements())
			System.out.println(se.getClass().getName());
		System.out.println();
	}
}
