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
import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import com.microworkflow.events.WorkflowContextListener;
import com.microworkflow.events.WorkflowEvent;
import com.microworkflow.process.Workflow;
import com.microworkflow.ui.actions.WFActionAnimate;
import com.microworkflow.ui.actions.WFActionExecute;
import com.microworkflow.ui.actions.WFCActionAnimate;

public class WorkflowMonitor extends JPanel {
	protected JSplitPane verticalPane;
	protected JSplitPane horizontalPane;
    /**
     * 
     */
    private static final long serialVersionUID = 3984550882955100106L;

    public WorkflowMonitor(IWorkflowListenerUI workflowListener,
    		WorkflowContextListener workflowControl,
            IWorkflowContextListenerUI workflowContextListener,
            IWorkflowFactory wff, IWorkflowContextFactory wfcf) {
        super(new BorderLayout());

        JPanel ph = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Results");
        label.setOpaque(true);
        label.setBackground(Color.orange);
        ph.add(label,BorderLayout.NORTH);
        ph.add(workflowContextListener.getUIComponent(),BorderLayout.CENTER);
        
        horizontalPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
        		ph,
        		new JScrollPane(new JTextArea()));
        
        JComponent leftpane = createWorkflowPane(workflowListener,workflowControl,wff,wfcf);
        

        
        /*
        label = new JLabel("Workflow");
        label.setOpaque(true);
        label.setBackground(Color.orange);
        p.add(label,BorderLayout.NORTH);
        */
        /*
        p.add(workflowListener.getUIComponent(),BorderLayout.NORTH);
        if (workflowControl != null)
        	p.add(workflowControl.getUIComponent(),BorderLayout.CENTER);
        p.add(createToolBar(wff,wfcf),BorderLayout.SOUTH);
        	*/
        verticalPane = new JSplitPane(
        		JSplitPane.HORIZONTAL_SPLIT,
        		leftpane,
        		horizontalPane
        		);

        
        add(verticalPane,BorderLayout.CENTER);

        

        
        WorkflowProgressBar pb = new WorkflowProgressBar();
        Workflow wf = wff.getWorkflow();
        wf.addPropertyChangeListener(WorkflowEvent.WF_EXECUTE,pb);
        wf.addPropertyChangeListener(WorkflowEvent.WF_COMPLETE,pb);
        wf.addPropertyChangeListener(WorkflowEvent.WF_START,pb);

        add(pb,BorderLayout.SOUTH);
        
    }
    
    protected JComponent createWorkflowPane(IWorkflowListenerUI workflowListener,
    		WorkflowContextListener workflowControl,
            IWorkflowFactory wff, IWorkflowContextFactory wfcf) {
    	JPanel pane = new JPanel();
    	pane.setLayout(new BoxLayout(pane,BoxLayout.PAGE_AXIS));
        /*

        JTaskPane p = new JTaskPane();
        JTaskPaneGroup workflowgroup = new JTaskPaneGroup();
        workflowgroup.setName("Workflow");
        workflowgroup.setExpanded(true);
        workflowgroup.setTitle("Workflow");
        workflowgroup.setSpecial(true);	 
        workflowgroup.add(workflowListener.getUIComponent());
        workflowgroup.add(createToolBar(wff,wfcf));
        p.add(workflowgroup);
        */
        
        pane.add(workflowListener.getUIComponent());
        pane.add(createToolBar(wff,wfcf));
        
        if (workflowControl != null) {
        	if (workflowControl instanceof IWorkflowContextListenerUI) {
        		/*
	        	JTaskPaneGroup control = new JTaskPaneGroup();
	        	control.setName("Workflow control");
	        	control.setExpanded(true);
	        	control.setTitle("Details");
	        	control.setSpecial(true);	 
	        	control.add(((IWorkflowContextListenerUI)workflowControl).getUIComponent());
	            p.add(control);
	            */
	            pane.add(((IWorkflowContextListenerUI)workflowControl).getUIComponent());
        	}
        }        
        return pane;

    }
    protected JToolBar createToolBar(IWorkflowFactory wff, IWorkflowContextFactory wfcf) {
        JToolBar tb = new JToolBar();
        tb.setFloatable(false);
        tb.add(new WFActionExecute(wff,wfcf));
        final WFActionAnimate wfaa = new WFActionAnimate(wff);
        JToggleButton b = new JToggleButton(wfaa);
        b.addItemListener(new ItemListener()  {
            public void itemStateChanged(ItemEvent arg0) {
                wfaa.setAnimate(!wfaa.isAnimate());
            }
        });
        tb.add(b);
        
        final WFCActionAnimate wfca = new WFCActionAnimate(wfcf);
        b = new JToggleButton(wfca);
        b.addItemListener(new ItemListener()  {
            public void itemStateChanged(ItemEvent arg0) {
                wfca.setAnimate(!wfca.isAnimate());
            }
        });
        
        tb.add(b);
        return tb;
    }
}
