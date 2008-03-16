/*
 * 	 
 *  Copyright (c) 2002, 2003 Dragos Manolescu (dam@micro-workflow.com)
 * 
 *  See the LICENSE file for licensing information.
 */

package com.microworkflow.test;

import java.util.Vector;

import com.microworkflow.execution.Closure;
import com.microworkflow.execution.Scheduler;

import junit.framework.TestCase;

public class TestScheduler extends TestCase {
	protected Scheduler scheduler;
	protected Vector results;

	public TestScheduler(String arg0) {
		super(arg0);
	}
	public static void main(String[] args) {
		junit.swingui.TestRunner.run(TestScheduler.class);
	}
	protected void setUp() throws Exception {
		scheduler = new Scheduler();
		results = new Vector();
	}
	protected void tearDown() throws Exception {
		scheduler.shutdown();
	}
	protected Closure makeCommandWith(final int id) {
		return new Closure() {
			public Object evaluate() {
				if ((id % 5) == 0) {
					synchronized(this) {
						try {
							wait(200);
						} catch (InterruptedException e) {
							;
						}
					}
				} 
				results.addElement(String.valueOf(id));
				return this;
			}
		};
	}
	public void testCommands(int numberOfCommands) {
		for (int i = 0; i < numberOfCommands; i++) {
			scheduler.scheduleCommand(makeCommandWith(i));
		}
	}
	public void waitForThreadCompletion() {
		scheduler.waitForCompletion();
	}
	public void testBoundary1Command() {
		testCommands(1);
		waitForThreadCompletion();
		assertEquals(1,results.size());
	}
	public void testBoundary2Commands() {
		testCommands(Scheduler.THREADS);
		waitForThreadCompletion();
		assertEquals(Scheduler.THREADS,results.size());
	}
	public void test2Commands() {
		testCommands(2);
		waitForThreadCompletion();
		assertEquals(2,results.size());
	}
	public void testManyCommands() {
		testCommands(50);
		waitForThreadCompletion();
		assertEquals(50,results.size());		
	}
}
