/*
 * 	 
 *  Copyright (c) 2002, 2003 Dragos Manolescu (dam@micro-workflow.com)
 * 
 *  See the LICENSE file for licensing information.
 */

package com.microworkflow.execution;

import com.microworkflow.process.TestCondition;
import com.microworkflow.process.WorkflowContext;

public class WhileContinuation extends Continuation {
	protected Continuation bodyCurrentContinuation;
	protected TestCondition test;

	public WhileContinuation(Continuation continuation) {
		super(continuation, continuation.getWorkflow());
	}

	public Continuation applyContinuationIn(WorkflowContext context) throws Exception {
        fireUnderExecution();
		Continuation ret=bodyCurrentContinuation.applyContinuationIn(context);
		if (ret!=this) {
			bodyCurrentContinuation=ret;
			return this;
		}
		else {
			resetState();
			return test.evaluateInContext(context) ? this : getNextContinuation();
		}
	}

	public void setBody(Continuation body) {
		this.bodyCurrentContinuation = body;
	}

	public void setTestCondition(TestCondition aClosure) {
		this.test = aClosure;
	}
	
}
