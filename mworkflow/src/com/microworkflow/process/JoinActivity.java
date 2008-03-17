/*
 * 	 
 *  Copyright (c) 2002, 2003 Dragos Manolescu (dam@micro-workflow.com)
 * 
 *  See the LICENSE file for licensing information.
 */

package com.microworkflow.process;

public abstract class JoinActivity extends Activity implements IEmbeddedActivity {
	protected Activity body=null;
	protected int numberOfBranches=0;

	public void setBody(Activity body) {
		this.body = body;
	}
	public void setNumberOfBranches(int numberOfBranches) {
		this.numberOfBranches = numberOfBranches;
	}
	public Activity getBody() {
	    return body;
	}
}
