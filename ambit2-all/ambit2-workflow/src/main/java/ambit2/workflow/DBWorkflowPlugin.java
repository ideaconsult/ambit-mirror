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

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JComponent;

import nplugins.core.Introspection;
import nplugins.core.NPluginsException;
import nplugins.shell.INanoPlugin;
import nplugins.shell.application.NPluginsAction;
import nplugins.shell.application.TaskMonitor;
import nplugins.workflow.ExecuteWorkflowTask;
import nplugins.workflow.MWorkflowPlugin;
import ambit2.core.data.ClassHolder;
import ambit2.core.processors.batch.BatchProcessor;
import ambit2.workflow.library.LogoutSequence;
import ambit2.workflow.ui.StatusPanel;
import ambit2.workflow.ui.WorkflowConsolePanel;
import ambit2.workflow.ui.WorkflowViewPanel;

import com.microworkflow.process.Activity;
import com.microworkflow.process.Workflow;
import com.microworkflow.process.WorkflowContext;
import com.microworkflow.ui.WorkflowTools;

public abstract class DBWorkflowPlugin extends MWorkflowPlugin implements IMultiWorkflowsPlugin {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7190224146559024307L;
	protected List<ClassHolder> workflows;	
	public DBWorkflowPlugin() {
		super();
		workflows = new ArrayList<ClassHolder>();
		setModified(false);
	}
	public List<ClassHolder> getWorkflows() {
		return workflows;
	}	
	protected NPluginsAction<WorkflowContext,Void> runAction = null;
	
	protected NPluginsAction<WorkflowContext,Void> getAction() {
		if (runAction == null) {
			ExecuteWorkflowTask task = new ExecuteWorkflowTask(workflow,workflowContext) {
				@Override
				public WorkflowContext execute(TaskMonitor monitor)
						throws NPluginsException {
					WorkflowTools tools = new WorkflowTools() {
						@Override
						public Object process(Activity[] parentActivity,
								Activity activity) {
							activity.setNotExecuted();
							return activity;
						}
					};
					tools.traverseActivity(null,getWorkflow().getDefinition(),0,true);
					return super.execute(monitor);
				}
			};
		    runAction =  new NPluginsAction<WorkflowContext,Void>(
		             task,"Run",null);
		    runAction.setTaskMonitor(getApplicationContext().getTaskMonitor());				
		}
		return runAction;
	}
	
	public void runWorkflow(ClassHolder clazz) throws Exception  {
		Object o = Introspection.loadCreateObject(clazz.getClazz()); 
		if ( o instanceof Workflow) {
			getWorkflow().setDefinition(((Workflow) o).getDefinition());
			try {
				runAction.setEnabled(false);
				runAction.actionPerformed(null);
			} catch (Exception x) {
				throw new Exception(x);
			} finally {
				runAction.setEnabled(true);
			}
		}
	}		

	@Override
	protected WorkflowContext createWorkflowContext() {
		return new DBWorkflowContext();
	}

	public JComponent[] createOptionsComponent() {
		if (optionsComponent == null) {
			ExecuteWorkflowTask task = new ExecuteWorkflowTask(workflow,workflowContext);
		    NPluginsAction action =  new NPluginsAction<WorkflowContext,Void>(
		             task,"Run",null);
		    action.setTaskMonitor(getApplicationContext().getTaskMonitor());
		    Workflow logout = new Workflow();
			logout.setDefinition(new LogoutSequence());		    
			StatusPanel p = new StatusPanel(getWorkflowContext());
			Vector<String> props = new Vector<String>();	
			props.add(DBWorkflowContext.LOGININFO);
			props.add(DBWorkflowContext.DBCONNECTION_URI);
			props.add(DBWorkflowContext.DATASOURCE);
			p.setProperties(props);		    
			optionsComponent = new JComponent[] {
					new WorkflowViewPanel(workflow,action),p};
		} 
		return optionsComponent;
	}		
	@Override
	public JComponent[] createDetailsComponent() {
		if (detailsComponent == null) {
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
	        props.add(BatchProcessor.PROPERTY_BATCHSTATS);		
			reports.setProperties(props);
			detailsComponent =  new JComponent[] {reports};
		}
		return detailsComponent;
	}	
	public int compareTo(INanoPlugin o) {
		return getOrder()-o.getOrder();
	}
}
