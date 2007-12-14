/* LiteratureEntryTableModel.java
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

package ambit.ui.data.literature;

import ambit.data.literature.JournalEntry;
import ambit.data.literature.LiteratureEntry;
import ambit.ui.data.AbstractPropertyTableModel;

/**
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-6-13
 */
public class LiteratureEntryTableModel extends AbstractPropertyTableModel {
    protected String rowNames[] = {"Authors","Title","Journal","Volume","Pages","Year","WWW"};
    /**
     * 
     */
    public LiteratureEntryTableModel(LiteratureEntry reference) {
        super(reference);
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
        return 7;
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        
        if (columnIndex == 0) return rowNames[rowIndex];
        LiteratureEntry reference = (LiteratureEntry) object;
        if (reference == null) return "NA";
        switch (rowIndex){
        case (0): { return reference.getAuthors();}
        case 1: return reference.getName();
        case 2: return reference.getJournal();
        case 3: return reference.getVolume();
        case 4: return reference.getPages();
        case 5: return Integer.toString(reference.getYear());
        case 6: return reference.getWWW();
        default: return "";
        }
        
    }
    /* (non-Javadoc)
     * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
     */
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 0) return;
        LiteratureEntry reference = (LiteratureEntry) object;
        if (reference == null) return ;
        switch (rowIndex){
        case 1: { reference.setName(aValue.toString()); break; }
        //case 2: { reference.getJournal().setName(aValue.toString()); break; }
        case 2: { reference.setJournal((JournalEntry)aValue); break; }
        case 3: { reference.setVolume(aValue.toString()); break; }
        case 4: { reference.setPages(aValue.toString()); break; }
        case 5: { reference.setYear(Integer.parseInt(aValue.toString())); break; }
        case 6: { reference.setWWW(aValue.toString()); break; }
        default: return;
        }
    }
    
    /* (non-Javadoc)
     * @see javax.swing.table.AbstractTableModel#getColumnName(int)
     */
    public String getColumnName(int column) {
        return "";
    }
    
}
