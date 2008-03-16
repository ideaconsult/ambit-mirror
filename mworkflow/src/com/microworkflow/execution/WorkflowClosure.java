/*
 * 	 
 *  Copyright (c) 2003 Dragos Manolescu (dam@micro-workflow.com)
 * 
 *  See the LICENSE file for licensing information.
 */
package com.microworkflow.execution;

import java.util.logging.Logger;

import com.microworkflow.process.WorkflowContext;

/**
 * This class provides a means to access the WorkflowContext from a closure without
 * explicitly passing it as an argument.
 *   
 * @author Dragos Manolescu (dam@micro-workflow.com)
 */
public abstract class WorkflowClosure extends Object {

	protected WorkflowContext context;
	protected static Logger logger = Logger.getLogger("com.microworkflow.execution.WorkflowContext");;

	public WorkflowContext getContext() {
		return context;
	}

	public void setContext(WorkflowContext context) {
		this.context = context;
	}

	protected Object get(String aKey) {
		if (!context.containsKey(aKey)) {
			logger.severe("Key " + aKey + " not found");
		}
		return context.get(aKey);
	}

}
