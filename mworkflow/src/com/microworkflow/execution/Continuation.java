/*
 * 	 
 *  Copyright (c) 2002, 2003 Dragos Manolescu (dam@micro-workflow.com)
 * 
 *  See the LICENSE file for licensing information.
 */

package com.microworkflow.execution;

import com.microworkflow.process.Activity;
import com.microworkflow.process.Workflow;
import com.microworkflow.process.WorkflowContext;

public abstract class Continuation {
	protected Continuation nextContinuation;
	protected Workflow workflow;
	protected Activity activity;
	
	public Continuation(Continuation continuation) {
		this(continuation, continuation.getWorkflow());
	}
	
	public Continuation(Continuation c, Workflow w) {
		initialize(c,w);
	}
	
	protected void initialize(Continuation c, Workflow w) {
		nextContinuation  = c;
		workflow = w;
	}
	
	public abstract Continuation applyContinuationIn(WorkflowContext context) throws Exception;

	public void resetState() {
		getActivity().computeStateFor(this);
	}
	
	public Continuation getNextContinuation() {
		return nextContinuation;
	}

	protected Scheduler getScheduler() {
		return workflow.getScheduler();
	}

	public Workflow getWorkflow() {
		return workflow;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}
	
	public Activity getActivity() {
		return activity;
	}
	
	public NullContinuation makeNullContinuation() {
		return new NullContinuation(getWorkflow());
	}

	public PrimitiveContinuation makePrimitiveContinuation(Activity activity) {
		PrimitiveContinuation ret=new PrimitiveContinuation(this);
		ret.setActivity(activity);
		return ret;
	}

	public ConditionalContinuation makeConditionalContinuation(Activity activity) {
		ConditionalContinuation ret=new ConditionalContinuation(this);
		ret.setActivity(activity);
		return ret;
	}

	public SequenceContinuation makeSequenceContinuation(Activity activity) {
		SequenceContinuation ret=new SequenceContinuation(this);
		ret.setActivity(activity);
		return ret;
	}

	public AsyncPrimitiveContinuation makeAsyncPrimitiveContinuation(Activity activity) {
		AsyncPrimitiveContinuation ret=new AsyncPrimitiveContinuation(this);
		ret.setActivity(activity);
		return ret;
	}

	public ForkContinuation makeForkContinuation(Activity activity) {
		ForkContinuation ret=new ForkContinuation(this);
		ret.setActivity(activity);
		return ret;
	}

	public OrJoinContinuation makeOrJoinContinuation(Activity activity) {
		OrJoinContinuation ret=new OrJoinContinuation(this);
		ret.setActivity(activity);
		return ret;
	}

	public AndJoinContinuation makeAndJoinContinuation(Activity activity) {
		AndJoinContinuation ret=new AndJoinContinuation(this);
		ret.setActivity(activity);
		return ret;
	}

	public WhileContinuation makeRepetitionContinuation(Activity activity) {
		WhileContinuation ret=new WhileContinuation(this);
		ret.setActivity(activity);
		return ret;
	}

	public IterativeContinuation makeIterativeContinuation(Activity activity) {
		IterativeContinuation ret=new IterativeContinuation(this);
		ret.setActivity(activity);
		return ret;
	}
    protected void fireUnderExecution() {
        workflow.fireExecuteActivityEvent(getActivity());
    }
    
}
