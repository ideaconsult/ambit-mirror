/*
 * 	 
 *  Copyright (c) 2002, 2003 Dragos Manolescu (dam@micro-workflow.com)
 * 
 *  See the LICENSE file for licensing information.
 */
 
package com.microworkflow.process;

import com.microworkflow.execution.Continuation;

public class NullActivity extends Activity {

	public Continuation continuationWith(Continuation continuation) {
		return continuation.makeNullContinuation();
	}

	public void computeStateFor(Continuation k) {
		// stateless
	}

}
