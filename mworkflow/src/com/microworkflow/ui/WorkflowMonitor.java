/* WorkflowMonitor.java
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

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import com.microworkflow.events.WorkflowEvent;
import com.microworkflow.process.Workflow;
import com.microworkflow.ui.actions.WFActionAnimate;
import com.microworkflow.ui.actions.WFActionExecute;

public class WorkflowMonitor extends JSplitPane {

    /**
     * 
     */
    private static final long serialVersionUID = 3984550882955100106L;

    public WorkflowMonitor(IWorkflowListenerUI workflowListener,
            IWorkflowContextListenerUI workflowContextListener,
            IWorkflowFactory wff, IWorkflowContextFactory wfcf) {
        super();
        JPanel p = new JPanel(new BorderLayout());
        p.add(workflowListener.getUIComponent(),BorderLayout.CENTER);
        //p.add(navigator.getUIComponent(),BorderLayout.NORTH);
        WorkflowProgressBar pb = new WorkflowProgressBar();
        Workflow wf = wff.getWorkflow();
        //WorkflowContext wfc = wfcf.getWorkflowContext();
        wf.addPropertyChangeListener(WorkflowEvent.WF_EXECUTE,pb);
        wf.addPropertyChangeListener(WorkflowEvent.WF_COMPLETE,pb);
        wf.addPropertyChangeListener(WorkflowEvent.WF_START,pb);
        p.add(pb,BorderLayout.SOUTH);
        
        JToolBar tb = new JToolBar();
        tb.add(new WFActionExecute(wff,wfcf));
        final WFActionAnimate wfaa = new WFActionAnimate(wff);
        JToggleButton b = new JToggleButton(wfaa);
        b.addItemListener(new ItemListener()  {
            public void itemStateChanged(ItemEvent arg0) {
                wfaa.setAnimate(!wfaa.isAnimate());
            }
        });
        tb.add(b);
        p.add(tb,BorderLayout.NORTH);
        setLeftComponent(p);
        setRightComponent(workflowContextListener.getUIComponent());
    }
    
}
