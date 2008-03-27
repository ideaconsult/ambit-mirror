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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class QMRFSubChapterQuestionEditor extends QMRFSubChapterTextEditor implements ActionListener {
	protected JComboBox listbox;
	
    public QMRFSubChapterQuestionEditor(QMRFSubChapterQuestion chapter, boolean editable) {
        this(chapter,4);
        setEditable(editable);
    }
    public QMRFSubChapterQuestionEditor(QMRFSubChapterQuestion chapter, int indent) {
        super(chapter,indent);
    }

	@Override
	protected JComponent[] createJComponents() {
		
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEADING,0,2));
		p.setBackground(darkColor);
		p.setBorder(BorderFactory.createEmptyBorder());
		//p.add(createTitle(chapter));
		
		 
        
        QMRFSubChapterQuestion c = (QMRFSubChapterQuestion)chapter;
		listbox = new JComboBox(c.getOptions());
        listbox.setSelectedItem(c.getAttribute(c.question));
		listbox.addActionListener(this);
		listbox.setMinimumSize(new Dimension(200,48));
		p.add(listbox);
		//p.add(createTextComponent());
		
		return new JComponent[] {
				p
		};
	}
	public void actionPerformed(ActionEvent e) {
		  JComboBox cb = (JComboBox)e.getSource();
	      QMRFSubChapterQuestion c = (QMRFSubChapterQuestion)chapter;
	      c.setAttribute(c.getQuestion(), cb.getSelectedItem().toString());
	}
    /*
	@Override
	protected JComponent createNorthComponent() {
		box = new JCheckBox("<html><b>"+chapter.getChapter()+"." + chapter.getTitle()+"</b></html>");
        box.setBackground(darkColor);
        box.setForeground(lightColor);		
		box.setHorizontalTextPosition(SwingConstants.LEFT);
		box.addItemListener(this);
		QMRFSubChapterQuestion c = (QMRFSubChapterQuestion)chapter;
		if ("Yes".equals(c.getQuestion())) {
			c.getQuestion();
		}	
		return box;
	}
    
	public void itemStateChanged(ItemEvent e) {
		Object source = e.getItemSelectable();
		QMRFSubChapterQuestion c = (QMRFSubChapterQuestion)chapter;
		
		c.getAttributes().put(c.getQuestion(), listbox.getSelectedItem().toString());
		
	}
	*/
}


