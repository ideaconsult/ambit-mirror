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

package nplugins.workflow;

import java.beans.PropertyChangeListener;

import javax.swing.ActionMap;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import nplugins.shell.INPApplicationContext;
import nplugins.shell.INPluginUI;
import nplugins.shell.INanoPlugin;
import nplugins.shell.application.NPluginsAction;

import com.microworkflow.process.Workflow;
import com.microworkflow.process.WorkflowContext;
import com.microworkflow.ui.WorkflowPanel;

public abstract class MWorkflowPlugin implements INanoPlugin {
	protected INPApplicationContext applicationContext;
	protected Workflow workflow;
	protected WorkflowContext workflowContext;
	protected JComponent[] detailsComponent = null;
	protected JComponent[] optionsComponent = null;
	protected INPluginUI<INanoPlugin> mainComponent = null;
	protected boolean modified;
	
	public boolean isModified() {
		return modified;
	}

	public void setModified(boolean modified) {
		this.modified = modified;
	}

	public MWorkflowPlugin() {
		setWorkflow(createWorkflow());
		setWorkflowContext(createWorkflowContext());
	}
	
	protected abstract Workflow createWorkflow();
	protected abstract WorkflowContext createWorkflowContext();
	
	public void clear() {
		
	}
	
	public JComponent[] createDetailsComponent() {
		if (detailsComponent == null) {
	        ActionMap a = getActions();
	        Object[] keys = a.keys();
	        JPanel p = new JPanel();
	        p.setLayout(new BoxLayout(p,BoxLayout.PAGE_AXIS));
	        for (int i=0; i < keys.length;i++)
	            p.add(new JButton(a.get(keys[i])));
	        detailsComponent = new JComponent[] {p};
		}
		return detailsComponent;
		
	}

	public JComponent[] createOptionsComponent() {
		if (optionsComponent == null)
			optionsComponent = new JComponent[] {new WorkflowPanel(getWorkflow())};
		return optionsComponent;
	}

	public ActionMap getActions() {
		ActionMap map = new ActionMap();
		ExecuteWorkflowTask task = new ExecuteWorkflowTask(getWorkflow(),getWorkflowContext());
	    NPluginsAction action =  new NPluginsAction<WorkflowContext,Void>(
	             task,"Run",null);
	    map.put(action,action);
	    
	    action.setTaskMonitor(getApplicationContext().getTaskMonitor());
	    return map;
	}
	

	public INPApplicationContext getApplicationContext() {
		return applicationContext;
	}


	public void setApplicationContext(INPApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public Workflow getWorkflow() {
		return workflow;
	}


	public void setWorkflow(Workflow workflow) {
		this.workflow = workflow;
	}


	public WorkflowContext getWorkflowContext() {
		return workflowContext;
	}


	public void setWorkflowContext(WorkflowContext workflowContext) {
		this.workflowContext = workflowContext;
		this.workflowContext.setName(toString());
	}
	public boolean canClose() {
		return !isModified();
	}
	public void close() {
		if (workflowContext!=null)
			workflowContext.clear();
		if (workflow!=null)
		for (PropertyChangeListener l : workflow.getPropertyChangeListeners())
			workflow.removePropertyChangeListener(l);
		
	}
}
