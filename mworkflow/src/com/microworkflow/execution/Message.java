/*
 * 	 
 *  Copyright (c) 2002 Dragos Manolescu (dam@micro-workflow.com)
 * 
 *  See the LICENSE file for licensing information.
 */

package com.microworkflow.execution;

import java.lang.reflect.Method;

public class Message {
	Method method;
	Object[] arguments;
	
	public Message(Method method, Object[] args) {
		this.method = method;
		this.arguments = args;
	}


}
