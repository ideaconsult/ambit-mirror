package org.opentox.fastox.ambit.plugins;

import java.beans.PropertyChangeEvent;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import nplugins.shell.INPluginUI;
import nplugins.shell.INanoPlugin;

import org.opentox.fastox.ambit.workflow.RDFWorkflowContext;
import org.opentox.fastox.ambit.workflow.RDFWorkflowContext.WFC_KEY;
import org.restlet.data.Reference;

import ambit2.base.data.ClassHolder;
import ambit2.workflow.DBWorkflowContext;
import ambit2.workflow.DBWorkflowPlugin;
import ambit2.workflow.ui.UserInteractionEvent;
import ambit2.workflow.ui.WorkflowOptionsLauncher;

import com.microworkflow.process.WorkflowContext;

/**
 * Fastox plugin
 * @author nina
 *
 */
public class FastoxPlugin extends DBWorkflowPlugin {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8172108299783764920L;
	protected WorkflowOptionsLauncher contextListener;
	
	public FastoxPlugin() {
		super();
		workflows.add(new ClassHolder("org.opentox.fastox.ambit.workflow.StructureReaderWorkflow","Structures","Search structures","images/search_256.png"));
		workflows.add(new ClassHolder("org.opentox.fastox.ambit.workflow.ModelsReaderWorkflow","Models","Search models","images/search_256.png"));
		
		contextListener = new WorkflowOptionsLauncher(null);
		Vector<String> props = new Vector<String>();		
		props.add(UserInteractionEvent.PROPERTYNAME);
		props.add(DBWorkflowContext.ERROR);
		props.add(RDFWorkflowContext.WFC_KEY.DATASET_REFERENCE.toString());
		props.add(RDFWorkflowContext.WFC_KEY.MODEL_REFERENCE.toString());
	
		contextListener.setProperties(props);
		contextListener.setWorkflowContext(getWorkflowContext());

		getWorkflowContext().put(WFC_KEY.DATASET_REFERENCE.toString(),
				new Reference("http://localhost:8080/ambit2-www/dataset/8?max=30"));
						//"http://ambit.uni-plovdiv.bg:8080/ambit2/dataset/8?max=30"));
				//"http://webservices.in-silico.ch/test/dataset/37"));
				
		getWorkflowContext().put(WFC_KEY.MODEL_REFERENCE.toString(),
				new Reference("http://ambit.uni-plovdiv.bg:8080/ambit2/model/ToxTree"));
				//"http://opentox.informatik.tu-muenchen.de:8080/OpenTox-dev/model"));
		//"));			

		
	}	
	public INPluginUI<INanoPlugin> createMainComponent() {
		if (mainComponent == null) {
			mainComponent = new FastoxModelsPanel(getWorkflowContext());
			contextListener.setFrame(mainComponent.getComponent());
		} 
		return mainComponent;
	}
	@Override
	public JComponent[] createDetailsComponent() {
		FastoxStructuresPanel c = new FastoxStructuresPanel(getWorkflowContext());
		return new JComponent[] {c.getComponent()};
	}
	public ImageIcon getIcon() {
		return null;
	}

	public int getOrder() {
		return 0;
	}

	public ResourceBundle getResourceBundle() {

		return null;
	}

	public void setParameters(String[] args) {

	}

	public void propertyChange(PropertyChangeEvent evt) {

	}

	public int compareTo(INanoPlugin o) {
		return 0;
	}


	@Override
	protected WorkflowContext createWorkflowContext() {
		return new RDFWorkflowContext();
	}
	@Override
	public void setWorkflowContext(WorkflowContext workflowContext) {
		super.setWorkflowContext(workflowContext);
		if (contextListener!= null)
		contextListener.setWorkflowContext(workflowContext);

	}	
	@Override
	public String toString() {
		return "Fastox";
	}
}
