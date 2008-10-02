/* TableHeaderTest.java
 * Author: Nina Jeliazkova
 * Date: Sep 5, 2008 
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

package ambit2.ui.test;


import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ambit2.ui.EditableHeader;
import ambit2.ui.EditableHeaderTableColumn;
import ambit2.ui.HeaderComboBoxModel;
import ambit2.ui.QueryBrowser;
import ambit2.ui.table.IHeaderAction;
/**
 * http://crionics.com/products/opensource/faq/swing_ex/SwingExamples.html
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Sep 5, 2008
 */
public class TableHeaderTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
    @Test
    public void test() throws Exception {
        final JTable table = new JTable(new Object[][] {{"a1","b1","c1"},{"a2","b1","c3"},{"a3","b3","c3"}}
            , new Object[] {"1","2","3"});
        TableColumnModel columnModel = table.getColumnModel();
        table.setTableHeader(new EditableHeader(columnModel,true));
        
        
        //String[] items = {"Header","Sort ascending","Sort descending","All","Custom"};
        /*
        JComboBox combo = new JComboBox();
        for (int i=0;i<items.length;i++) {
          combo.addItem(items[i]);
        }
        */
        //ComboRenderer renderer = new ComboRenderer(items);
        
        
        IHeaderAction[] actions = QueryBrowser.getHeaderActions();
        EditableHeaderTableColumn col;
        // column 1
        col = (EditableHeaderTableColumn)table.getColumnModel().getColumn(0);    
        //col.setHeaderValue(combo.getItemAt(0));    
        //col.setHeaderRenderer(renderer);   
        
        col.setHeaderEditor(new DefaultCellEditor(new JComboBox(new HeaderComboBoxModel(table,0,actions))));
        
        // column 3
        col = (EditableHeaderTableColumn)table.getColumnModel().getColumn(1);    
        //col.setHeaderValue(combo.getItemAt(0));    
        //col.setHeaderRenderer(renderer);   
        col.setHeaderEditor(new DefaultCellEditor(new JComboBox(new HeaderComboBoxModel(table,1,actions))));
        
        JScrollPane scrollPane = new JScrollPane(table);


        JOptionPane.showMessageDialog(null,scrollPane,"",JOptionPane.PLAIN_MESSAGE,null);
    }
    
    class ComboRenderer extends JComboBox implements TableCellRenderer {
    
    /**
         * 
         */
        private static final long serialVersionUID = -9141108065446072799L;

    ComboRenderer(String[] items) {
        for (int i=0;i<items.length;i++) {
        addItem(items[i]);
        }
    }

    public Component getTableCellRendererComponent(
            JTable table, Object value,
            boolean isSelected, boolean hasFocus, 
            int row, int column) {
        System.out.println("row " +row);
        System.out.println("col " +column);
        if ("Header".equals(value))
            setSelectedItem(table.getColumnName(column));
        return this;
    }
    
    }
    
   
}


