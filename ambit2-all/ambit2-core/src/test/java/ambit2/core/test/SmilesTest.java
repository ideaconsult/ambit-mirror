/* SmilesTest.java
 * Author: nina
 * Date: Apr 7, 2009
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2009  
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

package ambit2.core.test;

import java.io.InputStream;
import java.io.StringReader;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.FixBondOrdersTool;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.io.iterator.IteratingSDFReader;
import ambit2.core.data.MoleculeTools;
import ambit2.core.helper.CDKHueckelAromaticityDetector;
import ambit2.core.test.io.RawIteratingWrapperTest;

public class SmilesTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	// String[] smiles = {"NCCO","OCCN"};
	@Test
	public void testCanonicalSmiles() throws Exception {

		String[] smiles = { "c1cc(ccc1C(c2ccc(cc2)Cl)=C(Cl)Cl)Cl",
				"Clc1ccc(cc1)C(=C(Cl)Cl)c2ccc(Cl)cc2",
				"C(=C(Cl)Cl)(C1C=CC(=CC=1)Cl)C2=CC=C(C=C2)Cl" };
		String[] newSmiles = { "", "", "" };
		SmilesGenerator gen = SmilesGenerator.unique().aromatic();
		IAtomContainer m = getMolecule();

		// SmilesGenerator now wants implicit H counts set , and atom types
		// needs to be set before that
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(m);
		CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance())
				.addImplicitHydrogens(m);
		CDKHueckelAromaticityDetector.detectAromaticity(m);
		String m_smiles = gen.create(m);
		Assert.assertFalse("".equals(m_smiles));
		SmilesParser parser = new SmilesParser(
				SilentChemObjectBuilder.getInstance());
		UniversalIsomorphismTester uit = new UniversalIsomorphismTester();
		// TODO replace with Pattern.findIdentical (?)
		int count_differences = 0;
		for (int i = 0; i < smiles.length; i++) {
			IAtomContainer mol = parser.parseSmiles(smiles[i]);
			AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
			CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance())
					.addImplicitHydrogens(mol);
			CDKHueckelAromaticityDetector.detectAromaticity(mol);
			Assert.assertTrue(uit.isIsomorph(m, mol));
			newSmiles[i] = gen.create(mol);

			if (!newSmiles[i].equals(m_smiles)) {
				count_differences++;
			}
		}
		Assert.assertEquals(0, count_differences);
	}

	public IAtomContainer getMolecule() {

		String sdf =

		"\n\n\n"
				+

				"18 19  0  0  0  0  0  0  0  0  1 V2000\n"
				+ "    4.6054   -1.9895    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "    4.6054   -0.6632    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "    3.4540   -0.0000    0.0000 Cl  0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "    5.7567   -0.0000    0.0000 Cl  0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "    5.7567   -2.6527    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "    6.9080   -1.9895    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "    8.0594   -2.6527    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "    8.0594   -3.9882    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "    6.9080   -4.6514    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "    5.7567   -3.9882    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "    9.2107   -4.6514    0.0000 Cl  0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "    3.4540   -2.6527    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "    3.4540   -3.9882    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "    2.3027   -4.6514    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "    1.1513   -3.9882    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "    1.1513   -2.6527    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "    2.3027   -1.9895    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "    0.0000   -4.6514    0.0000 Cl  0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "  1  2  2  0  0  0  0\n" + "  1  5  1  0  0  0  0\n"
				+ "  1 12  1  0  0  0  0\n" + "  2  3  1  0  0  0  0\n"
				+ "  2  4  1  0  0  0  0\n" + "  5  6  1  0  0  0  0\n"
				+ "  5 10  2  0  0  0  0\n" + "  6  7  2  0  0  0  0\n"
				+ "  7  8  1  0  0  0  0\n" + "  8  9  2  0  0  0  0\n"
				+ "  8 11  1  0  0  0  0\n" + "  9 10  1  0  0  0  0\n"
				+ " 12 13  2  0  0  0  0\n" + " 12 17  1  0  0  0  0\n"
				+ " 13 14  1  0  0  0  0\n" + " 14 15  2  0  0  0  0\n"
				+ " 15 16  1  0  0  0  0\n" + " 15 18  1  0  0  0  0\n"
				+ " 16 17  2  0  0  0  0\n" + "M  END\n" + "\n" + "$$$$";

		org.openscience.cdk.io.MDLV2000Reader r = new org.openscience.cdk.io.MDLV2000Reader(
				new StringReader(sdf));
		IAtomContainer m = MoleculeTools.newMolecule(SilentChemObjectBuilder
				.getInstance());
		try {
			m = (IAtomContainer) r.read(m);
		} catch (CDKException x) {
			x.printStackTrace();
		}
		return m;
	}

	/*
	 * the 7 -ring aromatic smiles is not quite correct
	 * 
	 * @Test public void testAromaticityRing7() throws Exception { SmilesParser
	 * parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
	 * IAtomContainer mol = parser.parseSmiles("c1cccccc1"); for (IAtom atom :
	 * mol.atoms()) Assert.assertTrue(atom.getFlag(CDKConstants.ISAROMATIC)); }
	 * 
	 * @Test public void testAromaticityRing7a() throws Exception { SmilesParser
	 * parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
	 * IAtomContainer mol = parser.parseSmiles("c1cccccc1");
	 * //AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol); //for
	 * (IAtom atom : mol.atoms())
	 * Assert.assertTrue(atom.getFlag(CDKConstants.HYBRIDIZATION_SP2));
	 * FixBondOrdersTool d = new FixBondOrdersTool(); mol =
	 * d.kekuliseAromaticRings(mol); int doubleBonds = 0; for (IBond bond:
	 * mol.bonds()) if (IBond.Order.DOUBLE.equals(bond.getOrder()))
	 * doubleBonds++;
	 * 
	 * Assert.assertTrue(doubleBonds>0); }
	 */

	@Test
	public void testAromaticityRing6() throws Exception {
		SmilesParser parser = new SmilesParser(
				SilentChemObjectBuilder.getInstance());
		IAtomContainer mol = parser.parseSmiles("c1ccccc1");
		for (IAtom atom : mol.atoms())
			Assert.assertTrue(atom.getFlag(CDKConstants.ISAROMATIC));
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
		FixBondOrdersTool d = new FixBondOrdersTool();
		mol = d.kekuliseAromaticRings(mol);
		int doubleBonds = 0;
		for (IBond bond : mol.bonds())
			if (IBond.Order.DOUBLE.equals(bond.getOrder()))
				doubleBonds++;
		Assert.assertTrue(doubleBonds > 0);
	}

	@Test
	public void testHeteroaromaticRing() throws Exception {
		SmilesParser parser = new SmilesParser(
				SilentChemObjectBuilder.getInstance());
		IAtomContainer mol = parser
				.parseSmiles("Oc1ccc(cc1)c1coc2c(c1=O)c(O)cc(c2)O");
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
		CDKHueckelAromaticityDetector.detectAromaticity(mol);
		int c = 0;
		for (IAtom atom : mol.atoms())
			c += atom.getFlag(CDKConstants.ISAROMATIC) ? 1 : 0;

		Assert.assertEquals(12, c);
	}

	public static void printMol(IAtomContainer mol) {
		System.out.println(mol.getAtomCount());
		System.out.println(mol.getBondCount());
		for (IAtom atom : mol.atoms())
			if (atom.getFlag(CDKConstants.ISAROMATIC))
				System.out.println(atom);
		for (IBond bond : mol.bonds())
			System.out.println(bond.getOrder());
	}

	@Test
	public void test1() throws Exception {
		String s_in = "CC(C)(C)C1=CC(=C(OP2OCC3(COP(OC4=CC=C(C=C4C(C)(C)C)C(C)(C)C)OC3)CO2)C=C1)C(C)(C)C";
		SmilesParser sp = new SmilesParser(
				DefaultChemObjectBuilder.getInstance());
		IAtomContainer m = sp.parseSmiles(s_in);
		SmilesGenerator sg = SmilesGenerator.isomeric();
		Assert.assertEquals(
				"CC(C)(C)C1=CC(=C(OP2OCC3(COP(OC4=CC=C(C=C4C(C)(C)C)C(C)(C)C)OC3)CO2)C=C1)C(C)(C)C",
				sg.create(m));
	}

	@Test
	public void testRoundtrip_isomeric() throws Exception {
		testRoundtrip(new SmilesGenerator().isomeric(),
				"ambit2/core/chembl/roundtrip7.sdf", true);
	}

	@Test
	public void testRoundtrip_absolute() throws Exception {
		testRoundtrip(new SmilesGenerator().absolute(),
				"ambit2/core/chembl/roundtrip7.sdf", true);
	}

	@Test
	public void testRoundtrip_CHEMBL2369356() throws Exception {
		testRoundtrip(new SmilesGenerator().isomeric(),
				"ambit2/core/chembl/CHEMBL2369356.sdf", false);
	}

	public void testRoundtrip(SmilesGenerator g, String resource,
			boolean perceiveAtoms) throws Exception {

		SmilesParser p = new SmilesParser(SilentChemObjectBuilder.getInstance());
		IIteratingChemObjectReader<IAtomContainer> reader = null;
		int error_smiles_generation = 0;
		int error_smiles_parser = 0;
		try {
			InputStream in = RawIteratingWrapperTest.class.getClassLoader()
					.getResourceAsStream(resource);
			Assert.assertNotNull(in);
			reader = new IteratingSDFReader(in,
					SilentChemObjectBuilder.getInstance());
			Assert.assertNotNull(reader);
			while (reader.hasNext()) {
				IAtomContainer mol = reader.next();
				if (perceiveAtoms)
					AtomContainerManipulator
							.percieveAtomTypesAndConfigureAtoms(mol);
				try {
					String smi = g.create(mol);
					try {
						p.parseSmiles(smi);
					} catch (Exception x) {
						error_smiles_parser++;
						System.err.println(String.format("Error parsing generated SMILES %s %s",x.getMessage(),smi));
					}
				} catch (Exception x) {
					error_smiles_generation++;
					System.err.println(String.format("Error generating SMILES %s",x.getMessage()));
				}

			}

		} finally {
			if (reader != null)
				reader.close();
		}
		Assert.assertEquals(0, error_smiles_parser);
		Assert.assertEquals(0, error_smiles_generation);

	}

}
