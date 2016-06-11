/* InchiProcessorTest.java
 * Author: nina
 * Date: Jan 11, 2009
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2009  Ideaconsult Ltd.
 * 
 * Contact: nina
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package ambit2.core.processors.test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import net.sf.jniinchi.INCHI_OPTION;
import net.sf.jniinchi.INCHI_RET;

import org.junit.Test;
import org.openscience.cdk.fingerprint.CircularFingerprinter;
import org.openscience.cdk.fingerprint.IBitFingerprint;
import org.openscience.cdk.inchi.InChIGenerator;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.inchi.InChIToStructure;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.similarity.Tanimoto;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.core.config.AmbitCONSTANTS;
import ambit2.core.data.MoleculeTools;
import ambit2.core.helper.CDKHueckelAromaticityDetector;
import ambit2.core.io.FileInputState;
import ambit2.core.processors.structure.AtomConfigurator;
import ambit2.core.processors.structure.HydrogenAdderProcessor;
import ambit2.core.processors.structure.InchiProcessor;

public class InchiProcessorTest {

	@Test
	public void testProcessBenzene() throws Exception {
		generate(MoleculeFactory.makeBenzene(),
				"InChI=1S/C6H6/c1-2-4-6-5-3-1/h1-6H");
	}

	@Test
	public void testProcessCaffeineAromaticity() throws Exception {
		SmilesParser p = new SmilesParser(SilentChemObjectBuilder.getInstance());
		IAtomContainer mol = p.parseSmiles("CN1C=NC2=C1C(=O)N(C(=O)N2C)C");
		generate(
				mol,
				"InChI=1S/C8H10N4O2/c1-10-4-9-6-5(10)7(13)12(3)8(14)11(6)2/h4H,1-3H3",
				true);
	}

	@Test
	public void testProcess2() throws Exception {
		SmilesParser p = new SmilesParser(SilentChemObjectBuilder.getInstance());
		IAtomContainer mol = p.parseSmiles("NC1=CC(N)=NC(O)=N1");
		generate(mol,
				"InChI=1S/C4H6N4O/c5-2-1-3(6)8-4(9)7-2/h1H,(H5,5,6,7,8,9)",
				false);
	}

	@Test
	public void testProcessAromaticity() throws Exception {
		SmilesParser p = new SmilesParser(SilentChemObjectBuilder.getInstance());
		IAtomContainer mol = p.parseSmiles("CN1C=NC2=C1C(=O)N(C(=O)N2C)C");
		// IAtomContainer mol = p.parseSmiles("c1ccccc1");

		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);

		CDKHydrogenAdder ha = CDKHydrogenAdder
				.getInstance(SilentChemObjectBuilder.getInstance());
		ha.addImplicitHydrogens(mol);
		// AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol); //
		// this is the most important

		InChIGeneratorFactory factory = InChIGeneratorFactory.getInstance();
		InChIGenerator gen = factory.getInChIGenerator(mol);
		INCHI_RET ret = gen.getReturnStatus();
		if (ret != INCHI_RET.OKAY) {
			throw new Exception(String.format("InChI failed: %s [%s]",
					ret.toString(), gen.getMessage()));
		}
		String inchi = gen.getInchi();
		Assert.assertEquals(
				"InChI=1S/C8H10N4O2/c1-10-4-9-6-5(10)7(13)12(3)8(14)11(6)2/h4H,1-3H3",
				inchi);
	}

	@Test
	public void testProcess1() throws Exception {
		SmilesParser p = new SmilesParser(SilentChemObjectBuilder.getInstance());
		IAtomContainer m = p.parseSmiles("Cc1ccc(cc1)C(C)C=O");

	}

	@Test
	public void testProcessAlkane() throws Exception {
		generate(MoleculeFactory.makeAlkane(10),
				"InChI=1S/C10H22/c1-3-5-7-9-10-8-6-4-2/h3-10H2,1-2H3");

	}

	public void generate(IAtomContainer mol, String expected) throws Exception {
		generate(mol, expected, false);
	}

	public void generate(IAtomContainer mol, String expected, boolean aromatic)
			throws Exception {
		InchiProcessor p = new InchiProcessor();
		HydrogenAdderProcessor ha = new HydrogenAdderProcessor();
		mol = ha.process(mol);

		if (aromatic) {
			AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
			CDKHueckelAromaticityDetector.detectAromaticity(mol);
		}
		InChIGenerator gen = p.process(mol);
		String inchi = gen.getInchi();
		String auxinfo = gen.getAuxInfo();
		Assert.assertEquals(expected, inchi);
	}

	@Test
	public void parse() throws Exception {
		InChIGeneratorFactory f = InChIGeneratorFactory.getInstance();

		InChIToStructure c = f.getInChIToStructure(
				"InChI=1S/C5H12O/c1-3-4-5-6-2/h3-5H2,1-2H3",
				SilentChemObjectBuilder.getInstance());

		System.out.println(c.getLog());
		System.out.println(c.getWarningFlags());

		AtomConfigurator cfg = new AtomConfigurator();
		HydrogenAdderProcessor ha = new HydrogenAdderProcessor();

		IAtomContainer a = c.getAtomContainer();
		a = ha.process(a);

		generate(a, "InChI=1S/C5H12O/c1-3-4-5-6-2/h3-5H2,1-2H3");
		// InChI=1/C5O/c1-3-4-5-6-2
	}

	public IAtomContainer isInChI(String inchi) throws Exception {
		if ((inchi != null) && inchi.startsWith(AmbitCONSTANTS.INCHI)) {
			InChIGeneratorFactory f = InChIGeneratorFactory.getInstance();
			InChIToStructure c = f.getInChIToStructure(inchi,
					SilentChemObjectBuilder.getInstance());
			if ((c == null) || (c.getAtomContainer() == null)
					|| (c.getAtomContainer().getAtomCount() == 0))
				throw new Exception("Invalid InChI");
			return c.getAtomContainer();
		} else
			return null;
	}

	@Test
	public void testProcessStereo() throws Exception {
		InChIGeneratorFactory factory = InChIGeneratorFactory.getInstance();
		SmilesParser p = new SmilesParser(SilentChemObjectBuilder.getInstance());

		InputStream in = getClass().getClassLoader().getResourceAsStream(
				"ambit2/core/data/stereo/stereo.sdf");
		IIteratingChemObjectReader<IAtomContainer> reader = FileInputState
				.getReader(in, "stereo.sdf");
		int count = 0;
		try {
			while (reader.hasNext()) {
				IAtomContainer o = reader.next();
				InChIGenerator gen = factory.getInChIGenerator(o);
				INCHI_RET ret = gen.getReturnStatus();
				if (ret != INCHI_RET.OKAY) {
					System.err.println(String.format("InChI failed: %s [%s]",
							ret.toString(), gen.getMessage()));
				}
				String inchi = gen.getInchi();
				System.out.println(inchi);

				Assert.assertEquals(o.getProperty("InChI"), inchi);

				Object smiles = o.getProperty("SMILES");
				Assert.assertNotNull(smiles);
				System.out.println(smiles);
				IAtomContainer smilesMol = p.parseSmiles(smiles.toString());
				gen = factory.getInChIGenerator(smilesMol);
				System.out.println(gen.getReturnStatus() + " "
						+ gen.getMessage());
				System.out.println("from SMILES " + gen.getInchi());
				count++;
				Assert.assertEquals(inchi, gen.getInchi());

			}
			Assert.assertEquals(4, count);
		} finally {
			reader.close();
		}

	}

	@Test
	public void testProcessExplicitH() throws Exception {

		List<INCHI_OPTION> options = new ArrayList<INCHI_OPTION>();
		options.add(INCHI_OPTION.FixedH);
		options.add(INCHI_OPTION.SAbs);
		options.add(INCHI_OPTION.SAsXYZ);
		options.add(INCHI_OPTION.SPXYZ);
		options.add(INCHI_OPTION.FixSp3Bug);
		options.add(INCHI_OPTION.AuxNone);

		SmilesParser p = new SmilesParser(SilentChemObjectBuilder.getInstance());
		IAtomContainer mol = p.parseSmiles("CN1C=NC2=C1C(=O)N(C(=O)N2C)C");
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
		CDKHydrogenAdder.getInstance(mol.getBuilder())
				.addImplicitHydrogens(mol);

		IAtomContainer molH = mol.clone();
		MoleculeTools.convertImplicitToExplicitHydrogens(molH);

		InChIGeneratorFactory factory = InChIGeneratorFactory.getInstance();

		InChIGenerator gen = factory.getInChIGenerator(mol, options);
		Assert.assertEquals(INCHI_RET.OKAY, gen.getReturnStatus());
		InChIGenerator genH = factory.getInChIGenerator(mol, options);
		Assert.assertEquals(INCHI_RET.OKAY, genH.getReturnStatus());

		Assert.assertEquals(gen.getInchi(), genH.getInchi());
	}

	@Test
	public void testEquals() throws Exception {

		List<INCHI_OPTION> options = new ArrayList<INCHI_OPTION>();
		options.add(INCHI_OPTION.FixedH);
		options.add(INCHI_OPTION.SAbs);
		options.add(INCHI_OPTION.SAsXYZ);
		options.add(INCHI_OPTION.SPXYZ);
		options.add(INCHI_OPTION.FixSp3Bug);
		options.add(INCHI_OPTION.AuxNone);

		SmilesParser p = new SmilesParser(SilentChemObjectBuilder.getInstance());
		IAtomContainer mol = p.parseSmiles("CN1C=NC2=C1C(=O)N(C(=O)N2C)C");
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
		CDKHydrogenAdder.getInstance(mol.getBuilder())
				.addImplicitHydrogens(mol);

		IAtomContainer molH = mol.clone();
		MoleculeTools.convertImplicitToExplicitHydrogens(molH);

		InChIGeneratorFactory factory = InChIGeneratorFactory.getInstance();

		InChIGenerator gen = factory.getInChIGenerator(mol, options);
		Assert.assertEquals(INCHI_RET.OKAY, gen.getReturnStatus());
		InChIGenerator genH = factory.getInChIGenerator(mol, options);
		Assert.assertEquals(INCHI_RET.OKAY, genH.getReturnStatus());

		Assert.assertEquals(gen.getInchi(), genH.getInchi());
	}

	@Test
	public void testTautomers() throws Exception {

		List<INCHI_OPTION> options = new ArrayList<INCHI_OPTION>();
		options.add(INCHI_OPTION.FixedH);
		options.add(INCHI_OPTION.SAbs);
		options.add(INCHI_OPTION.SAsXYZ);
		options.add(INCHI_OPTION.SPXYZ);
		options.add(INCHI_OPTION.FixSp3Bug);
		options.add(INCHI_OPTION.AuxNone);

		SmilesParser p = new SmilesParser(SilentChemObjectBuilder.getInstance());
		IAtomContainer mol1 = p.parseSmiles("S=NNCC");
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol1);
		CDKHydrogenAdder.getInstance(mol1.getBuilder()).addImplicitHydrogens(
				mol1);

		IAtomContainer mol2 = p.parseSmiles("SN=NCC");
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol2);
		CDKHydrogenAdder.getInstance(mol2.getBuilder()).addImplicitHydrogens(
				mol2);

		InChIGeneratorFactory factory = InChIGeneratorFactory.getInstance();

		InChIGenerator gen1 = factory.getInChIGenerator(mol1, options);
		System.out.println(gen1.getReturnStatus());
		System.out.println(gen1.getMessage());
		// Assert.assertEquals(INCHI_RET.OKAY, gen1.getReturnStatus());

		InChIGenerator gen2 = factory.getInChIGenerator(mol2, options);
		// Assert.assertEquals(INCHI_RET.OKAY, gen2.getReturnStatus());
		System.out.println(gen2.getReturnStatus());
		System.out.println(gen2.getMessage());

		System.out.println(gen1.getInchi());
		System.out.println(gen1.getInchiKey());
		System.out.println(gen2.getInchi());
		System.out.println(gen2.getInchiKey());
		Assert.assertTrue(!gen1.getInchi().equals(gen2.getInchi()));
		Assert.assertTrue(!gen1.getInchiKey().equals(gen2.getInchiKey()));
	}

	@Test
	public void testInChI_pubchem() throws Exception {
		InChIGeneratorFactory f = InChIGeneratorFactory.getInstance();

		InChIToStructure c = f
				.getInChIToStructure(
						"InChI=1/C34H34N4O4/c1-7-21-17(3)25-13-26-19(5)23(9-11-33(39)40)31(37-26)16-32-24(10-12-34(41)42)20(6)28(38-32)15-30-22(8-2)18(4)27(36-30)14-29(21)35-25/h7-8,13-16H,1-2,9-12H2,3-6H3,(H4,35,36,37,38,39,40,41,42)/p-2/fC34H32N4O4/h39,41H/q-2",
						SilentChemObjectBuilder.getInstance());

		System.out.println(c.getLog());
		System.out.println(c.getWarningFlags());

		IAtomContainer a = c.getAtomContainer();
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(a);
		CDKHueckelAromaticityDetector.detectAromaticity(a);

		SmilesGenerator absolute_aromatic = SmilesGenerator.absolute()
				.aromatic();
		SmilesGenerator isomeric_aromatic = SmilesGenerator.isomeric()
				.aromatic();

		CircularFingerprinter fp = new CircularFingerprinter();
		IBitFingerprint cf_inchi = fp.getBitFingerprint(a);
		System.out.println(cf_inchi.asBitSet());
		String[] smiles = new String[] {
				"C=CC1=C(C)C2=NC1=CC3=C(C)C(=C(C=C4C(=C(CCC(=O)O)C(=N4)C=C5C(=C(C)C(=C2)[N-]5)CCC(=O)O)C)[N-]3)C=C",
				"C=CC1=C(C)C2=NC1=CC3=NC(=CC4=C(C)C(=C(C=C5C(=C(C)C(=C2)[N-]5)CCC(=O)O)[N-]4)CCC(=O)O)C(=C3C)C=C",
				"C=CC1=C2C=C3C(=C(C=C)C(=N3)C=C4C(=C(CCC(=O)O)C(=CC5=NC(=CC(=C1C)[N-]2)C(=C5CCC(=O)O)C)[N-]4)C)C" };
		String aromatic_smiles_a = absolute_aromatic.create(a);
		System.out.println(aromatic_smiles_a);
		String aromatic_smiles_i = isomeric_aromatic.create(a);
		System.out.println(aromatic_smiles_i);
		System.out.println();

		SmilesParser p = new SmilesParser(SilentChemObjectBuilder.getInstance());

		for (String smi : smiles) {
			IAtomContainer mol = p.parseSmiles(smi);
			AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
			CDKHueckelAromaticityDetector.detectAromaticity(mol);

			IBitFingerprint cf = fp.getBitFingerprint(mol);
			System.out.println(cf.asBitSet());
			String newsmiles = absolute_aromatic.create(mol);
			System.out.println(newsmiles);
			String newsmiles_i = isomeric_aromatic.create(mol);
			System.out.println(newsmiles_i);

			System.out.print(String.format("%s\t%s\t%s\t", Tanimoto.calculate(
					cf.asBitSet(), cf_inchi.asBitSet()), cf.asBitSet()
					.cardinality(), cf_inchi.asBitSet().cardinality()));

			cf.asBitSet().and(cf_inchi.asBitSet());

			System.out.println(cf.cardinality());
			System.out.println();
		}

	}
}
