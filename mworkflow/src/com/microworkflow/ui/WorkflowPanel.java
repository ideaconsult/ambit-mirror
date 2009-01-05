/* TestUI.java
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

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import com.microworkflow.events.WorkflowEvent;
import com.microworkflow.process.Activity;
import com.microworkflow.process.Sequence;
import com.microworkflow.process.Workflow;

public class WorkflowPanel extends JScrollPane implements IWorkflowListenerUI {
    protected Workflow workflow;
    protected WorkflowTableModel wftm;
    protected JTable table;
    protected boolean animate = true;
    protected String title="Workflow";    
    /**
     * 
     */
    private static final long serialVersionUID = -9177580035692770353L;
    public WorkflowPanel(Workflow wf) {
        super();
        wftm = new WorkflowTableModel(null);
        setWorkflow(wf);
        

        table = new JTable(wftm) {
			public void createDefaultColumnsFromModel() {
				TableModel m = getModel();
				if (m != null) {
					// Remove any current columns
					TableColumnModel cm = getColumnModel();
					while (cm.getColumnCount() > 0) {
						cm.removeColumn(cm.getColumn(0));
					}
					// Create new columns from the data model info
					final int columnSize[] = { 32, 32, 0};
					for (int i = 0; i < m.getColumnCount(); i++) {
						TableColumn newColumn = new TableColumn(i);
						if (columnSize[i]>0) {
							newColumn.setMaxWidth(columnSize[i]);
						}
						addColumn(newColumn);
					}
				}

			};
		};        
		
		table.setRowHeight(32);
        table.getColumnModel().getColumn(0).setMaxWidth(32);
        table.getColumnModel().getColumn(1).setMaxWidth(32);
        table.setShowVerticalLines(false);
        setViewportView(table);
        setPreferredSize(new Dimension(200,200));
        setMaximumSize(new Dimension(200,200));
    }
    public void setWorkflow(Workflow wf) {
        if (this.workflow != null)
            this.workflow.removePropertyChangeListener(this);

        
        wftm.setActivities(null);
        this.workflow = wf;
        if (wf != null) {
     	   final ArrayList<Activity> activities = getActivityList(wf.getDefinition());
            workflow.addPropertyChangeListener(this);    	   
     	   if (activities.size()>0 ) {
 	           wftm.setActivities(activities);

 	      	   title = wf.toString();
 	      	   return;
     	   }
        } 
        title="No workflow defined!";
     }
    public ArrayList<Activity> getActivityList(Activity activity) {
        final ArrayList<Activity> activities = new ArrayList<Activity>();
        WorkflowTools tools = new WorkflowTools() {
     	   @Override
     	public Object process(Activity[] parentActivity, Activity activity) {
     		if (activity instanceof Sequence) ;
     		else
     			activities.add(activity);
     		return activity;
     	}
        };
        if (activity != null) 
     	   tools.traverseActivity(null,activity,0,true);
        return activities;
    }    
    public JComponent getUIComponent() {
        return this;
    }
    public void propertyChange(PropertyChangeEvent arg0) {
        if (WorkflowEvent.WF_DEFINITION.equals(arg0.getPropertyName()))
            if (arg0.getNewValue() instanceof Activity) {
            	title = arg0.toString();
            	wftm.setActivities(null);
            	wftm.setActivities(getActivityList((Activity )arg0.getNewValue()));
            } else ;
        else if (WorkflowEvent.WF_EXECUTE.equals(arg0.getPropertyName())) {
            if (animate) {
                int index = wftm.findRow((Activity)arg0.getNewValue());
                table.scrollRectToVisible(table.getCellRect(index, 0, true));
            } else ;
        } else if (WorkflowEvent.WF_ANIMATE.equals(arg0.getPropertyName())) {
             setAnimate((Boolean) arg0.getNewValue());
        } else if (WorkflowEvent.WF_COMPLETE.equals(arg0.getPropertyName())) {
            wftm.setSelected(-1);
            //table.scrollRectToVisible(table.getCellRect(0, 0, true));
        }

        
    }
        public synchronized boolean isAnimate() {
            return animate;
        }
        public synchronized void setAnimate(boolean animate) {
            this.animate = animate;
        }        
}

