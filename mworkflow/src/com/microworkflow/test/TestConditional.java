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

public class TestConditional extends TestActivity {

	public TestConditional(String arg0) {
		super(arg0);
	}
	public static void main(String[] args) {
		junit.swingui.TestRunner.run(TestConditional.class);
	}
	public static Test suite() {
		return new TestSuite(TestConditional.class);
	}
	public void testThenOnly() {
		TestCondition test=new TestCondition(){
			public boolean evaluate() {
				return false;
			}
		};
		wc=executeWorkflow(new Conditional(test, getThenBranch()));
		assertEquals(
			"Empty else failure",
			false,
			wc.containsKey("result"));
	}	
	public void testConditional() {
		Primitive thenP=getThenBranch();
		Primitive elseP=getElseBranch();
		wc = executeWorkflow(new Conditional(
			new TestCondition() {
				public boolean evaluate() {
					return  ((Boolean)get("true")).booleanValue();
				}
			}, thenP, elseP));
		assertEquals(
			"Then branch failure",
			true,
			("this is a string".concat("first argument")).equalsIgnoreCase(
				(String) wc.get("result")));
		wc = executeWorkflow(new Conditional(new TestCondition() {
				public boolean evaluate() {
					return  ((Boolean)get("false")).booleanValue();
				}
			}, thenP, elseP));
		assertEquals(
			"Else branch failure",
			true,
			("this is a string".concat("second argument")).equalsIgnoreCase(
				(String) wc.get("result")));
	}
	public Primitive getElseBranch() {
		Primitive elseP = new Primitive(O1_KEY, "result", new Performer() {
			public Object execute() throws Exception {
				if (context.containsKey(targetKey)) {
					DomainObject domainObject = (DomainObject) getTarget();
					return domainObject.concat((String) get("argument2"));
				}
				return null;
			}
		});
		return elseP;
	}
	public Primitive getThenBranch() {
		Primitive thenP = new Primitive(O1_KEY, "result", new Performer() {
			public Object execute() throws Exception {
				if (context.containsKey(targetKey)) {
					DomainObject domainObject = (DomainObject) getTarget();
					return domainObject.concat((String) get("argument1"));
				}
				return null;
			}
		});
		return thenP;
	}

}
