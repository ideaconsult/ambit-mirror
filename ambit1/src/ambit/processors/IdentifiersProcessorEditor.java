/* IdentifiersProcessorEditor.java
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

package ambit.processors;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import ambit.data.IAmbitEditor;
import ambit.exceptions.AmbitException;
import ambit.ui.data.HashtableModel;

/**
 * Visualization of {@link ambit.processors.IdentifiersProcessor}
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-5-31
 */
public class IdentifiersProcessorEditor  implements IAmbitEditor {
    IdentifiersProcessor processor;
    JList casList;
    JList aliasList;
    JPanel panel; 
    /**
     * 
     */
    public IdentifiersProcessorEditor(IdentifiersProcessor processor) {
        super();
        this.processor = processor;
        panel = new JPanel(new GridLayout(1,2));
        panel.add(new JScrollPane(new JTable(new HashtableModel(processor.getIdentifiers()))));
        panel.setPreferredSize(new Dimension(300,200));
    }

    /* (non-Javadoc)
     * @see ambit.processors.IAmbitEditor#view(javax.swing.JComponent, boolean)
     */
    public boolean view(Component parent, boolean editable, String title) throws AmbitException {
    	return 
        JOptionPane.showConfirmDialog(parent,getJComponent(),processor.toString(),
        		JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE)==JOptionPane.OK_OPTION;

    }

    /* (non-Javadoc)
     * @see ambit.processors.IAmbitEditor#getJComponent()
     */
    public JComponent getJComponent() {
        return panel;
    }
    public void setEditable(boolean editable) {
    	
    }

	public boolean isEditable() {
		return true;
	}

}
