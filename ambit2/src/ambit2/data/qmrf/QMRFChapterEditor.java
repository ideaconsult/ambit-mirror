/* QMRFChapterEditor.java
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

package ambit2.data.qmrf;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.Point;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SpringLayout;

import ambit2.ui.AmbitColors;
import ambit2.ui.SpringUtilities;
import ambit2.ui.editors.IAmbitEditor;
import ambit2.data.AmbitList;

public class QMRFChapterEditor extends AbstractQMRFChapterEditor {
	protected JScrollPane scrollPane;
	protected JPanel centerPanel;
    public QMRFChapterEditor(QMRFChapter chapter) {
        this(chapter,2);
    }

    public QMRFChapterEditor(QMRFChapter chapter, int indent) {
        super(chapter, indent,Color.white,AmbitColors.DarkClr, AmbitColors.DarkClr, new BorderLayout());
        //centerPanel.scrollRectToVisible(new Rectangle(0,0,10,10));

    }
    protected void addWidgets(LayoutManager layout) {
        setBackground(Color.white);
        setLayout(new BorderLayout());
        
        add(super.createTitleLabel(chapter),BorderLayout.NORTH);

        centerPanel = new JPanel();
        SpringLayout l = new SpringLayout();
        centerPanel.setLayout(l);
        centerPanel.setBackground(lightColor);
        centerPanel.setForeground(darkColor);
        
        AmbitList list = ((QMRFChapter)chapter).getSubchapters();
        
        int row = 0;
        JComponent first = null;
        for (int i=0; i < list.size(); i++) {
        	
        	IAmbitEditor editor = list.getItem(i).editor(isEditable());
        	
            centerPanel.add(createTitle((AbstractQMRFChapter)list.getItem(i)));
            //add(createHelpComponent((AbstractQMRFChapter)list.getItem(i)),BorderLayout.CENTER);
            row++;
        	
            JComponent[] jc = ((QMRFSubChapterTextEditor)editor.getJComponent()).createJComponents();
            for (int j=0; j < jc.length; j++) {
            	
            	if (first == null) {
            		first = jc[0];
            		first.addFocusListener(new java.awt.event.FocusAdapter() { 
                	public void focusGained(java.awt.event.FocusEvent evt) {     
                		componentFocusGained(evt);            
                	}        
            		});
            	}
            	row++;
            	centerPanel.add(jc[j]);

            }	
        }
        SpringUtilities.makeCompactGrid(centerPanel,
                row,1, //rows, cols
                2, 2,        //initX, initY
                2, 2);       //xPad, yPad
        
        scrollPane =  new JScrollPane(centerPanel,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(400,400));
        add(scrollPane,BorderLayout.CENTER);

        setMinimumSize(new Dimension(200,400));
        scroll2Top();
    }    
    /*
    protected void addWidgets(LayoutManager layout) {
        setBackground(Color.white);
        setLayout(new BorderLayout());

        add(super.createTitle(chapter),BorderLayout.NORTH);
        
        centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(lightColor);
        GridBagConstraints c = new GridBagConstraints();
        
        c.fill =  GridBagConstraints.HORIZONTAL;
        c.weightx = 0.0;
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy = 1;
        
        c.insets = new Insets(5,5,5,5);
        AmbitList list = ((QMRFChapter)chapter).getSubchapters();
        
        int row = 0;
        JComponent first = null;
        for (int i=0; i < list.size(); i++) {
        	
        	IAmbitEditor editor = list.getItem(i).editor(isEditable());
        	
            JPanel p = new JPanel(new BorderLayout());
            p.setBackground(lightColor);
            p.add(createTitle((AbstractQMRFChapter)list.getItem(i)),BorderLayout.NORTH);
            p.add(createHelpComponent((AbstractQMRFChapter)list.getItem(i)),BorderLayout.CENTER);
        	
        	c.gridwidth = GridBagConstraints.RELATIVE;
 //       	else c.anchor = GridBagConstraints.NORTH;
        	c.weightx = 0.2;
        	c.gridx = 0;
        	c.gridy = row;
            centerPanel.add(p,c);

            JComponent[] jc = ((QMRFSubChapterTextEditor)editor.getJComponent()).createJComponents();
            for (int j=0; j < jc.length; j++) {
            	
            	if (first == null) {
            		first = jc[0];
            		first.addFocusListener(new java.awt.event.FocusAdapter() { 
                	public void focusGained(java.awt.event.FocusEvent evt) {     
                		componentFocusGained(evt);            
                	}        
            		});
            }
                
            	c.anchor = GridBagConstraints.NORTH;
                c.weightx = 0.8;
                c.gridx = 1;
                c.gridy = row++;
                if (j==(jc.length-1))
                	c.gridwidth = GridBagConstraints.REMAINDER;
                else
                	c.gridwidth = GridBagConstraints.RELATIVE;
            	centerPanel.add(jc[j],c);
            }	
        }
        scrollPane =  new JScrollPane(centerPanel,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(400,400));
        add(scrollPane,BorderLayout.CENTER);
        setMinimumSize(new Dimension(200,400));
        scroll2Top();
    }
    */
    @Override
    protected JComponent createTitleLabel(AbstractQMRFChapter chapter) {
    	JLabel l = new JLabel("<html><b>"+chapter.getChapter()+"." + chapter.getTitle()+"</b></html>");
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setOpaque(true);
        l.setBackground(lightColor);
        l.setForeground(darkColor);
        l.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
        l.setPreferredSize(new Dimension(300,32));
        l.setToolTipText(chapter.getWrappedHelp(80));
        l.setMaximumSize(new Dimension(Integer.MAX_VALUE,24));
        return l;
    }

    

    protected JComponent createTitle(final AbstractQMRFChapter chapter) {
    	JPanel p = new JPanel(new BorderLayout());
    	JComponent l = createTitleLabel(chapter);
        
        JComponent h = createHelpLabel(chapter);
        
        p.setBackground(lightColor);
        p.setForeground(darkColor);
        p.add(l,BorderLayout.CENTER);
        p.add(h,BorderLayout.EAST);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE,24));
        return p;    	
    }    
    @Override
    protected JComponent createSouthComponent() {
        JComponent c = super.createSouthComponent();
        c.setForeground(lightColor);
        c.setBackground(darkColor);        
        c.setMaximumSize(new Dimension(200,100));
        return c;
    }
    @Override
    protected JComponent[] createJComponents() {
    	centerPanel = new JPanel();
    	centerPanel.setBackground(Color.white);
        
        
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridwidth = GridBagConstraints.REMAINDER;
        gc.weightx =1;
        centerPanel.setLayout(layout);
        
        AmbitList list = ((QMRFChapter)chapter).getSubchapters();
        centerPanel.setLayout(new GridLayout(list.size(),1));
        JComponent first = null;
        for (int i=0; i < list.size(); i++) {
        	IAmbitEditor editor = list.getItem(i).editor(isEditable());
        	if (first == null) first = editor.getJComponent();
        	//editor.setEditable(chapter.isEditable());
            JComponent c = editor.getJComponent();
            c.addFocusListener(new java.awt.event.FocusAdapter() { 
            	public void focusGained(java.awt.event.FocusEvent evt) {     
            		componentFocusGained(evt);            
            }        
            });
            centerPanel.add(c,gc);
        }

        scrollPane =  new JScrollPane(centerPanel,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(400,400));
        if (first != null) {
        //	System.out.println(first + " request focus");
        	first.requestFocus();
        	//first.scrollRectToVisible(first.getBounds());
        	//centerPanel.scrollRectToVisible(new Rectangle(0,0,10,10));
        	//scrollPane.scrollRectToVisible(first.getBounds());
        	
        }	
        //sp.setMinimumSize(new Dimension(300,list.size()*100));
        //sp.setPreferredSize(new Dimension(300,list.size()*100));        
        return new JComponent[] {scrollPane};
        
    }
    private void componentFocusGained(java.awt.event.FocusEvent evt) {        
    	java.awt.Component focusedComponent = evt.getComponent();
    	//System.out.println(focusedComponent.toString());
    	scrollRectToVisible(focusedComponent.getBounds(null));        
    	repaint();    
    }    
    @Override
    protected void scroll2Top() {
    	super.scroll2Top();
    	
    	if (scrollPane != null) {
	    	JViewport view = scrollPane.getViewport();
	
	    	Point p = new Point(10,10);
	    	view.setViewPosition(p);
	    		    	
    	}

    }
}
