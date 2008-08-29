/* ConcealableList.java
 * Author: Nina Jeliazkova
 * Date: Aug 2, 2008 
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

package ambit2.core.groups;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * 
 * A list that can be filtered by setting a filter elements.
 * The iterator will return only visible elements.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 2, 2008
 */
public class FilteredList<T>  implements List<T> , IFiltered<T>{
    /**
     * 
     */
    private static final long serialVersionUID = 1018513075305858000L;
    protected int size = 0;
    protected ArrayList<T> activeList;    
    private ArrayList<T> content;
    private ArrayList<T> filter;

    public FilteredList() {
        super();
        content = new ArrayList<T>();
        filter = new ArrayList<T>();
        setFiltered(false);
    }
    public int size() {
    	return activeList.size();
    }
    public Iterator<T> iterator() {
    	return activeList.iterator();
    }
 
 
	public boolean isFiltered() {
		return activeList == filter;
	}
	public void setFiltered(boolean filtered) {
		if (filtered) activeList = filter;
		else activeList = content;
	}
	public boolean isEmpty() {
		return activeList.isEmpty();
	}
	public boolean contains(Object o) {
		return activeList.contains(o);
	}
	public Object[] toArray() {
		return activeList.toArray();
	}
	public <T> T[] toArray(T[] a) {
		return activeList.toArray(a);
	}
	public boolean add(T o) {
		if (isFiltered()) {
			boolean ok = content.add(o);
            if (filter.indexOf(o)==-1)
                return filter.add(o);
            return ok;
		} else 
			return content.add(o);
	}
	
	public boolean remove(Object o) {
		return delete((T)o);
		
	}
	public boolean delete(T o) {
		filter.remove(o);
		return content.remove(o);
	}	
	public boolean containsAll(Collection<?> c) {
		return activeList.containsAll(c);
	}
	public boolean addAll(Collection<? extends T> c) {
		if (isFiltered()) {
			content.addAll(c);
			return filter.addAll(c);
		} else 
			return content.addAll(c);		
	}
	public boolean addAll(int index, Collection<? extends T> c) {
		if (isFiltered())
			filter.addAll(index,c);
		return content.addAll(index, c);
	}
	public boolean removeAll(Collection<?> c) {
		filter.removeAll(c);
		return content.removeAll(c);
	}
	public boolean retainAll(Collection<?> c) {
		filter.retainAll(c);
		return content.retainAll(c);
	}
	public void clear() {
		activeList.clear();
//      if not filtered, we cleared up the content, therefore we need to clear the filtered content as well
		if (!isFiltered())  
			filter.clear();
	}
	public T get(int index) {
		return activeList.get(index);
	}
	public T set(int index, T element) {
		ArrayList<T> otherList = null;
		
		if (isFiltered()) {
			otherList = content;
		} else {
			otherList = filter;
		}
		T item = activeList.get(index);
		activeList.set(index, element);
		int oldindex = otherList.indexOf(item);
		if (oldindex > -1) {
			otherList.set(oldindex, element);
		} else
			if (otherList == content)
				throw new IllegalArgumentException("No such element");
		return element;
	}
	public void add(int index, T element) {
		content.add(index,element);
        if (filter.indexOf(element)==-1)
            filter.add(index,element);
	}
    public void addToFilter(T item) {
        if (item == null) return;
        if (content.indexOf(item)==-1)
            content.add(item);
        if (filter.indexOf(item)==-1)
            filter.add(item);
    }    
    public void addToFilter(int index) {
        T item = get(index);
        if (item == null) return;
        if (filter.indexOf(item)==-1)
            filter.add(item);
    }
    public void removeFromFilter(int index) {
        removeFromFilter(get(index));
    }
    public void removeFromFilter(T item) {
        if (item == null) return;
        filter.remove(item);
    }  
    public void clearFilter() {
        filter.clear();
        setFiltered(false);
    }       
    
	public T remove(int index) {
		T item = null;
		if (isFiltered()) {
			item = filter.get(index);
			filter.remove(index);
		} else {
			item = content.get(index);
			content.remove(index);
		}
		return item;
	}
	public int indexOf(Object o) {
		return activeList.indexOf(o);
	}
	public int lastIndexOf(Object o) {
		return activeList.lastIndexOf(o);
	}
	public ListIterator<T> listIterator() {
		return activeList.listIterator();
	}
	public ListIterator<T> listIterator(int index) {
		return activeList.listIterator(index);
	}
	public List<T> subList(int fromIndex, int toIndex) {
		return activeList.subList(fromIndex, toIndex);
	}
	public int getNumber(T item) {
        for (int i=0; i < activeList.size();i++)
            if (item == activeList.get(i)) return i;
        return -1;
	}
    public boolean isVisible(T item) {
        return activeList.contains(item);
    }
    public boolean isFiltered(T item) {
        return filter.contains(item);
    }
}
