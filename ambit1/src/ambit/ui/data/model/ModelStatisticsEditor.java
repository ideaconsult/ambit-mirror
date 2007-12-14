/* ModelStatisticsEditor.java
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

package ambit.ui.data.model;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import ambit.data.AmbitObject;
import ambit.data.model.ModelStatistics;
import ambit.ui.data.AbstractAmbitTableEditor;
import ambit.ui.data.AbstractPropertyTableModel;

/**
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-6-13
 */
public class ModelStatisticsEditor extends AbstractAmbitTableEditor{

    /**
     * 
     */
    public ModelStatisticsEditor(ModelStatistics stats) {
        super("",stats);
        setPreferredSize(new Dimension(300,4*24+12));
        setMaximumSize(new Dimension(Integer.MAX_VALUE,5*24));
    }
    /* (non-Javadoc)
     * @see ambit.ui.data.AbstractAmbitTableEditor#createTableModel(ambit.data.AmbitObject)
     */
    protected AbstractPropertyTableModel createTableModel(AmbitObject object) {
        return new ModelStatisticsTableModel((ModelStatistics)object);
    }
    public TableColumnModel createColumnModel() {
	    TableColumnModel m = new DefaultTableColumnModel();
	    
	    for (int i=0; i < 3; i++) {
		    TableColumn c = new TableColumn(i*2);
		    c.setMaxWidth(100);
		    m.addColumn(c);
		    c = new TableColumn(i*2+1);
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
	    }
	    return m;
	}        
}
