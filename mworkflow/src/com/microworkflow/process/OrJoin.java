/*
 * 	 
 *  Copyright (c) 2002, 2003 Dragos Manolescu (dam@micro-workflow.com)
 * 
 *  See the LICENSE file for licensing information.
 */

package com.microworkflow.process;

import com.microworkflow.execution.Continuation;
import com.microworkflow.execution.OrJoinContinuation;

public class OrJoin extends JoinActivity {

	public Continuation continuationWith(Continuation continuation) {
		OrJoinContinuation k = continuation.makeOrJoinContinuation(this);
		k.setNumberOfInputs(numberOfBranches);
		k.resetState();
		return k;
	}
	
	public void computeStateFor(Continuation k) {
		((OrJoinContinuation)k).setBody(body.continuationWith(k.getNextContinuation()));
	}
}
