/* ModelEditor.java
 * Author: Nina Jeliazkova
 * Date: 2006-6-6 
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

package ambit.ui.data.model;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import ambit.data.AmbitObject;
import ambit.data.model.Model;
import ambit.exceptions.AmbitException;
import ambit.ui.data.AbstractAmbitEditor;
import ambit.ui.data.AbstractPropertyTableModel;

/**
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-6-6
 */
public class ModelEditor extends AbstractAmbitEditor {

    /**
     * 
     */
    public ModelEditor(Model qsarModel) {
        super("QSAR Model",qsarModel);
    }
    
    protected void addWidgets(JComponent component) {
        JPanel panel = new JPanel();
        super.addWidgets(panel);
        Model qsarModel = (Model) object;
        
        /*
        JScrollPane p = new JScrollPane(createTable(object));
		setBorder(BorderFactory.createTitledBorder(caption));
		p.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		p.setPreferredSize(new Dimension(200,200));
		*/

		//JPanel mp = new JPanel(new BorderLayout());
		//mp.add(panel,BorderLayout.CENTER);
		//mp.add(qsarModel.getReference().editor().getJComponent(),BorderLayout.SOUTH);
        
        
        
        JTabbedPane pane = new JTabbedPane();
        pane.addTab("Model", panel);
        if (qsarModel.getStudy() != null)
        pane.addTab("Study",
                qsarModel.getStudy().editor(true).getJComponent());
        if (qsarModel.getDescriptors() != null)
        pane.addTab("Descriptors",
                qsarModel.getDescriptors().editor().getJComponent());
        component.add(pane,BorderLayout.CENTER);
    }
    
    /* (non-Javadoc)
     * @see ambit.ui.data.AbstractAmbitTableEditor#createTableModel(ambit.data.AmbitObject)
     */
    protected AbstractPropertyTableModel createTableModel(AmbitObject object) {
        return new ModelTable((Model)object);
    }
    public void editReference(Object source) {
        try {
            Model qsarModel = (Model)model.getObject();
            qsarModel.getReference().editor(true).view(this,true, "");
            if (source instanceof JLabel) 
                ((JLabel) source).setText(qsarModel.getReference().toString());
        } catch (AmbitException X) {
            
        }
    }
	public TableColumnModel createColumnModel() {
	    TableColumnModel m = new DefaultTableColumnModel();
	    
	    TableColumn c = new TableColumn(0,64);
	    m.addColumn(c);
	    c = new TableColumn(1,200);
	    
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
	    m.addColumn(c);
	    //c.setCellEditor(new AmbitCellEditor());
	    return m;
	}

    /* (non-Javadoc)
     * @see ambit.processors.IAmbitEditor#getJComponent()
     */
    public JComponent getJComponent() {
        return this;
    }

}
