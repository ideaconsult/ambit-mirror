/*
 * 	 
 *  Copyright (c) 2002, 2003 Dragos Manolescu (dam@micro-workflow.com)
 * 
 *  See the LICENSE file for licensing information.
 */

package com.microworkflow.execution;

import com.microworkflow.process.Workflow;
import com.microworkflow.process.WorkflowContext;

public class OrJoinContinuation extends JoinContinuation {
	protected int numberOfInputs=0;
	protected int counter;

	public OrJoinContinuation(Continuation c, Workflow w) {
		super(c, w);
	}

	public OrJoinContinuation(Continuation continuation) {
		super(continuation, continuation.getWorkflow());
	}

	public Continuation applyContinuationIn(WorkflowContext context) throws Exception {
        fireUnderExecution();
		counter=counter+1;
		if (counter==1) {
			return body;
		} else if (counter==numberOfInputs){
			resetState();
		}
		return makeNullContinuation();
	}
	
	public void resetState() {
		super.resetState();
		counter=0;
	}
	
	public void setNumberOfInputs(int numberOfInputs) {
		this.numberOfInputs = numberOfInputs;
	}

}
