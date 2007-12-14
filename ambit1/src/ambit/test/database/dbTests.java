/**
 * Created on 2005-3-18
 *
 */
package ambit.test.database;

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * JUnit test suite for database functionality 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class dbTests {

	/**
	 * 
	 */
	public dbTests() {
		super();
	}

	public static void main(String[] args) {
			junit.textui.TestRunner.run(dbTests.suite());
	}
	
	public static Test suite() {
		TestSuite suite = new TestSuite("Ambit database tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(DbDescriptorsTest.class);
		suite.addTestSuite(DbDescriptorValuesTest.class);
		suite.addTestSuite(DbExperimentstest.class);
		suite.addTestSuite(DbModeltest.class);
				
		//$JUnit-END$
		return suite;
	}	
}
