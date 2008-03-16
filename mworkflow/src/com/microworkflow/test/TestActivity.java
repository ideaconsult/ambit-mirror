/*
 * 	 
 *  Copyright (c) 2002, 2003 Dragos Manolescu (dam@micro-workflow.com)
 * 
 *  See the LICENSE file for licensing information.
 */

package com.microworkflow.test;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.microworkflow.process.*;
import junit.framework.TestCase;

public abstract class TestActivity extends TestCase {
	protected static final String O1_KEY = "object1";
	WorkflowContext context, wc;
	Workflow workflow;

	public TestActivity(String arg0) {
		super(arg0);
	}
	public static void main(String[] args) {
		junit.swingui.TestRunner.run(TestActivity.class);
	}
	protected void setUp() throws Exception {
		super.setUp();
		context = new WorkflowContext();
		context.put(O1_KEY, new DomainObject(DomainObject.TEST_STRING));
		context.put("argument1", "first argument");
		context.put("argument2", "second argument");
		context.put("true", new Boolean(true));
		context.put("false", new Boolean(false));
		workflow = new Workflow();
        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.FINEST);
        Logger.getLogger("com.microworkflow.execution.WorkflowContext").addHandler(ch);
        Logger.getLogger("com.microworkflow.execution.WorkflowContext").setLevel(Level.FINEST);
        Logger.getLogger("com.microworkflow.execution.Scheduler").addHandler(ch);
        Logger.getLogger("com.microworkflow.execution.Scheduler").setLevel(Level.FINEST);
        Logger.getLogger("com.microworklfow.process.workflow").addHandler(ch);
        Logger.getLogger("com.microworklfow.process.workflow").setLevel(Level.FINEST);        
                
	}
	protected void tearDown() throws Exception {
		super.tearDown();
	}	
	protected WorkflowContext executeWorkflow(Activity definition) {
		workflow.setDefinition(definition);
		return workflow.executeWith(context);
	}

}
