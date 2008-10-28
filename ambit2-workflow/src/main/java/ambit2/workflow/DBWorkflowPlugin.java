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

package ambit2.workflow;

import java.util.Vector;

import javax.swing.JComponent;

import nplugins.shell.application.NPluginsAction;
import nplugins.workflow.ExecuteWorkflowTask;
import nplugins.workflow.MWorkflowPlugin;
import ambit2.workflow.ui.WorkflowConsolePanel;
import ambit2.workflow.ui.WorkflowViewPanel;

import com.microworkflow.process.WorkflowContext;

public abstract class DBWorkflowPlugin extends MWorkflowPlugin {
	
	
	
	@Override
	protected WorkflowContext createWorkflowContext() {
		return new DBWorkflowContext();
	}
	public JComponent[] createOptionsComponent() {
		ExecuteWorkflowTask task = new ExecuteWorkflowTask(workflow,workflowContext);
	    NPluginsAction action =  new NPluginsAction<WorkflowContext,Void>(
	             task,"Run",null);
	    action.setTaskMonitor(getApplicationContext().getTaskMonitor());			
		return new JComponent[] {new WorkflowViewPanel(workflow,action)};
	}	
	@Override
	public JComponent[] createDetailsComponent() {
		JComponent[] c = super.createDetailsComponent();
		/*
		 * smt weird happen if passing workflow at constructor - workflows can't be run!
		 */
		WorkflowConsolePanel reports = new WorkflowConsolePanel();
		reports.setWorkflowContext(getWorkflowContext());
		Vector<String> props = new Vector<String>();	
		props.add(DBWorkflowContext.LOGININFO);
		props.add(DBWorkflowContext.DATASET);
		props.add(DBWorkflowContext.ERROR);
		props.add(DBWorkflowContext.BATCHSTATS);
		reports.setProperties(props);
		return new JComponent[] {reports};
	}	

}
