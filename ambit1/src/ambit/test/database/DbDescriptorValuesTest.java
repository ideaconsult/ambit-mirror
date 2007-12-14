/**
 * Created on 2005-3-18
 *
 */
package ambit.test.database;

import junit.framework.TestCase;
import ambit.data.descriptors.DescriptorDefinition;
import ambit.data.descriptors.DescriptorFactory;
import ambit.data.literature.ReferenceFactory;
import ambit.database.DbConnection;
import ambit.database.core.DbDescriptorValues;
import ambit.database.core.DbDescriptors;
import ambit.database.exception.DbAmbitException;

/**
 * JUnit test for {@link ambit.database.core.DbDescriptorValues} 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class DbDescriptorValuesTest extends TestCase {
	private DbConnection dbconn = null;
	private DbDescriptorValues dbv = null; 
	private DbDescriptors dbd = null;
	
	public static void main(String[] args) {
		junit.textui.TestRunner.run(DbDescriptorValuesTest.class);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		super.setUp();
		dbconn = new DbConnection("localhost","3306","ambittest","lri_admin","lri");
		dbconn.open();
		dbv = new DbDescriptorValues(dbconn);
		dbv.initialize();
		dbd = new DbDescriptors(dbconn);
		dbd.initialize();
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		dbv.close(); dbv = null;
		dbconn.close();
		dbconn = null;		
	}

	/**
	 * Constructor for DbDescriptorValuesTest.
	 * @param arg0
	 */
	public DbDescriptorValuesTest(String arg0) {
		super(arg0);
	}

	public void testAddValue() {
		
		try  {
			DescriptorDefinition d = DescriptorFactory.createLogP(ReferenceFactory.createKOWWinReference());
			int id = dbd.getDescriptorIdByName(d);
			if (id > -1) {
				int idstructure = 1;
				double value = 99.9;
				double error =0.01;
				dbv.addValue(id,idstructure,value,error);
				double value1;
				value1 = dbv.getValue(id,idstructure);
				assertEquals(value,value1,0);
				
			} else fail();	
		} catch (DbAmbitException x) {
			x.printStackTrace();
			fail();
		}
	}
		 		

}
