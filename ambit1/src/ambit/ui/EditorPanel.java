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

package ambit.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ambit.data.IAmbitEditor;

public class EditorPanel extends JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 4466287344655179932L;
	protected JComponent component = null;
	protected IAmbitEditor editor = null;
	protected String noDataText = "";//<html>Data not available. You may add a new item or select it from a catalog.</html>";
	protected JLabel nodatalabel = new JLabel(noDataText);

	public EditorPanel() {
        super(new BorderLayout());
        setFocusable(false);
    }

	public EditorPanel(IAmbitEditor editor) {
        super(new BorderLayout());
        setEditor(editor);
        setFocusable(false);
        setPreferredSize(new Dimension(200,200));
        setBorder(BorderFactory.createLineBorder(AmbitColors.BrightClr));
    }	
	@Override
	public boolean isFocusable() {
		return false;
	}
    public void setEditor(IAmbitEditor editor) {
        if (component!=null) remove(component);
        component = null;
        this.editor = editor;
        if (editor != null) {
            component = editor.getJComponent();        
            if (component==null)  component = nodatalabel;
        } else {
        	component = nodatalabel;
        }
        component.setFocusable(true);
       	add(component,BorderLayout.CENTER);
       	setPreferredSize(component.getPreferredSize());
       	setMaximumSize(component.getMaximumSize());
       	setMinimumSize(component.getMinimumSize());
        validate();
    }
    
    public void setEditable(boolean value) {
    	if (editor != null) editor.setEditable(value);
    }

	public String getNoDataText() {
		return noDataText;
	}

	public void setNoDataText(String noDataText) {
		this.noDataText = noDataText;
		nodatalabel.setText(noDataText);
	}
    
}


