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
public abstract class Performer extends WorkflowClosure {
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
	public Object performOperation() {
		Object ret = null;
		preExecute();
		logger.finer("About to send execute() in " + this);
		try {
			ret = execute();
		} finally {
			logger.finer(this +" executed; returned " + (ret == null ? "no result" : ret));
			postExecute(ret);
		}
		return ret;
	}
	public abstract Object execute();

	protected void preExecute() {
		hasExecuted = false;
	}

	protected void postExecute(Object ret) {
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

	protected Object getTarget() {
		return get(targetKey);
	}
    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "[" + targetKey + " --> " + resultKey + "]";
    }
}
