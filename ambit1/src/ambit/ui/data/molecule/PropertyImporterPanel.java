/* PropertyImporterPanel.java
 * Author: Nina Jeliazkova
 * Date: Nov 9, 2006 
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

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import ambit.data.molecule.PropertyTranslator;
import ambit.ui.UITools;
import ambit.ui.data.PropertiesPanel;

public class PropertyImporterPanel extends JPanel {
	protected PropertyTranslator properties;
	protected Object lookupProperty;
	protected String lookupType;
	protected JTextField lookupField;
    public PropertyImporterPanel(PropertyTranslator properties, Object lookupProperty) {
        super(new BorderLayout());
        this.properties = properties;
        this.lookupProperty = lookupProperty;
        PropertiesPanel panel = new PropertiesPanel(properties, 
                new String[] {PropertyTranslator.type_descriptors},null);
        String[] options = panel.getOptions();
        
        add(createLookupFieldPanel(options, lookupProperty),BorderLayout.NORTH);
        add(panel,BorderLayout.CENTER);
    }
    public JComponent createLookupFieldPanel(String[] options, Object lookupProperty) {
    	JButton b = new JButton(new AbstractAction("",UITools.createImageIcon("ambit/ui/images/arrowright_green_16.png")){
            /* (non-Javadoc)
             * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
             */
            public void actionPerformed(ActionEvent e) {
            	Object selected = properties.getSelectedProperty();
            	if (selected != null) {
            		move(selected);
            	}
            }
        });    	
    	lookupField = new JTextField(lookupProperty.toString());
    	lookupField.setEditable(false);
    	
    	lookupField.setToolTipText("This data field will be used to match records from file with descriptors and the dataset.");
        JPanel buttons = new JPanel(); //new GridLayout(1,options.length+1));
        buttons.setBorder(BorderFactory.createTitledBorder("Lookup field"));
        buttons.add(b);
        buttons.add(lookupField);
        ButtonGroup group = new ButtonGroup();
        lookupType = options[0];
        for (int i=0; i < options.length;i++) {
        	JRadioButton button = new JRadioButton(options[i]);
        	button.setActionCommand(options[i]);
        	button.setSelected(i==0);
        	group.add(button);
        	buttons.add(button);
        	button.addActionListener(new ActionListener() {
        		public void actionPerformed(ActionEvent e) {
        			changeType(e.getActionCommand());
        			
        		}
        	});

        }
        return buttons;
    }
    protected void changeType(String type) {
    	lookupType = type;
    	properties.getIdentifiers().put(lookupProperty, type);
    }
    protected void move(Object selected) {
		properties.moveBack(PropertyTranslator.type_identifiers, lookupProperty);
		lookupProperty = selected;
		lookupField.setText(lookupProperty.toString());
        
		if (lookupType.equals(""))
			properties.moveTo(PropertyTranslator.type_identifiers,selected);
		else
			properties.moveTo(PropertyTranslator.type_identifiers,selected,lookupType);
        properties.setSelectedProperty(null);
    }
    public synchronized Object getLookupProperty() {
        return lookupProperty;
    }
    public synchronized void setLookupProperty(Object lookupProperty) {
        this.lookupProperty = lookupProperty;
    }
}
