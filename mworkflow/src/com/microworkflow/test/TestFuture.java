/*
 * 	 
 *  Copyright (c) 2002, 2003 Dragos Manolescu (dam@micro-workflow.com)
 * 
 *  See the LICENSE file for licensing information.
 */

package com.microworkflow.test;

import com.microworkflow.execution.Future;

import junit.framework.TestCase;

public class TestFuture extends TestCase {
	protected DomainObjectInterface future;
	protected DomainObject domainObject;

	public TestFuture(String arg0) {
		super(arg0);
	}
	public static void main(String[] args) {
		junit.swingui.TestRunner.run(TestFuture.class);
	}
	protected void setUp() throws Exception {
		super.setUp();
		domainObject = new DomainObject("a string");
		future = (DomainObjectInterface) Future.newInstance(domainObject.getClass().getClassLoader(), domainObject, new Class[] { DomainObjectInterface.class });
	}
	public void testFuture() {
		String ret = future.getString();
		assertNotNull(ret);
		assertEquals("getString() failed", true, "a string".equals(ret));
		ret = future.concat(" right here");
		assertEquals("asUppercase() failed", true, "a string".concat(" right here").equals(ret));
	}

}
