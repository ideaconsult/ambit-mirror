/**
 * Created on 2005-3-19
 *
 */
package ambit.test.database;

import junit.framework.TestCase;
import ambit.data.model.Model;
import ambit.data.model.ModelFactory;
import ambit.database.DbConnection;
import ambit.database.core.DbDescriptors;
import ambit.database.core.DbModel;
import ambit.database.core.DbReference;
import ambit.exceptions.AmbitException;


/**
 * JUnit test for {@link ambit.database.core.DbModel}
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
