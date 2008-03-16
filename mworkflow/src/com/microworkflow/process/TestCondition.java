/*
 * 	 
 *  Copyright (c) 2003 Dragos Manolescu (dam@micro-workflow.com)
 * 
 *  See the LICENSE file for licensing information.
 */
package com.microworkflow.process;

import com.microworkflow.execution.WorkflowClosure;

/**
 * Instances of this class represent conditions in activity nodes such
 * as Conditional and While.
 *  
 * @author Dragos Manolescu (dam@micro-workflow.com)
 */
public abstract class TestCondition extends WorkflowClosure {
	
	public abstract boolean evaluate();
	
	public boolean evaluateInContext(WorkflowContext aContext) {
		setContext(aContext);
		return evaluate();
	}

}
