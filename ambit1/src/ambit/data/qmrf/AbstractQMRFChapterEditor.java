/* AbstractQMRFChapterEditor.java
 * Author: Nina Jeliazkova
 * Date: Feb 21, 2007 
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

package ambit.data.qmrf;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;

import ambit.data.IAmbitEditor;
import ambit.exceptions.AmbitException;
import ambit.ui.AmbitColors;
import ambit.ui.SpringUtilities;

public abstract class AbstractQMRFChapterEditor extends JPanel implements IAmbitEditor {
    protected AbstractQMRFChapter chapter;
    protected int indent = 2;
    protected Color lightColor;
    protected Color darkColor;
    protected Color hintColor;    
    public AbstractQMRFChapterEditor(AbstractQMRFChapter chapter) {
        this(chapter,2,Color.white,AmbitColors.DarkClr, Color.LIGHT_GRAY, new BorderLayout());
    }
    public AbstractQMRFChapterEditor(AbstractQMRFChapter chapter, int indent, Color lightColor, Color darkColor, Color hintColor, LayoutManager layout) {
        super();
        this.chapter = chapter;
        this.indent = indent;
        setLightColor(lightColor);
        setDarkColor(darkColor);
        setHintColor(hintColor);
        addWidgets(layout);
    }
    
    public JComponent getJComponent() {
        return this;
    }

    public void setEditable(boolean editable) {
        chapter.setEditable(editable);

    }
	public boolean isEditable() {
		return chapter.isEditable();
	}
    public boolean view(Component parent, boolean editable, String title)
            throws AmbitException {
        setEditable(editable);
        return JOptionPane.showConfirmDialog(parent,this) == JOptionPane.OK_OPTION;
    }
    protected void addWidgets(LayoutManager layout) {
        setBackground(Color.white);
        SpringLayout l = new SpringLayout();
        setLayout(l);
        
        JComponent[] cc = createJComponents();
        for (int i=0; i < cc.length; i++)
        	add(cc[i]);
        
        SpringUtilities.makeCompactGrid(this,
                cc.length,1, //rows, cols
                2, 2,        //initX, initY
                2, 2);       //xPad, yPad
        
        setBorder(BorderFactory.createEmptyBorder(indent,indent,indent,indent));
        /*
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AmbitColors.DarkClr),
                BorderFactory.createEmptyBorder(indent,indent,indent,indent)
                ));
                */
        setMinimumSize(new Dimension(200,400));
        scroll2Top();
    }
    
    protected void scroll2Top() {
    	
    }
    protected abstract JComponent[] createJComponents();
    	
    protected JComponent createHelpLabel(final AbstractQMRFChapter chapter) {
        JLabel h = new JLabel("<html><b><u>Help</u></b></html>");
        h.setAlignmentX(Component.RIGHT_ALIGNMENT);
        h.setOpaque(true);
        h.setBackground(lightColor);
        h.setForeground(darkColor);
        h.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
        h.setPreferredSize(new Dimension(100,32));
        h.setToolTipText(chapter.getWrappedHelp(80));
        h.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		showHelp(chapter);
        	}
        });
        return h;
    }
    protected JComponent createTitleLabel(final AbstractQMRFChapter chapter) {
        JLabel l = new JLabel("<html><b>"+chapter.getChapter()+"." + chapter.getTitle()+"</b></html>") {
        	@Override
        	public boolean isFocusable() {
        		return false;
        	}
        };
        
        l.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        l.setOpaque(true);
        l.setBackground(darkColor);
        l.setForeground(lightColor);
        l.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));

        l.setToolTipText(chapter.getWrappedHelp(80));
        l.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		showHelp(chapter);
        	}
        });
        return l;   
    }
    protected JComponent createTitle(final AbstractQMRFChapter chapter) {
    	return createTitleLabel(chapter);
          	
    }    

    /*
    protected JComponent createNorthComponent() {
    	return createTitle(chapter);
    }
    */
    protected JComponent createHelpComponent(AbstractQMRFChapter chapter) {
        JEditorPane help = new JEditorPane() {
        	@Override
        	public boolean isFocusable() {
        		return false;
        	}
        };
        
        help.setMaximumSize(new Dimension(300,100));
        help.setEditable(false);
        help.setText(chapter.getHelp());
        help.setToolTipText(chapter.getWrappedHelp(150));
        //help.setText("");
        help.setForeground(hintColor);
        help.setBackground(lightColor);
        help.setPreferredSize(new Dimension(300,200));
        return new JScrollPane(help);

    }   
    protected JComponent createSouthComponent() {
       return createHelpComponent(chapter);
    }
    public synchronized int getIndent() {
        return indent;
    }

    public synchronized void setIndent(int indent) {
        this.indent = indent;
    }
    public synchronized Color getDarkColor() {
        return darkColor;
    }
    public synchronized void setDarkColor(Color darkColor) {
        this.darkColor = darkColor;
    }
    public synchronized Color getLightColor() {
        return lightColor;
    }
    public synchronized void setLightColor(Color lightColor) {
        this.lightColor = lightColor;
    }
	public Color getHintColor() {
		return hintColor;
	}
	public void setHintColor(Color hintColor) {
		this.hintColor = hintColor;
	}
	@Override
	public String toString() {
		return chapter.toString();
	}
    protected void showHelp(final AbstractQMRFChapter chapter) {
    	JOptionPane.showMessageDialog(this, createHelpComponent(chapter),chapter.toString()+chapter.getTitle(),JOptionPane.INFORMATION_MESSAGE);
    }	
}

