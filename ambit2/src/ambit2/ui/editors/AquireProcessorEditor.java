/*
Copyright (C) 2007-2008  

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
*/

package ambit2.ui.editors;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import ambit2.database.aquire.DbAquireProcessor;
import ambit2.exceptions.AmbitException;

public class AquireProcessorEditor extends JPanel implements IAmbitEditor {
	protected DbAquireProcessor processor;
	public AquireProcessorEditor(final DbAquireProcessor processor) {
		super();
		this.processor = processor;
		setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));
		ButtonGroup g = new ButtonGroup();
		JRadioButton boxSimple = new JRadioButton("Retrieve main fields only");		
		JRadioButton boxAll = new JRadioButton("Retrieve all available data per measurement");
		
		boxSimple.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				Object source = e.getItemSelectable();
				JRadioButton box = (JRadioButton)source;
				processor.setUseSimpleTemplate(box.isSelected()); 
			}
		});
		boxAll.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				Object source = e.getItemSelectable();
				JRadioButton box = (JRadioButton)source;
				processor.setUseSimpleTemplate(!box.isSelected()); 
			}
		});
		g.add(boxAll);
		g.add(boxSimple);
		boxSimple.setSelected(true);
		add(boxSimple);
		add(boxAll);
		setBorder(BorderFactory.createTitledBorder("AQUIRE database"));
	}
	public JComponent getJComponent() {
		return this;
	}

	public boolean isEditable() {
		return true;
	}

	public void setEditable(boolean editable) {

		
	}

	public boolean view(Component parent, boolean editable, String title) throws AmbitException {
		return JOptionPane.showConfirmDialog(parent,this) == JOptionPane.OK_OPTION;
	}

}