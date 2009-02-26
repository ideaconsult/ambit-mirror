/*
 * 	 
 *  Copyright (c) 2002, 2003 Dragos Manolescu (dam@micro-workflow.com)
 * 
 *  See the LICENSE file for licensing information.
 */

package com.microworkflow.execution;

import com.microworkflow.process.WorkflowContext;

/**
 * @author dam
 *
 */
public abstract class Performer<Target,Result> extends WorkflowClosure {
	protected boolean hasExecuted;
	protected String targetKey;
	protected String resultKey;

	public Performer() {
		super();
	}
	public Performer(String targetKey, WorkflowContext context) {
		this.targetKey = targetKey;
		this.context = context;
	}
	public Performer(
		String targetKey,
		String resultKey,
		WorkflowContext context) {
		this(targetKey, context);
		this.resultKey = resultKey;
	}
	public Object performOperation() throws Exception {
		Result ret = null;
		preExecute();
		logger.finer("About to send execute() in " + this);
		ret = execute();
		logger.finer(this +" executed; returned " + (ret == null ? "no result" : ret));
		postExecute(ret);
		return ret;
	}
	public abstract Result execute() throws Exception;

	protected void preExecute() {
		hasExecuted = false;
	}

	protected void postExecute(Result ret) {
		hasExecuted = true;
		if (resultKey != null) {
			context.put(resultKey, ret);
		}
	}

	public boolean hasExecuted() {
		return hasExecuted;
	}

	public String getResultKey() {
		return resultKey;
	}

	public String getTargetKey() {
		return targetKey;
	}

	public void setResultKey(String resultKey) {
		this.resultKey = resultKey;
	}

	public void setTargetKey(String targetKey) {
		this.targetKey = targetKey;
	}

	protected Target getTarget() {
		return (Target)get(targetKey);
	}
    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "[" + targetKey + " --> " + resultKey + "]";
    }
    public void setNotExecuted() {
    	hasExecuted = false;
    }
}
