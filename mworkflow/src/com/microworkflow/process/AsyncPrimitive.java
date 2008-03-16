/*
 * 	 
 *  Copyright (c) 2002 Dragos Manolescu (dam@micro-workflow.com)
 * 
 *  See the LICENSE file for licensing information.
 */

package com.microworkflow.process;

import com.microworkflow.execution.AsyncPrimitiveContinuation;
import com.microworkflow.execution.Continuation;
import com.microworkflow.execution.Performer;

public class AsyncPrimitive extends Primitive {

	public AsyncPrimitive(Performer performer) {
		super(performer);
	}
	public AsyncPrimitive(String targetKey, String resultKey, Performer performer) {
		super(performer);
		this.targetKey = targetKey;
		this.resultKey = resultKey;
	}
		public Continuation continuationWith(Continuation continuation) {
		AsyncPrimitiveContinuation k = continuation.makeAsyncPrimitiveContinuation(this);
		k.setTargetKey(targetKey);
		k.setPerformer(performer);
		k.setResultKey(resultKey);
		return k;
	}

}
