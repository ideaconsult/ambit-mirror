/*
 * 	 
 *  Copyright (c) 2002 Dragos Manolescu (dam@micro-workflow.com)
 * 
 *  See the LICENSE file for licensing information.
 */
package com.microworkflow.test;
import java.lang.String;
public interface DomainObjectInterface {
	String getString();
	void setString(String string);
	String concat(String aString);
	String asUppercase();
}