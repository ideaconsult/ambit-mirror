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

public class TestAndJoin extends TestActivity {
	protected static final String O3_KEY = "object3";
	protected static final String O2_KEY = "object2";
	
	public TestAndJoin(String arg0) {
		super(arg0);
	}
	public static void main(String[] args) {
		junit.swingui.TestRunner.run(TestAndJoin.class);
	}
	public static Test suite() {
		return new TestSuite(TestAndJoin.class);
	}
	protected void setUp() throws Exception {
		super.setUp();
		context.put(O2_KEY, new DomainObject("branch2"));
		context.put(O3_KEY, new DomainObject("branch3"));
	}
	public void testAndJoin() {
		// branch 1
		Activity branch1 = new Primitive(O1_KEY, new Performer() {
			public Object execute() throws Exception {
				if (context.containsKey(targetKey)) {
					DomainObject domainObject = (DomainObject) getTarget();
					domainObject.setSlotA("a");
				}
				return null;
			}
		});
		// branch 2
		Primitive p21 = new Primitive(O2_KEY, new Performer() {
			public Object execute() throws Exception {
				if (context.containsKey(targetKey)) {
					DomainObject domainObject = (DomainObject) getTarget();
					domainObject.setSlotA("aa");
				}
				return null;
			}
		});
		Primitive p22 = new Primitive(O2_KEY, new Performer() {
			public Object execute() throws Exception {
				if (context.containsKey(targetKey)) {
					DomainObject domainObject = (DomainObject) getTarget();
					domainObject.setSlotB("bb");
				}
				return null;
			}
		});
		Sequence branch2=new Sequence();
		branch2.addStep(p21).addStep(p22);
		// branch 3
		Primitive p31 = new Primitive(O3_KEY, new Performer() {
			public Object execute() throws Exception {
				if (context.containsKey(targetKey)) {
					DomainObject domainObject = (DomainObject) getTarget();
					domainObject.setSlotA("aaa");
				}
				return null;
			}
		});
		Primitive p32 = new Primitive(O3_KEY, new Performer() {
			public Object execute() throws Exception {
				if (context.containsKey(targetKey)) {
					DomainObject domainObject = (DomainObject) getTarget();
					domainObject.setSlotB("bbb");
				}
				return null;
			}
		});
		Primitive p33 = new Primitive(O3_KEY, new Performer() {
			public Object execute() throws Exception {
				if (context.containsKey(targetKey)) {
					DomainObject domainObject = (DomainObject) getTarget();
					domainObject.setSlotC("ccc");
				}
				return null;
			}
		});
		Sequence branch3=new Sequence();
		branch3.addStep(p31).addStep(p32).addStep(p33);
		//
		AndJoin join = new AndJoin();
		join.setBody(new Primitive(O1_KEY, "result", new Performer() {
			public Object execute() throws Exception {
				if (context.containsKey(targetKey)) {
					DomainObject do1=(DomainObject) getTarget();
					DomainObject do2=(DomainObject) get(O2_KEY);
					DomainObject do3=(DomainObject) get(O3_KEY);
					return do1.getAllSlots() + do2.getAllSlots() + do3.getAllSlots();
				}
				return null;
			}
		}));
		Fork fork = new Fork();
		fork.addBranch(branch1);
		fork.addBranch(branch2);
		fork.addBranch(branch3);
		fork.setJoin(join);
		wc = executeWorkflow(fork);
		assertEquals(O1_KEY,true,wc.containsKey(O1_KEY));
		assertEquals(O1_KEY,"a",((DomainObject)wc.get(O1_KEY)).getSlotA());
		assertEquals(O2_KEY, true,wc.containsKey(O2_KEY));
		assertEquals(O2_KEY,"aa",((DomainObject)wc.get(O2_KEY)).getSlotA());
		assertEquals(O2_KEY,"bb",((DomainObject)wc.get(O2_KEY)).getSlotB());
		assertEquals(O3_KEY,true,wc.containsKey(O3_KEY));
		assertEquals(O3_KEY,"aaa",((DomainObject)wc.get(O3_KEY)).getSlotA());
		assertEquals(O3_KEY,"bbb",((DomainObject)wc.get(O3_KEY)).getSlotB());
		assertEquals(O3_KEY,"ccc",((DomainObject)wc.get(O3_KEY)).getSlotC());
		assertEquals("AndJoin failure","aBCaabbCaaabbbccc",(String) wc.get("result"));
	}
}
