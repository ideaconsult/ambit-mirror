package ambit2.mopac.test;

import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.silent.Atom;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.base.data.Property;
import ambit2.base.external.CommandShell;
import ambit2.base.external.ShellException;
import ambit2.core.helper.CDKHueckelAromaticityDetector;
import ambit2.core.io.IteratingDelimitedFileReader;
import ambit2.core.smiles.SmilesParserWrapper;
import ambit2.core.smiles.SmilesParserWrapper.SMILES_PARSER;
import ambit2.mopac.AbstractMopacShell;
import ambit2.mopac.Mopac7Writer;
import ambit2.mopac.MopacShell;

public abstract class MopacShellTest {
	protected SmilesParserWrapper parser;
	protected AbstractMopacShell shell;
	protected static Logger logger = Logger.getLogger(MopacShellTest.class
			.getName());

	@Before
	public void setUp() throws Exception {
		shell = new MopacShell();
		File homeDir = new File(System.getProperty("java.io.tmpdir")
				+ "/.ambit2/" + System.getProperty("user.name") + "/mopac/*");
		homeDir.delete();

		parser = SmilesParserWrapper.getInstance(SMILES_PARSER.CDK);
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
	public void getExecutableWin() throws Exception {
		String exec = shell.getExecutable(CommandShell.os_WINDOWS, null);
		// Assert.assertEquals(name,exec);
		File file = new File(exec);
		// System.out.println(file.getAbsolutePath());
		Assert.assertTrue(file.exists());
	}

	@Test
	public void testSimple() throws Exception {
		runShell("C=C");
	}

	@Test
	public void testLarger() throws Exception {
		runShell("[H]C1=C([H])C([H])=C([H])C([H])=C1([H])");
	}

	@Test
	public void testDisconnected() throws Exception {
		shell.setErrorIfDisconnected(false);
		runShell("CCC.CCC");
	}

	public void runShell(String smiles) throws Exception {
		// "[H]C1=C([H])C([H])=C([H])C([H])=C1([H])";
		IAtomContainer mol = parser.parseSmiles(smiles);
		mol.setProperty("SMILES", smiles);
		mol.setProperty("TITLE", smiles);

		IAtomContainer newmol = (IAtomContainer) shell.runShell(mol);
		// Assert.assertEquals(mol.getAtomCount(),newmol.getAtomCount());
		// Assert.assertEquals(mol.getBondCount(),newmol.getBondCount());
		Assert.assertNotNull("ELUMO not found", newmol.getProperty("ELUMO"));
		Assert.assertNotNull("EHOMO not found", newmol.getProperty("EHOMO"));
		Assert.assertNotNull("ELECTRONIC ENERGY not found",
				newmol.getProperty("ELECTRONIC ENERGY"));

		for (int i = 0; i < newmol.getAtomCount(); i++) {
			Assert.assertNotNull(newmol.getAtom(i).getPoint3d());
		}
		SmilesGenerator g = new SmilesGenerator();
		// String newsmiles = g.createSMILES(newmol);
		// assertEquals(smiles,newsmiles);
		// isisomorph returns false if createSmiles was not run before; perhaps
		// smth to do with atom types configuration

		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(newmol);

		CDKHydrogenAdder adder = CDKHydrogenAdder
				.getInstance(SilentChemObjectBuilder.getInstance());
		adder.addImplicitHydrogens(mol);
		AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol);

		UniversalIsomorphismTester uit = new UniversalIsomorphismTester();
		Assert.assertTrue("Isomorphism check", uit.isIsomorph(mol, newmol));
	}

	@Test
	public void testCalculateUnsupportedAtom() throws Exception {

		try {
			runShell("C[Si]");
			Assert.fail("Shouldn't get here");
		} catch (ShellException x) {
			Assert.assertEquals(AbstractMopacShell.MESSAGE_UNSUPPORTED_TYPE
					+ "Si", x.getMessage());
		}
	}

	@Test
	public void testPredictions() throws Exception {
		long now = System.currentTimeMillis();
		shell.setMopac_commands("AM1 NOINTER NOMM BONDS MULLIK PRECISE GNORM=0.0");
		InputStream in = AbstractMopacShell.class.getClassLoader()
				.getResourceAsStream("ambit2/mopac/qsar6train.csv");
		// DelimitedFileWriter writer = new DelimitedFileWriter(new
		// FileOutputStream(file));
		IteratingDelimitedFileReader reader = new IteratingDelimitedFileReader(
				in);
		double r2 = 0;
		double n = 0;

		double sxy = 0;
		double sx = 0;
		double sy = 0;
		double sx2 = 0;
		double sy2 = 0;
		int record = 0;
		try {
			while (reader.hasNext()) {
				Object o = reader.next();
				Assert.assertTrue(o instanceof IAtomContainer);

				IAtomContainer a = (IAtomContainer) o;
				AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(a);
				CDKHueckelAromaticityDetector.detectAromaticity(a);
				IAtomContainer mol = null;
				try {
					mol = shell.runShell(a);
				} catch (Exception x) {
					logger.log(
							Level.SEVERE,
							a.getProperty("Chemical Name (IUPAC)") + " "
									+ x.getMessage());
					continue;
				}
				if (mol == null)
					continue;
				if (mol.getProperty("ELUMO") == null)
					continue;
				double x = Double.parseDouble(mol.getProperty("ELUMO")
						.toString());
				Iterator i = a.getProperties().keySet().iterator();
				while (i.hasNext()) {
					String name = null;
					Object oo = i.next();
					if (oo instanceof Property)
						name = ((Property) oo).getName();
					else
						name = oo.toString();
					if ("LUMO (eV) AM1_MOPAC 4.10".equals(name)) {
						double y = Double.parseDouble(a.getProperty(oo)
								.toString());
						double error = x - y;
						r2 += error * error;
						n++;

						sxy += x * y;
						sx += x;
						sy += y;
						sx2 += x * x;
						sy2 += y * y;
						break;
					}
				}

				record++;
				System.out.print('.');
			}
		} catch (Exception x) {
			x.printStackTrace();
		}
		in.close();

		// calculate Pearson correlation coefficient
		double r = (n * sxy - sx * sy)
				/ (Math.sqrt(n * sx2 - sx * sx) * Math.sqrt(n * sy2 - sy * sy));

		System.out.println(String.format(
				"%s records %d Correlation %f elapsed time %d", shell
						.getClass().getName(), record, r, (System
						.currentTimeMillis() - now)));
		Assert.assertTrue(Math.abs(r) > 0.96);
		/*
		 * ehomo r=0.9736302412992341 r^2=0.9479558467724049 elumo
		 * r=0.9974458630252169 r^2=0.9948982496661197
		 */
		// ambit2.mopac.MopacShellBaloon records 98 Correlation 0.978940 elapsed
		// time 294659 200 gen
		// ambit2.mopac.MopacShellBaloon records 96 Correlation 0.977451 elapsed
		// time 379380 usesimplex
		// ambit2.mopac.MopacShell records 104 Correlation 0.994702 elapsed time
		// 181566
		// .ambit2.mopac.MopacShellBaloon records 96 Correlation 0.983883
		// elapsed time 275302 energy constant 20
		// >>>//ambit2.mopac.MopacShellBaloon records 100 Correlation 0.959101
		// elapsed time 281125 energy constant 40
		// ambit2.mopac.MopacShellBalloon records 99 Correlation 0.977806
		// elapsed time 282409energy constant 40 nosymmetry
		// ambit2.mopac.MopacShellBaloon records 97 Correlation 0.996873 elapsed
		// time 265643 energy constant 20 fullforce
		// ambit2.mopac.MopacShellBaloon records 97 Correlation 0.995814 elapsed
		// time 290206 NoGA 40
		// ambit2.mopac.MopacShellBaloon records 96 Correlation 0.966070 elapsed
		// time 248328 GA fullforce 40 energy
		// ambit2.mopac.MopacShellBaloon records 97 Correlation 0.951499 elapsed
		// time 366438 1 conf NoGA
		// ambit2.mopac.MopacShellBalloon records 98 Correlation 0.972936
		// elapsed time 286993 -t 0.1 e40 nosymmetry
		// ambit2.mopac.MopacShell records 111 Correlation 0.994930 elapsed time
		// 190709
		// ambit2.mopac.MopacShellOB records 111 Correlation 0.997760 elapsed
		// time 535000
		//
	}

	@Test
	public void testBenchmarkAmesFailures() throws Exception {
		long now = System.currentTimeMillis();
		shell.setMopac_commands("AM1 NOINTER NOMM BONDS MULLIK PRECISE GNORM=0.0");
		InputStream in = AbstractMopacShell.class
				.getClassLoader()
				.getResourceAsStream("ambit2/mopac/BenchmarkAmes3DFailures.csv");
		// DelimitedFileWriter writer = new DelimitedFileWriter(new
		// FileOutputStream(file));
		IteratingDelimitedFileReader reader = new IteratingDelimitedFileReader(
				in);

		int record = 0;
		try {
			while (reader.hasNext()) {
				Object o = reader.next();
				Assert.assertTrue(o instanceof IAtomContainer);

				IAtomContainer a = (IAtomContainer) o;
				AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(a);
				CDKHueckelAromaticityDetector.detectAromaticity(a);
				IAtomContainer mol = null;
				try {
					mol = shell.runShell(a);
				} catch (Exception x) {
					logger.log(Level.SEVERE, a.getProperty("Compound") + " "
							+ x.getMessage());
					continue;
				}
				if (mol == null)
					continue;
				if (mol.getAtomCount() == 0)
					continue;

				record++;
				System.out.println(record);
			}
		} catch (Exception x) {
			x.printStackTrace();
		}
		in.close();
		// calculate Pearson correlation coefficient
		System.out.println(String.format("%s records %d elapsed time %d", shell
				.getClass().getName(), record,
				(System.currentTimeMillis() - now)));

	}

	@Test
	public void testChargedCompounds() throws Exception {
		IAtomContainer mol = new AtomContainer();
		IAtom aluminum = new Atom("Al");
		aluminum.setFormalCharge(+3);
		mol.addAtom(aluminum);

		StringWriter strWriter = new StringWriter();
		Mopac7Writer writer = new Mopac7Writer(strWriter);
		writer.write(mol);
		writer.close();
		Assert.assertTrue(strWriter.toString().contains("CHARGE=3"));
	}
}
