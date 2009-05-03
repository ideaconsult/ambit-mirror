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

package ambit2.plugin.usermgr;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import nplugins.shell.INPluginUI;
import nplugins.shell.INanoPlugin;
import nplugins.shell.application.NPluginsAction;
import nplugins.shell.application.Utils;
import nplugins.workflow.ExecuteWorkflowTask;
import ambit2.base.data.ClassHolder;
import ambit2.db.LoginInfo;
import ambit2.workflow.DBWorkflowContext;
import ambit2.workflow.DBWorkflowPlugin;
import ambit2.workflow.IMultiWorkflowsPlugin;
import ambit2.workflow.library.LogoutSequence;
import ambit2.workflow.ui.MultiWorkflowsPanel;
import ambit2.workflow.ui.StatusPanel;
import ambit2.workflow.ui.UserInteractionEvent;
import ambit2.workflow.ui.WorkflowConsolePanel;
import ambit2.workflow.ui.WorkflowOptionsLauncher;
import ambit2.workflow.ui.WorkflowViewPanel;

import com.microworkflow.process.Workflow;
import com.microworkflow.process.WorkflowContext;

/**
 * Any administrative utilities, requiring admin DB rights 
 * @author nina
 *
 */
public class UserManagerPlugin extends DBWorkflowPlugin implements IMultiWorkflowsPlugin {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8337122459874022752L;

	protected WorkflowOptionsLauncher contextListener;
	public UserManagerPlugin() {
		super();
		workflows.add(new ClassHolder("ambit2.plugin.usermgr.CreateDatabaseWorkflow","Create database","Create new AMBIT database","images/newdatabase.png"));		
		workflows.add(new ClassHolder("ambit2.plugin.usermgr.UserManagerWorkflow","Users","Manage AMBIT database users","images/users.png"));
		workflows.add(new ClassHolder("ambit2.plugin.usermgr.DatasetsWorkflow","Datasets","Manage datasets","images/database_statistics.png"));		
		workflows.add(new ClassHolder("ambit2.plugin.usermgr.TemplatesWorkflow","Templates","Manage templates","images/database_statistics.png"));
		workflows.add(new ClassHolder("ambit2.plugin.usermgr.PropertiesWorkflow","Properties","Manage properties","images/database_statistics.png"));
		workflows.add(new ClassHolder("ambit2.plugin.usermgr.ReferencesWorkflow","References","Manage references","images/database_statistics.png"));		
		workflows.add(new ClassHolder("ambit2.plugin.usermgr.SavedResultsWorkflow","Saved results","Manage saved results","images/database_statistics.png"));		
		contextListener = new WorkflowOptionsLauncher(null);
		Vector<String> props = new Vector<String>();		
		props.add(UserInteractionEvent.PROPERTYNAME);
		props.add(DBWorkflowContext.ERROR);
		props.add(DBWorkflowContext.LOGININFO);
		props.add(DBWorkflowContext.DBCONNECTION_URI);

		contextListener.setProperties(props);
		contextListener.setWorkflowContext(getWorkflowContext());
		
		LoginInfo li = new LoginInfo();
		li.setUser("root");
		li.setDatabase("mysql");
		getWorkflowContext().put(DBWorkflowContext.LOGININFO,li);
		
	}
	public List<ClassHolder> getWorkflows() {
		return workflows;
	}
	@Override
	protected Workflow createWorkflow() {
		return new Workflow();
	}
	
	public INPluginUI<INanoPlugin> createMainComponent() {
		if (mainComponent == null) 
			mainComponent = new MultiWorkflowsPanel<UserManagerPlugin>(this);
		return mainComponent;
	}
	@Override
	public JComponent[] createDetailsComponent() {
		return null;
	}
	/*
	@Override
	public JComponent[] createDetailsComponent() {
		if (detailsComponent == null) {
			JComponent[] c = super.createDetailsComponent();

			WorkflowConsolePanel reports = new WorkflowConsolePanel();
			//WorkflowConsolePanel reports = new WorkflowConsolePanel(getWorkflow());
			reports.setWorkflowContext(getWorkflowContext());
	
			Vector<String> props = new Vector<String>();	
			props.add(DBWorkflowContext.LOGININFO);
			props.add(DBWorkflowContext.DATASET);
			props.add(DBWorkflowContext.ERROR);
			reports.setProperties(props);
			detailsComponent = new JComponent[] {reports,c[0]};
		}
		return detailsComponent;
	}	
	*/
	public ImageIcon getIcon() {
	    return Utils.createImageIcon("ambit2/plugin/usermgr/images/user_16.png");
	}

	public int getOrder() {
		return 4;
	}

	public ResourceBundle getResourceBundle() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setParameters(String[] args) {
		// TODO Auto-generated method stub

	}

	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub

	}


	@Override
	public String toString() {

		return "Administrative tools";
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
					new WorkflowViewPanel(workflow,action,getStopAction()),p,createConsole()};
		} 
		return optionsComponent;
	}			
}
