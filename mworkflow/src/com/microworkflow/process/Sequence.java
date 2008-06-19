/*
 * 	 
 *  Copyright (c) 2002 Dragos Manolescu (dam@micro-workflow.com)
 * 
 *  See the LICENSE file for licensing information.
 */

package com.microworkflow.process;

import com.microworkflow.execution.Continuation;
import com.microworkflow.execution.SequenceContinuation;

public class Sequence extends CompositeActivity {
	
	public Sequence addStep(Activity step) {
		components.add(step);
		return this;
	}

	public Continuation continuationWith(Continuation continuation) {
		SequenceContinuation k = continuation.makeSequenceContinuation(this);
		k.resetState();
		return k;
	}
	
	public void computeStateFor(Continuation k) {
		((SequenceContinuation)k).setContinuations(getContinuationsForComponentsWith(k));
	}

	
}
