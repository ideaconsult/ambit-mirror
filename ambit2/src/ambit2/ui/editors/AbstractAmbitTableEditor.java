/* AbstractAmbitTableEditor.java
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

package ambit2.ui.editors;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import ambit2.data.AmbitObject;
import ambit2.exceptions.AmbitException;
import ambit2.ui.data.AbstractPropertyTableModel;

/**
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-6-13
 */
public abstract class AbstractAmbitTableEditor extends JPanel implements IAmbitEditor {
    protected AbstractPropertyTableModel model;
    protected String caption = "";;
    protected boolean editable = false;
    /**
     * 
     */
    public AbstractAmbitTableEditor(String caption,AmbitObject object) {
    	
    		super(new BorderLayout());
    		this.caption = caption;
    		addWidgets(object);
    }
    protected void addWidgets(AmbitObject object) {
        JScrollPane p = new JScrollPane(createTable(object));
        if (!caption.equals(""))
		setBorder(BorderFactory.createTitledBorder(caption));
		p.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		add(p,BorderLayout.CENTER);
    }
    protected JTable createTable(AmbitObject object) {
		model = createTableModel(object);
		JTable table = new JTable(model,createColumnModel());
		table.setSurrendersFocusOnKeystroke(true);
		table.setShowVerticalLines(false);
		table.setShowHorizontalLines(false);
		table.setRowSelectionAllowed(false);
        table.setSurrendersFocusOnKeystroke(true);
		
        table.setRowHeight(24);
        return table;
        
    }
    protected abstract AbstractPropertyTableModel createTableModel(AmbitObject object);
	public TableColumnModel createColumnModel() {
	    TableColumnModel m = new DefaultTableColumnModel();
	    TableColumn c = new TableColumn(0);
	    c.setMaxWidth(100);
	    m.addColumn(c);
	    c = new TableColumn(1);
	    m.addColumn(c);
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            Border border = BorderFactory.createLineBorder(Color.GRAY);
            
        	public Component getTableCellRendererComponent(JTable table, Object arg1, boolean arg2, boolean arg3, int row, int column) {
        		Component c = super.getTableCellRendererComponent(table, arg1, arg2, arg3, row, column);
        		setToolTipText("Double click here to edit");
        		if (arg3) { 
        		    return c;
        		} else {
        		    setBorder(this.border);
        		}
        		//Object o = table.getValueAt(row, column);
        		
        		return c;
        	}
        };
        c.setCellRenderer(renderer);
	    return m;
	}        
	public boolean view(Component parent, boolean editable, String title) throws AmbitException {
		return 
		JOptionPane.showConfirmDialog(this,model.getObject().editor(model.getObject().isEditable()),caption,JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION;

	}

	public JComponent getJComponent() {
		return this;
	}

    public synchronized String getCaption() {
        return caption;
    }
    public synchronized void setCaption(String caption) {
        this.caption = caption;
    }
	public boolean isEditable() {
		return editable;
	}
	public void setEditable(boolean editable) {
		this.editable = editable;
	}
}
