/*
 * 	 
 *  Copyright (c) 2002 Dragos Manolescu (dam@micro-workflow.com)
 * 
 *  See the LICENSE file for licensing information.
 */

package com.microworkflow.execution;

import java.util.Iterator;

import com.microworkflow.process.WorkflowContext;

public class SequenceContinuation extends Continuation {
	protected Iterator continuations;

	public SequenceContinuation(Continuation c) {
		super(c, c.getWorkflow());
	}

	public Continuation applyContinuationIn(WorkflowContext context) throws Exception {
        fireUnderExecution();
		if (continuations.hasNext()) {
			Continuation step = (Continuation) continuations.next();
			return step.applyContinuationIn(context);
		}
		else {
			resetState();
			return getNextContinuation();
		}
	}
	
	public void setContinuations(Iterator continuations) {
		this.continuations = continuations;
	}	

}
