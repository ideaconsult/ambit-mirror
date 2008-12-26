package com.microworkflow.test;

import com.microworkflow.execution.Performer;
import com.microworkflow.process.*;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * JUnit test
 * @author Ringo De Smet
 */
public class TestDefinitionCycle extends TestCase {
	public int i = 0;
	public static final int MAX=10000;
	
	public static Test suite() {
		return new TestSuite(TestDefinitionCycle.class);
	}

	public void testStackOverFlow() {
		try {
			Workflow workflow = new Workflow();
			workflow.setDefinition(this.getDefinition());
			WorkflowContext wc = new WorkflowContext();
			wc.put("Object1", "foo");
			wc.put("Object2", "bar");
			workflow.executeWith(wc);
		} catch (StackOverflowError soe) {
			fail("Endless loop detected!");
		}
		assertTrue(i==MAX);
	}

	/**
	 * The chain of activity is: work1, test1, work2, test2, back to work1, etc.
	 * 
	 * @return the workflow definition.
	 */
	protected Activity getDefinition() {
		Activity performer1 = new Primitive("Object1", new Performer() {
			public Object execute() throws Exception {
				i=i+2;
				return null;
			}
		});
		Activity performer2 = new Primitive("Object2", new Performer() {
			public Object execute() throws Exception {
				i=i-1;
				return null;
			}
		});

		Activity loopFrom2BackTo1 = performer2.addStep(performer1);

		Activity end = new NullActivity();

		Conditional test1 =
			new Conditional(new SomeCondition(MAX), null, end);
		Conditional test2 =
			new Conditional(new SomeCondition(MAX), null, end);

		Activity performer1PlusTest = performer1.addStep(test1);
		Activity performer2PlusTest = performer2.addStep(test2);

		test1.setThenBranch(performer2PlusTest);
		test2.setThenBranch(performer1PlusTest);

		return performer1PlusTest;
	}

	protected class SomeCondition extends TestCondition {

		protected int max;

		public SomeCondition(int max) {
			this.max = max;
		}

		public boolean evaluate() {
			return i < max;
		}

	}
}
