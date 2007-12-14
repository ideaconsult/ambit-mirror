/* ModelStatisticsTableModel.java
 * Author: Nina Jeliazkova
 * Date: 2006-6-13 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2006  Nina Jeliazkova
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

package ambit.ui.data.model;

import ambit.data.model.ModelStatistics;
import ambit.ui.data.AbstractPropertyTableModel;

/**
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-6-13
 */
public class ModelStatisticsTableModel extends AbstractPropertyTableModel {
    
    protected static String[] columnNames={"Parameter","Value"};
    protected static String[][] rowNames = {
    	{"R","","R2","","R2 adjusted",""},
    	{"Q","","Q2","","F"},
    	{"P","","RMSE","","RMSE crossvalidation",""}
    };
    
    /**
     * 
     */
    public ModelStatisticsTableModel(ModelStatistics stats) { 
        super(stats);
    }
    public int getColumnCount() {
    	return 6;
    }
    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
        return 3;
    }

    
    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
    	int col = (columnIndex % 2);
        if (col==0) return rowNames[rowIndex][columnIndex];
        
        ModelStatistics stats = (ModelStatistics) object;
        switch (rowIndex) {
        case 0: 
	        switch (columnIndex) {
	        case 1: { return new Double(stats.getR());}	        
	        case 3: { return new Double(stats.getR2());}
	        case 5: { return new Double(stats.getR2adj());}
	        }
        case 1:
        	switch (columnIndex) {
	        case 1: { return new Double(stats.getQ());}
	        case 3: { return new Double(stats.getQ2());}
	        case 5: { return new Double(stats.getF());}
        	}
        case 2:
        	switch (columnIndex) {
	        case 1: { return new Double(stats.getP());}
	        case 3: { return new Double(stats.getRMSE());}
	        case 5: { return new Double(stats.getRMSE_CV());}
	        default : return "";
        }
        default : return "";
        }
    }
    public boolean isCellEditable(int rowIndex, int columnIndex) {
    	return (columnIndex % 2)==1;
    }
    /* (non-Javadoc)
     * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
     */
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    	int col = (columnIndex % 2);
        if (col==0) return;
        
        ModelStatistics stats = (ModelStatistics) object;
        switch (rowIndex) {
        case 0: 
	        switch (columnIndex) {
	        case 1: { stats.setR(((Double)aValue).doubleValue());}	        
	        case 3: { stats.setR2(((Double)aValue).doubleValue());}
	        case 5: { stats.setR2adj(((Double)aValue).doubleValue());}
	        }
	        return;
        case 1:
        	switch (columnIndex) {
	        case 1: { stats.setQ(((Double)aValue).doubleValue());}
	        case 3: { stats.setQ2(((Double)aValue).doubleValue());}
	        case 5: { stats.setF(((Double)aValue).doubleValue());}
        	}
        	return;
        case 2:
        	switch (columnIndex) {
	        case 1: { stats.setP(((Double)aValue).doubleValue());}
	        case 3: { stats.setRMSE(((Double)aValue).doubleValue());}
	        case 5: { stats.setRMSE_CV(((Double)aValue).doubleValue());}
        	}
        	return;
        default : return;
        }        
    }
  
    /* (non-Javadoc)
     * @see javax.swing.table.AbstractTableModel#getColumnName(int)
     */
    public String getColumnName(int column) {
        return columnNames[column];
    }
    /* (non-Javadoc)
     * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
     */
    public Class getColumnClass(int columnIndex) {
        switch (columnIndex) {
        case 0: return String.class;
        case 1: return Double.class;
        case 2: return String.class;
        case 3: return Double.class;
        case 4: return String.class;
        case 5: return Double.class;
        default: return super.getColumnClass(columnIndex);
        }
    }
}
