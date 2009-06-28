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
import java.beans.PropertyChangeEvent;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;
import javax.swing.JComponent;

import ambit2.base.data.Profile;
import ambit2.base.data.StructureRecord;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.IDBProcessor;
import ambit2.db.SessionID;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.results.StoredQueryTableModel;
import ambit2.db.search.IStoredQuery;
import ambit2.ui.editors.IAmbitEditor;
import ambit2.ui.table.IBrowserMode.BrowserMode;
import ambit2.workflow.DBWorkflowContext;

import com.microworkflow.process.WorkflowContext;
import com.microworkflow.ui.IWorkflowContextFactory;


public class QueryResultsPanel extends AbstractStructureBrowserPanel<IStoredQuery,StoredQueryTableModel> 
											implements IAmbitEditor<IStoredQuery>, IDBProcessor<IStoredQuery, IStoredQuery> {

    /**
     * 
     */
    private static final long serialVersionUID = -779943857847100493L;
    protected boolean editable;
    public boolean isEditable() {
		return editable;
	}
	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	public QueryResultsPanel() {
        super((IWorkflowContextFactory)null,BorderLayout.NORTH,BrowserMode.Matrix);
    }	
	public QueryResultsPanel(IWorkflowContextFactory wfcfactory,String controlsPosition,BrowserMode mode) {
        super(wfcfactory,controlsPosition,mode);
    }
    public QueryResultsPanel(IWorkflowContextFactory wfcfactory,BrowserMode mode) {
    	this(wfcfactory,BorderLayout.NORTH,mode);
    }    
    public QueryResultsPanel(WorkflowContext wfcontext,BrowserMode mode) {
        this(wfcontext,BorderLayout.NORTH,mode);
    }
    public QueryResultsPanel(WorkflowContext wfcontext,String controlsPosition,BrowserMode mode) {
        super(wfcontext,controlsPosition,mode);
    }    
    @Override
    protected StoredQueryTableModel createTableModel() {
    	return new StoredQueryTableModel();
    }
    @Override
    protected void processRecord(int record) {
		IStructureRecord struc = new StructureRecord();
		tableModel.update(record, struc);
		if(getWorkflowContext()!=null) {
			getWorkflowContext().put(DBWorkflowContext.RECORD,null);
			getWorkflowContext().put(DBWorkflowContext.RECORD,struc);
		}
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
        } else if (DBWorkflowContext.DBCONNECTION_URI.equals(arg0.getPropertyName())) {
            	if (arg0.getNewValue() == null) 
            	try {
            		tableModel.setConnection(null);
            	} catch (Exception x) {
            		x.printStackTrace();
            	}
        } else   if (DBWorkflowContext.PROFILE.equals(arg0.getPropertyName())) {
        	tableModel.setProfile(DBWorkflowContext.PROFILE,(Profile)getWorkflowContext().get(DBWorkflowContext.PROFILE));
        //} else   if (DBWorkflowContext.CALCULATED.equals(arg0.getPropertyName())) {
        //	tableModel.setProfile(DBWorkflowContext.CALCULATED,(Profile)getWorkflowContext().get(DBWorkflowContext.CALCULATED));
        } else   if (DBWorkflowContext.ENDPOINTS.equals(arg0.getPropertyName())) {
        	tableModel.setProfile(DBWorkflowContext.ENDPOINTS,(Profile)getWorkflowContext().get(DBWorkflowContext.ENDPOINTS));
        }
        
        
        
    }

    public synchronized IStoredQuery getObject() {
        return tableModel.getQuery();
    }
    public void setObject(IStoredQuery query) {
    	try {
    		tableModel.setQuery(query);
    	} catch (Exception x) {
    		//x.printStackTrace();
    	}
    	
    }
    public synchronized void setQuery(IStoredQuery query) throws AmbitException {
    	try {
	        Connection c = ((DataSource)getWorkflowContext().get(DBWorkflowContext.DATASOURCE)).getConnection();
	        tableModel.setConnection(c);
	        setObject(query);
	        tableModel.setProfile(DBWorkflowContext.PROFILE,(Profile)getWorkflowContext().get(DBWorkflowContext.PROFILE));
	       // tableModel.setProfile(DBWorkflowContext.CALCULATED,(Profile)getWorkflowContext().get(DBWorkflowContext.CALCULATED));
	        tableModel.setProfile(DBWorkflowContext.ENDPOINTS,(Profile)getWorkflowContext().get(DBWorkflowContext.ENDPOINTS));	        
    	} catch (SQLException x) {
    		throw new AmbitException(x);
    	}
        
    }
    @Override
    public String toString() {
    	return "Search results";
    }
    public JComponent getJComponent() {
    	return this;
    }
    public boolean confirm() {
    	try { 	close(); } catch (Exception x) {}
    	return true;
    }
    public Connection getConnection() {
    	return tableModel.getConnection();
    }
    public void setConnection(Connection connection) throws DbAmbitException {
    	try {
    	tableModel.setConnection(connection);
    	} catch (SQLException x) {
    		throw new DbAmbitException(x);
    	}
    	
    }
    public IStoredQuery process(IStoredQuery target) throws AmbitException {
    	return target;
    }
    public void close() throws SQLException {
    	if (getConnection()!=null)
    		getConnection().close(); 
    	
    }
    public SessionID getSession() {

    	return null;
    }
    public void setSession(SessionID session) {
    	
    }
    public void open() throws DbAmbitException {
    	setObject(getObject());
    }
    public long getID() {
    	return 0;
    }
    
}
