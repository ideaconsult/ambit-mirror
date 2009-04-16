/* AbstractStructureBrowserPanel.java
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

package ambit2.workflow.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.table.TableModel;

import nplugins.shell.INPluginUI;
import nplugins.shell.INanoPlugin;
import ambit2.base.exceptions.AmbitException;
import ambit2.ui.QueryBrowser;
import ambit2.ui.table.BrowsableTableModel;
import ambit2.ui.table.IBrowserMode.BrowserMode;

import com.microworkflow.process.WorkflowContext;
import com.microworkflow.ui.IWorkflowContextFactory;
import com.microworkflow.ui.WorkflowContextListenerPanel;

public abstract class AbstractStructureBrowserPanel<T, Table extends TableModel> extends WorkflowContextListenerPanel  implements INPluginUI<INanoPlugin>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7262823040040167921L;
	
    protected Table tableModel;
    protected QueryBrowser<BrowsableTableModel> browser;

    public AbstractStructureBrowserPanel(IWorkflowContextFactory wfcfactory,String controlsPosition,BrowserMode mode) {
        super(wfcfactory);
        addWidgets(controlsPosition,mode);
    }
    public AbstractStructureBrowserPanel(IWorkflowContextFactory wfcfactory,BrowserMode mode) {
    	this(wfcfactory,BorderLayout.NORTH,mode);
    }    
    public AbstractStructureBrowserPanel(WorkflowContext wfcontext,BrowserMode mode) {
        this(wfcontext,BorderLayout.NORTH,mode);
    }
    public AbstractStructureBrowserPanel(WorkflowContext wfcontext,String controlsPosition,BrowserMode mode) {
        super(null);
        setWorkflowContext(wfcontext);
        addWidgets(controlsPosition,mode);
    }    
    protected abstract Table createTableModel();
    protected void addWidgets(String controlsPosition,BrowserMode mode) {
        setLayout(new BorderLayout());
        setAnimate(true);
        tableModel = createTableModel();
        browser = new QueryBrowser<BrowsableTableModel>(
                    new BrowsableTableModel(tableModel),
                    new Dimension(100,100)) {
        	@Override
        	protected int setRecord(int row, int col) {
        		int record = super.setRecord(row, col);
        		processRecord(record);
        		return record;
        	}
        };
        browser.setMode(BrowserMode.Spreadsheet,BrowserMode.Columns);
        add(browser,BorderLayout.CENTER);
    }	
    protected abstract void processRecord(int record);
    @Override
    public void clear() {
    }
    public Component getComponent() {
        return this;
    }
    public INanoPlugin getPlugin() {
        return null;
    }
    public void setPlugin(INanoPlugin plugin) {
       
    };
	protected abstract T getQuery();
	protected abstract void setQuery(T object) throws AmbitException;
	
	
}
