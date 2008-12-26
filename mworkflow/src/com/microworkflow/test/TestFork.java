/*
 * 	 
 *  Copyright (c) 2002, 2003 Dragos Manolescu (dam@micro-workflow.com)
 * 
 *  See the LICENSE file for licensing information.
 */

package com.microworkflow.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.microworkflow.process.*;
import com.microworkflow.execution.*;

public class TestFork extends TestActivity {

	public TestFork(String arg0) {
		super(arg0);
	}
	public static void main(String[] args) {
		junit.swingui.TestRunner.run(TestFork.class);
	}
	public static Test suite() {
		return new TestSuite(TestFork.class);
	}
	public void testFork() {
		Primitive p1 = new Primitive(O1_KEY, new Performer() {
			public Object execute() throws Exception {
				if (context.containsKey(targetKey)) {
					DomainObject domainObject = (DomainObject) getTarget();
					domainObject.setSlotA("AA");
				}
				return null;
			}
		});
		Primitive p2 = new Primitive(O1_KEY, new Performer() {
			public Object execute() throws Exception {
				if (context.containsKey(targetKey)) {
					DomainObject domainObject = (DomainObject) getTarget();
					domainObject.setSlotB("BB");
				}
				return null;
			}
		});
		Primitive p3 = new Primitive(O1_KEY, new Performer() {
			public Object execute() throws Exception {
				if (context.containsKey(targetKey)) {
					DomainObject domainObject = (DomainObject) getTarget();
					domainObject.setSlotC("CC");
				}
				return null;
			}
		});
		Fork fork = new Fork();
		fork.addBranch(p1);
		fork.addBranch(p2);
		fork.addBranch(p3);
		wc = executeWorkflow(fork);
		DomainObject domainObject = (DomainObject) wc.get(O1_KEY);
		assertEquals("Slot A", "AA", domainObject.getSlotA());
		assertEquals("Slot B", "BB", domainObject.getSlotB());
		assertEquals("Slot C", "CC", domainObject.getSlotC());
	}

}
