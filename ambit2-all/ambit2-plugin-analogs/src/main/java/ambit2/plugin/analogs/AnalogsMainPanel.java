/* AnalogsMainPanel.java
 * Author: nina
 * Date: Apr 16, 2009
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2009  Ideaconsult Ltd.
 * 
 * Contact: nina
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package ambit2.plugin.analogs;

import java.awt.Component;
import java.util.Vector;

import javax.swing.JTabbedPane;

import nplugins.shell.INPluginUI;
import nplugins.shell.INanoPlugin;
import ambit2.ui.table.IBrowserMode.BrowserMode;
import ambit2.workflow.DBWorkflowContext;
import ambit2.workflow.ui.QueryResultsPanel;
import ambit2.workflow.ui.RecordsBrowserPanel;

import com.microworkflow.process.WorkflowContext;

public class AnalogsMainPanel implements INPluginUI<INanoPlugin> {
	protected INanoPlugin plugin;
	protected Component component;
	public AnalogsMainPanel(WorkflowContext context) {
		component = buildPanel(context);
	}
	public  Component buildPanel(WorkflowContext context) {
		JTabbedPane tabbedPane = new JTabbedPane();
		RecordsBrowserPanel query = new RecordsBrowserPanel(context,BrowserMode.Spreadsheet);
		Vector<String> p = new Vector<String>();
		p.add(DBWorkflowContext.RECORDS);
		p.add(DBWorkflowContext.ERROR);
		p.add(DBWorkflowContext.DBCONNECTION_URI);
		p.add(DBWorkflowContext.PROFILE);
		p.add(DBWorkflowContext.ENDPOINTS);
		query.setProperties(p);
		query.setAnimate(true);
		
	    QueryResultsPanel results = new QueryResultsPanel(context,BrowserMode.Spreadsheet);
		p = new Vector<String>();
		p.add(DBWorkflowContext.STOREDQUERY);
		p.add(DBWorkflowContext.ERROR);
		p.add(DBWorkflowContext.DBCONNECTION_URI);
		p.add(DBWorkflowContext.ENDPOINTS);

		results.setProperties(p);
		results.setAnimate(true);
		
		tabbedPane.addTab("Target chemical(s)", query);		
		tabbedPane.addTab("Source chemical(s)", results);

		return tabbedPane;
	}
	public Component getComponent() {
		return component;
	}

	public INanoPlugin getPlugin() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setPlugin(INanoPlugin plugin) {
		// TODO Auto-generated method stub
		
	}

}
