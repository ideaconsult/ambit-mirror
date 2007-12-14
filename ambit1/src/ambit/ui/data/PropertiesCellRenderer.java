/* PropertiesCellRenderer.java
 * Author: Nina Jeliazkova
 * Date: Nov 15, 2006 
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

package ambit.ui.data;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;


public class PropertiesCellRenderer extends AbstractCellEditor implements TableCellRenderer, TableCellEditor, ActionListener {
    /**
     * 
     */
    private static final long serialVersionUID = 140245537688869272L;
    protected HashtableModel model;
    protected JButton button = null;
    protected JScrollPane panel;
    protected Hashtable values;
    protected String number = "";
    protected static final String EDIT = "...";
    protected boolean editable = false;
    
    public PropertiesCellRenderer() {
        super();
        button = new JButton();
        button.setActionCommand(EDIT);
        button.addActionListener(this);
        model = new HashtableModel(null) {
            @Override
            public String getColumnName(int arg0) {
                switch (arg0) {
                case 0: return "#";
                case 1: return number;
                }
                return "";
            }
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return editable;
            }

        };
        JTable table = new JTable(model) {
        	@Override
        	public Component prepareRenderer(TableCellRenderer arg0, int row, int col) {
        		// TODO Auto-generated method stub
        		Component c = super.prepareRenderer(arg0, row, col);
        		if (c instanceof JComponent)
        			((JComponent)c).setToolTipText(getValueAt(row,col).toString());
        		return c;	
        	}
        };
        table.setRequestFocusEnabled(true);
        //table.get
        panel = new JScrollPane(table);
        panel.setBackground(Color.white);
        table.setBackground(Color.white);
        
    }
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof Hashtable) {
            setTable((Hashtable) value);
            
        } 
        return panel;
    }
    
    protected void setTable(Hashtable value) {
        number = value.get("#").toString();
        value.remove("#");
        model.setTable(value);
        values = value;
    }
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (value instanceof Hashtable) {
            setTable((Hashtable) value);
            
        } else values=null;
        button.setText("#"+number);
        return button;
    }
    public Object getCellEditorValue() {
        return values;
    }
    public void actionPerformed(ActionEvent e) {
        if (EDIT.equals(e.getActionCommand())) {
            //The user has clicked the cell, so
            //bring up the dialog.
            if (values != null) {
                model.setTable(values);    
                
                editable = true;
                JPanel p = new JPanel(new BorderLayout());
                p.add(panel,BorderLayout.CENTER);
                JToolBar b = new JToolBar();
                /*
                b.add(new AbstractAction("Add"){
                    public void actionPerformed(ActionEvent arg0) {
                        model.put("New property","New value");
                        
                    }
                });
                */
                p.add(b,BorderLayout.NORTH);
                JOptionPane.showMessageDialog(null,p,"Edit properties",JOptionPane.PLAIN_MESSAGE);
                System.out.println(model.getRowCount());
                editable = false;
            }    
            fireEditingStopped(); //Make the renderer reappear.

        }


    }

}
