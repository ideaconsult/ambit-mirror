/*
 * 	 
 *  Copyright (c) 2002, 2003 Dragos Manolescu (dam@micro-workflow.com)
 * 
 *  See the LICENSE file for licensing information.
 */

package com.microworkflow.test;

import junit.framework.Test;
import junit.framework.TestSuite;
import com.microworkflow.execution.Performer;
import com.microworkflow.process.*;

public class TestOrJoin extends TestActivity {

	public TestOrJoin(String arg0) {
		super(arg0);
	}
	public static void main(String[] args) {
		junit.swingui.TestRunner.run(TestOrJoin.class);
	}
	public static Test suite() {
		return new TestSuite(TestOrJoin.class);
	}
	public void testOrJoin() {
		Primitive p1 = new Primitive(O1_KEY, new Performer() {
			public Object execute() throws Exception {
				if (context.containsKey(targetKey)) {
					DomainObject domainObject = (DomainObject) getTarget();
					domainObject.setSlotA("A");
				}
				return null;
			}
		});
		Primitive p2 = new Primitive(O1_KEY, new Performer() {
			public Object execute() throws Exception {
				if (context.containsKey(targetKey)) {
					DomainObject domainObject = (DomainObject) getTarget();
					domainObject.setSlotB("B");
				}
				return null;
			}
		});
		Primitive p3 = new Primitive(O1_KEY, new Performer() {
			public Object execute() throws Exception {
				if (context.containsKey(targetKey)) {
					DomainObject domainObject = (DomainObject) getTarget();
					domainObject.setSlotC("C");
				}
				return null;
			}
		});
		OrJoin join = new OrJoin();
		join.setBody(new Primitive(O1_KEY, new Performer() {
			public Object execute() throws Exception {
				if (context.containsKey(targetKey)) {
					DomainObject domainObject = (DomainObject) getTarget();
					domainObject.setCounter(domainObject.getCounter()+1);	
				}
				return null;
			}
		}));
		Fork fork = new Fork();
		fork.addBranch(p1);
		fork.addBranch(p2);
		fork.addBranch(p3);
		fork.setJoin(join);
		wc = executeWorkflow(fork);
		DomainObject domainObject = (DomainObject) wc.get(O1_KEY);
		assertEquals("Slot A", "A", domainObject.getSlotA());
		assertEquals("Slot B", "B", domainObject.getSlotB());
		assertEquals("Slot C", "C", domainObject.getSlotC());
		assertEquals("counter", 1, domainObject.getCounter());
	}

}
