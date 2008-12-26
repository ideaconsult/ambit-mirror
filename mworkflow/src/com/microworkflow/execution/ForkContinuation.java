/*
 * 	 
 *  Copyright (c) 2002, 2003 Dragos Manolescu (dam@micro-workflow.com)
 * 
 *  See the LICENSE file for licensing information.
 */

package com.microworkflow.execution;

import java.util.Iterator;

import com.microworkflow.process.WorkflowContext;

public class ForkContinuation extends Continuation {
	protected Iterator branches;

	public ForkContinuation(Continuation continuation) {
		super(continuation, continuation.getWorkflow());
	}
	
	public Continuation applyContinuationIn(WorkflowContext context) throws Exception {
        fireUnderExecution();
		Continuation branch = this.makeNullContinuation();		
		if (branches.hasNext()) {
			branch = (Continuation) branches.next();
		}
		while (branches.hasNext()) {
			Continuation otherBranch = (Continuation) branches.next();
			getWorkflow().addContinuation(otherBranch);
		}
		resetState();
		return branch.applyContinuationIn(context);
	}
	
	public void setBranches(Iterator branches) {
		this.branches = branches;
	}
	
}
