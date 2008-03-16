/*
 * 	 
 *  Copyright (c) 2002 Dragos Manolescu (dam@micro-workflow.com)
 * 
 *  See the LICENSE file for licensing information.
 */

package com.microworkflow.process;

import java.util.HashMap;

public class WorkflowContext {
	protected HashMap context=new HashMap();
	
	public Object get(String key) {
		return context.get(key);
	}
	public void put(String key, Object value) {
		context.put(key,value);
	}
	public boolean containsKey(String key) {
		return context.containsKey(key);
	}
}
