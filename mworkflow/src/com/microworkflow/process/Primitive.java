/*
 * 	 
 *  Copyright (c) 2002 Dragos Manolescu (dam@micro-workflow.com)
 * 
 *  See the LICENSE file for licensing information.
 */

package com.microworkflow.process;

import com.microworkflow.execution.*;

/**
 * @author dam
 *
 * This activity node represents a point where the control flow passes from the
 * control tier (i.e., workflow) to the logic tier (i.e., domain objects).
 * The execution in the logic tier takes place synchronously.
 */
public class Primitive extends Activity {
	protected Performer performer;
	protected String resultKey = null;
	protected String targetKey = null;

	public Primitive(Performer performer) {
		this.performer = performer;
	}
	
	public Primitive(String targetKey, Performer performer) {
		this(performer);
		this.targetKey = targetKey;
	}
	
	public Primitive(String targetKey, String resultKey, Performer performer) {
		this(targetKey,performer);
		this.resultKey = resultKey;
	}

	public Continuation continuationWith(Continuation continuation) {
		PrimitiveContinuation k = continuation.makePrimitiveContinuation(this);
		k.setTargetKey(targetKey);
		k.setPerformer(performer);
		k.setResultKey(resultKey);
		return k;
	}
	
	public static Primitive makeNop(String targetKey,String resultKey) {
		Primitive nop=new Primitive(targetKey,resultKey,
			new Performer() {
				public Object execute() {
					return getTarget();
				}});
		return nop;
	}
	
	public void computeStateFor(Continuation k) {
		// stateless
	}
	public boolean hasExecuted() {
		if (performer != null) return performer.hasExecuted();
		else return false;
	}

}
