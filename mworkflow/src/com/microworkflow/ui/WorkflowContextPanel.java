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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import com.microworkflow.events.WorkflowContextEvent;
import com.microworkflow.process.WorkflowContext;
import com.microworkflow.ui.MapTableModel.Mode;
import com.microworkflow.ui.actions.WFCActionAnimate;

public class WorkflowContextPanel extends JPanel implements
        IWorkflowContextListenerUI {
    protected IWorkflowContextFactory wfcfactory;
    protected MapTableModel mtm;
    protected boolean animate = false;
    protected Vector<String> properties;
    /**
     * 
     */
    private static final long serialVersionUID = 2854619268267349642L;
    public WorkflowContextPanel(IWorkflowContextFactory wfcfactory) {
        super(new BorderLayout());
        
        properties = new Vector<String>();
        properties.add("Result");
        properties.add("Molecule");
        setWfcfactory(wfcfactory);
        mtm = new MapTableModel();
        mtm.setMode(Mode.labels_in_columns);
        JTable table = new JTable(mtm);
        //table.setPreferredSize(new Dimension(200,200));
        add(new JScrollPane(table), BorderLayout.CENTER);

        final WFCActionAnimate wfaa = new WFCActionAnimate(wfcfactory);
        JToggleButton b = new JToggleButton(wfaa);
        b.addItemListener(new ItemListener()  {
            public void itemStateChanged(ItemEvent arg0) {
                wfaa.setAnimate(!wfaa.isAnimate());
            }
        });
        
        JToolBar tb = new JToolBar();
        tb.add(b);
        add(tb, BorderLayout.SOUTH);
        
        
    }
    public JComponent getUIComponent() {
        return this;
    }

    public void propertyChange(PropertyChangeEvent arg0) {
        if (WorkflowContextEvent.WF_ANIMATE.equals(arg0.getPropertyName()))
            setAnimate((Boolean) arg0.getNewValue());
        else if (animate) {
            mtm.setMap(getWorkflowContext());
            mtm.keyChange(arg0.getPropertyName(),arg0.getNewValue());
        }
    }

    protected synchronized WorkflowContext getWorkflowContext() {
        return getWfcfactory().getWorkflowContext();
    }

    private synchronized void setWorkflowContext(WorkflowContext wfc) {
        clear();
        if (this.getWorkflowContext() != null) {
            for (String p : properties)
                this.getWorkflowContext().removePropertyChangeListener(p,this);
            this.getWorkflowContext().removePropertyChangeListener(WorkflowContextEvent.WF_ANIMATE,this);            
        }
        

        if (wfc != null) {
            for (String p : properties)
                this.getWorkflowContext().addPropertyChangeListener(p,this);
            this.getWorkflowContext().addPropertyChangeListener(WorkflowContextEvent.WF_ANIMATE,this);   
        }
     }
    public void clear() {
        if (mtm != null)
        mtm.setMap(null);
        
    }
    public synchronized boolean isAnimate() {
        return animate;
    }
    public synchronized void setAnimate(boolean animate) {
        this.animate = animate;
        if (animate)
            mtm.setMap(getWfcfactory().getWorkflowContext());            
    }
    public synchronized Vector<String> getProperties() {
        return properties;
    }
    public synchronized void setProperties(Vector<String> properties) {
        this.properties = properties;
    }

    public synchronized IWorkflowContextFactory getWfcfactory() {
        return wfcfactory;
    }
    public synchronized void setWfcfactory(IWorkflowContextFactory wfcfactory) {
        this.wfcfactory = wfcfactory;
        setWorkflowContext(wfcfactory.getWorkflowContext());
    }
}
