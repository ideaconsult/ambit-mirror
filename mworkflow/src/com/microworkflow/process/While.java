/*
 * 	 
 *  Copyright (c) 2002, 2003 Dragos Manolescu (dam@micro-workflow.com)
 * 
 *  See the LICENSE file for licensing information.
 */

package com.microworkflow.process;

import com.microworkflow.execution.Continuation;
import com.microworkflow.execution.WhileContinuation;

public class While extends Activity implements IEmbeddedActivity {
	protected Activity body;
	protected TestCondition test;

	public TestCondition getTestCondition() {
		return test;
	}
	public Continuation continuationWith(Continuation continuation) {
		WhileContinuation k = continuation.makeRepetitionContinuation(this);
		k.setTestCondition(test);
		k.resetState();
		return k;
	}
	public void setBody(Activity body) {
		this.body = body;
	}

	public void setTestCondition(TestCondition aClosure) {
		this.test = aClosure;
	}
	
	public void computeStateFor(Continuation k) {
		((WhileContinuation)k).setBody(body.continuationWith(k));	
	}
    public Activity getBody() {
        return body;
    }
}
