/*
 * 	 
 *  Copyright (c) 2002, 2003 Dragos Manolescu (dam@micro-workflow.com)
 * 
 *  See the LICENSE file for licensing information.
 */

package com.microworkflow.execution;

import com.microworkflow.process.Workflow;

public abstract class JoinContinuation extends Continuation {
	protected Continuation body;

	public JoinContinuation(Continuation c, Workflow w) {
		super(c, w);
	}
	public JoinContinuation(Continuation continuation) {
		super(continuation, continuation.getWorkflow());
	}
	public void setBody(Continuation bodyContinuation) {
		this.body = bodyContinuation;
	}
}
