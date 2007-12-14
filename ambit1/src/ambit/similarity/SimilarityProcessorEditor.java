/* SimilarityProcessorEditor.java
 * Author: Nina Jeliazkova
 * Date: Mar 14, 2007 
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

package ambit.similarity;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import ambit.data.IAmbitEditor;
import ambit.exceptions.AmbitException;

public class SimilarityProcessorEditor extends JPanel implements IAmbitEditor {
    protected JTextPane textArea;
    protected boolean editable = false;
    protected ISimilarityProcessor  model;
    protected Component editor;
    public SimilarityProcessorEditor(ISimilarityProcessor model) {
        super(new BorderLayout());
        this.model = model;
        
        add(createLabel(),BorderLayout.NORTH);
        
        editor = createEditor();
        add(editor,BorderLayout.CENTER);
        //add(detailsButton);
        setPreferredSize(new Dimension(200,32));
        setMaximumSize(new Dimension(200,100));
    }
    protected Component createLabel() {
        JTextField t =  new JTextField(model.toString());
        t.setToolTipText(t.getText());
        t.setEnabled(false);
        return t;
    }
    
    protected Component createEditor() {
        JTextPane textArea =  new JTextPane();
        textArea.setText(model.getStatus(true));
        textArea.setEditable(false);
        return new JScrollPane(textArea);
    }

    public JComponent getJComponent() {
        return this;
    }
    public boolean view(Component parent, boolean editable, String title) throws AmbitException {
        setEditable(editable);
        JOptionPane.showMessageDialog(parent,this);
        return true;
    }
    public boolean isEditable() {
        return editable;
    }
    public void setEditable(boolean editable) {
        this.editable = editable;
        if (textArea !=null)
        textArea.setEditable(editable);
    }    


}
