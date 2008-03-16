/*
 * 	 
 *  Copyright (c) 2002 Dragos Manolescu (dam@micro-workflow.com)
 * 
 *  See the LICENSE file for licensing information.
 */
package com.microworkflow.test;

import java.util.ArrayList;
import java.util.Iterator;

public class DomainObject implements DomainObjectInterface {
	public static String TEST_STRING = "this is a string";
	protected String string;
	protected String slotA = "A", slotB = "B", slotC = "C";
	protected int counter = 0;
	
	public DomainObject(String str) {
		string = str;
	}
	public String getString() {
		return string;
	}
	public void setString(String string) {
		this.string = string;
	}	
	public String concat(String aString) {
		return string.concat(aString);
	}	
	public String asUppercase() {
		return string.toUpperCase();
	}
	public String getSlotA() {
		return slotA;
	}
	public String getSlotB() {
		return slotB;
	}
	public String getSlotC() {
		return slotC;
	}
	public void setSlotA(String slotA) {
		this.slotA = slotA;
	}
	public void setSlotB(String slotB) {
		this.slotB = slotB;
	}
	public void setSlotC(String slotC) {
		this.slotC = slotC;
	}
	public String getAllSlots() {
		return slotA+slotB+slotC;
	}
	public int getCounter() {
		return counter;
	}
	public void setCounter(int counter) {
		this.counter = counter;
	}
	public Iterator getComponents() {
		ArrayList components=new ArrayList(4);
		components.add("one");
		components.add("two");
		components.add("three");
		components.add("four");
		return components.iterator();
	}

}
