/*
 * Created on 2005-9-25
 *
 */
package ambit.test.internet;

import junit.framework.TestCase;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.internet.WWMMatrixReader;

/**
 * This is a test
 * @author Nina Jeliazkova
 * <b>Modified</b> 2005-9-25
 */
public class WWWMatrixReaderTest extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(WWWMatrixReaderTest.class);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Constructor for WWWMatrixReaderTest.
	 * @param arg0
	 */
	public WWWMatrixReaderTest(String arg0) {
		super(arg0);
	}

	public void testSimple() {
		WWMMatrixReader r = new WWMMatrixReader("wwmm.ch.cam.ac.uk");
		
		r.setQuery("ichi","C4,");
		try {
			Molecule m = (Molecule)r.read(new Molecule());
			System.out.println(m);
		} catch (Exception x) {
			x.printStackTrace();
		}
	}
}
