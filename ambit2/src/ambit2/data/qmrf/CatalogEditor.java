/* CatalogEditor.java
 * Author: Nina Jeliazkova
 * Date: Dec 1, 2007 
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

package ambit2.data.qmrf;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import ambit2.ui.editors.AmbitListEditor;
import ambit2.data.AmbitList;

public class CatalogEditor extends AmbitListEditor {
    public CatalogEditor(Catalog list,
            boolean searchPanel,Dimension dimension) {
        super(list,false,"",dimension);
        setPreferredSize(new Dimension(450,250));
    }
    @Override
    protected JComponent createListPanel(AmbitList list, boolean searchPanel,Dimension dimension) {
        JComponent c = super.createListPanel(list, searchPanel,dimension);
        table.setTableHeader(new JTableHeader(table.getColumnModel()));
        table.setColumnSelectionAllowed(false);
        table.setRowSelectionAllowed(true);
        return c;
    }
    @Override
    protected AmbitListTableModelNew createTableModel(AmbitList list) {
        return new CatalogTableModel((Catalog)list);
    }
    @Override
    protected TableColumnModel createColumnsModel(TableModel model) {
        
        TableColumnModel m = new DefaultTableColumnModel();
        int[] widths = {64,200};
        int[] minwidths = {32,100};
        int start = 1;
        int index = 0;
        for (int i=0;i<model.getColumnCount();i++) {
            TableColumn t = new TableColumn(i);
            
            t.setPreferredWidth(widths[index]);
            t.setMinWidth(minwidths[index]);
            t.setHeaderValue(model.getColumnName(i));
            m.addColumn(t);
            index = 1;
        }
        return m;
    }    
}



class CatalogTableModel extends AmbitListTableModelNew {
    protected ArrayList<String> attributes;
    public CatalogTableModel(Catalog catalog) {
        super(catalog,false);
        String[] a = Catalog.getAttributeNames(catalog.getName());
        attributes = new ArrayList<String>();
        for (int i=0; i < a.length; i++)
            if ("number".equals(a[i])) continue;
            else if (a[i].endsWith("_ref")) continue;
            else attributes.add(a[i]);
    }
    @Override
    public int getColumnCount() {
        if (attributes == null)
            return super.getColumnCount();
        else return attributes.size();
    }
    @Override
    public String getColumnName(int column) {
        if (attributes == null)
            return super.getColumnName(column);
        else 
            if (column==0) return super.getColumnName(column);
            else return attributes.get(column-1);
    }
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if ((attributes == null) || (columnIndex ==0))
            return super.getValueAt(rowIndex, columnIndex);
        else 
            return ((CatalogEntry) ((Catalog)list).getItem(rowIndex)).getProperty(attributes.get(columnIndex-1));
        
    }
    
}