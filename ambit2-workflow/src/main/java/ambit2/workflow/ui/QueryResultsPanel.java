/* AbstractQueryResultsPanel.java
 * Author: Nina Jeliazkova
 * Date: Oct 5, 2008 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2008  Nina Jeliazkova
 * 
 * Contact: nina@acad.bg
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
import java.beans.PropertyChangeEvent;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import nplugins.shell.INPluginUI;
import nplugins.shell.INanoPlugin;
import ambit2.core.data.Profile;
import ambit2.core.exceptions.AmbitException;
import ambit2.db.search.IStoredQuery;
import ambit2.dbui.StoredQueryTableModel;
import ambit2.ui.QueryBrowser;
import ambit2.ui.table.BrowsableTableModel;
import ambit2.workflow.DBWorkflowContext;

import com.microworkflow.process.WorkflowContext;
import com.microworkflow.ui.IWorkflowContextFactory;
import com.microworkflow.ui.WorkflowContextListenerPanel;


public class QueryResultsPanel extends WorkflowContextListenerPanel  implements INPluginUI<INanoPlugin> {

    /**
     * 
     */
    private static final long serialVersionUID = -779943857847100493L;
    protected StoredQueryTableModel tableModel;
    protected QueryBrowser<BrowsableTableModel> browser;

    /*
        StoredQueryTableModel model = new StoredQueryTableModel();
        model.setConnection(datasource.getConnection());
        model.setQuery(query);
        assertTrue(model.getRowCount()>0);

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        QueryBrowser<BrowsableTableModel> browser = new QueryBrowser<BrowsableTableModel>(
                new BrowsableTableModel(model));
     */
    public QueryResultsPanel(IWorkflowContextFactory wfcfactory) {
        super(wfcfactory);
        addWidgets();
    }
    public QueryResultsPanel(WorkflowContext wfcontext) {
        super(null);
        setWorkflowContext(wfcontext);
        addWidgets();
    }
    
    protected void addWidgets() {
        setLayout(new BorderLayout());
        setAnimate(true);
        tableModel = new StoredQueryTableModel();
        browser = new QueryBrowser<BrowsableTableModel>(
                    new BrowsableTableModel(tableModel));
        add(browser,BorderLayout.CENTER);
    }
    @Override
    protected void animate(PropertyChangeEvent arg0) {
        if (DBWorkflowContext.STOREDQUERY.equals(arg0.getPropertyName())) {
            Object o = arg0.getNewValue();
 
            if (o instanceof IStoredQuery) {
                try {
                    setQuery((IStoredQuery)o);
                } catch (Exception x) {
                    x.printStackTrace();
                }
            }
        }
        
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub
        
    }
    public Component getComponent() {
        return this;
    }
    public INanoPlugin getPlugin() {
        // TODO Auto-generated method stub
        return null;
    }
    public void setPlugin(INanoPlugin plugin) {
        // TODO Auto-generated method stub
        
    };
    public synchronized IStoredQuery getQuery() {
        return tableModel.getQuery();
    }
    public synchronized void setQuery(IStoredQuery query) throws AmbitException, SQLException {
        Connection c = ((DataSource)getWorkflowContext().get(DBWorkflowContext.DATASOURCE)).getConnection();
        tableModel.setConnection(c);
        tableModel.setQuery(query);
        tableModel.setProfile((Profile)getWorkflowContext().get(DBWorkflowContext.PROFILE));

        
    }
}
