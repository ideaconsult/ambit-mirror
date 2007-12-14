/* ProcessorsChainEditor.java
 * Author: Nina Jeliazkova
 * Date: 2006-5-13 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2006  
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

package ambit.processors;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import ambit.data.IAmbitEditor;
import ambit.exceptions.AmbitException;

/**
 * Visualization for {@link ambit.processors.ProcessorsChain}
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-5-13
 */
public class ProcessorsChainEditor extends JScrollPane implements IAmbitEditor {
    protected JTable table;
    /**
     * 
     */
    public ProcessorsChainEditor(ProcessorsChain processor) {
        super(createTable(processor));
        setPreferredSize(new Dimension(300,300));
        setMinimumSize(new Dimension(200,200));
    }
    protected static JTable createTable(ProcessorsChain processor) {
        TableColumnModel cm = new DefaultTableColumnModel();
        TableColumn c = new TableColumn(0);
        c.setMinWidth(32);
        c.setPreferredWidth(32);
        cm.addColumn(c);
   
         
        
        c = new TableColumn(1);
        c.setPreferredWidth(300);
        cm.addColumn(c);
        
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setToolTipText("Click here for details");
        c.setCellRenderer(renderer);
 
        c.setCellEditor(new ProcessorCellEditor());
        JTable table =  new JTable(new ProcesorsChainTableModel(processor),cm);
        //table.setDefaultRenderer(IAmbitProcessor.class, new DefaultprocessorEditor(null));
        table.setRowHeight(24);
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);
        return table;
    }
    /* (non-Javadoc)
     * @see ambit.processors.IAmbitEditor#view(javax.swing.JComponent, boolean)
     */
    public boolean view(Component parent, boolean editable, String title) throws AmbitException {
        return
        JOptionPane.showConfirmDialog(parent,new JScrollPane(getJComponent()),"Processing options",
        		JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE)==JOptionPane.OK_OPTION;
    }

    /* (non-Javadoc)
     * @see ambit.processors.IAmbitEditor#getComponent()
     */
    public JComponent getJComponent() {
			return this;
    }
    /* (non-Javadoc)
     * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
     */
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        return this;
    }
    public void setEditable(boolean editable) {
    	// TODO Auto-generated method stub
    	
    }
	public boolean isEditable() {
		return true;
	}
}

class ProcesorsChainTableModel extends AbstractTableModel {
    protected ProcessorsChain processors;
    public ProcesorsChainTableModel(ProcessorsChain processors) {
        super();
        this.processors = processors;
    }
    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount() {
        return 2;
    }
    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
        return processors.size();
    }
    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
        case 0: { return new Boolean(processors.getProcessor(rowIndex).isEnabled());}
        case 1: { return processors.getProcessor(rowIndex);}
        case 2: { return processors.getProcessor(rowIndex);}
        default: return "";
        }
    }
    /* (non-Javadoc)
     * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
     */
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        switch (columnIndex) {
        case 0: { processors.getProcessor(rowIndex).setEnabled(((Boolean)aValue).booleanValue());}
        }

    }
    /* (non-Javadoc)
     * @see javax.swing.table.AbstractTableModel#getColumnName(int)
     */
    public String getColumnName(int column) {
        switch (column) {
        case 0: { return "Enabled";}
        case 1: { return "Action";}
        case 2: { return "Details";}
        default: return "";
        }

    }
    /* (non-Javadoc)
     * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
     */
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true; //columnIndex==0;
    }
    /* (non-Javadoc)
     * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
     */
    public Class getColumnClass(int columnIndex) {
        switch (columnIndex) {
        case 0: return Boolean.class;
        //case 1: return IAmbitProcessor.class;
        default: return String.class;
        }

    }
    
    
}

class ProcessorCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
    protected JButton button = null;
    protected static final String EDIT = "...";
    protected IAmbitProcessor processor = null;
    protected JTable table = null;
    
    public ProcessorCellEditor() {
        super();
        button = new JButton();
        button.setActionCommand(EDIT);
        button.addActionListener(this);

    }
    /* (non-Javadoc)
     * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
     */
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        this.table = table;
        processor = (IAmbitProcessor)value;
        return button;

    }
    /* (non-Javadoc)
     * @see javax.swing.CellEditor#getCellEditorValue()
     */
    public Object getCellEditorValue() {
        return processor;
    }
    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if (EDIT.equals(e.getActionCommand())) {
            //The user has clicked the cell, so
            //bring up the dialog.
            if (processor != null)
                JOptionPane.showMessageDialog(table,processor.getEditor().getJComponent(),processor.toString(),JOptionPane.PLAIN_MESSAGE);
            fireEditingStopped(); //Make the renderer reappear.

        }


    }

}
