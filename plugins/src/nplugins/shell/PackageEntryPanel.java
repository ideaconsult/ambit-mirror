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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

import nplugins.core.PluginPackageEntry;

public class PackageEntryPanel extends JScrollPane  implements PropertyChangeListener {
    protected PackageEntryModel ptm;
    protected JTable table;
    protected boolean animate = true;
    protected String title="Workflow";    
    /**
     * 
     */
    private static final long serialVersionUID = -9177580035692770353L;
    public PackageEntryPanel() {
		this(null);
	}
    public PackageEntryPanel(PluginPackageEntry entry) {
        super();
        ptm = new PackageEntryModel(entry);

        table = new JTable(ptm);
        table.setShowVerticalLines(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setColumnSelectionAllowed(false);

        setViewportView(table);
        setPreferredSize(new Dimension(400,400));
        setMaximumSize(new Dimension(400,400));
        setMinimumSize(new Dimension(200,200));
    }
    @Override
    public String toString() {
    	return "Plugins";
    }
	public PluginPackageEntry getEntry() {
		return ptm.getEntry();
	}

	public void setEntry(PluginPackageEntry entry) {
		ptm.setEntry(entry);
	}    
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getNewValue() instanceof PluginPackageEntry) {
			setEntry((PluginPackageEntry)evt.getNewValue());
		}
		
	}
}

class PackageEntryModel extends AbstractTableModel {
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 477557537371696375L;
	protected ArrayList<Attributes.Name> names = new ArrayList<Attributes.Name>();
	protected PluginPackageEntry entry;
	public PluginPackageEntry getEntry() {
		return entry;
	}

	public void setEntry(PluginPackageEntry entry) {
		this.entry = entry;
		names.clear();
		if ((entry != null) && (entry.getJar()!= null)) try {
		        Attributes a = entry.getJar().getAttributes();
		        Iterator ai = a.keySet().iterator();
		        while (ai.hasNext()) 
		            names.add((Attributes.Name)ai.next());
		        Collections.sort(names,new Comparator<Attributes.Name>() {
		        	public int compare(Name o1, Name o2) {

		        		return o1.toString().compareTo(o2.toString());
		        	}
		        });		        
	        } catch (Exception x) {
	        	//names.add(x.getMessage());
	        }
	        
	    else {
	    	names.add(Attributes.Name.IMPLEMENTATION_TITLE);
	    	names.add(Attributes.Name.MAIN_CLASS);
	    }
		fireTableStructureChanged();
	}

	public PackageEntryModel(PluginPackageEntry entry) {
		setEntry(entry);
	}
	/*
	protected Attributes.Name[] keys = new Attributes.Name[] {
		Attributes.Name.IMPLEMENTATION_TITLE,
		Attributes.Name.IMPLEMENTATION_VERSION,
		Attributes.Name.IMPLEMENTATION_VENDOR,
		Attributes.Name.IMPLEMENTATION_URL,
		Attributes.Name.IMPLEMENTATION_VENDOR_ID,
		
		Attributes.Name.SPECIFICATION_TITLE.toString(),
		Attributes.Name.SPECIFICATION_VERSION.toString(),
		Attributes.Name.SPECIFICATION_VENDOR.toString(),
		
		Attributes.Name.SIGNATURE_VERSION.toString(),
		
		Attributes.Name.SEALED.toString(),
		
		Attributes.Name.CLASS_PATH.toString(),
		Attributes.Name.CONTENT_TYPE.toString(),
		Attributes.Name.EXTENSION_NAME.toString(),
		Attributes.Name.EXTENSION_INSTALLATION.toString(),
		Attributes.Name.EXTENSION_LIST.toString(),
		
		Attributes.Name.MAIN_CLASS.toString(),
		
	};
	*/
	

	public int getColumnCount() {
		return 2;
	}

	public int getRowCount() {
		return names.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex==0) return names.get(rowIndex);
		if (entry == null) return "";
		if (entry.getJar() != null) 
			try {
				return entry.getJar().getAttributes().get(names.get(rowIndex));
			} catch (IOException x) {
				return x.getMessage();
			}
		else
			switch (rowIndex) {
			case 0: return entry.getTitle();
			case 1: return entry.getClassName();
			default: return "";
			}
		
	}
	@Override
	public String getColumnName(int column) {
		switch (column) {
		case 0: return "Name";
		case 1: return "Value";
		default: return "";
		}
	}
}