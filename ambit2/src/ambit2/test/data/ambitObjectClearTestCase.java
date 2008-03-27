/**
 * Created on 2005-3-29
 *
 */
package ambit2.test.data;

import junit.framework.TestCase;
import ambit2.data.AmbitObject;
import ambit2.data.descriptors.Descriptor;
import ambit2.data.experiment.DefaultTemplate;
import ambit2.data.experiment.Study;
import ambit2.data.literature.LiteratureEntry;
import ambit2.data.literature.ReferenceFactory;
import ambit2.data.model.Model;
import ambit2.data.molecule.Compound;


/**
 * JUnit test for {@link ambit2.data.AmbitObject#clear()} 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class ambitObjectClearTestCase extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(ambitObjectClearTestCase.class);
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

	protected void ambitObjectClear(AmbitObject ao) {
		try {
			ao.clear();
			assertTrue(true);
		} catch (Exception x) {
			assertTrue(false);
		}
	}
	public void testClear() {
		ambitObjectClear(new AmbitObject());
	}
	public void testReferenceClear() {
		ambitObjectClear(ReferenceFactory.createEmptyReference());
	}
	public void testDecsriptorsClear() {
		ambitObjectClear(new Descriptor("test", new LiteratureEntry()));
	}
	public void testModelClear() {
		ambitObjectClear(new Model());
	}
	public void testCompoundClear() {
		ambitObjectClear(new Compound());
	}
	public void testStudyClear() {
		ambitObjectClear(new Study("s",new DefaultTemplate("s")));
	}		
}
