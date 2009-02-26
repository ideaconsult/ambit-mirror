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
	protected enum STATUS {NOTEXECUTED,YES,NO};
	protected STATUS status = STATUS.NOTEXECUTED;
	
	public boolean getResult() {
		return status.equals(STATUS.YES);
	}

	public abstract boolean evaluate();
	
	public boolean evaluateInContext(WorkflowContext aContext) {
		setContext(aContext);
		status =  evaluate() ? STATUS.YES : STATUS.NO;
		return status.equals(STATUS.YES);
	}
	public boolean hasExecuted() {
		return !status.equals(STATUS.NOTEXECUTED);
	}
    public void setNotExecuted() {
    	status = STATUS.NOTEXECUTED;
    }
}
