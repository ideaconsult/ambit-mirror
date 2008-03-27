package ambit2.test.processors;

import junit.framework.TestCase;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.templates.MoleculeFactory;

import ambit2.ui.editors.IdentifiersProcessor;
import ambit2.exceptions.AmbitException;

public class IdentifiersProcessorTest extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(IdentifiersProcessorTest.class);
	}

	public IdentifiersProcessorTest(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	public void test() {
		IdentifiersProcessor p = new IdentifiersProcessor();
		IMolecule m = MoleculeFactory.makeBenzene();
		m.setProperty("CAS","000071-43-2");
		m.setProperty("Name", "Benzene");
		try {
			p.process(m);
			assertEquals("71-43-2", m.getProperty(CDKConstants.CASRN));
			assertEquals("Benzene", m.getProperty(CDKConstants.NAMES));
		} catch (AmbitException x) {
			x.printStackTrace();
			fail();
		}
		
	}

}
