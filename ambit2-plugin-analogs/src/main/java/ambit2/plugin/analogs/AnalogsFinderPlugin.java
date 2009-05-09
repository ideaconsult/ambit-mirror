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

package ambit2.plugin.analogs;

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
import ambit2.workflow.DBWorkflowContext;
import ambit2.workflow.DBWorkflowPlugin;
import ambit2.workflow.ui.UserInteractionEvent;
import ambit2.workflow.ui.WorkflowOptionsLauncher;

public class AnalogsFinderPlugin extends DBWorkflowPlugin {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3973575896403234559L;
	protected WorkflowOptionsLauncher contextListener;
	public AnalogsFinderPlugin() {
		super();
		workflows.add(new ClassHolder("ambit2.plugin.analogs.CategoryBuildingWorkflow","Category building","Category building",Utils.createImageIcon("ambit2/plugin/analogs/images/32x32_category_building.png")));
		workflows.add(new ClassHolder("ambit2.plugin.analogs.CategoryVerificationWorkflow","Category verification","Category verification",Utils.createImageIcon("ambit2/plugin/analogs/images/32x32_category_verification.png")));
		workflows.add(new ClassHolder("ambit2.plugin.analogs.EndpointValidationWorkflow","Endpoint validation","Endpoint validation",Utils.createImageIcon("ambit2/plugin/analogs/images/32x32_endpoint_validation.png")));
		workflows.add(new ClassHolder("ambit2.plugin.analogs.ReadAcrossWorkflow","Read across","Read across",Utils.createImageIcon("ambit2/plugin/analogs/images/32x32_read_across.png")));
		workflows.add(new ClassHolder("ambit2.plugin.analogs.ProfileWorkflow","Profile","Profile",Utils.createImageIcon("images/database_profiling.png")));		
		workflows.add(new ClassHolder("ambit2.workflow.library.StructuresExportWorkflow","Export results","Export results as PDF/RTF/HTML/SDF files","images/report.png"));
		workflows.add(new ClassHolder("ambit2.workflow.library.QueryStatisticsWorkflow","Statistics","Generate search results statistics /see Log tab","images/database_statistics.png"));		
		//workflows.add(new ClassHolder("ambit2.plugin.pbt.SavePBTWorkflow","Save","Save PBT assessment results into database","images/import.png"));
		
		contextListener = new WorkflowOptionsLauncher(null);
		Vector<String> props = new Vector<String>();		
		props.add(UserInteractionEvent.PROPERTYNAME);
		props.add(DBWorkflowContext.ERROR);
		props.add(DBWorkflowContext.LOGININFO);
		props.add(DBWorkflowContext.DBCONNECTION_URI);
		props.add(DBWorkflowContext.DATASOURCE);
        props.add(DBWorkflowContext.DATASET);
        props.add(DBWorkflowContext.QUERY_POPUP);
        
        props.add("PROFILE_PROPERTY");
        
		contextListener.setProperties(props);
		contextListener.setWorkflowContext(getWorkflowContext());
		
		/*
	  	Profile profile = new Profile();
	  	profile.add(Property.getInstance("CAS","CAS Registry Number", "http://www.cas.org"));
	  	profile.add(Property.getInstance("CAS","CAS Registry Number", "http://www.cas.org"));
	  	profile.add(Property.getInstance("Name","Chemical name","http://www.iupac.org"));
	  	Property p = Property.getInstance("org.openscience.cdk.qsar.descriptors.molecular.XLogPDescriptor","Descriptors");
	  	p.setLabel("LogP");
	  	p.setClazz(org.openscience.cdk.qsar.descriptors.molecular.XLogPDescriptor.class);
	  	profile.add(p);
    	getWorkflowContext().put(DBWorkflowContext.PROFILE, profile);
    	
	  	Profile endpoints = new Profile();
	  	endpoints.add(Property.getInstance("Aquatic toxicity","Endpoints"));
	  	p =Property.getInstance("Skin sensitisation","Endpoints");
	  	p.setLabel("EC3");
	  	endpoints.add(p);
    	getWorkflowContext().put(DBWorkflowContext.ENDPOINTS, endpoints);    	
    	*/
		LoginInfo li = new LoginInfo();
		li.setUser("guest");
		li.setPassword(li.getUser());
		String uri = DatasourceFactory.getConnectionURI(li.getScheme(), li.getHostname(), li.getPort(), li.getDatabase(), li.getUser(), li.getPassword());
		getWorkflowContext().put(DBWorkflowContext.LOGININFO,li);
		getWorkflowContext().put(DBWorkflowContext.DBCONNECTION_URI,uri);	    	
    	
	}

	public INPluginUI<INanoPlugin> createMainComponent() {
		if (mainComponent == null) {
			mainComponent = new AnalogsMainPanel(getWorkflowContext());
			contextListener.setFrame(mainComponent.getComponent());
		} 
		return mainComponent;
	}
	//
	
	
	public ImageIcon getIcon() {
	    return Utils.createImageIcon("ambit2/plugin/analogs/images/molecule_16.png");
	}

	public int getOrder() {
		return 2;
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
	public JComponent[] createDetailsComponent() {
		return null;
	}

	@Override
	public String toString() {
	    return "Profile & Category";
	}
}
