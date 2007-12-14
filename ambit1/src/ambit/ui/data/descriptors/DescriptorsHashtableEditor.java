/* DescriptorsHashtableEditor.java
 * Author: Nina Jeliazkova
 * Date: 2006-5-31 
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

package ambit.ui.data.descriptors;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.openscience.cdk.qsar.IDescriptor;

import ambit.data.IAmbitEditor;
import ambit.data.descriptors.DescriptorDefinition;
import ambit.data.descriptors.DescriptorsHashtable;
import ambit.exceptions.AmbitException;
import ambit.ui.data.HashtableModel;

import com.l2fprod.common.swing.renderer.BooleanCellRenderer;

/**
 * Visualization of {@link ambit.data.descriptors.DescriptorsHashtable}
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-5-31
 */
public class DescriptorsHashtableEditor implements IAmbitEditor {
    protected JScrollPane pane = null;
    protected JPanel panel;
    protected HashtableModel model = null;
    //protected DescriptorsHashtable lookup;
    
    protected boolean editable = false;
	/**
     * Creates a read-only JTable within a JScrollPane, containing {@link ambit.data.descriptors.DescriptorsHashtable}.
     * Three columns - selected, descriptor name, remark  
	 * @param lookup
	 */
    public DescriptorsHashtableEditor(DescriptorsHashtable lookup) {
    	this(lookup,false);
    }
    /**
     * Creates a JTable within a JScrollPane, containing {@link ambit.data.descriptors.DescriptorsHashtable}.
     * Three columns - selected, descriptor name, remark  
     * @param lookup descriptors
     * @param editable false if read only
     */
    public DescriptorsHashtableEditor(DescriptorsHashtable lookup, boolean editable) {
        super();
        this.editable = editable;
        model = new HashtableModel(lookup,true) {
        	public String getColumnName(int arg0) {
        		switch (arg0) {
        		case 0: return "Selected";
        		case 1: return "Descriptor";
        		case 2: return "Remark";
        		default: return "";
        		}
        	}
        	public int getColumnCount() { return 3;};
            public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
  
                   switch (columnIndex) {
                   case 2: {
                    enabled[rowIndex] = ((Boolean) aValue).booleanValue();
                    break;
                   }
                   };
            }
        	public Object getValueAt(int row, int col) {
        		Object d = super.getValueAt(row, col);
        		switch (col) {
        		case 0:
        			if (d ==null) return "";
            		else {
            			String s = ((IDescriptor) d).getSpecification().getImplementationTitle();
            			return s.substring(s.lastIndexOf('.')+1);
            		}
        		case 1: return ((DescriptorDefinition) d).getRemark();
                case 2: return new Boolean(enabled[row]);
        		default: return d;
        		}
        		
        	}
        	public boolean isCellEditable(int rowIndex, int columnIndex) {
        		return columnIndex ==2; //isEditable() && (columnIndex == 0) ;//super.isCellEditable(rowIndex, columnIndex);
        	}
        	public Class getColumnClass(int columnIndex) {
        		switch (columnIndex) {
        		case 0: return String.class;
        		case 1: return String.class;
        		case 2: return Boolean.class;
        		default : return super.getColumnClass(columnIndex);
        		}
        	}

        };
        JTable t = new JTable(model,createTableColumnModel());
        t.setRowHeight(24);
        //t.setPreferredSize(new Dimension(400,300));
        pane = new JScrollPane(t);
        //TODO select/unselect, add available descriptors
        pane.setToolTipText("TODO select/unselect, add available descriptors");

        pane.setPreferredSize(new Dimension(400,400));
        pane.setBorder(BorderFactory.createTitledBorder("Descriptors"));
        
        JToolBar bar = new JToolBar();
        bar.add(new AbstractAction("Select all") {
            public void actionPerformed(ActionEvent e) {
                model.enableAll(true);
                
            }
        });
        bar.add(new AbstractAction("Unselect all") {
            public void actionPerformed(ActionEvent e) {
                model.enableAll(false);
                
            }
        });        
        panel = new JPanel(new BorderLayout());
        panel.add(bar,BorderLayout.SOUTH);
        panel.add(pane,BorderLayout.CENTER);
    }
    protected TableColumnModel createTableColumnModel() {
        DefaultTableColumnModel cm = new DefaultTableColumnModel();
        TableColumn c = new TableColumn(2);
        c.setHeaderValue("Selected");
        
        c.setMaxWidth(32);
        c.setCellRenderer(new BooleanCellRenderer() {
        	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        		Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
        				row, column);
        		setToolTipText("Will calculate this descriptor if checked");
        		return c;
        	}
        });
        cm.addColumn(c);
        
        
        
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
    
        	public Component getTableCellRendererComponent(JTable table, Object arg1, boolean arg2, boolean arg3, int row, int column) {
        		Component c = super.getTableCellRendererComponent(table, arg1, arg2, arg3, row, column);
        		Object o = table.getValueAt(row, column);
        		setToolTipText(o.toString());
        		return c;
        	}
        };
        
        c = new TableColumn(0);
        c.setHeaderValue("Descriptor");
        
        cm.addColumn(c);
        
        c = new TableColumn(1);
        c.setHeaderValue("Remark"); 
        c.setCellRenderer(renderer);
        cm.addColumn(c);

        
        return cm;
    }


    /* (non-Javadoc)
     * @see ambit.processors.IAmbitEditor#view(javax.swing.JComponent, boolean)
     */
    public boolean view(Component parent, boolean editable, String title) throws AmbitException {
    	setEditable(editable);
        return JOptionPane.showConfirmDialog(parent,panel,"Descriptors to be calculated",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE)==JOptionPane.OK_OPTION;

    }

    /* (non-Javadoc)
     * @see ambit.processors.IAmbitEditor#getJComponent()
     */
    public JComponent getJComponent() {
        return panel;
    }

	public DescriptorsHashtable getLookup() {
		return (DescriptorsHashtable)model.getTable();
	}

	public void setLookup(DescriptorsHashtable lookup) {
		model.setTable(lookup);
		
		
	}
	public boolean isEditable() {
		return editable;
	}
	public void setEditable(boolean editable) {
		this.editable = editable;
	}

}
