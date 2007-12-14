/* SetOfAtomContainersTableModel.java
 * Author: Nina Jeliazkova
 * Date: Aug 11, 2006 
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

package ambit.ui.data.molecule;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import org.openscience.cdk.interfaces.ISetOfAtomContainers;


/**
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 11, 2006
 */
public class SetOfAtomContainersTableModel extends AbstractTableModel implements PropertyChangeListener {
    protected ISetOfAtomContainers atomContainers;
    protected ArrayList properties;
    
    public SetOfAtomContainersTableModel(ISetOfAtomContainers atomContainers, ArrayList properties) {
        super();
        setAtomContainers(atomContainers);
        setProperties(properties);
    }
    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
        if (atomContainers == null) return 0;
        else
        return atomContainers.getAtomContainerCount();
        
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount() {

        return properties.size()+1;
    }
    
    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex ==0) return new Integer(rowIndex+1);
        else
	        if (atomContainers == null) return null;
	        else
	        	if (columnIndex < (properties.size()+1)) {
	        		return atomContainers.getAtomContainer(rowIndex).getProperty(properties.get(columnIndex-1));
	        	}	
	        	else return atomContainers.getAtomContainer(rowIndex);
    }
    /* (non-Javadoc)
     * @see javax.swing.table.AbstractTableModel#getColumnName(int)
     */
    public String getColumnName(int column) {
        if (column ==0) return "No";
        else
        return properties.get(column-1).toString();
    }
    public synchronized ISetOfAtomContainers getAtomContainers() {
        return atomContainers;
    }
    public synchronized void setAtomContainers(
            ISetOfAtomContainers atomContainers) {
        this.atomContainers = atomContainers;
        fireTableStructureChanged();
    }
    public synchronized ArrayList getProperties() {
        return properties;
    }
    public synchronized void setProperties(ArrayList properties) {
        this.properties = properties;
        fireTableStructureChanged();
    }
    public void propertyChange(PropertyChangeEvent e) {
    		if (e.getOldValue() != null) 
    			properties.remove(e.getOldValue());
    		if (e.getNewValue() != null)
    			properties.add(e.getNewValue());

    		fireTableStructureChanged();

    }
    
}
