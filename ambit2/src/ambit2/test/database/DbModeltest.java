/**
 * Created on 2005-3-19
 *
 */
package ambit2.test.database;

import junit.framework.TestCase;
import ambit2.database.DbConnection;
import ambit2.data.model.Model;
import ambit2.data.model.ModelFactory;
import ambit2.database.core.DbDescriptors;
import ambit2.database.core.DbModel;
import ambit2.database.core.DbReference;
import ambit2.exceptions.AmbitException;


/**
 * JUnit test for {@link ambit2.database.core.DbModel}
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class DbModeltest extends TestCase {
	private DbConnection dbconn = null;
	private DbDescriptors dbd = null;
	private DbReference dbr = null;
	private DbModel dbm = null;	
	
	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		dbconn = new DbConnection("localhost","33060","ambittest","root","");
		dbconn.open();
		dbd = new DbDescriptors(dbconn);
		dbd.initialize();
		dbr = new DbReference(dbconn);
		dbr.initialize();
		dbm = new DbModel(dbconn);
		dbm.initialize();
		
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		dbm.close(); dbm = null;
		dbd.close(); dbd = null;
		dbr.close(); dbr = null;
		dbconn.close();
		dbconn = null;
		
		
	}

	public void testAddModel() {
		try {
			dbm.initializeInsertModel();
			
			Model model = ModelFactory.createDebnathMutagenicityQSAR();
			int id = dbm.addModel(model);
			assertTrue(id>-1);

			
		} catch (AmbitException x) {
			x.printStackTrace();
			fail();
		}
	}


	public void testAddQSARPoint() {
		//TODO Implement addQSARPoint().
	}

	public void testDeleteQSARpoint() {
		//TODO Implement deleteQSARpoint().
	}

	public void testDeleteQSARpoints() {
		//TODO Implement deleteQSARpoints().
	}

}
