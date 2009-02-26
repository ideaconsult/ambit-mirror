/* PBTMainPanel.java
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

package ambit2.plugin.pbt;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.sql.Connection;
import java.sql.ResultSet;

import javax.sql.DataSource;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import nplugins.shell.INPluginUI;
import nplugins.shell.INanoPlugin;

import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.core.data.IStructureRecord;
import ambit2.db.readers.IRetrieval;
import ambit2.db.readers.RetrieveAtomContainer;
import ambit2.db.search.IQueryObject;
import ambit2.db.search.IStoredQuery;
import ambit2.db.search.QueryExecutor;
import ambit2.workflow.DBWorkflowContext;

import com.microworkflow.process.WorkflowContext;
import com.microworkflow.ui.WorkflowContextListenerPanel;

public class PBTMainPanel extends WorkflowContextListenerPanel implements INPluginUI<INanoPlugin>  {
    /**
	 * 
	 */
	private static final long serialVersionUID = 2943141896545107613L;
	protected JTabbedPane tabbedPane;
	protected PBTWorkBook pbt_workbook;
	//protected boolean hack=true;


    public PBTMainPanel(WorkflowContext wfcontext) {
        this();
        try {
        setWorkflowContext(wfcontext);
        } catch (Exception x) {
        	x.printStackTrace();
        }
    }    
    public PBTMainPanel() {
    	
        tabbedPane = new JTabbedPane();
        tabbedPane.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e) {
        		if (getWorkbook()!=null) {
        			int index = ((JTabbedPane)e.getSource()).getSelectedIndex();
        			HSSFFormulaEvaluator.evaluateAllFormulaCells(getWorkbook().workbook);
        			//for (int i=0; i < getWorkbook().size();i++)
        				getWorkbook().getWorksheet(index).notifyCells(-1,-1);
        			tabbedPane.getComponentAt(index).repaint();
        			
        		}
            }        	
        });
        add(tabbedPane);
        setWorkbook(null);

    }
  
    protected PBTWorkBook getWorkbook() {
    	return pbt_workbook;
    }
    protected void setWorkbook(PBTWorkBook pbt_workbook) {
    	this.pbt_workbook = pbt_workbook;
        try {
        	//hack = true;
        	tabbedPane.removeAll();
        	if (pbt_workbook !=null) {
        	
				for (int i=0; i < pbt_workbook.size();i++)  {
					if (pbt_workbook.getWorksheet(i)!=null)
					tabbedPane.add(pbt_workbook.getTitle(i),
							new JScrollPane(PBTPageBuilder.buildPanel(pbt_workbook.getWorksheet(i),1,1)));
				}
	
		        
				tabbedPane.setSelectedIndex(1);	      
        	}
        } catch (Exception x) {
        	x.printStackTrace();
        } finally {
        	//hack = false;
        }
    }

    @Override
    protected void finalize() throws Throwable {
    	super.finalize();
    }
    @Override
    protected void animate(PropertyChangeEvent event) {
    	//if (hack) return; 
        if (event.getPropertyName().equals(ambit2.workflow.DBWorkflowContext.STOREDQUERY)) {
        	try {
	        	IAtomContainer a = execute(((IStoredQuery) event.getNewValue()));
	        	if (pbt_workbook!= null)
	        		pbt_workbook.getWorksheet(1).setExtendedCell(a, 10,5);
        	} catch (Exception x) {
        		x.printStackTrace();
        	}
        } else if (event.getPropertyName().equals(PBTWorkBook.PBT_WORKBOOK)) {   
        	if (event.getNewValue() instanceof PBTWorkBook) {
        		setWorkbook((PBTWorkBook) event.getNewValue() );
        	}
        }
    }
    
	public IAtomContainer execute(IStoredQuery q) throws Exception {
		IQueryObject<IStructureRecord> query = q.getQuery();
		if (query == null)
			throw new Exception("Undefined query");
     	DataSource datasource = (DataSource) getWorkflowContext().get(DBWorkflowContext.DATASOURCE);
		if (datasource == null)
			throw new Exception("Undefined datasource");
        Connection c = datasource.getConnection();     
		if (datasource == null)
			throw new Exception("Undefined db connection");        
		QueryExecutor  queryExecutor = new QueryExecutor<IQueryObject> ();
        queryExecutor.setConnection(c);            
        queryExecutor.open();
        ResultSet resultSet = null;
        try {
	        resultSet = queryExecutor.process(query);
	        if ((query instanceof IRetrieval)&&(resultSet.next())) {
	        	
	        	Object result = ((IRetrieval) query).getObject(resultSet);
	        	queryExecutor.closeResults(resultSet);
	        	if (result instanceof IStructureRecord) {
	        		
	    	        RetrieveAtomContainer rc = new RetrieveAtomContainer();
	    	        rc.setValue((IStructureRecord)result);
	    	        ResultSet rs1 = queryExecutor.process(rc);
	    	        if (rs1.next()) {
	    	        	IAtomContainer a =  rc.getObject(rs1);
	    	        	queryExecutor.closeResults(rs1);
	    	        	return a;
	    	        }
	    	        return null;	        		
	        	}
	        }
	        
	        return null;
        } catch (Exception x) {
        	throw new Exception();
        } finally {
        	
            queryExecutor.close();            
            if (!c.isClosed()) c.close();	        	
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
        
    }
    @Override
    public String toString() {
    	return "PBT SCREENING TOOL FOR REACH";
    }
}
