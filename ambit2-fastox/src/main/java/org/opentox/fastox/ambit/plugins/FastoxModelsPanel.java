package org.opentox.fastox.ambit.plugins;

import java.awt.Component;
import java.util.Vector;

import nplugins.shell.INPluginUI;
import nplugins.shell.INanoPlugin;

import org.opentox.fastox.ambit.ui.RDFModelsBrowserPanel;
import org.opentox.fastox.ambit.workflow.RDFWorkflowContext;

import ambit2.ui.table.IBrowserMode.BrowserMode;

import com.microworkflow.process.WorkflowContext;

/**
 * Fastox main panel
 * @author nina
 *
 */
public class FastoxModelsPanel implements INPluginUI<INanoPlugin> {
	protected INanoPlugin plugin;
	protected Component component;
	public FastoxModelsPanel(WorkflowContext context) {
		super();
		component = buildPanel(context);
	}
	public  Component buildPanel(WorkflowContext context) {

		RDFModelsBrowserPanel query = new RDFModelsBrowserPanel(context,BrowserMode.Spreadsheet);
		Vector<String> p = new Vector<String>();
		p.add(RDFWorkflowContext.WFC_KEY.MODEL_REFERENCE.toString());
		p.add(RDFWorkflowContext.WFC_KEY.ONTMODEL.toString());
		query.setProperties(p);
		query.setAnimate(true);
	
		return query;
	}
	public Component getComponent() {
		return component;
	}

	public INanoPlugin getPlugin() {
		return plugin;
	}

	public void setPlugin(INanoPlugin plugin) {
		this.plugin = plugin;
		
	}
}
