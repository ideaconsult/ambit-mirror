/*
 * 	 
 *  Copyright (c) 2002, 2003 Dragos Manolescu (dam@micro-workflow.com)
 * 
 *  See the LICENSE file for licensing information.
 */

package com.microworkflow.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.microworkflow.execution.*;
import com.microworkflow.process.*;

public class TestPrimitive extends TestActivity {

	public TestPrimitive(String arg0) {
		super(arg0);
	}
	public static void main(String[] args) {
		junit.swingui.TestRunner.run(TestPrimitive.class);
	}
	public static Test suite() {
		return new TestSuite(TestPrimitive.class);
	}
	public void testPrimitive() {
		Primitive p = new Primitive(O1_KEY,"result1", new Performer() {
			public Object execute() throws Exception {
				if (context.containsKey(targetKey)) {
					DomainObject domainObject = (DomainObject) getTarget();
					return domainObject.concat((String) get("argument1"));
				}
				return null;
			}
		});
		wc = executeWorkflow(p);
		String expectedResult = "this is a string".concat("first argument");
		assertEquals(
			"Strings not equal",
			true,
			expectedResult.equalsIgnoreCase((String) wc.get("result1")));
	}
}
