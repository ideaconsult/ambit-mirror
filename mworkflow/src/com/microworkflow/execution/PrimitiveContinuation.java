/*
 * 	 
 *  Copyright (c) 2002, 2003 Dragos Manolescu (dam@micro-workflow.com)
 * 
 *  See the LICENSE file for licensing information.
 */

package com.microworkflow.execution;

import com.microworkflow.process.WorkflowContext;

public class PrimitiveContinuation extends Continuation {
	protected Performer performer;
	protected String resultKey = null;
	protected String targetKey = null;

	public PrimitiveContinuation(Continuation continuation) {
		super(continuation);
	}
	public Continuation applyContinuationIn(WorkflowContext context) throws Exception {
        fireUnderExecution();
		performer.setTargetKey(targetKey);
		performer.setResultKey(resultKey);
		performer.setContext(context);
		performer.performOperation();
		return getNextContinuation();
	}
	public void setPerformer(Performer performer) {
		this.performer = performer;
	}
	public void setResultKey(String resultKey) {
		this.resultKey = resultKey;
	}
	public void setTargetKey(String targetKey) {
		this.targetKey = targetKey;
	}

}
