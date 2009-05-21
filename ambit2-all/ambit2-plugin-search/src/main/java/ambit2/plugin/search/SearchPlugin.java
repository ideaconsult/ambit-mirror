
package ambit2.plugin.search;


import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;

import nplugins.shell.INPluginUI;
import nplugins.shell.INanoPlugin;
import ambit2.base.data.ClassHolder;
import ambit2.db.DatasourceFactory;
import ambit2.db.LoginInfo;
import ambit2.ui.table.IBrowserMode.BrowserMode;
import ambit2.workflow.DBWorkflowContext;
import ambit2.workflow.DBWorkflowPlugin;
import ambit2.workflow.ui.QueryResultsPanel;
import ambit2.workflow.ui.UserInteractionEvent;
import ambit2.workflow.ui.WorkflowOptionsLauncher;

import com.microworkflow.process.WorkflowContext;

public class SearchPlugin extends DBWorkflowPlugin {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4102054050740161163L;
	protected WorkflowOptionsLauncher contextListener;
	public SearchPlugin() {
		super();
		workflows.add(new ClassHolder("ambit2.plugin.search.SearchWorkflow","Search","Search by single property or identifier","images/search_256.png"));
		workflows.add(new ClassHolder("ambit2.plugin.search.AnalogsFinderWorkflow","Advanced search","Search by multiple criteria","images/search_256.png"));
		workflows.add(new ClassHolder("ambit2.plugin.search.RefineSearchWorkflow","Refine search results","Search within last results","images/search_256.png"));
		workflows.add(new ClassHolder("ambit2.plugin.search.BrowseStoredWorkflow","Browse previous search results","Browse results from previous queries","images/search_256.png"));		
		workflows.add(new ClassHolder("ambit2.plugin.search.ProfileWorkflow","Properties","Select properties to be displayed","images/database_profiling.png"));		
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
		contextListener.setProperties(props);
		contextListener.setWorkflowContext(getWorkflowContext());
		
		LoginInfo li = new LoginInfo();
		li.setUser("guest");
		li.setPassword(li.getUser());
		String uri = DatasourceFactory.getConnectionURI(li.getScheme(), li.getHostname(), li.getPort(), li.getDatabase(), li.getUser(), li.getPassword());
		getWorkflowContext().put(DBWorkflowContext.LOGININFO,li);
		getWorkflowContext().put(DBWorkflowContext.DBCONNECTION_URI,uri);			
		
		
		
	}
	@Override
	public JComponent[] createDetailsComponent() {
		return null;
	}
	@Override
	public void setWorkflowContext(WorkflowContext workflowContext) {
		super.setWorkflowContext(workflowContext);
		if (contextListener!= null)
		contextListener.setWorkflowContext(workflowContext);

	}

	@Override
	public synchronized void runWorkflow(ClassHolder clazz) throws Exception {
		getWorkflowContext().put(DBWorkflowContext.QUERY,null);
		super.runWorkflow(clazz);
	}
	public INPluginUI<INanoPlugin> createMainComponent() {
		if (mainComponent == null) {
			QueryResultsPanel results = new QueryResultsPanel(getWorkflowContext(),BrowserMode.Spreadsheet);
			Vector<String> p = new Vector<String>();
			p.add(DBWorkflowContext.STOREDQUERY);
			p.add(DBWorkflowContext.ERROR);
			p.add(DBWorkflowContext.DBCONNECTION_URI);			
			results.setProperties(p);
			results.setAnimate(true);
			
			
		    StatisticsPanel stats = new StatisticsPanel(getWorkflowContext());
			p = new Vector<String>();
			p.add(DBWorkflowContext.STOREDQUERY);
			p.add(DBWorkflowContext.ERROR);
			p.add(DBWorkflowContext.DBCONNECTION_URI);
			p.add(DBWorkflowContext.ENDPOINTS);

			stats.setProperties(p);
			stats.setAnimate(true);
			
			MainPanel mp = new MainPanel();
			mp.addTab("Results",results);
			mp.addTab("Stats", stats);
			
			mainComponent = mp;
			contextListener.setFrame(mainComponent.getComponent());
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

class MainPanel extends JTabbedPane implements INPluginUI<INanoPlugin>  {
	public MainPanel() {
		
	}
	public Component getComponent() {
		// TODO Auto-generated method stub
		return this;
	}

	public INanoPlugin getPlugin() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setPlugin(INanoPlugin arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
