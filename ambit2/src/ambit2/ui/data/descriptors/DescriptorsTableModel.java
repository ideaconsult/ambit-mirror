/* DescriptorsTableModel.java
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

package ambit2.ui.data.descriptors;

import ambit2.data.descriptors.DescriptorDefinition;
import ambit2.data.descriptors.DescriptorGroups;
import ambit2.data.descriptors.DescriptorsList;
import ambit2.data.literature.LiteratureEntry;
import ambit2.ui.data.AbstractPropertyTableModel;

/**
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-6-6
 */
public class DescriptorsTableModel extends AbstractPropertyTableModel {
    protected String[] columnNames = {
            "Name",
            "Units",
            "Atom descriptor",
            "Note",
            "Default error",
            "Groups",
            "Reference",
            
    };

    /**
     * 
     */
    public DescriptorsTableModel(DescriptorsList descriptors) {
        super(descriptors);
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount() {
        return columnNames.length;
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
        return ((DescriptorsList) object).size();
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
       DescriptorDefinition d = ((DescriptorsList) object).getDescriptorDef(rowIndex); 
       switch (columnIndex) {
       case 0: return d.getName();
       case 1: return d.getUnits();
       case 2: return new Boolean(d.isLocal());
       case 3: return d.getRemark();
       case 4: return new Double(d.getDefaultError());
       case 5: return d.getDescriptorGroups();
       case 6: return d.getReference();
       default: return "";
       }
    }
    /* (non-Javadoc)
     * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
     */
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        DescriptorDefinition d = ((DescriptorsList) object).getDescriptorDef(rowIndex); 
        switch (columnIndex) {
        case 0: { d.setName(aValue.toString()); return; }
        case 1: { d.setUnits(aValue.toString()); return; }
        case 2: { d.setLocal(((Boolean) aValue).booleanValue()); return; }
        case 3: { d.setRemark(aValue.toString()); return; }
        case 4: { d.setDefaultError(((Double) aValue).doubleValue()); return; }
        case 5: return;
        case 6: return;
        default: return;
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
        case 2: return Boolean.class;
        case 4: return Double.class;
        case 5: return DescriptorGroups.class;
        case 6: return LiteratureEntry.class;
        default: return super.getColumnClass(columnIndex);
        }
        
    }
    /* (non-Javadoc)
     * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
     */
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

}
