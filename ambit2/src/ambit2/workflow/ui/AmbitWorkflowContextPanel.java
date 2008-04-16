/*
Copyright (C) 2005-2008  

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
*/

package ambit2.workflow.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;
import javax.sql.rowset.CachedRowSet;
import javax.swing.AbstractAction;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.repository.QueryID;
import ambit2.ui.data.ImageCellRenderer;
import ambit2.workflow.CachedRowSetTableModel;
import ambit2.workflow.DBWorkflowContext;

import com.microworkflow.ui.IWorkflowContextFactory;
import com.microworkflow.ui.WorkflowContextListener;
import com.sun.rowset.CachedRowSetImpl;

public class AmbitWorkflowContextPanel extends WorkflowContextListener  {
    protected CachedRowSetTableModel srtm;
    private static final long serialVersionUID = 2854619268267349642L;
    public AmbitWorkflowContextPanel(IWorkflowContextFactory wfcfactory) {
        super(wfcfactory);

        srtm = new CachedRowSetTableModel();
        JTable table = new JTable(srtm);
        table.setRowHeight(150);
        table.setDefaultRenderer(IAtomContainer.class, new ImageCellRenderer(new Dimension(150,150)));
        //table.setPreferredSize(new Dimension(200,200));
        add(new JScrollPane(table), BorderLayout.CENTER);
        JToolBar b = new JToolBar();
        b.add(new AbstractAction("<<") {
           public void actionPerformed(ActionEvent arg0) {
               try {
                srtm.previousPage();
               } catch (SQLException x) {
                   x.printStackTrace();
               }
                
            } 
        });
        b.add(new AbstractAction(">>") {
            public void actionPerformed(ActionEvent arg0) {
                try {
                 srtm.nextPage();
                } catch (SQLException x) {
                    x.printStackTrace();
                }
                 
             } 
         });        
        add(b,BorderLayout.NORTH);

        
        
    }
    @Override
    protected void animate(PropertyChangeEvent arg0) {
        	if ("Result".equals(arg0.getPropertyName())) {
        		Object o = getWorkflowContext().get("Result");
        		if (o instanceof CachedRowSet)
        			srtm.setRecords((CachedRowSet)o);
        		else if (o instanceof QueryID) {
        			try {
        			srtm.setRecords(getQuery(((QueryID)o)));
        			} catch (Exception x) {
        				x.printStackTrace();
        			}
        		}
        	}
    }
    
    protected CachedRowSet getQuery(QueryID query) throws SQLException {
        CachedRowSet q = new CachedRowSetImpl();
        q.setPageSize(5);
        q.setCommand("SELECT s.idstructure,selected,uncompress(structure),format FROM query_results as q join structure as s using (idstructure) where idquery="+query.getId());
        
        Connection c = ((DataSource)wfcfactory.getWorkflowContext().get(DBWorkflowContext.DATASOURCE)).getConnection();
        q.execute(c);
        //c.close();
        return q;
    	
    }
    @Override
    public void clear() {
    	// TODO Auto-generated method stub
    	
    }
/*
    private synchronized void setWorkflowContext(WorkflowContext wfc) {
        clear();
        if (this.getWorkflowContext() != null) {
            for (String p : properties)
                this.getWorkflowContext().removePropertyChangeListener(p,this);
            this.getWorkflowContext().removePropertyChangeListener(WorkflowContextEvent.WF_ANIMATE,this);            
        }
        

        if (wfc != null) {
            for (String p : properties)
                this.getWorkflowContext().addPropertyChangeListener(p,this);
            this.getWorkflowContext().addPropertyChangeListener(WorkflowContextEvent.WF_ANIMATE,this);   
        }
     }
     */
}


