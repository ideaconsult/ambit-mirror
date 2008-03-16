/*
 * 	 
 *  Copyright (c) 2002, 2003 Dragos Manolescu (dam@micro-workflow.com)
 * 
 *  See the LICENSE file for licensing information.
 */

package com.microworkflow.process;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

import com.microworkflow.execution.*;

import sun.misc.Queue;

/**
 * @author dam
 *
 * This class executes a workflow instance according to its definition.
 * It contains the trampoline residing at the focal point of the execution
 * component. I have described this workflow enactment mechanism in
 * <a href="http://micro-workflow.com/PDF/wecfo.pdf">Workflow Enactment with
 * Continuation and Future Objects</a>.
 */
public class Workflow {
	
	protected Queue queue; 
	protected Activity definition;
	protected ArrayList additionalContinuations;
	protected Scheduler scheduler;
	protected Logger logger;
	
	public Workflow () {
		initialize();
	}

	protected void initialize() {
		queue=new Queue();
		additionalContinuations=new ArrayList();
		scheduler=new Scheduler();
		logger=Logger.getLogger("com.microworklfow.process.workflow");
	}

	public WorkflowContext executeWith(WorkflowContext context) {
		WorkflowContext currentContext = context;
		Continuation continuation = firstContinuation();
		while (continuation.getClass() != NullContinuation.class) {
			continuation = bounce(continuation, currentContext);
		}
		scheduler.shutdown();
		return currentContext;
	}

	protected Continuation bounce(Continuation continuation, WorkflowContext context) {
		Continuation ret = null;
		
		logger.finer("trampoline bouncing");
		queue.enqueue(continuation.applyContinuationIn(context));
		if (hasAdditionalContinuations()) {
			enqueueAdditionalContinuations();
		}
		do {
			try {
				ret = (Continuation) queue.dequeue();
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
		while (ret.getClass() == NullContinuation.class && !queue.isEmpty()); 
		return ret;
	}

	public void addContinuation(Continuation continuation) {
		additionalContinuations.add(continuation);
	}

	protected void enqueueAdditionalContinuations() {
		Iterator iterator = additionalContinuations.iterator();
		while (iterator.hasNext()) {
			Continuation each = (Continuation) iterator.next();
			queue.enqueue(each);
		}
		additionalContinuations.clear();
	}

	public boolean hasAdditionalContinuations() {
		return !additionalContinuations.isEmpty();
	}

	protected Continuation firstContinuation() {
		return definition.continuationWith(new NullContinuation(this));
	}

	public Activity getDefinition() {
		return definition;
	}

	public void setDefinition(Activity definition) {
		this.definition = definition;
	}

	public Scheduler getScheduler() {
		return scheduler;
	}
}
