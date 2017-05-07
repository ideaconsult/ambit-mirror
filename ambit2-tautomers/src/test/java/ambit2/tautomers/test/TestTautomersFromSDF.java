package ambit2.tautomers.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.List;

import junit.framework.Assert;
import net.idea.modbcum.p.DefaultAmbitProcessor;

import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.aromaticity.ElectronDonation;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.inchi.InChIGenerator;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.io.SDFWriter;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.STRUC_TYPE;
import ambit2.core.data.MoleculeTools;
import ambit2.core.io.FileInputState;
import ambit2.core.io.IRawReader;
import ambit2.core.processors.structure.InchiProcessor;
import ambit2.core.processors.structure.MoleculeReader;
import ambit2.tautomers.TautomerConst;
import ambit2.tautomers.TautomerManager;
import ambit2.tautomers.processor.TautomerProcessor;

/**
 * Test for tautomer generation. Different number of tautomers obtained ,
 * depending on the starting structure (SDF vs SMILES and different tautomers as
 * a starting point)
 * 
 * @author nina
 * 
 */
public class TestTautomersFromSDF {
	protected TautomerManager tman = new TautomerManager();
	protected SmilesGenerator g = SmilesGenerator.unique();
	protected InchiProcessor inchip = null;

	protected Aromaticity aromaticity = new Aromaticity(ElectronDonation.cdk(),
			Cycles.all());

	@Test
	public void testWarfarin() throws Exception {
		URL uri = TestTautomersFromSDF.class
				.getResource("/ambit2/tautomers/test/warfarin_aromatic.sdf");
		Assert.assertNotNull(uri);

		InputStream in = new FileInputStream(new File(uri.getFile()));
		Assert.assertNotNull(in);

		List<IAtomContainer> tautomersSDF = null;
		try {
			IIteratingChemObjectReader<IAtomContainer> reader = FileInputState
					.getReader(in, ".sdf");
			while (reader.hasNext()) {
				IAtomContainer mol = reader.next();

				tautomersSDF = testTautomerGeneration(mol);
			}
		} catch (Exception x) {
			throw x;
		} finally {
			if (in != null)
				in.close();
		}
		// 3 tautomers from SDF
		Assert.assertNotNull(tautomersSDF);
		System.out.println(String.format(
				"Tautomers from SDF (bond order 4) %d", tautomersSDF.size()));

		// two valid smiles for warfarin
		String warfarin1 = "O=C1OC3=CC=CC=C3(C(O)C1C(C=2C=CC=CC=2)CC(=O)C)";
		String warfarin2 = "C3=C(C(C1=C(OC2=C(C1=O)C=CC=C2)O)CC(=O)C)C=CC=C3";
		SmilesParser parser = new SmilesParser(
				SilentChemObjectBuilder.getInstance());

		IAtomContainer mol = parser.parseSmiles(warfarin1);
		List<IAtomContainer> tautomers1 = testTautomerGeneration(mol);
		System.out.println(String.format("Tautomers from SMILES(1) %d ",
				tautomers1.size()));
		// 6 tautomers
		mol = parser.parseSmiles(warfarin2);
		List<IAtomContainer> tautomers2 = testTautomerGeneration(mol);
		System.out.println(String.format("Tautomers from SMILES(2) %d ",
				tautomers2.size()));

		// 18 tautomers
		Assert.assertEquals(tautomers2.size(), tautomersSDF.size());
		Assert.assertEquals(tautomers1.size(), tautomersSDF.size());
		Assert.assertEquals(tautomers1.size(), tautomers2.size());

	}

	public List<IAtomContainer> testTautomerGeneration(IAtomContainer mol)
			throws Exception {
		if (MoleculeTools.repairBondOrder4(mol)) {
			// ok
			int aromatic = 0;
			for (IBond b : mol.bonds()) {
				Assert.assertNotSame(IBond.Order.UNSET, b.getOrder());
				int ba = 0;
				for (IAtom a : b.atoms())
					if (a.getFlag(CDKConstants.ISAROMATIC)) {
						ba++;
					}
				if (ba > 1)
					aromatic++;
			}
			//System.out.println(aromatic);
		} else {
			AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
			CDKHydrogenAdder.getInstance(mol.getBuilder())
					.addImplicitHydrogens(mol);

		}
		aromaticity.apply(mol);
		mol = AtomContainerManipulator.suppressHydrogens(mol);

		/*
		 * if (!aromatic) CDKHueckelAromaticityDetector.detectAromaticity(mol);
		 * aromatic = false; for (IBond bond : mol.bonds()) if
		 * (bond.getFlag(CDKConstants.ISAROMATIC)) {aromatic = true; break;}
		 * //implicit H count is NULL if read from InChI ...
		 * CDKHydrogenAdder.getInstance
		 * (mol.getBuilder()).addImplicitHydrogens(mol); mol =
		 * AtomContainerManipulator.removeHydrogens(mol);
		 */

		//System.out.println(g.aromatic().create(mol));

		if (inchip == null)
			inchip = new InchiProcessor();
		InChIGenerator igen = inchip.process(mol);
		System.out.println(igen.getInchi());

		tman.setStructure(mol);

		return tman.generateTautomersIncrementaly();
	}

	@Test
	public void test_CHEMBL440060() throws Exception {
		test_CHEMBLSDF("/ambit2/tautomers/test/CHEMBL440060.zip");
	}

	@Test
	public void test_CHEMBL2009356() throws Exception {
		test_CHEMBLSDF("/ambit2/tautomers/test/CHEMBL2009356.zip");
	}

	public void test_CHEMBLSDF(String resource) throws Exception {
		URL uri = TestTautomersFromSDF.class.getResource(resource);
		Assert.assertNotNull(uri);

		File infile = new File(uri.getFile());
		Assert.assertNotNull(infile);
		Assert.assertTrue(infile.exists());

		MoleculeReader molReader = new MoleculeReader();
		TautomerProcessor tautomers = new TautomerProcessor(null);

		tautomers
				.setCallback(new DefaultAmbitProcessor<IAtomContainer, IAtomContainer>() {
					int no_rank;

					@Override
					public IAtomContainer process(IAtomContainer target)
							throws Exception {
						Object rank = target
								.getProperty(TautomerConst.TAUTOMER_RANK);
						if (rank == null) {
							rank = target
									.getProperty(TautomerConst.CACTVS_ENERGY_RANK);
							if (rank == null) {
								System.out.println();
								no_rank++;
								if (no_rank > 1)
									throw new Exception(
											String.format(
													"More than one (%d) structure without rank ! [%s]",
													no_rank,
													target.getProperties()));
							}
						}
						return target;
					}

					@Override
					public void close() throws Exception {
						Assert.assertTrue(no_rank <= 1);
					}
				});
		IIteratingChemObjectReader<IChemObject> reader = null;
		try {
			reader = FileInputState.getReader(infile);
			// RawIteratingSDFReader reader = new RawIteratingSDFReader(new
			// InputStreamReader(in));
			while (reader.hasNext()) {
				if (reader instanceof IRawReader) {
					IStructureRecord record = ((IRawReader<IStructureRecord>) reader)
							.nextRecord();
					IAtomContainer mol = molReader.process(record);
					IAtomContainer tautomer = tautomers.process(mol);
					tautomer.addProperties(mol.getProperties());
					// return normalizer.process(record);
					StringWriter w = new StringWriter();
					SDFWriter sdfwriter = new SDFWriter(w);
					sdfwriter.write(tautomer);
					sdfwriter.close();
					record.setContent(w.toString());
					record.setType(STRUC_TYPE.D2noH);
					// System.out.println(record.getContent());
				}

			}
		} catch (Exception x) {
			throw x;
		} finally {
			if (reader != null)
				reader.close();
		}

	}
}
