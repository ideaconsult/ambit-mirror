/* WorkflowContextTableModel.java
 * Author: Nina Jeliazkova
 * Date: Mar 17, 2008 
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

package com.microworkflow.ui;

import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

public class MapTableModel<K,V> extends AbstractTableModel {
    public static String NA = "N/A";
    private Vector<K> labels;
    protected Map<K,V> map;
    public enum Mode { labels_in_rows,labels_in_columns };
    protected Mode mode = Mode.labels_in_rows;
    protected String[] columnNames = {"Property","Value"};


    /**
     * 
     */
    private static final long serialVersionUID = -666464749854177791L;
    public MapTableModel(Map<K,V> map) {
        this();
        setMap(map);
    }
    
    public MapTableModel() {
        super();
        labels = new Vector<K>();
        map = null;
    }
    public int getColumnCount() {
        if (Mode.labels_in_columns.equals(mode)) 
            return labels.size();
        else 
            return 2;
    }

    public int getRowCount() {
        if (Mode.labels_in_columns.equals(mode)) 
            return 1;
        else 
            return labels.size();
    }

    public Object getValueAt(int row, int col) {
        if (Mode.labels_in_columns.equals(mode))
            return getValue(col);
        else    
            switch (col) {
            case 0:
                return getLabel(row);
            default:
                return getValue(row);
            }
    }
    @Override
    public String getColumnName(int col) {
        if (Mode.labels_in_columns.equals(mode))
            return labels.get(col).toString();
        else return columnNames[col];
    }
    
    protected K getLabel(int index) {
        return labels.get(index);
    }
    protected V getValue(int index) {
        if (map ==null) return null;
        else return map.get(labels.get(index));        
    }
    public synchronized Map<K,V> getMap() {
        return map;
    }
    public synchronized void keyChange(K label,V value) {
        if (labels.indexOf(label) == -1) {
            labels.add(label);
            fireTableStructureChanged();
        } else fireTableDataChanged();
    }
    public synchronized void setMap(Map<K,V> map) {
        if (this.map == map) return;
        this.map = map;
        if (map != null) {
            Iterator<K> i = map.keySet().iterator();
            labels.clear();
            /*
            while (i.hasNext())
                labels.add(i.next());
                */
            
        } else labels.clear();
        fireTableStructureChanged();

    }

    public synchronized Mode getMode() {
        return mode;
    }

    public synchronized void setMode(Mode mode) {
        if (this.mode != mode) {
            this.mode = mode;
            fireTableStructureChanged();
        }
    }

}
