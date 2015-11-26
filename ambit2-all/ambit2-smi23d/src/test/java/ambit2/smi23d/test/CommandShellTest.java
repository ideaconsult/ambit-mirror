package ambit2.smi23d.test;

import java.io.File;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.base.external.CommandShell;
import ambit2.core.helper.CDKHueckelAromaticityDetector;
import ambit2.core.processors.structure.AtomConfigurator;
import ambit2.core.smiles.SmilesParserWrapper;
import ambit2.core.smiles.SmilesParserWrapper.SMILES_PARSER;
import ambit2.smi23d.ShellMengine;
import ambit2.smi23d.ShellSmi2SDF;

public class CommandShellTest {
	protected CommandShell shell;

	@Before
	public void setUp() throws Exception {
		shell = new ShellSmi2SDF();

		File homeDir = new File(System.getProperty("java.io.tmpdir")
				+ "/.ambit2/" + System.getProperty("user.name") + "/smi23d/*");
		homeDir.delete();

	}

	/*
	 * public void testGetInstance() throws Exception { shell =
	 * CommandShell.getInstance(); assertNotNull(shell); CommandShell shell1 =
	 * CommandShell.getInstance(); assertNotNull(shell1);
	 * assertEquals(shell,shell1); }
	 */
	@Test
	public void testAddExecutable() throws Exception {

		String name = "bin/smi23d/win/smi2sdf.exe";
		shell.addExecutable("nt", name, null);
		Assert.assertTrue(shell.getExecutable("nt", null).endsWith(
				"smi2sdf.exe"));
	}

	public void testAddExecutableFreeBSD() throws Exception {
		String name = "bin/smi23d/freebsd/smi2sdf";
		shell.addExecutableFreeBSD(name, null);
		Assert.assertEquals(name,
				shell.getExecutable(CommandShell.os_FreeBSD, null));
	}

	public void testAddExecutableMac() throws Exception {
		String name = "bin/smi23d/mac/smi2sdf";
		shell.addExecutableMac(name, null);
		Assert.assertEquals(name,
				shell.getExecutable(CommandShell.os_MAC, null));
	}

	@Test
	public void testAddExecutableWin() throws Exception {
		String name = "bin/smi23d/win/smi2sdf.exe";
		shell.addExecutableWin(name, null);
		String exec = shell.getExecutable(CommandShell.os_WINDOWS, null);
		// Assert.assertEquals(name,exec);
		File file = new File(exec);
		// System.out.println(file.getAbsolutePath());
		Assert.assertTrue(file.exists());
	}

	@Test
	public void testAddExecutableLinux() throws Exception {
		String name = "bin/smi23d/linux/smi2sdf";
		shell.addExecutableLinux(name, null);
		String exec = shell.getExecutable(CommandShell.os_LINUX, "x86");
		File file = new File(exec);
		// System.out.println(file.getAbsolutePath());
		Assert.assertTrue(file.exists());
	}

	@Test
	public void testAddExecutableLinux64() throws Exception {
		String name = "bin/smi23d/linux64/smi2sdf";
		shell.addExecutableLinux64(name, null);
		String exec = shell.getExecutable(CommandShell.os_LINUX, "amd64");
		File file = new File(exec);

		Assert.assertTrue(file.exists());
	}

	@Test
	public void testRunSMI2SDF() throws Exception {
		ShellSmi2SDF smi2sdf = new ShellSmi2SDF();
		SmilesParserWrapper p = SmilesParserWrapper
				.getInstance(SMILES_PARSER.CDK);
		String smiles = "Nc3ccc2c(ccc1ccccc12)c3";
		// "[H]C1=C([H])C([H])=C([H])C([H])=C1([H])";
		IAtomContainer mol = p.parseSmiles(smiles);
		mol.setProperty("SMILES", smiles);
		mol.setProperty("TITLE", smiles);
		smi2sdf.setReadOutput(true);
		smi2sdf.setInputFile("smi2sdf_test.smi");
		smi2sdf.setOutputFile("smi2sdf_test.sdf");
		IAtomContainer newmol = smi2sdf.runShell(mol);
		Assert.assertEquals(mol.getAtomCount(), newmol.getAtomCount());
		Assert.assertEquals(mol.getBondCount(), newmol.getBondCount());
		for (int i = 0; i < newmol.getAtomCount(); i++) {
			Assert.assertNotNull(newmol.getAtom(i).getPoint3d());
		}
		int aromatic = 0;
		for (IAtom atom : mol.atoms())
			aromatic += atom.getFlag(CDKConstants.ISAROMATIC) ? 1 : 0;
		AtomConfigurator cfg = new AtomConfigurator();
		cfg.process(newmol);
		CDKHueckelAromaticityDetector.detectAromaticity(newmol);
		int newaromatic = 0;
		for (IAtom atom : newmol.atoms())
			newaromatic += atom.getFlag(CDKConstants.ISAROMATIC) ? 1 : 0;
		Assert.assertEquals(aromatic, newaromatic);
		UniversalIsomorphismTester uit = new UniversalIsomorphismTester();
		Assert.assertTrue(uit.isIsomorph(mol, newmol));
	}

	@Test
	public void testRunMENGINE() throws Exception {
		ShellMengine mengine = new ShellMengine();
		IAtomContainer mol = MoleculeFactory.makeAlkane(3);
		mengine.runShell(mol);
	}

	@Test
	public void testRunSMI23D() throws Exception {
		ShellSmi2SDF smi2sdf = new ShellSmi2SDF();
		IAtomContainer mol = MoleculeFactory.makeAlkane(3);
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
		CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance()).addImplicitHydrogens(mol);
		smi2sdf.setOutputFile("test.sdf");
		smi2sdf.runShell(mol);
		ShellMengine mengine = new ShellMengine();
		mengine.setInputFile("test.sdf");
		mengine.setOutputFile("opt.sdf");
		IAtomContainer newmol = mengine.runShell(mol);
		Assert.assertNotNull(newmol);
		IAtomContainer c = AtomContainerManipulator
				.suppressHydrogens(newmol);
		UniversalIsomorphismTester uit = new UniversalIsomorphismTester();
		Assert.assertTrue(uit.isIsomorph(mol, c));
		for (int i = 0; i < newmol.getAtomCount(); i++) {
			Assert.assertNotNull(newmol.getAtom(i).getPoint3d());
			// System.out.println(newmol.getAtom(i).getSymbol() + "\t" +
			// newmol.getAtom(i).getPoint3d());
		}

	}
	/*
	 * @Test public void testOpenBabel() throws Exception { String testfile =
	 * "babel_test.sdf"; OpenBabelShell babel = new OpenBabelShell();
	 * babel.setOutputFile(testfile); IAtomContainer newmol =
	 * babel.runShell("c1ccccc1"); MFAnalyser mf = new MFAnalyser(newmol);
	 * IAtomContainer c = mf.removeHydrogensPreserveMultiplyBonded();
	 * 
	 * IAtomContainer mol = MoleculeFactory.makeBenzene();
	 * 
	 * Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(mol,c)); new
	 * File(babel.getOutputFile()).delete(); }
	 */

}
