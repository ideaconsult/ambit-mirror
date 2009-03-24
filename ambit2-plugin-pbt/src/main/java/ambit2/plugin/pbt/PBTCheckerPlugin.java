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

package ambit2.plugin.pbt;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import nplugins.shell.INPluginUI;
import nplugins.shell.INanoPlugin;
import nplugins.shell.application.Utils;
import ambit2.base.data.ClassHolder;
import ambit2.db.DatasourceFactory;
import ambit2.db.LoginInfo;
import ambit2.ui.table.IBrowserMode.BrowserMode;
import ambit2.workflow.DBWorkflowContext;
import ambit2.workflow.DBWorkflowPlugin;
import ambit2.workflow.IMultiWorkflowsPlugin;
import ambit2.workflow.library.LogoutSequence;
import ambit2.workflow.ui.MultiWorkflowsPanel;
import ambit2.workflow.ui.QueryResultsPanel;
import ambit2.workflow.ui.StatusPanel;
import ambit2.workflow.ui.UserInteractionEvent;
import ambit2.workflow.ui.WorkflowOptionsLauncher;
import ambit2.workflow.ui.WorkflowViewPanel;

import com.microworkflow.process.Workflow;
import com.microworkflow.process.WorkflowContext;

public class PBTCheckerPlugin extends DBWorkflowPlugin {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2604797602709881671L;
	protected WorkflowOptionsLauncher contextListener;
	protected PBTUpdater updater;
	
	public PBTCheckerPlugin() {
		workflows.add(new ClassHolder("ambit2.plugin.pbt.PBTCreator","New","Creates a new PBT workbook",Utils.createImageIcon("ambit2/plugin/pbt/images/pbt_new.png")));
		workflows.add(new ClassHolder("ambit2.plugin.pbt.PBTWorkflow","PBT","Verifies if PBT assessment is complete",Utils.createImageIcon("ambit2/plugin/pbt/images/pbt_verify.png")));
		workflows.add(new ClassHolder("ambit2.plugin.pbt.SearchWorkflow","Search","Search for structures to be imported into PBT assessment",Utils.createImageIcon("ambit2/plugin/pbt/images/pbt_search.png")));
		workflows.add(new ClassHolder("ambit2.plugin.pbt.ExportWorkflow","Export results","Export results as PDF/RTF/HTML/SDF files","images/report.png"));
		workflows.add(new ClassHolder("ambit2.plugin.pbt.SavePBTWorkflow","Save","Save PBT assessment results into database","images/import.png"));

		
		contextListener = new WorkflowOptionsLauncher(null);
		Vector<String> props = new Vector<String>();		
		props.add(UserInteractionEvent.PROPERTYNAME);
		props.add(DBWorkflowContext.ERROR);
		props.add(DBWorkflowContext.LOGININFO);
		props.add(DBWorkflowContext.DBCONNECTION_URI);
		props.add(DBWorkflowContext.DATASOURCE);
        props.add(DBWorkflowContext.DATASET);		
        props.add(DBWorkflowContext.STOREDQUERY);	        
		contextListener.setProperties(props);
		contextListener.setWorkflowContext(getWorkflowContext());
		/*
		QueryResultsPanel results = new QueryResultsPanel(getWorkflowContext());
		Vector<String> p = new Vector<String>();
		p.add(DBWorkflowContext.STOREDQUERY);
		p.add(DBWorkflowContext.ERROR);
		results.setProperties(p);
		results.setAnimate(true);
		return new JComponent[] {results};		
		*/
		try {
			getWorkflowContext().put(PBTWorkBook.PBT_WORKBOOK,new PBTWorkBook());
			
			LoginInfo li = new LoginInfo();
			li.setUser("guest");
			li.setPassword(li.getUser());
			String uri = DatasourceFactory.getConnectionURI(li.getScheme(), li.getHostname(), li.getPort(), li.getDatabase(), li.getUser(), li.getPassword());
			getWorkflowContext().put(DBWorkflowContext.LOGININFO,li);
			getWorkflowContext().put(DBWorkflowContext.DBCONNECTION_URI,uri);
		} catch (Exception x) {
			getWorkflowContext().put(DBWorkflowContext.ERROR,x);
		}
		updater = new PBTUpdater(getWorkflowContext());
		
	}
	@Override
	public void setWorkflowContext(WorkflowContext workflowContext) {
		super.setWorkflowContext(workflowContext);
		if (contextListener!= null)
		contextListener.setWorkflowContext(workflowContext);

	}	
	@Override
	protected WorkflowContext createWorkflowContext() {
		return new WorkflowContext();
	}

	public INPluginUI<INanoPlugin> createMainComponent() {
		if (mainComponent == null) {
			PBTMainPanel results = new PBTMainPanel(getWorkflowContext());
	
			Vector<String> p = new Vector<String>();
			p.add(PBTWorkBook.PBT_WORKBOOK);
			//p.add(DBWorkflowContext.STOREDQUERY);
			//p.add(DBWorkflowContext.ERROR);
			//p.add(DBWorkflowContext.RECORD);
			//p.add(DBWorkflowContext.STRUCTURES);			
			results.setProperties(p);
			results.setAnimate(true);
			mainComponent = results;
		}
		Object pbt = getWorkflowContext().get(PBTWorkBook.PBT_WORKBOOK);
		if (pbt != null)
			((PBTMainPanel)mainComponent).setWorkbook((PBTWorkBook)pbt);
		return mainComponent;

	}

	public ImageIcon getIcon() {
	    return Utils.createImageIcon("images/pill_16.png");
	}

	public int getOrder() {
		return 3;
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
		return "PBT";
	}
	@Override
	public JComponent[] createDetailsComponent() {
		if (detailsComponent == null) {
		    QueryResultsPanel results = new QueryResultsPanel(getWorkflowContext(),BorderLayout.NORTH,BrowserMode.Columns);
		    
			Vector<String> p = new Vector<String>();
			p.add(DBWorkflowContext.STOREDQUERY);
			p.add(DBWorkflowContext.ERROR);
			p.add(DBWorkflowContext.DBCONNECTION_URI);			
			results.setProperties(p);
			results.setAnimate(true);
			detailsComponent = new JComponent[] {results};			
		}
			//detailsComponent = new JComponent[] {new MultiWorkflowsPanel<PBTCheckerPlugin>(this,getAction())};
			
		return detailsComponent;
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
	public boolean isModified() {
		try {
			Object o = getWorkflowContext().get(PBTWorkBook.PBT_WORKBOOK);
			if ((o!=null) && (o instanceof PBTWorkBook))
				return ((PBTWorkBook)o).isModified();
			else 
				return false;
		} catch (Exception x) {
			return false;
		}
	}
	
}
