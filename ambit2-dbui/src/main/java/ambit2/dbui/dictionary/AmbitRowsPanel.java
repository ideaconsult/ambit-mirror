/*
Copyright (C) 2005-2008  

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

package ambit2.dbui.dictionary;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JPanel;

import ambit2.base.interfaces.IAmbitEditor;
import ambit2.db.results.AmbitRows;
import ambit2.dbui.CachedRowSetTableModel;
import ambit2.ui.QueryBrowser;
import ambit2.ui.table.BrowsableTableModel;

public class AmbitRowsPanel<T, Rows extends AmbitRows<T>> 
							extends JPanel implements IAmbitEditor<Rows> {
    protected CachedRowSetTableModel srtm;
    private static final long serialVersionUID = 2854619268267349642L;
    protected boolean editable;
    protected QueryBrowser<BrowsableTableModel> browser ;
    
    public AmbitRowsPanel() {
        super();
        addWidgets();
    }
    protected void addWidgets() {
    	setLayout(new BorderLayout());
    	srtm = new CachedRowSetTableModel();
    	browser = new QueryBrowser<BrowsableTableModel>(null,new Dimension(150,20));
    	add(browser, BorderLayout.CENTER);
   
    }
    public JComponent getJComponent() {
    	return this;
    }
    public boolean isEditable() {
    	return editable;
    }
    public void setEditable(boolean editable) {
    	this.editable = editable;
    	
    }
    public Rows getObject() {
    	return (Rows)srtm.getRecords();
    }
    public void setObject(Rows object) {
    	try { getObject().close(); } catch (Exception x) {};
  		srtm.setRecords(object.getRowSet());
  		browser.setObject(new BrowsableTableModel(srtm));
    }
    
    public boolean confirm() {
    	return true;
    }
}


