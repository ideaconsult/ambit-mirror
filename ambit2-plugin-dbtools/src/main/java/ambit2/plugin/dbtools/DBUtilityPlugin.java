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

package ambit2.plugin.dbtools;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.ImageIcon;

import nplugins.shell.INPluginUI;
import nplugins.shell.INanoPlugin;
import nplugins.shell.application.Utils;
import ambit2.core.data.ClassHolder;
import ambit2.core.io.FileInputState;
import ambit2.core.io.MolFileFilter;
import ambit2.core.processors.batch.BatchProcessor;
import ambit2.db.processors.MySQLCommand;
import ambit2.workflow.DBWorkflowContext;
import ambit2.workflow.DBWorkflowPlugin;
import ambit2.workflow.IMultiWorkflowsPlugin;
import ambit2.workflow.library.InputFileSelection;
import ambit2.workflow.ui.MultiWorkflowsPanel;
import ambit2.workflow.ui.UserInteractionEvent;
import ambit2.workflow.ui.WorkflowOptionsLauncher;

import com.microworkflow.process.Workflow;

/**
 * Provides workflows for several database related tasks
 * (create db, import data, start/stop mysql, calculate various properties).
 * @author nina
 *
 */
public class DBUtilityPlugin extends DBWorkflowPlugin implements IMultiWorkflowsPlugin{
	/**
	 * 
	 */
	private static final long serialVersionUID = -189422892768202994L;
	protected List<ClassHolder> workflows;	
	protected WorkflowOptionsLauncher contextListener;
	public DBUtilityPlugin() {
		super();
		workflows = new ArrayList<ClassHolder>();

		workflows.add(new ClassHolder("ambit2.plugin.dbtools.ImportWorkflow","Import","Import chemical structures into database","images/import.png"));
		workflows.add(new ClassHolder("ambit2.plugin.dbtools.DBUtilityWorkflow","Calculate","Calculate fingerprints and descriptors for structures in database","images/calculate.png"));
		workflows.add(new ClassHolder("ambit2.plugin.dbtools.MysqlServerLauncher","MySQL","Start local MySQL database server","images/mysql_start.png"));
		workflows.add(new ClassHolder("ambit2.plugin.dbtools.MySQLServerStop","MySQL","Stop local MySQL database server","images/mysql_stop.png"));
		workflows.add(new ClassHolder("ambit2.plugin.dbtools.DBStatisticsWorkflow","DB statistics","Displays database statistics","images/database3.png"));
		
		contextListener = new WorkflowOptionsLauncher(null);
		Vector<String> props = new Vector<String>();		
		props.add(UserInteractionEvent.PROPERTYNAME);
		props.add(DBWorkflowContext.ERROR);
		props.add(DBWorkflowContext.LOGININFO);
		props.add(DBWorkflowContext.DBCONNECTION_URI);
		props.add(DBWorkflowContext.DATASOURCE);
        props.add(DBWorkflowContext.DATASET);
        props.add(DBWorkflowContext.BATCHSTATS);
        props.add(BatchProcessor.PROPERTY_BATCHSTATS);
		contextListener.setProperties(props);
		contextListener.setWorkflowContext(getWorkflowContext());
		
		getWorkflowContext().put(MySQLCommand.MYSQLCOMMAND, new MySQLCommand());
		FileInputState fs = new FileInputState();
		fs.setSupportedExtDescriptions(MolFileFilter.supported_exts_description);
		fs.setSupportedExtensions(MolFileFilter.supported_extensions);
		getWorkflowContext().put(InputFileSelection.INPUTFILE, fs);
	}
	public List<ClassHolder> getWorkflows() {
		return workflows;
	}
	@Override
	protected Workflow createWorkflow() {
		return new Workflow() {
			@Override
			public String toString() {
				return "Database tools";
			}
		};
	}
	
	public INPluginUI<INanoPlugin> createMainComponent() {
		if (mainComponent == null)
			mainComponent = new MultiWorkflowsPanel<DBUtilityPlugin>(this,getAction());
		return mainComponent;
	}

	public ImageIcon getIcon() {
	    return Utils.createImageIcon("ambit2/plugin/dbtools/images/database_16.png");
	}

	public int getOrder() {
		return 5;
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
		return "Database tools";
	}

	
}
