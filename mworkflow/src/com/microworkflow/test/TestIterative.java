/*
 * 	 
 *  Copyright (c) 2003 Dragos Manolescu (dam@micro-workflow.com)
 * 
 *  See the LICENSE file for licensing information.
 */
package com.microworkflow.test;

import com.microworkflow.execution.Performer;
import com.microworkflow.process.Iterative;
import com.microworkflow.process.Primitive;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author dam
 */
public class TestIterative extends TestActivity {

	public TestIterative(String arg0) {
		super(arg0);
	}
	
	public static void main(String[] args) {
		junit.swingui.TestRunner.run(TestIterative.class);
	}

	public static Test suite() {
		return new TestSuite(TestIterative.class);
	}

	public void testIterative() {
		Primitive body=new Primitive("component", new Performer(){
			public Object execute() throws Exception {
				String component=(String)getTarget();
				DomainObject domainObject=(DomainObject)get(O1_KEY);
				if (domainObject.getString() != null) {
					domainObject.setString(domainObject.getString().concat(component));
				} else {
					domainObject.setString(component);
				}
				return this;
			}
		});
		Iterative iterative=new Iterative(O1_KEY,
			new Performer(){
				public Object execute() throws Exception {
					DomainObject target=(DomainObject)getTarget();
					return target.getComponents();
				}
			},
			"component",
			body);
		wc = executeWorkflow(iterative);
		DomainObject domainObject=(DomainObject)wc.get(O1_KEY);
		assertTrue(DomainObject.TEST_STRING.concat("onetwothreefour").equalsIgnoreCase(domainObject.getString()));
	}
}
