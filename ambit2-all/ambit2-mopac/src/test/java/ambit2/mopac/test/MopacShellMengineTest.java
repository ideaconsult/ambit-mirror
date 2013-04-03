package ambit2.mopac.test;

import java.io.File;

import org.junit.Before;

import ambit2.core.smiles.SmilesParserWrapper;
import ambit2.core.smiles.SmilesParserWrapper.SMILES_PARSER;
import ambit2.mopac.MopacShell;

public class MopacShellMengineTest extends MopacShellTest {

	@Before
	public void setUp() throws Exception {
		shell = new MopacShell();
		File homeDir = new File(System.getProperty("java.io.tmpdir") +"/.ambit2/" + System.getProperty("user.name") + "/mopac/*");
		homeDir.delete();
		
		parser =  SmilesParserWrapper.getInstance(SMILES_PARSER.CDK);		
	}

}
