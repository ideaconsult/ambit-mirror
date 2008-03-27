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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import ambit2.exceptions.AmbitException;
import ambit2.processors.experiments.ExperimentParser;

/**
 * 
 * Visualization of {@link ExperimentParser}
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 30, 2006
 */
public class ExperimentParserEditor extends JPanel implements IAmbitEditor {
    /**
     * Creates {@link JPanel} embedding {@link LiteratureEntryEditor} and {@link StudyTemplatePanel}
     * @param parser
     */
	public ExperimentParserEditor(ExperimentParser parser) {
		super(new BorderLayout());
		LiteratureEntryEditor le = new LiteratureEntryEditor(parser.getReference());
		le.setBorder(BorderFactory.createTitledBorder("Assign this reference to imported experimental data"));
		add(le,BorderLayout.NORTH);
		StudyTemplateEditor p = new StudyTemplateEditor(parser.getTemplate(),parser.getTemplate().isEditable(),"Template");
		p.setPreferredSize(new Dimension(400,200));
		p.setBorder(BorderFactory.createTitledBorder("Experimental data template"));
		add(p,BorderLayout.CENTER);
	}
	public JComponent getJComponent() {
		return this;
	}
	public boolean view(Component parent, boolean editable, String title) throws AmbitException {
		return 
		JOptionPane.showConfirmDialog(parent, getJComponent(),"Experimental data",
				JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE)==JOptionPane.OK_OPTION;
		
	}
	public void setEditable(boolean editable) {
		// TODO Auto-generated method stub
		
	}
	public boolean isEditable() {
		// TODO Auto-generated method stub
		return true;
	}
}