/* PBTPage.java
 * Author: Nina Jeliazkova
 * Date: Oct 4, 2008 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2008  Nina Jeliazkova
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

package ambit2.plugin.pbt;

import java.awt.Color;
import java.awt.Insets;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.Border;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;


public class PBTPageBuilder {
    protected static Color inputColor = new Color(255,255,153);
    protected static Color labelColor = new Color(204,255,204);
    protected static Border loweredBorder = BorderFactory.createLoweredBevelBorder();
    protected static Border risedBorder = BorderFactory.createEmptyBorder();
    protected PBTPageBuilder() {
    }
    private static String getLayoutString(int repeat) {
    	StringBuffer b = new StringBuffer();
    	for (int i=0; i < repeat; i++) {
    		if (i>0) b.append(",");
    		b.append("pref");
    	}
    	return b.toString();
    }
    public static JPanel buildPanel(PBTTableModel model) {
    	return buildPanel(model,0,0);
    }
    public static JPanel buildPanel(PBTTableModel model, int rowOffset, int colOffset) {
        FormLayout layout = new FormLayout(
                getLayoutString(model.getColumnCount()+colOffset),
                getLayoutString(model.getRowCount()+rowOffset));
        PanelBuilder builder = new PanelBuilder(layout);
        CellConstraints cc = new CellConstraints();  
        cc.insets = new Insets(3,3,3,3);
        cc.hAlign = CellConstraints.DEFAULT;
        Enumeration<Cell> cells = model.getTable().keys();
        while (cells.hasMoreElements())  {
        	Cell cell = cells.nextElement();
        	if ((cell.row + rowOffset) <= 0) continue;
        	if ((cell.column + colOffset) <= 0) continue;
        	
        	Object o = model.getTable().get(cell);
        	JComponent c = null;
        	switch (cell.mode) {
        	case SECTION: {
        		builder.addSeparator(o.toString().trim(), 
        				cc.xyw(cell.column+colOffset,cell.row+rowOffset,cell.colspan));
        		break;
        	}
        	case TITLE: {
        		c = new JLabel("<html>"+o.toString().replace("\\n", "<br>")+"</html>");
        		c.setOpaque(true);
        		//c.setBackground(labelColor);
        		c.setBorder(risedBorder);
        		break;
        	}
        	case ERROR: {
        		/*wrappable labels*/
        		JTextPane txtMyTextPane= new JTextPane();
        		txtMyTextPane.setText("<html>"+o.toString().replace("\\n", "<br>")+"</html>");
        		txtMyTextPane.setBackground(null);
        		txtMyTextPane.setEditable(false);
        		txtMyTextPane.setBorder(null);
        		c = txtMyTextPane;
        		break; 
        		        	}        	
        	case LIST: {
        		c = BasicComponentFactory.createComboBox(new SelectionInList());
        		c.setBackground(inputColor);
        		break;
        	}
        	case FORMULA: {
        		//c = BasicComponentFactory.createTextField(valueModel,true);
        		c = new JTextField(o.toString());
        		c.setEnabled(false);
        		c.setBorder(BorderFactory.createEtchedBorder());
        		c.setToolTipText("This is an automatically calculated value");
        		break;
        	}
        	case INPUT: {
        		//c = BasicComponentFactory.createTextField(valueModel,true);
        		c = new JTextField(o.toString());
        		c.setBackground(inputColor);
        		c.setEnabled(true);
        		c.setBorder(loweredBorder);
        		break;
        	}        	
        	}
        	if (c != null)
        		builder.add(c,cc.xywh(cell.column+colOffset,cell.row+rowOffset,cell.colspan,cell.rowspan));
        }
        builder.setBorder(BorderFactory.createEtchedBorder());
        return builder.getPanel();
    }    

}
