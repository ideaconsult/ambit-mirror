/* ValueLatch.java
 * Author: Nina Jeliazkova
 * Date: Apr 15, 2008 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2008  Nina Jeliazkova
 * 
 * Contact: nina@acad.bg
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package com.microworkflow.process;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


public class ValueLatch<T> {
    protected long timeout_seconds = 5;
    public long getTimeout_seconds() {
		return timeout_seconds;
	}

	public void setTimeout_seconds(long timeout_seconds) {
		this.timeout_seconds = timeout_seconds;
	}
	private T value = null;
    private final CountDownLatch done = new CountDownLatch(1);

    public boolean isSet() {
        return (done.getCount() == 0);
    }

    public synchronized void setValue(T newValue) {
        if (!isSet()) {
            value = newValue;
            done.countDown();
        }
    }

    public T getValue() throws InterruptedException {
        if (getTimeout_seconds()<0)
        	done.await();
        else
        	done.await(getTimeout_seconds(),TimeUnit.SECONDS);
    
        synchronized (this) {
            return value;
        }
    }
    @Override
    public String toString() {
    	if (isSet()) return value.toString();
    	else return "Waiting for user input";
    }
}

