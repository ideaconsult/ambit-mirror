/*
Copyright (C) 2005-2008  

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
*/

package com.microworkflow.process;

/**
 * An instance can be put into workflow context and used by the user interface. 
 * The user enters data into the value part and then it is used to open the latch by latch.setValue().
 * @author nina
 *
 * @param <T>
 */
public class ValueLatchPair<T> {
	protected T value;
	protected ValueLatch<T> latch;
	
	public ValueLatchPair() {
		latch = new ValueLatch<T>();
	}
	public ValueLatchPair(T value) {
		this();
		setValue(value);
	}
	
	public ValueLatch<T> getLatch() {
		return latch;
	}
	public void setLatch(ValueLatch<T> latch) {
		this.latch = latch;
	}
	public T getValue() {
		return value;
	}
	public void setValue(T value) {
		this.value = value;
	}
	public Class getType() {
		return value.getClass();
	}
	
}


