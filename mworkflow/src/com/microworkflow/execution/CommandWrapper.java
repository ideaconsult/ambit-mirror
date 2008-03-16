/*
 * 	 
 *  Copyright (c) 2002 Dragos Manolescu (dam@micro-workflow.com)
 * 
 *  See the LICENSE file for licensing information.
 */

package com.microworkflow.execution;

public class CommandWrapper implements Runnable {
	protected Closure command;
	
	public CommandWrapper(Closure command) {
		this.command=command;
	}
	public void run() {
		command.evaluate();
	}
}

