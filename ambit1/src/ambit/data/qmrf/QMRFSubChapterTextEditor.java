/* QMRFSubChapterTextEditor.java
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.KeyboardFocusManager;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFormattedTextField;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import ambit.ui.AmbitColors;

public class QMRFSubChapterTextEditor extends AbstractQMRFChapterEditor implements FocusListener {
	protected 		Color disabled = new Color(236,233,216);
    protected JEditorPane textPane;
    protected JFormattedTextField textField;
    public QMRFSubChapterTextEditor(QMRFSubChapterText chapter, boolean editable) {
        this(chapter,4);
        setEditable(editable);
    }
    public QMRFSubChapterTextEditor(QMRFSubChapterText chapter, int indent) {
        super(chapter,indent,AmbitColors.DarkClr,Color.white,Color.gray, new GridLayout(2,1));
    }
    
    /*
    protected void addWidgets(LayoutManager layout) {
        setBackground(Color.white);
        setLayout(new BorderLayout());
        
        JPanel p = new JPanel(new BorderLayout());
        p.add(createNorthComponent(),BorderLayout.NORTH);
        p.add(createSouthComponent(),BorderLayout.CENTER);

        add(createCenterComponent(),BorderLayout.CENTER);
        add(p,BorderLayout.WEST);
        
        setBorder(BorderFactory.createEmptyBorder(indent,indent,indent,indent));
        setMinimumSize(new Dimension(200,400));
        scroll2Top();
    }
    */
    @Override
    protected JComponent[] createJComponents() {
    	return new JComponent[] {createTextComponent()};
    }
    @Override
    protected JComponent createSouthComponent() {
    	JComponent c = super.createSouthComponent();
    	c.setBackground(darkColor);
    	return c;
    }


    


    
    protected JComponent createTextComponent() {
        textField = null;
        textPane = null;
        if (((QMRFSubChapterText)chapter).isMultiline()) {
	        textPane = new PatchedTextPane();
	        
        	//textPane = new JEditorPane();
	        
        	//textPane.setContentType("text/html;charset=UTF-8");
	        textPane.setEditorKit(new HTMLEditorKit());

	        HTMLDocument doc = (HTMLDocument)textPane.getDocument();
	        doc.putProperty("IgnoreCharsetDirective", Boolean.TRUE);
	        doc.setPreservesUnknownTags(false);
	        
	        /**
	         * Default traversal keys - tab will go to the next component
	         */
	        textPane.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
	        textPane.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);
        	
	        
	        textPane.addFocusListener(this);
	        textPane.setToolTipText(chapter.getWrappedHelp(150));
	        textPane.setText(((QMRFSubChapterText)chapter).getText());

	        if (!isEditable()) textPane.setBackground(disabled);
	        textPane.setEditable(isEditable());
	        /*
	        Action nextFocusAction = new AbstractAction("Move Focus Forwards") {
	            public void actionPerformed(ActionEvent evt) {
	                ((Component)evt.getSource()).transferFocus();
	            }
	        };
	        Action prevFocusAction = new AbstractAction("Move Focus Backwards") {
	            public void actionPerformed(ActionEvent evt) {
	                ((Component)evt.getSource()).transferFocusBackward();
	            }
	        };	        
	        textPane.getActionMap().put(nextFocusAction.getValue(Action.NAME), nextFocusAction);
	        textPane.getActionMap().put(prevFocusAction.getValue(Action.NAME), prevFocusAction);
	        */
	        
	        JScrollPane p = new JScrollPane(textPane);
            p.setPreferredSize(new Dimension(300,46));
            return p;
        } else {
        	textField = new JFormattedTextField();
        	if (!isEditable()) textField.setBackground(disabled);
        	textField.setEditable(isEditable());
        	textField.addFocusListener(this);
        	textField.setToolTipText(chapter.getWrappedHelp(150));
        	textField.setText(((QMRFSubChapterText)chapter).getText());
        	Dimension d = new Dimension(Integer.MAX_VALUE,24);
        	textField.setPreferredSize(d);
        	textField.setMaximumSize(d);
            textField.setMinimumSize(new Dimension(256,128));   
	        return textField;
        }
    }
	public void focusGained(FocusEvent e) {
		
		
	}
	public void focusLost(FocusEvent e) {
		if (textField != null)
			((QMRFSubChapterText)chapter).setText(textField.getText());
		else if (textPane != null) {
/*
		      StringWriter writer = new StringWriter();
	          MinimalHTMLWriter htmlWriter = new MinimalHTMLWriter(writer, 
	            (StyledDocument)textPane.getDocument());
	          try {
	        	  htmlWriter.write();
	        	  ((QMRFSubChapterText)chapter).setText(writer.toString());	
	        	  
	          } catch (Exception x) {
	          */
	          
	        	  ((QMRFSubChapterText)chapter).setText(textPane.getText());	  
	        //  }

			
		}	
		
	}
	@Override
	public void setEditable(boolean editable) {

		super.setEditable(editable);
		if (textField != null) {
			textField.setEditable(editable);
			if (editable) textField.setBackground(Color.white);
			else textField.setBackground(disabled);			
		} else if (textPane != null) {
			textPane.setEditable(editable);
			if (editable) textPane.setBackground(Color.white);
			else textPane.setBackground(disabled);
		}	
	}

}


