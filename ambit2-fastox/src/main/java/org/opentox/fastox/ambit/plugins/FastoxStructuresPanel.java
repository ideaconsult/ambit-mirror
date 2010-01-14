package org.opentox.fastox.ambit.plugins;

import java.util.Vector;

import javax.swing.JComponent;

import nplugins.shell.INPluginUI;
import nplugins.shell.INanoPlugin;

import org.opentox.fastox.ambit.ui.RDFBrowserPanel;
import org.opentox.fastox.ambit.workflow.RDFWorkflowContext;

import ambit2.ui.table.IBrowserMode.BrowserMode;

import com.microworkflow.process.WorkflowContext;

public class FastoxStructuresPanel implements INPluginUI<INanoPlugin> {
	protected INanoPlugin plugin;
	protected JComponent component;
	public FastoxStructuresPanel(WorkflowContext context) {
		component = buildPanel(context);
	}
	public  JComponent buildPanel(WorkflowContext context) {

		RDFBrowserPanel query = new RDFBrowserPanel(context,BrowserMode.Spreadsheet);
		Vector<String> p = new Vector<String>();
		p.add(RDFWorkflowContext.WFC_KEY.DATASET_REFERENCE.toString());
		p.add(RDFWorkflowContext.WFC_KEY.ONTMODEL.toString());
		query.setProperties(p);
		query.setAnimate(true);
	
		return query;
	}
	public JComponent getComponent() {
		return component;
	}

	public INanoPlugin getPlugin() {
		return plugin;
	}

	public void setPlugin(INanoPlugin plugin) {
		this.plugin = plugin;
		
	}
}
