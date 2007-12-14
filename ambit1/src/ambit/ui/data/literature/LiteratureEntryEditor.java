/* LiteratureEntryEditor.java
 * Author: Nina Jeliazkova
 * Date: 2006-6-13 
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

package ambit.ui.data.literature;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ambit.data.AmbitObject;
import ambit.data.literature.LiteratureEntry;
import ambit.ui.data.AbstractAmbitEditor;
import ambit.ui.data.AbstractPropertyTableModel;
import ambit.ui.data.AmbitListEditor;

/**
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-6-13
 */
public class LiteratureEntryEditor extends AbstractAmbitEditor {
    public LiteratureEntryEditor() {
        super("Reference",null);
    }	
    /**
     * 
     */
    public LiteratureEntryEditor(LiteratureEntry reference) {
        super("Reference",reference);
    }
    /* (non-Javadoc)
     * @see ambit.ui.data.AbstractAmbitEditor#addWidgets(javax.swing.JComponent)
     */
    protected void addWidgets(JComponent component) {
        component.setLayout(new BoxLayout(component,BoxLayout.PAGE_AXIS));
        text = new JFormattedTextField[model.getRowCount()];
        JPanel p = null;
        JLabel l ;
        
        Dimension dlarge = new Dimension(100,24);
        Dimension dsmall = new Dimension(64,24);
        Dimension d = dlarge;
        
                
        for (int i=0; i < model.getRowCount(); i++) {
        	if ((i < 4) || (i>5)) {
        		p = new JPanel();
        		p.setLayout(new BoxLayout(p,BoxLayout.LINE_AXIS));
        		if (i==3) d = dsmall;
        		else d = dlarge;
        	} else d = dsmall;
        	l = new JLabel(model.getValueAt(i,0).toString());
        	l.setPreferredSize(dsmall);
        	p.add(l);
        	JFormattedTextField t =new JFormattedTextField(model.getValueAt(i,1).toString() );
        	t.setFocusLostBehavior(JFormattedTextField.COMMIT);
        	t.setMaximumSize(new Dimension(Integer.MAX_VALUE,24));
        	t.setMinimumSize(new Dimension(32,24));
        	if (i==0) {
        		t.setEditable(false);
        		t.setToolTipText("Click here to edit");
        		t.addKeyListener(new KeyAdapter() {
        			public void keyPressed(KeyEvent e) {
        				showAuthors();
        			}
        		});
        		t.addMouseListener(new MouseAdapter() {
        			public void mouseClicked(MouseEvent e) {
        				showAuthors();
        			}
        		});
        	}
        	t.setName(Integer.toString(i));
	        	t.addPropertyChangeListener("value",new PropertyChangeListener() {
        		public void propertyChange(PropertyChangeEvent evt) {
        			for (int i=0; i < text.length;i++)
       				if ((text[i]!=null) && (text[i].equals(evt.getSource())))
	        			try {
	        			model.setValueAt(evt.getNewValue(), 
	        					Integer.parseInt(
	        							((JTextField) (evt.getSource())).getName()), 
	        					1);
	        			} catch (Exception x) {
	        				
	        			}
        			
        		}
        	});
        	t.addActionListener(new ActionListener() {
        		public void actionPerformed(java.awt.event.ActionEvent e) {
        			
        			model.setValueAt(((JTextField)e.getSource()).getText(), 
        					Integer.parseInt(e.getActionCommand()), 
        					1);
        		};
        	});
        	t.setPreferredSize(d);
        	text[i] = t;
        	p.add(text[i]);
        	component.add(p);
        }
        
        setMinimumSize(new Dimension(300,24*5));
        setPreferredSize(new Dimension(300,24*6));
        setMaximumSize(new Dimension(Integer.MAX_VALUE,32*6));
    }
    
    /* (non-Javadoc)
     * @see ambit.ui.data.AbstractAmbitEditor#createModel(ambit.data.AmbitObject)
     */
    protected AbstractPropertyTableModel createTableModel(AmbitObject object) {
        return new LiteratureEntryTableModel((LiteratureEntry) object);
    }
    protected void showAuthors() {
        LiteratureEntry reference = (LiteratureEntry) object;
    	JOptionPane.showMessageDialog(this,
    			new AmbitListEditor(reference.getAuthors(),reference.getAuthors().isEditable())
    			,"Authors",JOptionPane.PLAIN_MESSAGE);
    	((JFormattedTextField)text[0]).setText(reference.getAuthors().toString());
    }


	public JComponent getJComponent() {
		return this;
	}

}
