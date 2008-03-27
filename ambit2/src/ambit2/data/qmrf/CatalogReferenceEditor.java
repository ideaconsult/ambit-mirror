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
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import ambit2.ui.UITools;
import ambit2.ui.actions.AbstractActionWithTooltip;
import ambit2.ui.editors.AmbitListOneItemEditor;
import ambit2.ui.editors.IAmbitEditor;
import ambit2.data.AmbitList;

public class CatalogReferenceEditor extends CatalogEditor {
	/*
	public CatalogReferenceEditor(CatalogReference list, boolean editable) {
		super(list,editable);
		setPreferredSize(new Dimension(200,300));
	}
	*/
	public CatalogReferenceEditor(CatalogReference list,
			boolean searchPanel) {
		super(list,false,new Dimension(200,58));
		setPreferredSize(new Dimension(200,250));
	}
	@Override
	public JToolBar createJToolbar() {
		JToolBar toolbar = super.createJToolbar();
		Catalog external = ((CatalogReference) list).getExternal_catalog();
		if ((external == null) || (external.size()==0)) return toolbar; 
		toolbar.setFocusable(false);
		JButton b = new JButton(new AbstractActionWithTooltip("Lookup",UITools.createImageIcon("ambit2/ui/images/search.png"),
				"Find item in the " + list.toString() +" catalog and add it here") {
			public void actionPerformed(ActionEvent e) {
                Catalog catalog = ((CatalogReference) list).getCatalog();
                Catalog external_catalog = ((CatalogReference) list).getExternal_catalog();                
                if (catalog != null) {
                    CatalogEntry c =selectEntries(external_catalog);
                    if (c != null) {
                		c.setproperty("id", "");
                		//((CatalogReference) list).addReference(c);
                		((CatalogReference) list).addItem(c);
                    }
                    selectItem(((CatalogReference) list).size()-1,true);
                };   
				
			}
			
		});
		b.setFocusable(false);
		toolbar.add(b);
		return toolbar;
	}
    protected CatalogEntry selectEntries(Catalog catalog) {
    	IAmbitEditor e = catalog.editor(false);
        if (JOptionPane.showConfirmDialog(this,e.getJComponent(),catalog.toString(),JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE)
            == JOptionPane.OK_OPTION ) {
        	int index = ((AmbitListOneItemEditor)e).getCurrentIndex();
        	return (CatalogEntry)catalog.getItem(index);
        } else return null;
    }
 
}



