package ambit2.test.database;

import java.sql.SQLException;

import junit.framework.TestCase;
import ambit2.database.DbConnection;
import ambit2.exceptions.AmbitException;

public class DbConnectionTest extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(DbConnectionTest.class);
	}

	public DbConnectionTest(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void test() {
		DbConnection conn = new DbConnection("localhost","3306","ambit","guest","guest");
		try {
			conn.open();
			assertTrue(!conn.getConn().isClosed());
			conn.close();
			assertTrue(conn.getConn().isClosed());
			conn.open();
			assertTrue(!conn.getConn().isClosed());
			conn.close();
		} catch (AmbitException x) {
			x.printStackTrace();
			fail();
		} catch (SQLException ex) {
			ex.printStackTrace();
			fail();
		}
		
	}
}
