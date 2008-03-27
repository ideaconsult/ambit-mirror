/* ModelTable.java
 * Author: Nina Jeliazkova
 * Date: 2006-6-6 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2006  Ideaconsult Ltd.
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

package ambit2.ui.data.model;

import ambit2.data.model.Model;
import ambit2.data.model.ModelType;
import ambit2.ui.data.AbstractPropertyTableModel;

/**
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-6-6
 */
public class ModelTable extends AbstractPropertyTableModel {
    protected String[] rowNames = {"Name","Note","Keywords","Reference",
            "N points","N descriptors",
            "Equation",
            "Model type",
            "Statistics"
            };
    protected String[] columnNames = {"Parameter","Value"};
        
    
    /**
     * 
     */
    public ModelTable(Model qsarModel) {
        super(qsarModel);
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
        return rowNames.length;
    }
    
    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (object == null) return "";
        if (columnIndex==0) return rowNames[rowIndex];
       Model qsarModel = (Model) object;
       switch (rowIndex) {
       case 0: return qsarModel.getName();
       case 1: return qsarModel.getNote();
       case 2: return qsarModel.getKeywords();
       case 3: return qsarModel.getReference();
       case 4: return Integer.toString(qsarModel.getN_Points());
       case 5: return Integer.toString(qsarModel.getN_Descriptors());
       case 6: return qsarModel.getEquation();       
       case 7: return qsarModel.getModelType();
       case 8: return qsarModel.getStats();
       
       default : return "";
       }
    }
    /* (non-Javadoc)
     * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
     */
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex==0) return;
        Model qsarModel = (Model) object;
        switch (rowIndex) {
        case 0: {qsarModel.setName(aValue.toString()); return;}
        case 1: {qsarModel.setNote(aValue.toString()); return;}
        case 2: {qsarModel.setKeywords(aValue.toString()); return;}
        
        case 7: {qsarModel.setModelType(new ModelType(aValue.toString())); return;}
        case 4: {qsarModel.setN_Points(Integer.parseInt(aValue.toString())); return;}
        //case 3: {qsarModel.setModelType(aValue.toString()); return;}
        
        case 6: {qsarModel.setEquation(aValue.toString()); return;}
        
        
        }
    }
    public boolean isCellEditable(int rowIndex, int columnIndex) {
    	return (columnIndex== 1) && (
    			(rowIndex == 0) || (rowIndex == 1) || (rowIndex == 2) 
    			|| (rowIndex == 4) || (rowIndex == 5) || (rowIndex == 8)
    			);
    }
    public String getColumnName(int column) {

    	return columnNames[column];
    }
}
