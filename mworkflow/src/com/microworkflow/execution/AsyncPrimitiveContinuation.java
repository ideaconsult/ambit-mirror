/*
 * 	 
 *  Copyright (c) 2002, 2003 Dragos Manolescu (dam@micro-workflow.com)
 * 
 *  See the LICENSE file for licensing information.
 */

package com.microworkflow.execution;

import com.microworkflow.process.WorkflowContext;

public class AsyncPrimitiveContinuation extends Continuation {
	protected Performer performer;
	protected Object result;
	protected String resultKey = null;
	protected String targetKey = null;
	protected boolean isScheduled = false;

	public AsyncPrimitiveContinuation(Continuation continuation) {
		super(continuation, continuation.getWorkflow());
	}
	public Continuation applyContinuationIn(final WorkflowContext context) throws Exception {
        fireUnderExecution();        
		Continuation ret = getNextContinuation().makeNullContinuation();
		
		if (!isScheduled) {
			performer.setTargetKey(targetKey);
			performer.setResultKey(resultKey);
			performer.setContext(context);
			scheduleAsynchronousCommand(context);
			isScheduled = true;
			ret = getNextContinuation();
			getWorkflow().addContinuation(this);
		} else {
			if (!performer.hasExecuted) {
				ret = this;
			} 
		}
		return ret;
	}
	public void scheduleAsynchronousCommand(final WorkflowContext context) throws Exception {
		Closure command = new Closure() {
			public Object evaluate() {
				try {
					performer.performOperation();
				} catch (Exception e) {
					//TODO exception handling
					e.printStackTrace();
				} finally {
				}
				return this;
				
			}
		};
		getScheduler().scheduleCommand(command);
	}
	public void setTargetKey(String targetKey) {
		this.targetKey = targetKey;
	}
	public void setPerformer(Performer performer) {
		this.performer = performer;
	}
	public void setResultKey(String resultKey) {
		this.resultKey = resultKey;
	}

}
