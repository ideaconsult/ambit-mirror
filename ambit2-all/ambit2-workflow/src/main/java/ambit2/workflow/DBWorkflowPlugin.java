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

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.JComponent;

import nplugins.core.Introspection;
import nplugins.core.NPluginsException;
import nplugins.shell.INanoPlugin;
import nplugins.shell.application.NPluginsAction;
import nplugins.shell.application.TaskMonitor;
import nplugins.workflow.ExecuteWorkflowTask;
import nplugins.workflow.MWorkflowPlugin;
import ambit2.base.data.ClassHolder;
import ambit2.base.processors.batch.BatchProcessor;
import ambit2.workflow.library.LogoutSequence;
import ambit2.workflow.ui.MultiWorkflowsPanel;
import ambit2.workflow.ui.StatusPanel;
import ambit2.workflow.ui.WorkflowConsolePanel;
import ambit2.workflow.ui.WorkflowViewPanel;

import com.microworkflow.events.WorkflowEvent;
import com.microworkflow.events.WorkflowListener;
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
	protected NPluginsAction<WorkflowContext,Void> runAction = null;
	private boolean running = false;
	public synchronized boolean isRunning() {
		return running;
	}

	public synchronized void setRunning(boolean running) {
		System.out.println(running);
		this.running = running;
		getAction().setEnabled(!running);		
	}

	public DBWorkflowPlugin() {
		super();
		workflows = new ArrayList<ClassHolder>();
		setModified(false);

		getWorkflow().addPropertyChangeListener(new WorkflowListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				System.out.println(evt);
				if (evt.getPropertyName().equals(WorkflowEvent.WF_COMPLETE)) {

					setRunning(false);
				}
				if (evt.getPropertyName().equals(WorkflowEvent.WF_START)) {

					setRunning(true);
				}
				if (evt.getPropertyName().equals(WorkflowEvent.WF_ABORTED)) {

					setRunning(false);
				}				
			}
		});		
	}
	
	@Override
	public boolean canClose() {
		return !running && !isModified();
	}
	@Override
	protected Workflow createWorkflow() {
		return new MyWorkflow();
	}	
	public List<ClassHolder> getWorkflows() {
		return workflows;
	}	
	
	
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
	
	public synchronized void runWorkflow(ClassHolder clazz) throws Exception  {
		if (isRunning()) {
			getWorkflowContext().put(DBWorkflowContext.ERROR, "Unable to start '"+clazz.getTitle()+ "' workflow! \n'"+ getWorkflow() + "' workflow already running!");
			return;
		}
		Object o = Introspection.loadCreateObject(clazz.getClazz());
		if ( o instanceof Workflow) 
			if ( getWorkflow() instanceof MyWorkflow)
				((MyWorkflow)getWorkflow()).setWorkflow((Workflow)o);			
			else
				getWorkflow().setDefinition(((Workflow) o).getDefinition());
		else throw new Exception("Not an workflow");
		
		
			Action action = getAction();
			if (action == null)
				throw new Exception("Unable to run workflow!");
			try {
				getWorkflowContext().put(DBWorkflowContext.BATCHSTATS,"Workflow '"+clazz.getTitle()+"'");
				//System.out.println("Started "+o);
				action.setEnabled(false);
				action.actionPerformed(null);
				//System.out.println("Done "+o);
			} catch (Exception x) {
				getWorkflowContext().put(DBWorkflowContext.BATCHSTATS,"Error when running '"+clazz.getTitle()+"' : "+x.getMessage());
				throw new Exception(x);
			} finally {
				action.setEnabled(true);
			
				o = null;
			}
		
	}		

	@Override
	protected WorkflowContext createWorkflowContext() {
		return new DBWorkflowContext();
	}
	public JComponent[] createOptionsComponent() {
		if (optionsComponent == null) {
			if (this instanceof IMultiWorkflowsPlugin) {
				
				Workflow logout = new Workflow();
				logout.setDefinition(new LogoutSequence());
				StatusPanel p = new StatusPanel(getWorkflowContext());
				Vector<String> props = new Vector<String>();	
				props.add(DBWorkflowContext.LOGININFO);
				props.add(DBWorkflowContext.DBCONNECTION_URI);
				props.add(DBWorkflowContext.DATASOURCE);
				p.setProperties(props);
				getWorkflowContext().addPropertyChangeListener(p);
				
				MultiWorkflowsPanel mw = new MultiWorkflowsPanel((IMultiWorkflowsPlugin)this,32);

				optionsComponent =  new JComponent[] {
						new WorkflowViewPanel(workflow,getAction()),
						mw,
						p
				};
			} else
				optionsComponent =  new JComponent[] {new WorkflowViewPanel(workflow,getAction())};
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
	        props.add(DBWorkflowContext.RECORD);		
			reports.setProperties(props);
			detailsComponent =  new JComponent[] {reports};
		}
		return detailsComponent;
	}	
	public int compareTo(INanoPlugin o) {
		return getOrder()-o.getOrder();
	}
}

class MyWorkflow extends Workflow {
	String title = "Workflow";
	public void setWorkflow(Workflow workflow) {
		title = workflow.toString();
		setDefinition(workflow.getDefinition());
	}
	@Override
	public String toString() {
		return title;
	}
}
