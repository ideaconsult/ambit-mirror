/* HeaderComboBoxModel.java
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

package ambit2.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.JTable;

import ambit2.ui.table.IHeaderAction;

/**
 * JTable header as a combobox, allowing sorting and filtering
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Sep 5, 2008
 */
public class HeaderComboBoxModel extends AbstractListModel implements
        ComboBoxModel {
    /**
     * 
     */
    private static final long serialVersionUID = -4586230477632082997L;
    /*
    public enum HEADER_ACTION {

        HA_NAME   { 
            public boolean action(String line) { 
                return line.startsWith("M  ALS"); 
            }
        };
        public abstract boolean action(String line);        
    } 
    */   
    //protected static final String[] items = {"Sort ascending","Sort descending","All","Custom"};
    protected JTable table;
    protected int column;
    protected Object selectedObject;
    protected IHeaderAction[] actions;
    protected List values;
    
    public HeaderComboBoxModel(JTable table, int column, IHeaderAction[] actions) {
        super();
        this.table = table;
        this.column = column;
        this.actions = actions;
        this.values = new ArrayList();
        int max =0;
        /*
        for (int i=0; i < table.getRowCount();i++) {
        	try {
            Object value = table.getValueAt(i, column);
            if (values.indexOf(value) == -1) {
                values.add(value);
                if (max > 10) break;
            }
        	} catch (Exception x) {
        		
        	}
            
        }
        */
            
    }

    public Object getElementAt(int index) {
        if (index == 0) 
            return table.getColumnName(column);
        else if ((index > 0) && (index <= actions.length))
            return actions[index-1];
        else return values.get(index-actions.length-1);
    }
    public int getSize() {
        return 1 + actions.length + values.size();
    }

    public Object getSelectedItem() {
        return selectedObject;
    }

    public void setSelectedItem(Object anObject) {
        if ((selectedObject != null && !selectedObject.equals( anObject )) ||
                selectedObject == null && anObject != null) {
            
                if (anObject instanceof IHeaderAction)
                    ((IHeaderAction)anObject).action(table,column,anObject);
                selectedObject = getElementAt(0);
                fireContentsChanged(this, -1, -1);
        }
        
    }

}
