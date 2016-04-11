package ambit2.smarts.processors;

import java.io.File;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.smarts.SMIRKSManager;

public class SmirksProcessorTest {
	static Logger logger = Logger.getLogger(SMIRKSProcessor.class.getName());
	@Test
	public void testOptions() throws Exception {
		SMIRKSProcessor p = new SMIRKSProcessor(logger);
		Assert.assertFalse(p.isAtomtypeasproperties());
		Assert.assertTrue(p.isSparseproperties());
		Assert.assertFalse(p.isTransformationasproperties());
		URL config = this.getClass().getClassLoader()
				.getResource("ambit2/smirks/smirks.json");
		p.loadReactionsFromJSON(new File(config.getFile()));
		Assert.assertFalse(p.isAtomtypeasproperties());
		
		Assert.assertFalse(p.isTransformationasproperties());
		// only USE=true reactions are loaded
		Assert.assertEquals(39, p.getTransformations().size());
	}

	@Test
	public void test() throws Exception {
		URL config = this.getClass().getClassLoader()
				.getResource("ambit2/smirks/smirks.json");
		SMIRKSProcessor p = new SMIRKSProcessor(logger);
		p.setLoadExamples(true);
		p.loadReactionsFromJSON(new File(config.getFile()));
		p.setTransformationasproperties(true);
		Assert.assertEquals(39, p.getTransformations().size());
		Assert.assertFalse(p.isAtomtypeasproperties());
		p.setAtomtypeasproperties(true);
		SMIRKSManager m = new SMIRKSManager(
				SilentChemObjectBuilder.getInstance());
		SmilesParser parser = new SmilesParser(
				SilentChemObjectBuilder.getInstance());
		for (SMIRKSTransformation t : p.getTransformations()) {
			if (t.getExample() != null) {
				IAtomContainer mol = parser.parseSmiles(t.getExample());
				Assert.assertNotNull(mol);
				AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
				mol = p.process(mol);
				logger.log(Level.INFO, String.format("%s\t%s", t.getName(),mol.getProperties()));
				Assert.assertTrue((int)mol.getProperty("Transformed")>0);
				Assert.assertTrue((int)mol.getProperty("Transformed")>0);
				Assert.assertEquals(1, (int)mol.getProperty("T."+t.getName()));
				Assert.assertEquals("[]",mol.getProperty("AtomTypes.added").toString());
			}
		}
	}
}
