/* RecordsBrowserPanel.java
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
import java.beans.PropertyChangeEvent;
import java.util.List;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.ui.table.StructureRecordsTableModel;
import ambit2.ui.table.IBrowserMode.BrowserMode;
import ambit2.workflow.DBWorkflowContext;

import com.microworkflow.process.WorkflowContext;

public class RecordsBrowserPanel extends AbstractStructureBrowserPanel<List<IStructureRecord>, StructureRecordsTableModel> {
    /**
	 * 
	 */
	private static final long serialVersionUID = -2532610383622328873L;

	public RecordsBrowserPanel(WorkflowContext wfcontext,BrowserMode mode) {
        super(wfcontext,BorderLayout.NORTH,mode);
    }

	@Override
	protected StructureRecordsTableModel createTableModel() {
		return new StructureRecordsTableModel();
	}

	@Override
	protected List<IStructureRecord> getQuery() {
		return tableModel.getRecords();
	}

	@Override
	protected void processRecord(int record) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void setQuery(List<IStructureRecord> object) throws AmbitException {
		tableModel.setRecords(object);
		
	}

	@Override
	protected void animate(PropertyChangeEvent arg0) {
		  if (DBWorkflowContext.RECORDS.equals(arg0.getPropertyName())) {
	            Object o = arg0.getNewValue();
                try {
                    setQuery((List<IStructureRecord>)o);
                } catch (Exception x) {
                    x.printStackTrace();
                }
	        } else if (DBWorkflowContext.RECORD.equals(arg0.getPropertyName())) {
	                try {
	                    setQuery(getQuery());
	                } catch (Exception x) {
	                    x.printStackTrace();
	                }
		     } 	        	
		  /*else if (DBWorkflowContext.DBCONNECTION_URI.equals(arg0.getPropertyName())) {
	            	if (arg0.getNewValue() == null) 
	            	try {
	            		tableModel.setConnection(null);
	            	} catch (Exception x) {
	            		x.printStackTrace();
	            	}
	        } 
	        /*else   if (DBWorkflowContext.PROFILE.equals(arg0.getPropertyName())) {
	        	tableModel.setProfile((Profile)getWorkflowContext().get(DBWorkflowContext.PROFILE));
	        }
	        */
	        
		
	}
}
