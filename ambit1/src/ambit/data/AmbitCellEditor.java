/* AmbitCellEditor.java
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

package ambit.data;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

/**
 * Implementation of {@link javax.swing.table.TableCellEditor} for {@link ambit.data.AmbitObject}
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-6-6
 */
public class AmbitCellEditor extends AbstractCellEditor implements
        TableCellEditor, ActionListener {
    protected JTextField defaultEditor;
    protected JButton button = null;
    protected static final String EDIT = "...";
    protected AmbitObject object = null;
    protected JTable table = null;
    /**
     * 
     */
    public AmbitCellEditor() {
        super();
        button = new JButton();
        button.setActionCommand(EDIT);
        button.addActionListener(this);
        defaultEditor = new JTextField();
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
     */
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        if (value instanceof AmbitObject) {
	        this.table = table;
	        button.setText(value.toString());
	        object = (AmbitObject)value;
	        return button;
        } else {
        	if (value != null)
        		defaultEditor.setText(value.toString());
            return defaultEditor;
        }
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if (EDIT.equals(e.getActionCommand())) {
            //The user has clicked the cell, so
            //bring up the dialog.
            if (object != null) 
                JOptionPane.showMessageDialog(table,object.editor(object.isEditable()).getJComponent(),object.toString(),JOptionPane.PLAIN_MESSAGE);
            fireEditingStopped(); //Make the renderer reappear.

        }

    }

    /* (non-Javadoc)
     * @see javax.swing.CellEditor#getCellEditorValue()
     */
    public Object getCellEditorValue() {
        return object;
    }

}
