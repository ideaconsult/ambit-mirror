package ambit.test.database;

import java.sql.SQLException;

import junit.framework.TestCase;
import ambit.data.AmbitUser;
import ambit.database.DbAdmin;
import ambit.exceptions.AmbitException;

public class DbAdminTest extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(DbAdminTest.class);
	}

	public DbAdminTest(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/*
	 * Test method for 'ambit.database.DbAdmin.createUsers(AmbitUser)'
	 */
	public void doCreateUsers() {
		DbAdmin dba = new DbAdmin("localhost","33060","ambittest","root","");
		try {
			if (dba.open(true)) {
				AmbitUser user = new AmbitUser("guest");
				assertEquals(user.getUserType(),AmbitUser.USERTYPE_GUEST);
				dba.createUsers("ambittest",user);
				dba.close();
			}
		} catch (SQLException x) {
			x.printStackTrace();
			fail();			
		} catch (AmbitException x) {
			x.printStackTrace();
			fail();
		}
	}

	/*
	 * Test method for 'ambit.database.DbAdmin.createDatabase(String)'
	 */
	public void testCreateDatabase() {
		DbAdmin dba = new DbAdmin("localhost","33060","mysql","root","");
		try {
			if (dba.open(false)) {
				try {
					dba.dropDatabase("ambittest");
				} catch (AmbitException x) {
					x.printStackTrace();
				}
				dba.createDatabase("ambittest");
				dba.close();
				doCreateTables();
				doCreateUsers();
				
			}
			
		} catch (SQLException x) {
			x.printStackTrace();
			fail();
		} catch (AmbitException x) {
			x.printStackTrace();
			fail();
		}
	}

	/*
	 * Test method for 'ambit.database.DbAdmin.createTables()'
	 */
	public void doCreateTables() {
		DbAdmin dba = new DbAdmin("localhost","33060","ambittest","root","");
		try {
			if (dba.open(false)) {
				dba.createTables("ambittest");
				dba.close();
			} else fail();
		} catch (SQLException x) {
			x.printStackTrace();
			fail();
		} catch (AmbitException x) {
			x.printStackTrace();
			fail();
		}		
	}

}
