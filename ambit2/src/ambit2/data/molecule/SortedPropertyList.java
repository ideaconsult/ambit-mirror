/* SortedPropertyList.java
 * Author: Nina Jeliazkova
 * Date: Nov 8, 2006 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2006  Nina Jeliazkova
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

package ambit2.data.molecule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SortedPropertyList extends ArrayList<SortedProperty> {
    protected Object propertyName;
    public SortedPropertyList(Object propertyName) {
        super();
    }
    public synchronized Object getPropertyName() {
        return propertyName;
    }
    public synchronized void setPropertyName(Object propertyName) {
        this.propertyName = propertyName;
    }
    public void sort(boolean ascending) {
        Collections.sort(this, new SortedPropertyComparator(ascending));
    }

    public void addProperty(Object o, int index) {
        add(new SortedProperty(o,index));
    }
    public void addList(IAtomContainersList list) throws Exception {
        if ((propertyName ==null) || (propertyName.equals(""))) throw new Exception("Lookup property not defined!");
        clear();
        for (int i=0; i < list.getAtomContainerCount();i++) {
           Object o = list.getProperty(i, propertyName);
           addProperty(o,i);
        }   
    }
    @Override
    public void clear() {
        super.clear();
    }
    @Override
    public int indexOf(Object elem) {
        if (elem instanceof SortedProperty)
            return super.indexOf(elem);
        else 
            return super.indexOf(new SortedProperty(elem,-1));
    }
    public int getOriginalIndexOf(Object elem) {
        int index = indexOf(elem);
        if (index < 0) return index;
        //SortedProperty.index
        return get(index).getIndex();
    }
    @Override
    public String toString() {
        StringBuffer b = new StringBuffer();
        for (int i=0;i< size();i++) {
            b.append(get(i).toString());
            b.append('\n');
        }    
        return b.toString();
    }
}

class SortedProperty implements Comparable<Object> {
    protected Comparable property;
    protected int index;
    
    public SortedProperty(Object property, int index) {
        super();
        setIndex(index);
        if (property == null) setProperty(null);
        else
            if (property instanceof Comparable)
                setProperty((Comparable)property);
            else 
                setProperty(property.toString());
    }
    public synchronized int getIndex() {
        return index;
    }
    public synchronized void setIndex(int index) {
        this.index = index;
    }
    public synchronized Comparable getProperty() {
        return property;
    }
    public synchronized void setProperty(Comparable property) {
        this.property = property;
    }
    public int compareTo(Object o) {
        if ((property==null) && (o==null)) return 0;
        try {
            if (o instanceof SortedProperty)
                if (property ==null) return -1;
                else return property.compareTo(((SortedProperty) o).getProperty());
            else 
                if (property ==null) return -1;
                else return property.compareTo(o);
        } catch (Exception x) {
            return -1;
        }
    }
    @Override
    public boolean equals(Object o) {
        if (o instanceof SortedProperty)
            return property.equals(((SortedProperty) o).getProperty());
        else 
            return property.equals(o);
    }
    @Override
    public String toString() {
        return index + ".\t" + property.toString();
    }
}


class SortedPropertyComparator implements Comparator<SortedProperty> {
    protected boolean ascending;
    public synchronized boolean isAscending() {
        return ascending;
    }
    public synchronized void setAscending(boolean ascending) {
        this.ascending = ascending;
    }
    public SortedPropertyComparator(boolean ascending) {
        super();
        setAscending(ascending);
    }
    public int compare(SortedProperty o1, SortedProperty o2) {
        int r = o1.compareTo(o2);
        if (!ascending) return -r; else return r;
    }
}