/*
 * 	 
 *  Copyright (c) 2002 Dragos Manolescu (dam@micro-workflow.com)
 * 
 *  See the LICENSE file for licensing information.
 */

package com.microworkflow.execution;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class Future implements InvocationHandler {
	
	protected Object value = null;
	protected ArrayList messageList = null;
	
	public Future() {
	 value = null;
	}

	public Future(Object obj) {
	 value = obj;
	}

	public static Object newInstance(ClassLoader cl, Object obj, Class[] interfaces) {
		return (Object) Proxy.newProxyInstance(cl, interfaces, new Future(obj));		
	}

	public Object invoke(Object proxy, Method method, Object[] args)
		throws Throwable {
			if (getValue() != null) {
				return method.invoke(value, args);
			}
			else {
				add(method, args);
			}
		return null;
	}

	protected void add(Method method, Object[] arguments) {
		getMessageList().add(new Message(method, arguments));
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
		if (messageList != null && messageList.size() > 0) {
			invokePendingMessages();
		}
	}

	protected void invokePendingMessages() {
	}

	public ArrayList getMessageList() {
		if (messageList == null) {
			setMessageList(new ArrayList());
		}
		return messageList;
	}

	public void setMessageList(ArrayList messageList) {
		this.messageList = messageList;
	}

}
