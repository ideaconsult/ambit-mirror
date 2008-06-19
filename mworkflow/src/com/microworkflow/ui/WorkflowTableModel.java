/* WorkflowTableModel.java
 * Author: Nina Jeliazkova
 * Date: Mar 16, 2008 
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

package com.microworkflow.ui;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import com.microworkflow.process.Activity;

public class WorkflowTableModel extends AbstractTableModel  {
    ArrayList<Activity> activities;
    protected String[] columnNames = {"No.","Running","Activity"};
    protected int selected = -1;
    protected static final String PTR= ">>";
    protected static final String NA= "";
    /**
     * 
     */
    private static final long serialVersionUID = -3757282646931180423L;
    public WorkflowTableModel(Activity activity) {
        super();
        activities = new ArrayList<Activity>();
        setActivity(activity);
    }
    public int getColumnCount() {
        return 3;
    }

    public int getRowCount() {
        return activities.size();
    }

    public Object getValueAt(int row, int col) {
        switch (col) {
        case 0:
            return row+1;
        case 1:
        	if (selected == row)
        		return PTR; 
        	else return NA;            
        case 2:
            return activities.get(row).getName();

        default:
            return row;
        }
    }
    public void setActivity(Activity activity) {
        activities.clear();
        WorkflowTools.traverseActivity(activity, 0, new ILookAtActivity() {
            public void look(Activity activity, int level) {
                activities.add(activity);
                
            }
        });
        fireTableStructureChanged();        
    }
    public int findRow(Activity activity) {
    	int old_row = getSelected();
        setSelected(activities.indexOf(activity));
        int row = getSelected();
        fireTableCellUpdated(old_row ,1);
        fireTableCellUpdated(row ,1);
        return row;
    }
    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }
	public int getSelected() {
		return selected;
	}
	public void setSelected(int selected) {
		this.selected = selected;
	}

}
