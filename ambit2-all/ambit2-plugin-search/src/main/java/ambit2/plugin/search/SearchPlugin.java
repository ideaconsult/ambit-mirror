package ambit2.plugin.search;


import java.beans.PropertyChangeEvent;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.ImageIcon;

import nplugins.shell.INPluginUI;
import nplugins.shell.INanoPlugin;
import ambit2.workflow.DBWorkflowContext;
import ambit2.workflow.DBWorkflowPlugin;
import ambit2.workflow.ui.QueryResultsPanel;
import ambit2.workflow.ui.UserInteractionEvent;
import ambit2.workflow.ui.WorkflowOptionsLauncher;

import com.microworkflow.process.Workflow;
import com.microworkflow.process.WorkflowContext;

public class SearchPlugin extends DBWorkflowPlugin {
	protected WorkflowOptionsLauncher contextListener;
	public SearchPlugin() {
		super();
		contextListener = new WorkflowOptionsLauncher(null);
		Vector<String> props = new Vector<String>();		
		props.add(UserInteractionEvent.PROPERTYNAME);
		props.add(DBWorkflowContext.ERROR);
		props.add(DBWorkflowContext.LOGININFO);
		props.add(DBWorkflowContext.DBCONNECTION_URI);
		props.add(DBWorkflowContext.DATASOURCE);
        props.add(DBWorkflowContext.DATASET);		
		contextListener.setProperties(props);
		contextListener.setWorkflowContext(getWorkflowContext());
		
	}
	@Override
	public void setWorkflowContext(WorkflowContext workflowContext) {
		super.setWorkflowContext(workflowContext);
		if (contextListener!= null)
		contextListener.setWorkflowContext(workflowContext);

	}
	@Override
	protected Workflow createWorkflow() {
		return new SearchWorkflow();
	}
	
	public INPluginUI<INanoPlugin> createMainComponent() {
		if (mainComponent == null) {
			QueryResultsPanel results = new QueryResultsPanel(getWorkflowContext());
			Vector<String> p = new Vector<String>();
			p.add(DBWorkflowContext.STOREDQUERY);
			p.add(DBWorkflowContext.ERROR);
			results.setProperties(p);
			results.setAnimate(true);
			mainComponent = results;
		} 
		return mainComponent;
	}

	public ImageIcon getIcon() {
	    String path = "ambit2/plugin/search/images/search_16.png";
	    URL url = SearchPlugin.class.getClassLoader().getResource(path);
	    //return Utils.createImageIcon("ambit2/plugin/search/images/search_16.png");
        if (url != null) {
            return new ImageIcon(url);
        } else {
            System.out.println("Couldn't find file: " + path);
            return null;
        }	    
	}
	

	public int getOrder() {
		return 1;
	}

	public ResourceBundle getResourceBundle() {
		return null;
	}

	public void setParameters(String[] args) {
	}

	public void propertyChange(PropertyChangeEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public String toString() {
		return "Simple Search";
	}

}
