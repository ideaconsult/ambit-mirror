/*
 * 	 
 *  Copyright (c) 2003 Dragos Manolescu (dam@micro-workflow.com)
 * 
 *  See the LICENSE file for licensing information.
 */
package com.microworkflow.process;

import com.microworkflow.execution.Continuation;
import com.microworkflow.execution.IterativeContinuation;
import com.microworkflow.execution.Performer;

/**
 * @author dam
 *
 * This activity node obtains the components of a domain object (e.g., a premium) and
 * then executes its body on each component (e.g., premium charges or credits).
 */
public class Iterative extends Activity {
	protected String targetKey;
	protected String iterationTargetKey;
	protected Performer performer;
	protected Activity body;
	
	public Iterative(String targetKey,Performer performer,String iterationTargetKey,Activity body) {
		this.targetKey=targetKey;
		this.iterationTargetKey=iterationTargetKey;
		this.performer=performer;
		this.body=body;
	}

	public Continuation continuationWith(Continuation continuation) {
		IterativeContinuation k=continuation.makeIterativeContinuation(this);
		k.setTargetKey(targetKey);
		k.setPerformer(performer);
		k.setIterationTargetKey(iterationTargetKey);
		k.resetState();
		return k;
	}

	public void computeStateFor(Continuation k) {
		((IterativeContinuation)k).setBody(body.continuationWith(k));		
	}

}
