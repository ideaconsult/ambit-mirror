/*
 * 	 
 *  Copyright (c) 2002, 2003 Dragos Manolescu (dam@micro-workflow.com)
 * 
 *  See the LICENSE file for licensing information.
 */

package com.microworkflow.process;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

import sun.misc.Queue;

import com.microworkflow.events.ObjectWithPropertyChangeSupport;
import com.microworkflow.events.WorkflowEvent;
import com.microworkflow.execution.Continuation;
import com.microworkflow.execution.NullContinuation;
import com.microworkflow.execution.Scheduler;
import com.microworkflow.process.IWorkflowExceptionHandler.RETURN_MODE;

/**
 * @author dam
 *
 * This class executes a workflow instance according to its definition.
 * It contains the trampoline residing at the focal point of the execution
 * component. I have described this workflow enactment mechanism in
 * <a href="http://micro-workflow.com/PDF/wecfo.pdf">Workflow Enactment with
 * Continuation and Future Objects</a>.
 */
public class Workflow extends ObjectWithPropertyChangeSupport implements PropertyChangeListener {
	protected Queue queue; 
	protected Activity definition;
	protected ArrayList<Continuation> additionalContinuations;
	protected Scheduler scheduler;
	protected Logger logger;
	protected IWorkflowExceptionHandler exceptionHandler = null;
	protected boolean interrupted = false;
	
	protected synchronized boolean isInterrupted() {
		return interrupted;
	}

	protected synchronized void setInterrupted(boolean interrupted) {
		this.interrupted = interrupted;
	}

	public Workflow () {
		initialize();
	}

	protected void initialize() {
		queue=new Queue();
		additionalContinuations=new ArrayList<Continuation>();
		scheduler=new Scheduler();
		logger=Logger.getLogger("com.microworkflow.process.workflow");
		psp = new PropertyChangeSupport(this);
	}

	public WorkflowContext executeWith(WorkflowContext context) {
		setInterrupted(false);
        firePropertyChange(new WorkflowEvent(this,WorkflowEvent.WF_START,null,context));
		WorkflowContext currentContext = context;
		Continuation continuation = firstContinuation();
		while (continuation.getClass() != NullContinuation.class) {
			try {
				if (isInterrupted()) {
					Exception x = new Exception("Interrupted");
					exceptionHandler.processException(x,continuation,context);
					firePropertyChange(new WorkflowEvent(this,WorkflowEvent.WF_ABORTED,null,x));	
				}				
				continuation = bounce(continuation, currentContext);
			} catch (Exception x) {
				if (exceptionHandler == null)
					exceptionHandler = new WorkflowExceptionHandler();
				RETURN_MODE returnmode = exceptionHandler.processException(x,continuation,context);
				if (RETURN_MODE.RESUME ==returnmode) {
					firePropertyChange(new WorkflowEvent(this,WorkflowEvent.WF_RESUMED,null,x));					
					continue;
				} else {
			        firePropertyChange(new WorkflowEvent(this,WorkflowEvent.WF_ABORTED,null,x));					
					break;
				}
			}
		}
		scheduler.shutdown();
        firePropertyChange(new WorkflowEvent(this,WorkflowEvent.WF_COMPLETE,null,currentContext));
		return currentContext;
	}

	protected Continuation bounce(Continuation continuation, WorkflowContext context) throws Exception {
		Continuation ret = null;
		
		logger.finer("trampoline bouncing "+continuation);
        Continuation nextContinuation = continuation.applyContinuationIn(context);
		queue.enqueue(nextContinuation);
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
        Activity old_definition = this.definition;
		this.definition = definition;
        firePropertyChange(new WorkflowEvent(this,WorkflowEvent.WF_DEFINITION,old_definition,definition));
	}

	public Scheduler getScheduler() {
		return scheduler;
	}
    public void fireExecuteActivityEvent(Activity activity) {
        firePropertyChange(new WorkflowEvent(this,WorkflowEvent.WF_EXECUTE,null,activity));
    }
       
    public void fireAnimateEvent(boolean enable) {
        firePropertyChange(new WorkflowEvent(this,WorkflowEvent.WF_ANIMATE,null,new Boolean(enable)));
    }    
    public void propertyChange(PropertyChangeEvent evt) {
    	if (evt.getPropertyName().equals(WorkflowEvent.WF_ABORTED))
    		setInterrupted(true);
    	
    }
    
}
