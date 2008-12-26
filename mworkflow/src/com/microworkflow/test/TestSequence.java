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

public class TestSequence extends TestActivity {

	public TestSequence(String arg0) {
		super(arg0);
	}
	public static void main(String[] args) {
		junit.swingui.TestRunner.run(TestSequence.class);
	}
	public static Test suite() {
		return new TestSuite(TestSequence.class);
	}
	public void testSequence() {
		Primitive step1 = new Primitive(O1_KEY, "result1", new Performer() {
			public Object execute() throws Exception {
				if (context.containsKey(targetKey)) {
					DomainObject domainObject = (DomainObject) getTarget();
					return domainObject.concat((String) get("argument1"));
				}
				return null;
			}
		});
		Primitive step2 = new Primitive("result1", "result2", new Performer() {
			public Object execute() throws Exception {
				if (context.containsKey(targetKey)) {
					String str = (String) getTarget();
					return str.replaceAll("stringfirst","string; first");
				}
				return null;
			}
		});
		Primitive step3 = new Primitive("result2", "result3", new Performer() {
			public Object execute() throws Exception {
				if (context.containsKey(targetKey)) {
					String str = (String) getTarget();
					return new Integer(str.length());
				}
				return null;
			}
		});
		wc = executeWorkflow(step1.addStep(step2).addStep(step3));
		assertEquals("sequence failure",
			"this is a string; first argument".length(),
			((Integer) wc.get("result3")).intValue());
	}

}
