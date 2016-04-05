package ambit2.dbcli.test;

import java.io.File;
import java.net.URL;

import junit.framework.Assert;

import org.junit.Test;

import ambit2.dbcli.AmbitCli;
import ambit2.dbcli.CliOptions;

public class StandardisationTest {

	@Test
	public void test() throws Exception {
		URL in = getClass().getClassLoader().getResource(
				"ambit2/db/processors/txt/test.txt");
		Assert.assertNotNull(in);
		String out = String.format("%s/output.txt",
				System.getProperty("java.io.tmpdir"));
		String[] args = new String[] { "-a", "standardize", "-i", in.getFile(),
				"-m", "post", "-o", out ,"-d","smiles=true","-d","inchi=false", "-d","tautomers=true"};
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
		URL in = getClass().getClassLoader().getResource(
				"ambit2/dbcli/test/testfp.txt");
		Assert.assertNotNull(in);
		String out = String.format("%s/output",
				System.getProperty("java.io.tmpdir"));
		String[] args = new String[] { "-a", "fingerprint", "-i", in.getFile(),
				"-m", "post", "-o", out ,"-d","fpclass=CircularFingerprinter","-d","inputtag_smiles=AMBIT_SMILES"};
		CliOptions options = new CliOptions();
		if (options.parse(args))
			try {
				AmbitCli cli = new AmbitCli(options);
				cli.go(options.getCmd(), options.getSubcommand().name());
			} finally {
				(new File(out)).delete();
			}
	}
}
