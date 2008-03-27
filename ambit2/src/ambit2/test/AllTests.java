/**
 * Created on 2004-11-4
 *
 */
package ambit2.test;


import junit.framework.Test;
import junit.framework.TestSuite;
import ambit2.test.data.ambitListTestCase;
import ambit2.test.data.ambitObjectClearTestCase;
import ambit2.test.data.ambitObjectCloneTestCase;
import ambit2.test.data.domain.DataModuleTest;
import ambit2.test.data.domain.ambitQSARDatasetTestCase;
import ambit2.test.data.molecule.CompoundTestCase;




/**
 * JUnit tests 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class AllTests {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(AllTests.suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for ambit2.test");
		//$JUnit-BEGIN$
		suite.addTestSuite(ambitObjectCloneTestCase.class);
		suite.addTestSuite(ambitObjectClearTestCase.class);		
		suite.addTestSuite(ambitListTestCase.class);		
		suite.addTestSuite(CompoundTestCase.class);		
		suite.addTestSuite(ambitQSARDatasetTestCase.class);
		suite.addTestSuite(DataModuleTest.class);
				
		
		//$JUnit-END$
		return suite;
	}
}
