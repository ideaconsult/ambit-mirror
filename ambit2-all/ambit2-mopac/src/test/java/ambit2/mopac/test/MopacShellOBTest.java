package ambit2.mopac.test;

import java.io.File;

import org.junit.Before;

import ambit2.base.external.ShellException;
import ambit2.core.smiles.OpenBabelGen3D;
import ambit2.core.smiles.SmilesParserWrapper;
import ambit2.core.smiles.SmilesParserWrapper.SMILES_PARSER;
import ambit2.mopac.MopacShellOB;

public class MopacShellOBTest extends MopacShellTest {

	@Before
	public void setUp() throws Exception {
		shell = new MopacShellOB(new OpenBabelGen3D() {
			@Override
			protected String getOBabelHome() throws ShellException {
				return System.getenv(OBABEL_HOME);
			}
		});
		File homeDir = new File(System.getProperty("java.io.tmpdir") +"/.ambit2/" + System.getProperty("user.name") + "/mopac/*");
		homeDir.delete();
		
		parser =  SmilesParserWrapper.getInstance(SMILES_PARSER.CDK);		
	}
}
