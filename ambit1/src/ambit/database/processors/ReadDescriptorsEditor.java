/* ReadDescriptorsEditor.java
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

package ambit.database.processors;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import ambit.data.IAmbitEditor;
import ambit.database.query.DescriptorQueryList;
import ambit.exceptions.AmbitException;
import ambit.ui.query.DescriptorQueryPanel;

/**
 * Visualization of {@link ambit.database.processors.ReadDescriptorsProcessor}
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-5-31
 */
public class ReadDescriptorsEditor implements IAmbitEditor {
    DescriptorQueryList list;
    /**
     * 
     */
    public ReadDescriptorsEditor(DescriptorQueryList list) {
        super();
        this.list = list;
    }

    /* (non-Javadoc)
     * @see ambit.processors.IAmbitEditor#view(javax.swing.JComponent, boolean)
     */
    public boolean view(Component parent, boolean editable, String title) throws AmbitException {
        return JOptionPane.showConfirmDialog(parent,getJComponent(),"Descriptors",
        		JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE)==JOptionPane.OK_OPTION;

    }

    /* (non-Javadoc)
     * @see ambit.processors.IAmbitEditor#getJComponent()
     */
    public JComponent getJComponent() {
        return new DescriptorQueryPanel(list,null,"Select descriptors to retrieve",true);
    }
    public void setEditable(boolean editable) {
    	// TODO Auto-generated method stub
    	
    }

	public boolean isEditable() {
		// TODO Auto-generated method stub
		return true;
	}

}
