/*
 * 	 
 *  Copyright (c) 2002, 2003 Dragos Manolescu (dam@micro-workflow.com)
 * 
 *  See the LICENSE file for licensing information.
 */

package com.microworkflow.process;

import com.microworkflow.execution.AndJoinContinuation;
import com.microworkflow.execution.Continuation;

public class AndJoin extends JoinActivity {

	public Continuation continuationWith(Continuation continuation) {
		AndJoinContinuation k = continuation.makeAndJoinContinuation(this);
		k.setNumberOfInputs(numberOfBranches);
		k.resetState();
		return k;
	}
	
	public void computeStateFor(Continuation k) {
		((AndJoinContinuation)k).setBody(body.continuationWith(k.getNextContinuation()));
	}

}
