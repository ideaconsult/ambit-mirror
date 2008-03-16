/*
 * 	 
 *  Copyright (c) 2003 Dragos Manolescu (dam@micro-workflow.com)
 * 
 *  See the LICENSE file for licensing information.
 */
package com.microworkflow.pcexample;

/**
 * @author dam
 */
public class Number extends Object {

	protected Double value;

	public Number(Double value) {
		this.value = value;
	}

	public Double getValue() {
		return value;
	}

}
