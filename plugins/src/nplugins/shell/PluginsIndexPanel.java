/* TestUI.java
 * Author: Nina Jeliazkova
 * Date: Mar 16, 2008 
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

package nplugins.shell;

import java.awt.Dimension;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import nplugins.core.PluginPackageEntry;
import nplugins.core.PluginsPackageEntries;

public class PluginsIndexPanel extends JScrollPane  {
    protected PluginsTableModel ptm;
    protected JTable table;
    protected boolean animate = true;
    protected String title="Workflow";    
    /**
     * 
     */
    private static final long serialVersionUID = -9177580035692770353L;
    public PluginsIndexPanel(PluginsPackageEntries packages) {
        super();
        ptm = new PluginsTableModel(packages);

        table = new JTable(ptm);
        table.setTableHeader(null);
        /*{
			public void createDefaultColumnsFromModel() {
				TableModel m = getModel();
				if (m != null) {
					// Remove any current columns
					TableColumnModel cm = getColumnModel();
					while (cm.getColumnCount() > 0) {
						cm.removeColumn(cm.getColumn(0));
					}
					// Create new columns from the data model info
					final int columnSize[] = { 32, 32, 0};
					for (int i = 0; i < m.getColumnCount(); i++) {
						TableColumn newColumn = new TableColumn(i);
						if (columnSize[i]>0) {
							newColumn.setMaxWidth(columnSize[i]);
						}
						addColumn(newColumn);
					}
				}

			};
		};        
		*/
		table.setRowHeight(32);
        table.getColumnModel().getColumn(0).setMaxWidth(32);
        table.setShowVerticalLines(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setColumnSelectionAllowed(false);
		ListSelectionListener listener = new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) return;
				selectEntry(table.getSelectedRow());
		      }
		};
	    table.getSelectionModel().addListSelectionListener(listener);
        setViewportView(table);
        setPreferredSize(new Dimension(200,200));
        setMaximumSize(new Dimension(200,200));
    }
    @Override
    public String toString() {
    	return "Plugins";
    }
    protected PluginPackageEntry selectEntry(int row) {
		return ptm.getPackages().get(row);
    }
}

