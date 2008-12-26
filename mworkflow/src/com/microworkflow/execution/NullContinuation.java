/*
 * 	 
 *  Copyright (c) 2002 Dragos Manolescu (dam@micro-workflow.com)
 * 
 *  See the LICENSE file for licensing information.
 */

package com.microworkflow.execution;

import com.microworkflow.process.Workflow;
import com.microworkflow.process.WorkflowContext;

public class NullContinuation extends Continuation {
	public NullContinuation(Workflow w) {
		super(null, w);
	}
	public Continuation applyContinuationIn(WorkflowContext context) throws Exception {
        fireUnderExecution();
		return this;
	}
}
