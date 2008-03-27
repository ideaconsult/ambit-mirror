/*
Copyright (C) 2005-2006  

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

package ambit2.data.qmrf;

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

public class QMRFSubChapterDatasetEditor extends QMRFSubChapterTextEditor implements ItemListener {

	public QMRFSubChapterDatasetEditor(QMRFSubChapterDataset chapter,boolean editable) {
		super(chapter,editable);
	}

	public QMRFSubChapterDatasetEditor(QMRFSubChapterDataset chapter, int indent) {
		super(chapter, indent);
	}
	@Override

	protected JComponent[] createJComponents() {
		SpringLayout layout = new SpringLayout();
		JPanel p = new JPanel(layout);
		p.setBackground(darkColor);
		String[] options = ((QMRFSubChapterDataset) chapter).options;
		JCheckBox box  = null;
		for (int i=0; i < options.length;i++) {
			JCheckBox newBox = new JCheckBox(options[i]);
            newBox.setSelected("Yes".equals(
                    ((QMRFSubChapterDataset) chapter).getAttribute(options[i])
                    )); 
			newBox.addItemListener(this);
			p.add(newBox);
			newBox.setBackground(darkColor);
			if (box != null)
				layout.putConstraint(SpringLayout.WEST, newBox, 5, SpringLayout.EAST, box);
			box = newBox;
		}	
		JComponent t = createTextComponent();
		t.setPreferredSize(new Dimension(Integer.MAX_VALUE,18));
		t.setMaximumSize(new Dimension(Integer.MAX_VALUE,24));
		t.setMinimumSize(new Dimension(Integer.MAX_VALUE,18));
		layout.putConstraint(SpringLayout.WEST, t, 5, SpringLayout.EAST, box);
		
		layout.putConstraint(SpringLayout.NORTH, t, 1, SpringLayout.NORTH, p);
		layout.putConstraint(SpringLayout.EAST, p,
                1,                SpringLayout.EAST, t);
		layout.putConstraint(SpringLayout.SOUTH, p,
                1,                SpringLayout.SOUTH, t);


		p.add(t,JComponent.RIGHT_ALIGNMENT);
		p.setMinimumSize(new Dimension(100,18));
		return new JComponent[]{p};
	}
	
	public void itemStateChanged(ItemEvent e) {
		Object source = e.getItemSelectable();
		QMRFSubChapterDataset c = (QMRFSubChapterDataset)chapter;
		JCheckBox box = (JCheckBox)source;
		if (box.isSelected()) 
			c.setAttribute(box.getText(), "Yes"); 
		else 
			c.setAttribute(box.getText(), "No");
		
	}	
}


