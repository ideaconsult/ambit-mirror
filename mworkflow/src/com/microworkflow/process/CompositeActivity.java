/*
 *
 *  Copyright (c) 2002, 2003 Dragos Manolescu (dam@micro-workflow.com)
 *
 *  See the LICENSE file for licensing information.
 */

package com.microworkflow.process;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.microworkflow.execution.Continuation;

public abstract class CompositeActivity extends Activity implements ICompositeActivity {
	protected ArrayList<Activity> components = new ArrayList<Activity>();

	/**
	 * Delay generating the set of continuations for a CompositeActivity. 
	 * This will prevent StackOverflowError with loops in the Workflow.
	 * 
	 * @author Ringo De Smet
	 */
	class DelayedContinuationIterator implements Iterator {

		protected Continuation parent;
		protected Iterator componentsIterator;

		public DelayedContinuationIterator(Continuation parent,List components) {
			this.parent = parent;
			this.componentsIterator = components.iterator();
		}
		public boolean hasNext() {
			return this.componentsIterator.hasNext();
		}
		public Object next() {
			return ((Activity) this.componentsIterator.next()).continuationWith(this.parent);
		}
		public void remove() {
			// do nothing.
		}
	}

	protected Iterator getContinuationsForComponentsWith(Continuation parent) {
		return new DelayedContinuationIterator(parent, this.components);
	}
	@Override
	public String toString() {
	    Iterator<Activity> i = components.iterator();
	    StringBuffer b = new StringBuffer();
        b.append(super.toString());
        String del = "{";
        
        while (i.hasNext()) {
            b.append(del);
            b.append(i.next());
            del = ",";
        }
        b.append("}");
        return b.toString();
	}
    public ArrayList<Activity> getComponents() {
        return components;
    }
}
