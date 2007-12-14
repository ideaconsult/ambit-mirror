/**
 * <b>Filename</b> DbSubstanceIteratorTest.java 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Created</b> 2005-6-16
 * <b>Project</b> ambit
 */
package ambit.test.database;

import java.sql.SQLException;

import junit.framework.TestCase;
import ambit.benchmark.Benchmark;
import ambit.database.DbConnection;
import ambit.database.old.DbFPAtomEnvironments;
import ambit.database.old.DbSubstanceIterator;
import ambit.exceptions.AmbitException;

/**
 * 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-6-16
 */
public class DbSubstanceIteratorTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(DbSubstanceIteratorTest.class);
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
     * Constructor for DbSubstanceIteratorTest.
     * @param arg0
     */
    public DbSubstanceIteratorTest(String arg0) {
        super(arg0);
    }

    public void testRun() {
        try {
	        DbConnection conn = new DbConnection("localhost","3306","ambit","lri","cefic");
	        conn.open();
	        DbSubstanceIterator sbi = new DbSubstanceIterator(conn);
	        sbi.setSqlSubstances("select idsubstance from substance limit 10;");
	        sbi.setLimit(3);
	        sbi.setMaxRows(10);
	        
			Thread t = new Thread(sbi);
			Benchmark b = new Benchmark();
			b.mark();
			t.start();
			try {
					t.join();
					sbi.close();
			} catch (InterruptedException e) {
					e.printStackTrace();				
			}
			conn.close();
			assertEquals(sbi.getRowsProcessed(),sbi.getMaxRows()); 
			b.record();
			b.report();
        } catch (AmbitException x) {
            x.printStackTrace();
            fail();
        } catch (SQLException x) {
            x.printStackTrace();
            fail();
        }
    }

    public void testRunFPAE() {
        try {
	        DbConnection conn = new DbConnection("localhost","3306","ambit","lri","cefic");
	        conn.open();
	        DbFPAtomEnvironments fpae = new DbFPAtomEnvironments(conn);
	        fpae.setSqlSubstances("select substance.idsubstance,status from substance left join fpaeid using(idsubstance) where status is null limit 100;");
	        fpae.setAction(fpae);
	        fpae.setLimit(10);
	        fpae.setMaxRows(50);
	        
			Thread t = new Thread(fpae);
			Benchmark b = new Benchmark();
			b.mark();
			t.start();
			try {
					t.join();
					fpae.close();
			} catch (InterruptedException e) {
					e.printStackTrace();				
			}
			conn.close();
			assertEquals(fpae.getRowsProcessed(),1000); 
			b.record();
			b.report();
	        
        } catch (AmbitException x) {
            x.printStackTrace();
            fail();
	        
        } catch (SQLException x) {
            x.printStackTrace();
            fail();
        }
    }    
}

