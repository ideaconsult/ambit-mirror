package org.ideaconsult.iuclid5client.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.junit.runner.JUnitCore;


public final class AllEnginesTest
{

    public static Test suite()
        throws Exception
    {
        TestSuite suite = new TestSuite("Test iuclid5 webservices");
        suite.addTestSuite(org.ideaconsult.iuclid5client.test.SessionEngineTest.class);
        suite.addTestSuite(org.ideaconsult.iuclid5client.test.QueryEngineTest.class);
        suite.addTestSuite(org.ideaconsult.iuclid5client.test.DocumentAccessEngineTest.class);
        
        return suite;
    }

    public static void main(String args[])
    {
        JUnitCore.main(new String[] { AllEnginesTest.class.getName() });
    }

}