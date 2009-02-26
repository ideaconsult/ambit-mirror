/*
 * 	 
 *  Copyright (c) 2002 Dragos Manolescu (dam@micro-workflow.com)
 * 
 *  See the LICENSE file for licensing information.
 */

package com.microworkflow.process;

import com.microworkflow.execution.ConditionalContinuation;
import com.microworkflow.execution.Continuation;

/**
 * @author dam
 *
 * This activity node represents an if-then-else conditional; not both branches are
 * required. The calling continuation replaces the missing branch.
 */
public class Conditional extends Activity implements IConditionalActivity {
	protected TestCondition test;
	protected Activity thenBranch;
	protected Activity elseBranch;
	
	public Conditional(TestCondition aClosure, Activity thenBranch, Activity elseBranch) {
		this.test=aClosure;
		this.thenBranch=thenBranch;
		this.elseBranch=elseBranch;
	}
	
	public TestCondition getTestCondition() {
		return test;
	}

	public Conditional(TestCondition aClosure, Activity thenBranch) {
		this(aClosure,thenBranch,null);
	}
	
	public Continuation continuationWith(Continuation continuation) {
		ConditionalContinuation k = continuation.makeConditionalContinuation(this);
		k.setTestCondition(test);
		k.resetState();
		return k;
	}
	
	public void setElseBranch(Activity elseBranch) {
		this.elseBranch = elseBranch;
	}
	
	public void setThenBranch(Activity thenBranch) {
		this.thenBranch = thenBranch;
	}

	public void computeStateFor(Continuation k) {
		Continuation next=k.getNextContinuation();
		ConditionalContinuation ck=(ConditionalContinuation)k;
		if (thenBranch != null) {
			ck.setThenContinuation(thenBranch.continuationWith(next));
		} else {
			ck.setThenContinuation(next);
		}
		if (elseBranch != null) {
			ck.setElseContinuation(elseBranch.continuationWith(next));
		} else {
			ck.setElseContinuation(next);
		}		
	}

    public Activity getElseBranch() {
        return elseBranch;
    }

    public Activity getThenBranch() {
        return thenBranch;
    }
    public void setNotExecuted() {
    	if (test != null) test.setNotExecuted();
    }
}
