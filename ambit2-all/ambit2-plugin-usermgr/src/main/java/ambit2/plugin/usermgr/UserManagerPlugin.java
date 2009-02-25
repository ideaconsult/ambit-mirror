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
import nplugins.shell.application.Utils;
import ambit2.core.data.ClassHolder;
import ambit2.workflow.DBWorkflowContext;
import ambit2.workflow.DBWorkflowPlugin;
import ambit2.workflow.IMultiWorkflowsPlugin;
import ambit2.workflow.ui.MultiWorkflowsPanel;
import ambit2.workflow.ui.UserInteractionEvent;
import ambit2.workflow.ui.WorkflowConsolePanel;
import ambit2.workflow.ui.WorkflowOptionsLauncher;

import com.microworkflow.process.Workflow;

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
	protected List<ClassHolder> workflows;	
	protected WorkflowOptionsLauncher contextListener;
	public UserManagerPlugin() {
		workflows = new ArrayList<ClassHolder>();
		workflows.add(new ClassHolder("ambit2.plugin.usermgr.CreateDatabaseWorkflow","Create database","Create new AMBIT database","images/newdatabase.png"));		
		workflows.add(new ClassHolder("ambit2.plugin.usermgr.UserManagerWorkflow","Add user","Add new user in AMBIT database","images/users.png"));
		contextListener = new WorkflowOptionsLauncher(null);
		Vector<String> props = new Vector<String>();		
		props.add(UserInteractionEvent.PROPERTYNAME);
		props.add(DBWorkflowContext.ERROR);
		props.add(DBWorkflowContext.LOGININFO);
		props.add(DBWorkflowContext.DBCONNECTION_URI);

		contextListener.setProperties(props);
		contextListener.setWorkflowContext(getWorkflowContext());
		
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
			mainComponent = new MultiWorkflowsPanel<UserManagerPlugin>(this,getAction());
		return mainComponent;
	}
	@Override
	public JComponent[] createDetailsComponent() {
		if (detailsComponent == null) {
			JComponent[] c = super.createDetailsComponent();
			/*
			 * smt weird happen if passing workflow at constructor - workflows can't be run!
			 */
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
}
