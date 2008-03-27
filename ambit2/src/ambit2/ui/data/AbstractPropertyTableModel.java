/* AbstractPropertyTableModel.java
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

package ambit2.ui.data;

import javax.swing.table.AbstractTableModel;

import ambit2.data.AmbitObject;
import ambit2.data.AmbitObjectChanged;
import ambit2.data.IAmbitObjectListener;

/**
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-6-13
 */
public abstract class AbstractPropertyTableModel extends AbstractTableModel implements IAmbitObjectListener {
    protected AmbitObject object;
    
    /**
     * 
     */
    public AbstractPropertyTableModel(AmbitObject object) {
        super();
        this.object = object;
        if (object != null)
        	object.addAmbitObjectListener(this);
    }
	/* (non-Javadoc)
     * @see java.lang.Object#finalize()
     */
    protected void finalize() throws Throwable {
    	if (object != null)
    		object.removeAmbitObjectListener(this);
        super.finalize();
    }
    /* (non-Javadoc)
     * @see ambit2.data.IAmbitObjectListener#ambitObjectChanged(ambit2.data.AmbitObjectChanged)
     */
    public void ambitObjectChanged(AmbitObjectChanged event) {
        fireTableDataChanged();
    }
    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount() {
        return 2;
    }

    /* (non-Javadoc)
     * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
     */
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex==1;
    }

    public synchronized AmbitObject getObject() {
        return object;
    }
    public synchronized void setObject(AmbitObject object) {
	    if (this.object != null) object.removeAmbitObjectListener(this);
	    this.object = object;
	    if (object != null)
	    	object.addAmbitObjectListener(this);
		fireTableStructureChanged();

    }
    public boolean isExpanded(int rowIndex) {
    	return true;
    }
}
