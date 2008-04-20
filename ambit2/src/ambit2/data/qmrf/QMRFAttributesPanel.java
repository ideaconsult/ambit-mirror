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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import ambit2.exceptions.AmbitException;
import ambit2.ui.AmbitColors;
import ambit2.ui.SpringUtilities;
import ambit2.ui.editors.IAmbitEditor;

public class QMRFAttributesPanel extends JPanel implements IAmbitEditor<QMRFAttributes>, FocusListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3273797535238357131L;
	protected boolean editable = true;
	protected QMRFAttributes attributes;
	protected JTextField[] field;

	public QMRFAttributesPanel() {
		
	}	
	public QMRFAttributesPanel(QMRFAttributes attributes) {
		this(attributes,attributes.getNames(),attributes.getNames());
	}
    public QMRFAttributesPanel(QMRFAttributes attributes, String[] fields, String[] fieldNames) {
    	this.attributes = attributes;
        ArrayList<String> f = new ArrayList<String>();
        ArrayList<String> n = new ArrayList<String>();
        for (int i=0; i < fields.length;i++) {
            f.add(fields[i]);
            n.add(fieldNames[i]);
        }
        addWidgets(f,n);
    }
	public QMRFAttributesPanel(QMRFAttributes attributes, ArrayList<String> fields, ArrayList<String> fieldNames) {
		super();
		this.attributes = attributes;
		addWidgets(fields, fieldNames);
	}
	public QMRFAttributes getObject() {
		// TODO Auto-generated method stub
		return null;
	}
	public void setObject(QMRFAttributes object) {
		this.attributes = object;
		if (object != null)
			addWidgets(attributes.getNames(), attributes.getNames());
	}	
	@Override
	public boolean isFocusable() {
		return false;
	}
    public void addWidgets(ArrayList<String> fields, ArrayList<String> fieldNames) {
        SpringLayout layout = new SpringLayout();
        setLayout(layout);
        setBackground(Color.white);
        field = new JTextField[fields.size()];
        Dimension mind = new Dimension(100,18);
        Dimension maxd = new Dimension(Integer.MAX_VALUE,18);
        
        int h = 0;
        for (int i=0; i < fields.size();i++) {
            JLabel label = new JLabel(fieldNames.get(i));
            label.setForeground(AmbitColors.DarkClr);
            field[i] = new JTextField(attributes.get(fields.get(i)));
            field[i].setName(fields.get(i));
            field[i].addFocusListener(this);
            field[i].setEditable(editable);
            //field[i].setMinimumSize(mind);
            field[i].setMaximumSize(maxd);
            field[i].setForeground(AmbitColors.DarkClr);
            label.setLabelFor(field[i]);
            
            add(label);
            add(field[i]);
            h += 24 +6;
        }
        SpringUtilities.makeCompactGrid(this,
                field.length, 2, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad
        /*
        Dimension d = new Dimension(Integer.MAX_VALUE,h+6);
        //setPreferredSize(d);
         * */
        Dimension md = new Dimension(100,h+10);
        setMinimumSize(md);
        setPreferredSize(md);

        
    }
	public void setEditable(boolean editable) {
		this.editable = editable;
		if (field != null)
		for (int i=0; i < field.length;i++) field[i].setEditable(editable);
	}
	public JComponent getJComponent() {
		//return new JScrollPane(this);
		return this;
	}
	public boolean view(Component parent, boolean editable, String title) throws AmbitException {
		return JOptionPane.showConfirmDialog(parent,this) == JOptionPane.OK_OPTION;
	}
	public void focusGained(FocusEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void focusLost(FocusEvent e) {
		if (e.getSource() instanceof JTextField) {
			
			String key = ((JTextField)e.getSource()).getName();
			update(key,((JTextField)e.getSource()).getText());
		}
		
	}
	protected void update(String key,String value) {
		attributes.put(key, value);
	}
	public boolean isEditable() {
		return editable;
	}

	
}


