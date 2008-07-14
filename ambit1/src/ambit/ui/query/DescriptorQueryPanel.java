/* QueryPanel.java
 * Author: Nina Jeliazkova
 * Date: 2006-4-12 
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

package ambit.ui.query;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.ActionMap;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import ambit.database.query.DescriptorQuery;
import ambit.database.query.DescriptorQueryList;
import ambit.ui.AmbitColors;

/**
 * User interface for specifying database query by descriptors. See example for {@link ambit.database.data.AmbitDatabaseToolsData}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-4-12
 */
public class DescriptorQueryPanel extends JPanel  {
	protected DescriptorQueryTableModel tableModel ;
    protected JScrollPane scrollPane;
    protected String caption = "Search by descriptors and distance between atoms";
    /**
     * 
     */
    public DescriptorQueryPanel(DescriptorQueryList list, ActionMap actions, boolean captionOnly) {
        super();
        addWidgets(list,actions,captionOnly);
    }
    public DescriptorQueryPanel(DescriptorQueryList list, ActionMap actions, String caption, boolean captionsOnly) {
        super();
        this.caption = caption;
        addWidgets(list,actions,captionsOnly);
    }

    /**
     * @param isDoubleBuffered
     */
    public DescriptorQueryPanel(DescriptorQueryList list, ActionMap actions,boolean isDoubleBuffered, boolean captionsOnly) {
        super(isDoubleBuffered);
        addWidgets(list,actions,captionsOnly);
    }

    /**
     * @param layout
     */
    public DescriptorQueryPanel(DescriptorQueryList list, ActionMap actions,LayoutManager layout, boolean captionsOnly) {
        super(layout);
        addWidgets(list,actions,captionsOnly);
    }

    /**
     * @param layout
     * @param isDoubleBuffered
     */
    public DescriptorQueryPanel(DescriptorQueryList list, ActionMap actions,LayoutManager layout, boolean isDoubleBuffered, boolean captionsOnly) {
        super(layout, isDoubleBuffered);
        addWidgets(list,actions,captionsOnly);
    }
    protected void addWidgets(DescriptorQueryList list, ActionMap actions, boolean captionsOnly) {
        setLayout(new BorderLayout());
        add(new JLabel(caption),BorderLayout.NORTH);
        //setBorder(BorderFactory.createTitledBorder(caption));
        setBackground(AmbitColors.BrightClr);
        setForeground(AmbitColors.DarkClr);
        
        tableModel = new DescriptorQueryTableModel(list,captionsOnly);
        JTable table = new JTable(tableModel,createTableColumnModel(tableModel,captionsOnly));
        table.setSurrendersFocusOnKeystroke(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowSelectionAllowed(false);
        table.getTableHeader().setReorderingAllowed(false);
        table.setRowHeight(24);
        
        TableColumn col;
		
		int[] pSize = {32,240,32,32,64,32,32,32};
		int[] mSize = {24,120,32,32,64,32,32,32};
		for (int i=0; i < table.getColumnCount();i++) {
			col = table.getColumnModel().getColumn(i);
			col.setPreferredWidth(pSize[i]);		col.setMinWidth(mSize[i]);
		}
		//table.setShowGrid(false);
		table.setShowHorizontalLines(true);
		table.setShowVerticalLines(false);
		
		
		
        
		
		scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(420,360));
        scrollPane.setMinimumSize(new Dimension(370,120));        
        scrollPane.setAutoscrolls(true);
        
        //tPane.addTab("Search descriptors",scrollPane);
        add(scrollPane,BorderLayout.CENTER);
        /*
        JToolBar t = new JToolBar();
        //t.setBackground(Color.white);
        t.setFloatable(false);
        //t.setBorder(BorderFactory.createTitledBorder("Search by descriptors and distance between atoms"));
        Object[] keys = actions.allKeys();
        if (keys != null) {
	        for (int i=0; i < keys.length;i++)
	        	t.add(actions.get(keys[i]));
	        add(t,BorderLayout.SOUTH);
        }
        */
        if (actions != null) {
        Object[] keys = actions.allKeys();
        if (keys != null) {
	        for (int i=0; i < keys.length;i++) {
	        	add(new DescriptorSearchActionPanel(list,
	        	        actions.get(keys[i]),keys[i].toString()),BorderLayout.SOUTH);
	        	break;
	        }	
        }
        }
        
    }    
    protected TableColumnModel createTableColumnModel(AbstractTableModel tableModel, boolean captionsOnly) {
        DefaultTableColumnModel cm = new DefaultTableColumnModel();
        TableColumn c = new TableColumn(0);
        c.setHeaderValue(tableModel.getColumnName(0));
        cm.addColumn(c);
        
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
    
        	public Component getTableCellRendererComponent(JTable table, Object arg1, boolean arg2, boolean arg3, int row, int column) {
        		Component c = super.getTableCellRendererComponent(table, arg1, arg2, arg3, row, column);
        		Object o = table.getValueAt(row, column);
        		setToolTipText(((DescriptorQuery) o).getRemark());
        		return c;
        	}
        };
        c = new TableColumn(1);
        c.setHeaderValue(tableModel.getColumnName(1));
        c.setCellRenderer(renderer);
        cm.addColumn(c);
        if (captionsOnly) return cm;
        
        TableColumn atom1Column = new TableColumn(2);
        atom1Column.setHeaderValue(tableModel.getColumnName(2));
        cm.addColumn(atom1Column);
        
        BorderCellRenderer brenderer = new BorderCellRenderer();
        brenderer.setToolTipText("Double click here to edit ");
        
        TableColumn atom2Column = new TableColumn(3);
        atom1Column.setHeaderValue(tableModel.getColumnName(3));
        cm.addColumn(atom2Column);
        JComboBox comboBox1 = new JComboBox();
        JComboBox comboBox2 = new JComboBox();
        
        String[] atoms = {"C","N","O","P","S","Br","Cl","F","I"};
        
        for (int i=0; i < atoms.length;i++) {
            comboBox1.addItem(atoms[i]);
            comboBox2.addItem(atoms[i]);
        }    
        atom1Column.setCellEditor(new DefaultCellEditor(comboBox1));
        atom2Column.setCellEditor(new DefaultCellEditor(comboBox2));

        TableColumn conditionColumn = new TableColumn(4);
        conditionColumn.setHeaderValue(tableModel.getColumnName(4));
        cm.addColumn(conditionColumn);
        JComboBox comboBox = new JComboBox();
        for (int i=0; i < DescriptorQuery.conditions.length;i++)
            comboBox.addItem(DescriptorQuery.conditions[i]);
        conditionColumn.setCellEditor(new DefaultCellEditor(comboBox));
        
        renderer = new DefaultTableCellRenderer();
        renderer.setToolTipText("Click here ");
        conditionColumn.setCellRenderer(renderer);
        renderer = new DefaultTableCellRenderer();
        renderer.setToolTipText("Click here ");
        atom1Column.setCellRenderer(renderer);
        renderer = new DefaultTableCellRenderer();
        renderer.setToolTipText("Click here ");
        atom2Column.setCellRenderer(renderer);
        
        
        
        c = new TableColumn(5);
        c.setCellRenderer(brenderer);
        cm.addColumn(c);
        
        c = new TableColumn(6);
        cm.addColumn(c);
        
        c = new TableColumn(7);
        c.setCellRenderer(brenderer);
        cm.addColumn(c);
        
        return cm;
    }
    public void setDescriptors(DescriptorQueryList descriptors) {
    	tableModel.setDescriptors(descriptors);
    }
    
}


class DescriptorQueryEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
    DescriptorQuery query = null;
    protected JButton button;
    protected JTable table;
    protected final String command = "Edit";
    public DescriptorQueryEditor() {
        button = new JButton();
        button.setActionCommand(command);
        button.addActionListener(this);
        button.setBorderPainted(false);
    }
    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if (command.equals(e.getActionCommand())) {
            //The user has clicked the cell, so
            //bring up the dialog.
            JOptionPane.showMessageDialog(
                    (Component) null,"edit");

            fireEditingStopped(); //Make the renderer reappear.

        } else { //User pressed dialog's "OK" button.
            //currentColor = colorChooser.getColor();
        }


    }
    /* (non-Javadoc)
     * @see javax.swing.CellEditor#getCellEditorValue()
     */
    public Object getCellEditorValue() {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
     */
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        this.query = (DescriptorQuery) value;
        this.table = table;
        return button;
    }

}
