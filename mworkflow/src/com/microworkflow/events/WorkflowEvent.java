/* WorkflowEvent.java
 * Author: Nina Jeliazkova
 * Date: Mar 16, 2008 
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

package com.microworkflow.events;

import java.beans.PropertyChangeEvent;

import com.microworkflow.execution.Continuation;

public class WorkflowEvent extends PropertyChangeEvent {
    public final static String WF_START = "com.microworkflow.process.Workflow.START";
    public final static String WF_DEFINITION = "com.microworkflow.process.Workflow.DEFINITION";
    public final static String WF_EXECUTE = "com.microworkflow.process.Workflow.EXECUTION";
    public final static String WF_COMPLETE = "com.microworkflow.process.Workflow.COMPLETE";
    public final static String WF_ABORTED = "com.microworkflow.process.Workflow.ABORTED";    
    public final static String WF_RESUMED = "com.microworkflow.process.Workflow.RESUMED";    
    public final static String WF_ANIMATE = "com.microworkflow.process.Workflow.ANIMATE";

    /**
     * 
     */
    private static final long serialVersionUID = -2992459826163395277L;

    public WorkflowEvent(Object source, String propertyName, Object oldValue, Object newValue) {
        super(source,propertyName,oldValue,newValue);
    }
    @Override
    public String toString() {
        Object o2 = getNewValue();
        if (getNewValue() instanceof Continuation) {
            o2 = ((Continuation)getNewValue()).getActivity();
        }
        Object o1 = getOldValue();
        if (getOldValue() instanceof Continuation) {
            o1 = ((Continuation)getOldValue()).getActivity();
        }                
        return "WF:\t"+getPropertyName()+"\t"+o1+"-->\t"+o2;
    }

}
