/* DescriptorSearchActionPanel.java
 * Author: Nina Jeliazkova
 * Date: 2006-4-15 
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

package ambit.ui.query;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import ambit.database.query.IDBQueryList;
import ambit.ui.AmbitColors;
import ambit.ui.data.molecule.ActionPanel;

/**
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-4-15
 */
public class DescriptorSearchActionPanel extends ActionPanel implements ActionListener{
    protected JRadioButton andButton;
    protected JRadioButton orButton;
    protected static final String[] commands = {"AND","OR"};
    protected JFormattedTextField limitText ;
    protected IDBQueryList descriptors;
    /**
     * @param action
     * @param title
     */
    public DescriptorSearchActionPanel(IDBQueryList descriptors,Action action, String title) {
        super(action, title,BorderLayout.EAST);
        this.descriptors = descriptors;
        andButton.setSelected(descriptors.isCombineWithAND());
    }

    /**
     * @param action
     * @param title
     * @param bClr
     * @param fClr
     */
    public DescriptorSearchActionPanel(IDBQueryList descriptors,
            Action action, String title, Color bClr,
            Color fClr) {
        super(action,title, bClr, fClr, BorderLayout.EAST);
        this.descriptors = descriptors;
        andButton.setSelected(descriptors.isCombineWithAND());
    }

    /**
     * @param actions
     * @param title
     */
    public DescriptorSearchActionPanel(IDBQueryList descriptors,ActionMap actions, String title) {
        super(actions, title);
        this.descriptors = descriptors;
        andButton.setSelected(descriptors.isCombineWithAND());
    }
    /* (non-Javadoc)
     * @see ambit.toolbox.ActionPanel#addWidgets()
     */
    protected void addWidgets() {
        super.addWidgets();
        andButton = new JRadioButton(commands[0]);
        andButton.setMnemonic(KeyEvent.VK_A);
        andButton.setActionCommand(commands[0]);

        orButton = new JRadioButton(commands[1]);
        orButton.setMnemonic(KeyEvent.VK_O);
        orButton.setActionCommand(commands[1]);
        
        ButtonGroup group = new ButtonGroup();
        group.add(andButton);
        group.add(orButton);
        
        andButton.addActionListener(this);
        
        orButton.addActionListener(this);
        
        
        //limitText.set
        JPanel p = new JPanel(new FlowLayout());
        p.add(new JLabel("Combine with "));
        p.add(andButton);
        p.add(orButton);
        
        
        
        
        p.setBackground(backClr);
        p.setForeground(foreClr);
        andButton.setBackground(backClr);
        andButton.setForeground(foreClr);
        orButton.setBackground(backClr);
        orButton.setForeground(foreClr);
        add(p,BorderLayout.WEST);
        setBorder(BorderFactory.createMatteBorder(2,2,2,2,AmbitColors.BrightClr));
    }
    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
	        if (commands[0].equals(e.getActionCommand())) {
	            descriptors.setCombineWithAND(true);    
	        } else if (commands[1].equals(e.getActionCommand())) { 
	            descriptors.setCombineWithAND(false);
	        }
    }
    /* (non-Javadoc)
     * @see ambit.toolbox.ActionPanel#getResultComponent()
     */
    protected JComponent getResultComponent() {
        JPanel p = new JPanel();
        p.setBackground(backClr);
        p.setForeground(foreClr);

        return    p;
    }

}
