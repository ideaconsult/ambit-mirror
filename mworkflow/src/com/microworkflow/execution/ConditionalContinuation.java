/*
 * 	 
 *  Copyright (c) 2002, 2003 Dragos Manolescu (dam@micro-workflow.com)
 * 
 *  See the LICENSE file for licensing information.
 */

package com.microworkflow.execution;

import com.microworkflow.process.TestCondition;
import com.microworkflow.process.WorkflowContext;

/**
 * @author dam
 *
 * This continuation evaluates the test condition and returns to the trampoline
 * the continuation corresponding to the appropriate branch.
 */
public class ConditionalContinuation extends Continuation {
	protected TestCondition test;
	protected Continuation thenContinuation;
	protected Continuation elseContinuation;

	public ConditionalContinuation(Continuation continuation) {
		super(continuation, continuation.getWorkflow());
	}

	public void setElseContinuation(Continuation elseContinuation) {
		this.elseContinuation = elseContinuation;
	}

	public void setThenContinuation(Continuation thenContinuation) {
		this.thenContinuation = thenContinuation;
	}

	public void setTestCondition(TestCondition aBlock) {
		test = aBlock;
	}

	public Continuation applyContinuationIn(WorkflowContext context) throws Exception {
        fireUnderExecution();        
		Continuation ret =
			test.evaluateInContext(context) ? thenContinuation : elseContinuation;
		resetState();
		return ret;
	}

}
