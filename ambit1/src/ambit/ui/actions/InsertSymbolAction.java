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

package ambit.ui.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;

import ambit.data.qmrf.JUnicodePanel;

public class InsertSymbolAction extends TextAction {
	protected Component parent;
	public InsertSymbolAction(Component parent, String name) {
		super(name);
		this.parent = parent;
	}

	public void actionPerformed(ActionEvent e) {
        JTextComponent target = getTextComponent(e);
        if (target != null) {
        	JUnicodePanel p = new JUnicodePanel();
    		if (JOptionPane.showConfirmDialog(null,p,"Select symbol",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {

    			Document doc = target.getDocument();
    			try {
    				doc.insertString(
    					target.getCaretPosition(), 
    					JUnicodePanel.getChar(p.getCodepoint()).toString(), null);
    				
    			} catch (Exception x) {
    				x.printStackTrace();
    			}

//    			target.paste();
    		}	
        }
	}

}


