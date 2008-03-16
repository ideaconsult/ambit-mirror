/*
 * 	 
 *  Copyright (c) 2002, 2003 Dragos Manolescu (dam@micro-workflow.com)
 * 
 *  See the LICENSE file for licensing information.
 */

package com.microworkflow.test;

import junit.framework.TestSuite;

public class TestProcess extends TestSuite {

	public static TestSuite suite() {
		TestSuite suite = new TestSuite();
		
		suite.addTest(TestPrimitive.suite());
		suite.addTest(TestSequence.suite());
		suite.addTest(TestConditional.suite());
		suite.addTest(TestAsyncPrimitive.suite());	
		suite.addTest(TestFork.suite());	
		suite.addTest(TestOrJoin.suite());	
		suite.addTest(TestAndJoin.suite());	
		suite.addTest(TestIterative.suite());	
		suite.addTest(TestDefinitionCycle.suite());
		return suite;
	}
	public static void main(String args[]) {
		junit.swingui.TestRunner.run(TestSuite.class);
	}
}
