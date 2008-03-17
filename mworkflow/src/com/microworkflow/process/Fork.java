/*
 *
 *  Copyright (c) 2002, 2003 Dragos Manolescu (dam@micro-workflow.com)
 *
 *  See the LICENSE file for licensing information.
 */
 
package com.microworkflow.process;

import com.microworkflow.execution.Continuation;
import com.microworkflow.execution.ForkContinuation;

public class Fork extends CompositeActivity implements IEmbeddedActivity {
	protected JoinActivity join=null;
	Activity theJoin = new NullActivity();
	
	public Fork addBranch(Activity branch) {
		components.add(branch);
		return this;
	}

	public Continuation continuationWith(Continuation continuation) {
		ForkContinuation k = continuation.makeForkContinuation(this);
		if (join!=null) {
			join.setNumberOfBranches(components.size());
			theJoin = join;
		}
		k.resetState();
		return k;
	}

	public void setJoin(JoinActivity join) {
		this.join = join;
	}

	public void computeStateFor(Continuation k) {
		Continuation joinContinuation = theJoin.continuationWith(k.getNextContinuation());
		((ForkContinuation)k).setBranches(getContinuationsForComponentsWith(joinContinuation)); 
	}	
	public Activity getBody() {
	    return join;
	}
}
