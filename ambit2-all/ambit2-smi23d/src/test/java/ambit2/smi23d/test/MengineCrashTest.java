/* MengineCrashTest.java
 * Author: Nina Jeliazkova
 * Date: Mar 18, 2008 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2008  Nina Jeliazkova
 * 
 * Contact: nina@acad.bg
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

package ambit2.smi23d.test;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.io.iterator.IteratingSDFReader;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.base.config.Preferences;
import ambit2.core.helper.CDKHueckelAromaticityDetector;
import ambit2.core.processors.structure.AtomConfigurator;
import ambit2.core.processors.structure.HydrogenAdderProcessor;
import ambit2.smi23d.ShellMengine;
import ambit2.smi23d.ShellSmi2SDF;

/**
 * Test for Mengine - moved here from Toxtree source code
 * 
 * @author nina
 * 
 */
public class MengineCrashTest {

	protected static Logger logger = Logger.getLogger(MengineCrashTest.class
			.getName());
	protected ShellSmi2SDF smi2sdf;
	protected ShellMengine mengine;

	@Before
	public void setUp() throws Exception {
		smi2sdf = new ShellSmi2SDF();
		smi2sdf.setGenerateSmiles(true);
		smi2sdf.setDropHydrogens(false);
		mengine = new ShellMengine();
		Logger tempLogger = logger;
		Level level = Level.ALL;
		while (tempLogger != null) {
			tempLogger.setLevel(level);
			for (Handler handler : tempLogger.getHandlers())
				handler.setLevel(level);
			tempLogger = tempLogger.getParent();
		}
	}

	@Test
	public void test1() throws Exception {
		IAtomContainer a = getChemical("ambit2/data/mengine/problem-001.sdf");
		IAtomContainer b = getChemical("ambit2/data/mengine/problem-001-chemidplus.sdf");
		isIsomorph(a, b);
	}

	@Test
	public void test1_1() throws Exception {
		IAtomContainer a = getChemical("ambit2/data/mengine/problem-001.sdf");
		IAtomContainer b = getChemical("ambit2/data/mengine/smi2sdf_generated.sdf");
		isIsomorph(a, b);
	}

	@Test
	public void test2() throws Exception {
		IAtomContainer a = getChemical("ambit2/data/mengine/problem-002.sdf");
		IAtomContainer b = getChemical("ambit2/data/mengine/problem-002-chemidplus.sdf");
		isIsomorph(a, b);
	}

	/*
	 * The two compounds are not the same!
	 * 
	 * @throws Exception
	 */
	public void test3() throws Exception {
		IAtomContainer a = getChemical("ambit2/data/mengine/problem-003.sdf");
		IAtomContainer b = getChemical("ambit2/data/mengine/problem-003-chemidplus.sdf");
		isIsomorph(a, b);
	}

	@Test
	public void test4() throws Exception {
		IAtomContainer a = getChemical("ambit2/data/mengine/problem-004.sdf");
		IAtomContainer b = getChemical("ambit2/data/mengine/problem-004-chemidplus.sdf");
		isIsomorph(a, b);
	}

	public void isIsomorph(IAtomContainer a, IAtomContainer b) throws Exception {

		SmilesGenerator g = new SmilesGenerator();
		AtomConfigurator c = new AtomConfigurator();
		HydrogenAdderProcessor h = new HydrogenAdderProcessor();
		CDKHueckelAromaticityDetector
				.detectAromaticity(c.process(h.process(a)));
		CDKHueckelAromaticityDetector
				.detectAromaticity(c.process(h.process(b)));
		Assert.assertEquals(a.getAtomCount(), b.getAtomCount());

		UniversalIsomorphismTester uit = new UniversalIsomorphismTester();
		Assert.assertTrue(uit.isIsomorph(a, b));

		String s1 = g.create(a);
		String s2 = g.create(b);

		SmilesParser p = new SmilesParser(SilentChemObjectBuilder.getInstance());
		IAtomContainer a1 = p.parseSmiles(s1);
		IAtomContainer b1 = p.parseSmiles(s2);

		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(a1);
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(b1);
		CDKHueckelAromaticityDetector
				.detectAromaticity(c.process(h.process(a1)));
		CDKHueckelAromaticityDetector
				.detectAromaticity(c.process(h.process(b1)));

		Assert.assertTrue(uit.isIsomorph(a1, b1));
		Assert.assertTrue(uit.isIsomorph(a, a1));
		Assert.assertTrue(uit.isIsomorph(b, b1));

		CDKHueckelAromaticityDetector
				.detectAromaticity(h.process(c.process(a)));
		CDKHueckelAromaticityDetector
				.detectAromaticity(h.process(c.process(b)));
		Assert.assertTrue(uit.isIsomorph(a, b));

	}

	@Test
	public void testCrash1() throws Exception {
		smi2sdf.setGenerateSmiles(true);
		smi2sdf.setDropHydrogens(false);
		IAtomContainer a = getChemical("ambit2/data/mengine/problem-001.sdf");
		Assert.assertEquals(0, goCrash(a, "GENERATED_SMILES"));
	}

	@Test
	public void testCrash2() throws Exception {
		smi2sdf.setGenerateSmiles(true);
		smi2sdf.setDropHydrogens(false);
		IAtomContainer a = getChemical("ambit2/data/mengine/problem-002.sdf");
		Assert.assertEquals(0, goCrash(a, "GENERATED_SMILES"));
	}

	@Test
	public void testCrash3() throws Exception {
		smi2sdf.setGenerateSmiles(true);
		smi2sdf.setDropHydrogens(false);
		IAtomContainer a = getChemical("ambit2/data/mengine/problem-003.sdf");
		Assert.assertEquals(0, goCrash(a, "GENERATED_SMILES"));
	}

	@Test
	public void testCrash4() throws Exception {
		smi2sdf.setGenerateSmiles(true);
		smi2sdf.setDropHydrogens(false);
		IAtomContainer a = getChemical("ambit2/data/mengine/problem-004.sdf");
		Assert.assertEquals(0, goCrash(a, "GENERATED_SMILES"));
	}

	@Test
	public void testCrash5() throws Exception {
		smi2sdf.setGenerateSmiles(true);
		smi2sdf.setDropHydrogens(false);
		IAtomContainer a = getChemical("ambit2/data/mengine/problem-005.sdf");
		Assert.assertEquals(0, goCrash(a, "GENERATED_SMILES"));
	}

	@Test
	public void testCrash6() throws Exception {
		smi2sdf.setGenerateSmiles(true);
		smi2sdf.setDropHydrogens(false);
		IAtomContainer a = getChemical("ambit2/data/mengine/problem-006.sdf");
		Assert.assertEquals(0, goCrash(a, "GENERATED_SMILES"));
	}

	@Test
	public void testCrash7() throws Exception {
		smi2sdf.setGenerateSmiles(true);
		smi2sdf.setDropHydrogens(false);
		IAtomContainer a = getChemical("ambit2/data/mengine/problem-007.sdf");
		Assert.assertEquals(0, goCrash(a, "GENERATED_SMILES"));
	}

	@Test
	public void testCrashNoH1() throws Exception {
		smi2sdf.setGenerateSmiles(true);
		smi2sdf.setDropHydrogens(true);
		IAtomContainer a = getChemical("ambit2/data/mengine/problem-001.sdf");
		Assert.assertEquals(0, goCrash(a, "SMILES"));
	}

	@Test
	public void testCrashNoH2() throws Exception {
		smi2sdf.setGenerateSmiles(true);
		smi2sdf.setDropHydrogens(true);
		IAtomContainer a = getChemical("ambit2/data/mengine/problem-002.sdf");
		Assert.assertEquals(0, goCrash(a, "GENERATED_SMILES"));
	}

	@Test
	public void testCrashNoH3() throws Exception {
		smi2sdf.setGenerateSmiles(true);
		smi2sdf.setDropHydrogens(true);
		IAtomContainer a = getChemical("ambit2/data/mengine/problem-003.sdf");
		Assert.assertEquals(0, goCrash(a, "GENERATED_SMILES"));
	}

	@Test
	public void testCrashNoH4() throws Exception {
		smi2sdf.setGenerateSmiles(true);
		smi2sdf.setDropHydrogens(true);
		IAtomContainer a = getChemical("ambit2/data/mengine/problem-004.sdf");
		Assert.assertEquals(0, goCrash(a, "GENERATED_SMILES"));
	}

	@Test
	public void testCrashNoH5() throws Exception {
		smi2sdf.setGenerateSmiles(true);
		smi2sdf.setDropHydrogens(true);
		IAtomContainer a = getChemical("ambit2/data/mengine/problem-005.sdf");
		Assert.assertEquals(0, goCrash(a, "GENERATED_SMILES"));
	}

	@Test
	public void testCrashNoH6() throws Exception {
		smi2sdf.setGenerateSmiles(true);
		smi2sdf.setDropHydrogens(true);
		IAtomContainer a = getChemical("ambit2/data/mengine/problem-006.sdf");
		Assert.assertEquals(0, goCrash(a, "GENERATED_SMILES"));
	}

	@Test
	public void testCrashNoH7() throws Exception {
		smi2sdf.setGenerateSmiles(true);
		smi2sdf.setDropHydrogens(true);
		IAtomContainer a = getChemical("ambit2/data/mengine/problem-007.sdf");
		Assert.assertEquals(0, goCrash(a, "GENERATED_SMILES"));
	}

	@Test
	public void testCrash8() throws Exception {
		smi2sdf.setGenerateSmiles(true);
		smi2sdf.setDropHydrogens(true);
		SmilesParser p = new SmilesParser(SilentChemObjectBuilder.getInstance());
		IAtomContainer a = p.parseSmiles("N#N=CC(=O)NCC(=O)NN");
		Assert.assertEquals(0, goCrash(a, "GENERATED_SMILES"));
	}

	/**
	 * https://sourceforge.net/tracker/?func=detail&aid=3138563&group_id=152702&
	 * atid=785126
	 */

	public void test_Toxtree_bug_3138563() throws Exception {
		smi2sdf.setGenerateSmiles(true);
		smi2sdf.setDropHydrogens(true);
		SmilesParser p = new SmilesParser(SilentChemObjectBuilder.getInstance());
		IAtomContainer a = p.parseSmiles("Cc2c(NS(=O)(=O)c1ccc(N)cc1)onc2C");
		Assert.assertEquals(0, goCrash(a, "GENERATED_SMILES"));
	}

	public int goCrash(IAtomContainer a, String smilesfield) throws Exception {
		AtomConfigurator c = new AtomConfigurator();
		HydrogenAdderProcessor h = new HydrogenAdderProcessor();
		CDKHueckelAromaticityDetector
				.detectAromaticity(h.process(c.process(a)));
		Preferences.setProperty(Preferences.SMILES_FIELD, smilesfield);
		smi2sdf.setOutputFile("test.sdf");
		smi2sdf.runShell(a);
		mengine.setInputFile("test.sdf");
		mengine.setOutputFile("good.sdf");
		IAtomContainer newmol = mengine.runShell(a);
		return mengine.getExitCode();
	}

	public IAtomContainer getChemical(String file) throws Exception {
		IAtomContainer a = null;
		IIteratingChemObjectReader<IAtomContainer> reader = new IteratingSDFReader(
				getClass().getClassLoader().getResourceAsStream(file),
				SilentChemObjectBuilder.getInstance());
		while (reader.hasNext()) {
			a = reader.next();
			break;
		}
		reader.close();
		return a;
	}

}
