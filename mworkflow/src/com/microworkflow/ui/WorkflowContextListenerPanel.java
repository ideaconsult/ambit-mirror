/* WorkflowContextPanel.java
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

package com.microworkflow.ui;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JPanel;

import com.microworkflow.events.WorkflowContextEvent;
import com.microworkflow.process.WorkflowContext;

public abstract class WorkflowContextListenerPanel extends JPanel implements
        IWorkflowContextListenerUI {
    protected IWorkflowContextFactory wfcfactory;
    protected WorkflowContext context;
    protected boolean animate = false;
    protected Vector<String> properties;
    /**
     * 
     */
    private static final long serialVersionUID = 2854619268267349642L;
    public WorkflowContextListenerPanel() {
        this(null);
    }    
    public WorkflowContextListenerPanel(IWorkflowContextFactory wfcfactory) {
        super(new BorderLayout());
        
        properties = new Vector<String>();
        properties.add("Result");
        properties.add("Molecule");
        setWfcfactory(wfcfactory);
    }
    public JComponent getUIComponent() {
        return this;
    }

    public void propertyChange(PropertyChangeEvent arg0) {
        if (WorkflowContextEvent.WF_ANIMATE.equals(arg0.getPropertyName()))
            setAnimate((Boolean) arg0.getNewValue());
        else if (animate) {
        	animate(arg0);
        }
    }
    protected abstract void animate(PropertyChangeEvent arg0);
    protected synchronized WorkflowContext getWorkflowContext() {
    	if (wfcfactory!= null)
    		return getWfcfactory().getWorkflowContext();
    	else return context;
    }

    public synchronized void setWorkflowContext(WorkflowContext wfc) {
        clear();
        if (this.getWorkflowContext() != null) {
            for (String p : properties)
                this.getWorkflowContext().removePropertyChangeListener(p,this);
            this.getWorkflowContext().removePropertyChangeListener(WorkflowContextEvent.WF_ANIMATE,this);            
        }
        
        this.context = wfc;

        if (context != null) {
            for (String p : properties)
                this.context.addPropertyChangeListener(p,this);
            this.context.addPropertyChangeListener(WorkflowContextEvent.WF_ANIMATE,this);   
        }
     }
    public abstract void clear() ;
    public synchronized boolean isAnimate() {
        return animate;
    }
    public synchronized void setAnimate(boolean animate) {
        this.animate = animate;
    }
    public synchronized Vector<String> getProperties() {
        return properties;
    }
    public synchronized void setProperties(Vector<String> properties) {
        if (this.getWorkflowContext() != null) {
            for (String p : this.properties)
                this.getWorkflowContext().removePropertyChangeListener(p,this);
            this.getWorkflowContext().removePropertyChangeListener(WorkflowContextEvent.WF_ANIMATE,this);            
        }    	
        this.properties = properties;
        if (this.getWorkflowContext() != null) {
            for (String p : properties)
                this.getWorkflowContext().addPropertyChangeListener(p,this);
            this.getWorkflowContext().addPropertyChangeListener(WorkflowContextEvent.WF_ANIMATE,this);   
        }
    }

    public synchronized IWorkflowContextFactory getWfcfactory() {
        return wfcfactory;
    }
    public synchronized void setWfcfactory(IWorkflowContextFactory wfcfactory) {
        this.wfcfactory = wfcfactory;
        if (wfcfactory != null)
        	setWorkflowContext(wfcfactory.getWorkflowContext());
    }
}
