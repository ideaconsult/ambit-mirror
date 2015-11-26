package ambit2.smarts.test;

import java.util.BitSet;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.LoggingTool;

import ambit2.smarts.SmartsFingerprinter;
import ambit2.smarts.SmartsHelper;
import ambit2.smarts.SmartsParser;

public class TestSmartsFingerprinter extends TestCase {
	public LoggingTool logger;
	public SmartsFingerprinter sfp = new SmartsFingerprinter(
			SilentChemObjectBuilder.getInstance());
	public Fingerprinter fp = new Fingerprinter();
	public SmartsParser sp = new SmartsParser();

	String smiles[] = { "c1ccccc1", "CNNNN", "CCCCCC(CCCCC)CCCC1CCNCC1CC" };

	public TestSmartsFingerprinter() {
		logger = new LoggingTool(this);
	}

	public static Test suite() {
		return new TestSuite(TestSmartsFingerprinter.class);
	}

	/**
	 * Each SMILES is treated as SMILES first and then as SMARTS query The
	 * obtained Bits sets are expected to be equal
	 */
	public void testSMILESSet() {
		for (int i = 0; i < smiles.length; i++) {
			try {
				IAtomContainer mol = SmartsHelper
						.getMoleculeFromSmiles(smiles[i]);
				BitSet bs1 = fp.getBitFingerprint(mol).asBitSet();

				IQueryAtomContainer query = sp.parse(smiles[i]);
				BitSet bs2 = sfp.getFingerprint(query);

				assertEquals("Different BitsSet for " + smiles[i],
						bs1.toString(), bs2.toString());
			} catch (Exception e) {
				System.out.println(e.toString());
			}
		}
	}
}
