/*
 * Created on 2005-12-18
 *
 */
package ambit2.test.dbadmin;

import junit.framework.TestCase;
import ambit2.database.data.AmbitDatabaseToolsData;
import ambit2.database.data.DefaultSharedDbData;
import ambit2.exceptions.AmbitException;

/**
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2005-12-18
 */
public class DbAdminDataTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(DbAdminDataTest.class);
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
     * Constructor for DbAdminDataTest.
     * @param arg0
     */
    public DbAdminDataTest(String arg0) {
        super(arg0);
    }
    public void test() {
        DefaultSharedDbData d = new AmbitDatabaseToolsData(true);
        try {
            d.open("localhost","3306","ambittest","lri_admin","lri");
            d.close();
            DefaultSharedDbData d1 = new AmbitDatabaseToolsData(true);
            assertEquals("lri_admin",d1.getUser().getName());
        } catch (AmbitException x) {
            x.printStackTrace();
            fail();
        }
    }
}
