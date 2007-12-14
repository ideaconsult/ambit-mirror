/**
 * Created on 2005-4-5
 *
 */
package ambit.test.data.domain;

import junit.framework.TestCase;
import ambit.data.AmbitListChanged;
import ambit.data.IAmbitListListener;
import ambit.data.AmbitObjectChanged;
import ambit.data.IAmbitObjectListener;
import ambit.domain.DataModule;


/**
 * JUnit test for {@link ambit.domain.DataModule} 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class DataModuleTest extends TestCase {
	protected DataModule dm = null;
	public static void main(String[] args) {
		junit.textui.TestRunner.run(DataModuleTest.class);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		dm = new DataModule();
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		dm = null;
	}

	/**
	 * Constructor for DataModuleTest.
	 * @param arg0
	 */
	public DataModuleTest(String arg0) {
		super(arg0);
	}


	public void testCreateDemoModel() {
	
		IAmbitListListener al = new IAmbitListListener() {
			public void ambitListChanged(AmbitListChanged event) {
				System.err.print("List changed\t");
				System.err.println(event.getList().toString());

			}
			public void ambitObjectChanged(AmbitObjectChanged event) {
				System.err.print("Object in studyList changed\t");				
				System.err.println(event.getSource().toString());

			}
		};
		IAmbitObjectListener ao = new IAmbitObjectListener() {
			public void ambitObjectChanged(AmbitObjectChanged event) {
				System.err.print("Object changed\t");
				System.err.println(event.getObject().toString());
				//assertEquals(event.getObject().getName(),
						//"ambit/data/domain/demo/Debnath_smiles.csv");
			}
		};
		try {
		dm.createDemoModel(ao);
		assertTrue(!dm.modelData.isEmpty());
		} catch (Exception e) {
		    e.printStackTrace();
            fail();
        }
		dm.modelData.getModel().getDescriptors().addAmbitObjectListener(al);
		System.err.println("Loading test data set\n");
		try {
		dm.createDemoTestset();
		} catch (Exception e) {
		    e.printStackTrace();
            fail();
        }
	}

}
