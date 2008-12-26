/*
 * 	 
 *  Copyright (c) 2003 Dragos Manolescu (dam@micro-workflow.com)
 * 
 *  See the LICENSE file for licensing information.
 */
package com.microworkflow.execution;

import java.util.Iterator;

import com.microworkflow.process.WorkflowContext;

/**
 * @author dam
 */
public class IterativeContinuation extends Continuation {
	protected String targetKey;
	protected String iterationTargetKey;
	protected Performer performer;
	protected Continuation body;
	protected Iterator components;
	protected boolean iterating;

	public IterativeContinuation(Continuation continuation) {
		super(continuation);
	}

	public Continuation applyContinuationIn(WorkflowContext context) throws Exception {
        fireUnderExecution();
		if (!iterating) {
			iterating=true;
			performer.setContext(context);
			performer.setTargetKey(targetKey);
			components=(Iterator)performer.execute();
		}
		if (components.hasNext()) {
			Object component=components.next();
			context.put(iterationTargetKey,component);
			return body;
		}
		else {
			resetState();
			return getNextContinuation();
		}
	}

	public void resetState() {
		super.resetState();
		iterating=false;
	}
	
	public void setIterationTargetKey(String iterationTargetKey) {
		this.iterationTargetKey = iterationTargetKey;
	}

	public void setPerformer(Performer performer) {
		this.performer = performer;
	}

	public void setTargetKey(String targetKey) {
		this.targetKey = targetKey;
	}

	public void setBody(Continuation body) {
		this.body = body;
	}

}
