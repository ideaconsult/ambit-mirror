/*
 * 	 
 *  Copyright (c) 2002, 2003 Dragos Manolescu (dam@micro-workflow.com)
 * 
 *  See the LICENSE file for licensing information.
 */

package com.microworkflow.process;

import com.microworkflow.execution.Continuation;

/**
 * @author dam
 *
 * The micro-workflow framework uses an activity-based process model. I have described most of the
 * control structures in my <a href="http://micro-workflow.com/PhDThesis/">thesis</a>.
 */
public abstract class Activity {
	public Sequence addStep(Activity anActivity) {
		Sequence seq=new Sequence();
		seq.addStep(this);
		seq.addStep(anActivity);
		return seq;
	}
	
	public abstract Continuation continuationWith(Continuation continuation);
	
	public abstract void computeStateFor(Continuation k);

}
