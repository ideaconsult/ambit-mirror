/* AllRingsFinderTimeoutEditor.java
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

package ambit.processors.structure;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.openscience.cdk.ringsearch.AllRingsFinder;

import ambit.data.IAmbitEditor;
import ambit.exceptions.AmbitException;

/**
 * An editor allowing to set a timeout for {@link org.openscience.cdk.ringsearch.AllRingsFinder}
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-5-31
 */
public class AllRingsFinderTimeoutEditor implements IAmbitEditor, ActionListener {
    protected AllRingsFinder arf = null;
    protected JComponent textField;
    /**
     *  Creates an editor allowing to set a timeout for {@link org.openscience.cdk.ringsearch.AllRingsFinder}
     */
    public AllRingsFinderTimeoutEditor(AllRingsFinder arf) {
        super();
        this.arf = arf;
        textField = new JTextField(Long.toString(arf.getTimeout()));
        ((JTextField)textField).addActionListener(this);
        textField.setBorder(BorderFactory.createTitledBorder("Timeout, ms"));
    }

    /* (non-Javadoc)
     * @see ambit.processors.IAmbitEditor#view(javax.swing.JComponent, boolean)
     */
    public boolean view(Component parent, boolean editable, String title) throws AmbitException {
        return 
        JOptionPane.showConfirmDialog(parent,getJComponent(),"Timeout",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE)
        == JOptionPane.OK_OPTION;

    }

    /* (non-Javadoc)
     * @see ambit.processors.IAmbitEditor#getJComponent()
     */
    public JComponent getJComponent() {
        
        return textField;
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        try {
            arf.setTimeout(Long.parseLong(((JTextField)textField).getText()));
        } catch (Exception x) {
            
        }

    }
    public void setEditable(boolean editable) {
    	// TODO Auto-generated method stub
    	
    }

	public boolean isEditable() {
		return true;
	}
}
