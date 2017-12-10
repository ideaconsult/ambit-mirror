package ambit2.dbcli.test;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.iterator.IteratingSDFReader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.base.data.Property;
import ambit2.core.io.InteractiveIteratingMDLReader;
import ambit2.dbcli.AmbitCli;
import ambit2.dbcli.CliOptions;
import ambit2.tautomers.processor.StructureStandardizer;
import junit.framework.Assert;

public class StandardisationTest {

	@Test
	public void test_xv5_nitro() throws Exception {
		URL in = getClass().getClassLoader().getResource("ambit2/dbcli/test/xv5.txt");
		Assert.assertNotNull(in);
		String out = String.format("%s/xv5_std.txt", System.getProperty("java.io.tmpdir"));
		System.out.println(out);
		String[] args = new String[] { "-a", "standardize", "-i", in.getFile(), "-m", "post", "-o", out, "-d",
				"smiles=true", "-d", "inchi=false", "-d", "tautomers=true", "-d", "neutralise=true", "-d",
				"tag_tokeep=ID" };
		CliOptions options = new CliOptions();
		if (options.parse(args))
			try {
				AmbitCli cli = new AmbitCli(options);
				cli.go(options.getCmd(), options.getSubcommand().name());
			} finally {
				// (new File(out)).delete();
			}
	}

	@Test
	public void test_xv5_sh() throws Exception {
		try (InputStream in = getClass().getClassLoader().getResourceAsStream("ambit2/dbcli/test/xv5.sdf")) {
			//IteratingSDFReader reader = new IteratingSDFReader(in, SilentChemObjectBuilder.getInstance());
			InteractiveIteratingMDLReader reader = new InteractiveIteratingMDLReader(in, SilentChemObjectBuilder.getInstance());
			StructureStandardizer z = new StructureStandardizer();
			z.setImplicitHydrogens(true);
			z.setNeutralise(false);
			z.setGenerateTautomers(false);
			z.setGenerateSMILES(true);
			z.setGenerateSMILES_Canonical(false);
			z.setGenerateSMILES_Aromatic(false);
			z.setSplitFragments(false);
			z.setClearIsotopes(true);
			z.setGenerateInChI(true);
			
			CDKHydrogenAdder h = CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance());
			while (reader.hasNext()) {
				IAtomContainer mol = reader.next();
				AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
				
				h.addImplicitHydrogens(mol);


				System.out.println(SmilesGenerator.generic().create(mol));
				mol = z.process(mol);
				
				//mol = AtomContainerManipulator.suppressHydrogens(mol);
				//System.out.println(SmilesGenerator.generic().create(mol));
				System.out.println(mol.getProperties().get(Property.getSMILESInstance()));
			}
		}
	}

	@Test
	public void test_xv5_step1() throws Exception {
		URL in = getClass().getClassLoader().getResource("ambit2/dbcli/test/xv5.sdf");
		Assert.assertNotNull(in);
		String out = String.format("%s/xv5_std.txt", System.getProperty("java.io.tmpdir"));
		System.out.println(out);
		String[] args = new String[] { "-a", "standardize", "-i", in.getFile(), "-m", "post", "-o", out, "-d",
				"smiles=true", "-d", "inchi=true", "-d", "tautomers=false", "-d", "neutralise=false", "-d",
				"implicith=true", "-d", "smilescanonical=false", "-d", "tag_tokeep=PUBCHEM_COMPOUND_CID" };
		CliOptions options = new CliOptions();
		if (options.parse(args))
			try {
				AmbitCli cli = new AmbitCli(options);
				cli.go(options.getCmd(), options.getSubcommand().name());
			} finally {
				// (new File(out)).delete();
			}
	}

	@Test
	public void test9815161() throws Exception {
		URL in = getClass().getClassLoader().getResource("ambit2/dbcli/test/pubchem9815161.sdf");
		Assert.assertNotNull(in);
		String out = String.format("%s/output_sdf.txt", System.getProperty("java.io.tmpdir"));
		System.out.println(out);
		String[] args = new String[] { "-a", "standardize", "-i", in.getFile(), "-m", "post", "-o", out, "-d",
				"smiles=true", "-d", "inchi=false", "-d", "tautomers=true", "-d", "tag_tokeep=cdk:Title" };
		CliOptions options = new CliOptions();
		if (options.parse(args))
			try {
				AmbitCli cli = new AmbitCli(options);
				cli.go(options.getCmd(), options.getSubcommand().name());
			} finally {
				(new File(out)).delete();
			}
	}

	@Test
	public void test9815161_smi() throws Exception {
		URL in = getClass().getClassLoader().getResource("ambit2/dbcli/test/pubchem9815161.txt");
		Assert.assertNotNull(in);
		String out = String.format("%s/output_smi.txt", System.getProperty("java.io.tmpdir"));
		System.out.println(out);
		String[] args = new String[] { "-a", "standardize", "-i", in.getFile(), "-m", "post", "-o", out, "-d",
				"splitfragments=true", "-d", "smiles=true", "-d", "smilescanonical=false",
				// "-d","implicith=true",
				// "-d", "inchi=true",
				"-d", "tautomers=true", "-d", "tag_tokeep=cdk:Title",
				// "-d","neutralise=true"
		};
		CliOptions options = new CliOptions();
		if (options.parse(args))
			try {
				AmbitCli cli = new AmbitCli(options);
				cli.go(options.getCmd(), options.getSubcommand().name());
			} finally {
				// (new File(out)).delete();
			}
	}

	@Test
	public void test() throws Exception {
		URL in = getClass().getClassLoader().getResource("ambit2/db/processors/txt/test.txt");
		Assert.assertNotNull(in);
		String out = String.format("%s/output.txt", System.getProperty("java.io.tmpdir"));
		String[] args = new String[] { "-a", "standardize", "-i", in.getFile(), "-m", "post", "-o", out, "-d",
				"smiles=true", "-d", "inchi=false", "-d", "tautomers=true" };
		CliOptions options = new CliOptions();
		if (options.parse(args))
			try {
				AmbitCli cli = new AmbitCli(options);
				cli.go(options.getCmd(), options.getSubcommand().name());
			} finally {
				(new File(out)).delete();
			}
	}

	@Test
	public void fptest() throws Exception {
		URL in = getClass().getClassLoader().getResource("ambit2/dbcli/test/testfp.txt");
		Assert.assertNotNull(in);
		String out = String.format("%s/output", System.getProperty("java.io.tmpdir"));
		String[] args = new String[] { "-a", "fingerprint", "-i", in.getFile(), "-m", "post", "-o", out, "-d",
				"fpclass=CircularFingerprinter", "-d", "inputtag_smiles=AMBIT_SMILES" };
		CliOptions options = new CliOptions();
		if (options.parse(args))
			try {
				AmbitCli cli = new AmbitCli(options);
				cli.go(options.getCmd(), options.getSubcommand().name());
			} finally {
				// (new File(out)).delete();
			}
	}

	@Test
	public void simtest() throws Exception {
		URL in = getClass().getClassLoader().getResource("ambit2/dbcli/test/mccp10.txt");
		Assert.assertNotNull(in);
		String out = String.format("%s/output", System.getProperty("java.io.tmpdir"));
		String[] args = new String[] { "-a", "fingerprint", "-i", in.getFile(), "-m", "post", "-o", out, "-d",
				"fpclass=CircularFingerprinter", "-d", "inputtag_smiles=SMILES", "-d", "tag_tokeep=AMBIT_InChIKey" };
		CliOptions options = new CliOptions();
		if (options.parse(args))
			try {
				AmbitCli cli = new AmbitCli(options);
				cli.go(options.getCmd(), options.getSubcommand().name());
				simmatrix(new File(out + "org.openscience.cdk.fingerprint.CircularFingerprinter.hashed.csv"));
			} finally {
				// (new File(out)).delete();
			}
	}

	public void simmatrix(File input) throws Exception {

		String out = String.format("%s/simmatrix", System.getProperty("java.io.tmpdir"));
		String[] args = new String[] { "-a", "simmatrix", "-i", input.getAbsolutePath(), "-m", "post", "-o", out, "-d",
				"threshold=0.1", "-d", "sparse=TRUE" };
		CliOptions options = new CliOptions();
		if (options.parse(args))
			try {
				AmbitCli cli = new AmbitCli(options);
				cli.go(options.getCmd(), options.getSubcommand().name());

			} finally {
				// (new File(out)).delete();
			}
	}
}
