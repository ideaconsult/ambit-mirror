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

package ambit2.ui.data;

import java.awt.Dimension;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.table.AbstractTableModel;

import org.openscience.cdk.CDKConstants;

import ambit2.data.molecule.IAtomContainersList;

public class RandomAccessFileTableModel extends AbstractTableModel implements PropertyChangeListener {
	protected CompoundImageTools imageTools;
    protected ArrayList<String> properties = null;
    protected boolean table = false;
    protected boolean multiColumn = false;
    /**
	 * 
	 */
	private static final long serialVersionUID = -3795703962981201655L;
    
	protected IAtomContainersList reader;

    public RandomAccessFileTableModel()  {
       this(null,new Dimension(200,200));
    }
    
    public RandomAccessFileTableModel(IAtomContainersList atomContainersList, Dimension cellSize) {
        super();
        setReader(atomContainersList);
        imageTools = new CompoundImageTools(cellSize);
        
        ArrayList<String> properties = new ArrayList<String>();
        properties.add(CDKConstants.CASRN);
        properties.add(CDKConstants.NAMES);
        setProperties(properties);
    }
    public void setCellSize(Dimension cell) {
    	imageTools.setImageSize(cell);
    	fireTableStructureChanged();
    }
    public int getRowCount() {
        if (reader ==null) return 0;
        return reader.getAtomContainerCount();

    }
    public int getColumnCount() {
        if (table) return properties.size()+1;
        else if (multiColumn) return properties.size()+2;
        else return 2;
    }
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        try {
            if (reader ==null) return;
            if (table) {
                
            } else {
                if (aValue instanceof Hashtable) {
                    Enumeration keys = ((Hashtable) aValue).keys();
                    while (keys.hasMoreElements()) {
                        Object key = keys.nextElement();
                        Object value = ((Hashtable) aValue).get(key);
                        reader.setProperty(rowIndex, key, value);
                    }
                }
            }
        } catch (Exception x) {
            
        }
    }
    public Object getValueAt(int rowIndex, int columnIndex) {
    	try {
	        if (reader ==null) return "";
	        if (table)
	        	if (columnIndex==0) return new Integer(rowIndex+1);
	        	else return reader.getProperty(rowIndex, properties.get(columnIndex-1));
	        else if (isMultiColumn()) {
	        	switch(columnIndex) {
	            case 0: return new Integer(rowIndex+1);
	            case 1: { return imageTools.getImage(reader.getAtomContainer(rowIndex)); }
	            default: {
	            	Object val = reader.getProperty(rowIndex,properties.get(columnIndex-2));
	            	if (val != null) return val; else return "N/A";
	            }
	            }
	        	
	        } else
	            switch(columnIndex) {
	            case 0: try {
	                    Object o = reader.getAtomContainer(rowIndex);
	                    return imageTools.getImage(o);
	            } catch (Exception x) {
	                x.printStackTrace();
	                return x.getMessage();
	            }
	            case 1: {
	                try {
                        Hashtable b = new Hashtable();
                        b.put("#",Integer.toString(rowIndex+1));
                        //Collections.sort(properties);
                        for (int i=0; i < properties.size();i++) {
                        	Object key = properties.get(i);
                        	if ((key != null) && !("".equals(key))) {
                        		Object val = reader.getProperty(rowIndex,key);
                        		if (val != null)
                        			b.put(key.toString(),val);
                        	}
                        }
                        return b;
	                	/*
	                    ArrayList<String> b = new ArrayList<String>();
	                    b.add("#"+Integer.toString(rowIndex+1));
	                    for (int i=0; i < properties.size();i++) {
	                        b.add(properties.get(i).toString() + '=' + reader.getProperty(rowIndex, properties.get(i)));
	                    }
	                    return imageTools.getImage(b);
                        */
	                    //return b.toString();
	                } catch (Exception x) {
	                    x.printStackTrace();
	                    return x.getMessage();
	                }
	            }
	        
	            }
	        return "N/A";
	    } catch (Exception x) {
	    	return x.getMessage();
	    }	
    }
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (table)super.getColumnClass(columnIndex);
        else
        	if (isMultiColumn()) {
	            switch(columnIndex) {
	            case 0: return Integer.class;
	            case 1: return Image.class;
	            default: return String.class;
	            }
        		
        	} else 
	            switch(columnIndex) {
	            case 0: return Image.class;
	            case 1: return Hashtable.class;
	            //case 1: return Image.class;
	            }
	            return super.getColumnClass(columnIndex);
    }

    public synchronized IAtomContainersList getReader() {
        return reader;
    }
    public synchronized void setReader(IAtomContainersList reader) {
        this.reader = reader;
        
        //System.out.println(getRowCount());
        fireTableStructureChanged();
    }
    @Override
    public String toString() {
        if (reader == null) return "";
        return reader.toString();
    }
    public synchronized ArrayList getProperties() {
        return properties;
    }
    public synchronized void setProperties(ArrayList properties) {
    	if (this.properties == null) this.properties = new ArrayList<String>();
    	else this.properties.clear();
    	if (properties != null)
    		for (int i=0; i < properties.size();i++)
    			this.properties.add(properties.get(i).toString());
        
        fireTableStructureChanged();
    }
    public void propertyChange(PropertyChangeEvent e) {
        	Object oldValue = e.getOldValue();
            if (e.getOldValue() != null) 
            	if (oldValue instanceof Collection) {
                    
	        		Iterator i = ((Collection) oldValue).iterator();
	        		while (i.hasNext()) {
	        			 properties.remove(i.next());
	        		}
	        	} else properties.remove(e.getOldValue());
    
            
            Object newValue = e.getNewValue();
            if (e.getNewValue() != null)
	        	if (newValue instanceof Collection) {
                    properties.clear();
	        		Iterator i = ((Collection) newValue).iterator();
                    
	        		while (i.hasNext()) {
                        Object o = i.next();
                        //properties.remove(o);
	        			addProperty(o);
	        		}
	        	} else {
                    properties.remove(e.getNewValue());
                    addProperty(e.getNewValue());
                }
            else properties.clear();
            fireTableDataChanged();

    }
    protected void addProperty(Object property) {
    	if (!properties.contains(property))
        	properties.add(property.toString());
    }
    public synchronized boolean isTable() {
        return table;
    }
    public synchronized void setTable(boolean table) {
        this.table = table;
        fireTableStructureChanged();
    }
    @Override
    public String getColumnName(int column) {
    	try {
        if (table)
        	if (column==0) return "#";
        	else return properties.get(column-1).toString();
        else 
        	if (isMultiColumn())
	            switch (column) {
	            case 0: return "#";
	            case 1: return "Structure diagram";
	            default: return properties.get(column-2);
	            }
        	else	
	            switch (column) {
	            case 0: return "Structure";
	            case 1: return "Properties";
	            }
    	} catch (Exception x) {
    		
    	}
        return "";
    }
    @Override
    public boolean isCellEditable(int row, int col) {
    	return (reader!= null)            && ((col==1) );
    }

	public boolean isMultiColumn() {
		return multiColumn;
	}

	public void setMultiColumn(boolean multiColumn) {
		this.multiColumn = multiColumn;
	}
}


