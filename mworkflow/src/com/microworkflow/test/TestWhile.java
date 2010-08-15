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

public class TestWhile extends TestActivity {
	private static final String O2_KEY = "object2";
	protected While w;
	
	public TestWhile(String arg0) {
		super(arg0);
	}
	public static void main(String[] args) {
		junit.swingui.TestRunner.run(TestWhile.class);
	}
	public static Test suite() {
		return new TestSuite(TestWhile.class);
	}
	public void setUp() throws Exception {
		super.setUp();
		context.put(O2_KEY,new DomainObject("foo"));
		Primitive p1 = new Primitive(O1_KEY, new Performer() {
			public Object execute() throws Exception {
				DomainObject obj=(DomainObject)getTarget();
				obj.setCounter(obj.getCounter()+1);
				return null;
			}
		});
		Primitive p2 = new Primitive(O2_KEY, new Performer() {
			public Object execute() throws Exception {
				DomainObject obj=(DomainObject)getTarget();
				obj.setCounter(obj.getCounter()+2);
				return null;
			}});
		w = new While();
		w.setBody(p1.addStep(p2));	}
	
	public void testWhile() {
		int times[] = {1,2,3,23,10000};
		
		for (int i = 0; i < times.length; i++) {
			int count = times[i];
			w.setTestCondition(getTestFor(count));
			wc = executeWorkflow(w);
			assertEquals(
				"Counter1 error",
				count,
				((DomainObject) wc.get(O1_KEY)).getCounter());
			assertEquals(
				"Counter2 error",
				count*2,
				((DomainObject) wc.get(O2_KEY)).getCounter());			
		}
	}
	
	protected TestCondition getTestFor(final int count) {
		return new TestCondition() {
			public boolean evaluate() {
				DomainObject domainObject = (DomainObject)get(O1_KEY);
				return domainObject.getCounter() < count;
			}
		};
	}

}
