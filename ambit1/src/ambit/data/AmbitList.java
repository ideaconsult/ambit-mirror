/**
 * Filename: AmbitList.java 
 * @Author: Nina Jeliazkova
 * @Date: 2005-3-23
 *
 * Copyright (C) 2005  AMBIT project http://luna.acad.bg/nina/projects
 *
 * Contact: nina@acad.bg
 * 
 */
package ambit.data;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;

import ambit.exceptions.AmbitIOException;
import ambit.ui.data.AmbitListEditor;
import ambit.ui.data.AmbitListOneItemEditor;

/**
 * A list of {@link ambit.data.AmbitObject}s
 * @author Nina Jeliazkova
 * Modified: 2005-4-6
 *
 * Copyright (C) 2005,2006  AMBIT project http://ambit.acad.bg
 *
 * Contact: nina@acad.bg
 * 
 */
public class AmbitList extends AmbitObject  {
	protected AmbitListChanged listsEvent = null;
	protected ArrayList list  = null;
	protected int selectedIndex = -1;	
	/**
	 * 
	 */
	public AmbitList() {
		super();
		list = new ArrayList();
	}

	/**
	 * @param initialCapacity
	 */
	public AmbitList(int initialCapacity) {
		super();
		list = new ArrayList(initialCapacity);
	}

	/**
	 * @param c
	 */
	public AmbitList(Collection c) {
		super();
		list = new ArrayList(c);
	}
	

	
	/**
	 * @return Returns the selectedIndex.
	 */
	public int getSelectedIndex() {
		return selectedIndex;
	}
	
	public void setSelectedIndex(int selectedIndex) {
		setSelectedIndex(selectedIndex,true);
	}	
	public AmbitObject getSelectedItem() {
		if ((selectedIndex < 0) || (selectedIndex >= size()))  {
			//System.err.println("getselectedDataSet\tnone" );
			return null;
		}
		else return getItem(selectedIndex);
	}	
	/**
	 * @param selectedIndex The selectedIndex to set.
	 */
	public void setSelectedIndex(int selectedIndex, boolean notify) {
		if (this.selectedIndex != selectedIndex) { 
			this.selectedIndex = selectedIndex;
		}
		if (notify) {
			fireAmbitListEvent();
			AmbitObject s = getSelectedItem();
			if (s != null) s.fireAmbitObjectEvent();
		}
	}
	
	public int addItem(AmbitObject entry) {
		list.add(entry);
		setSelectedIndex(size()-1,false);		
		setModified(true);
		return size()-1;
	}
	public int addItem(int index, AmbitObject entry) {
		list.add(index,entry);
		setSelectedIndex(index,false);		
		setModified(true);
		return index;
	}
	public int setItem(int index, AmbitObject entry) {
		list.set(index,entry);
		setSelectedIndex(index,false);		
		setModified(true);
		return index;
	}	
	public AmbitObject getItem(int index ) {
	    if ((index <0) || (index >= list.size())) return null;
	    Object o = list.get(index);
	    if (o != null) return (AmbitObject) list.get(index); else return null;
	}	
	public boolean hasID() {
		boolean r = true;
		for (int i = 0; i < list.size(); i++)
			r &= getItem(i).hasID();
		return r;
	}
	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {

			buf.append(getItem(i).toString());
			buf.append(" ");			
		}	
		return buf.toString();
	}
	public boolean save(OutputStream out) throws AmbitIOException{
		DataOutputStream ds = new DataOutputStream(out);
		StringBuffer buf = new StringBuffer();
		try { 
			for (int i = 0; i < list.size(); i++) {
				ds.writeBytes(getItem(i).toString());
				ds.write('\n');		
			}
			return true;
		} catch ( IOException x) {
			throw new AmbitIOException(x);
		}
	}	
	public int size() {
		return list.size();
	}
	public void clear() {
		setSelectedIndex(-1,false);		
		for (int i = size()-1; i >= 0; i--) {
			AmbitObject o = (AmbitObject)remove(i,false);
			o.clear();
			o = null;
		}
		list.clear();
		super.clear();
		setModified(true);
	}
	protected Object remove(int i, boolean notify) {
		if ((i >= 0) && (i < size())) {
			Object o = list.remove(i);
			setSelectedIndex(size()-1,false);
			if (notify)	setModified(true);
			return o;
		} else return null;	
	}
	public void remove() {
		remove(getSelectedIndex(),true);
	}	
	public Object remove(int i) {
		return remove(i,true);
	}
	public boolean remove(Object o) {
		boolean m =  list.remove(o);
		if (m) setSelectedIndex(size()-1,false);		
		setModified(m);
		return m;
	}
	
	public boolean equals(Object obj) {
		AmbitList alist = (AmbitList) obj;
		if (super.equals(obj) && ((size() ==  alist.size()))) {
			for (int i =0; i < size(); i++) 
				if (!getItem(i).equals(alist.getItem(i)))
					return false;
			return true;
		} else return false;
	}
	public Object clone()  throws CloneNotSupportedException {
		AmbitList alist = (AmbitList) super.clone();
		for (int i =0; i < size(); i++) 
			alist.addItem((AmbitObject) getItem(i).clone());
		return alist;
	}
	public boolean isModified() {
		if (super.isModified()) return true;
		else for (int i = 0; i < list.size(); i++) {
			if (getItem(i).isModified()) return true;
		}
		return false;
	}
	public void setModified(boolean m) {
		this.modified |= m;
		if (m) fireAmbitListEvent();
	}
	 public void fireAmbitObjectEvent() {
	     // Guaranteed to return a non-null array
	     Object[] listeners = aoListeners.getListenerList();
	     // Process the listeners last to first, notifying
	     // those that are interested in this event
	     for (int i = listeners.length-1; i>=0; i--) {
	         if (listeners[i] instanceof IAmbitObjectListener) {
	             if (changeEvent == null)
	                 changeEvent = new AmbitObjectChanged(this,getSelectedItem());
	             ((IAmbitObjectListener)listeners[i]).ambitObjectChanged(changeEvent);
	         } 
	     }
       changeEvent = null;
	 }
	 public void fireAmbitListEvent() {
	     // Guaranteed to return a non-null array
	     Object[] listeners = aoListeners.getListenerList();
	     // Process the listeners last to first, notifying
	     // those that are interested in this event
	     //for (int i = listeners.length-2; i>=0; i-=2) {
	     for (int i = listeners.length-1; i>=0; i--) {
	         if (listeners[i] instanceof IAmbitListListener) {
	             if (listsEvent == null)
	                 listsEvent = new AmbitListChanged(this,this,getSelectedItem());
	             ((IAmbitListListener)listeners[i]).ambitListChanged(listsEvent);
	             //((IAmbitListListener)listeners[i]).ambitListChanged(listsEvent);
	         } 
	     }
      listsEvent = null;
	 }
	public Object first() {
		setSelectedIndex(0);
		return getItem(getSelectedIndex());
	}
	public Object last() {
		setSelectedIndex(list.size()-1);
		return getItem(getSelectedIndex());
	}	
	public Object next() {
		int i = (getSelectedIndex() +1) % size();
		setSelectedIndex(i);
		return getItem(getSelectedIndex());
	}
	
	public Object prev() {
		int i = (getSelectedIndex() - 1) % size();
		setSelectedIndex(i);
		return getItem(getSelectedIndex());
	}
	public Object jumpto(int index) {
		setSelectedIndex(index);
		return getItem(getSelectedIndex());	    
	}
	public synchronized void addListListener(IAmbitListListener listener) 
	{
	     aoListeners.add(IAmbitListListener.class, listener);
	}

	public void removeListListener(IAmbitListListener listener) 
	{
		aoListeners.remove(IAmbitListListener.class, listener);
	}

	public AmbitObject createNewItem() {
		return null;
	}
	public int getRowID(int row) {
		return row;
	}
	public int indexOf(Object o) {
	    return list.indexOf(o);
	}
	public Object[] toArray() {
			return list.toArray();
	}
    @Override
    public IAmbitEditor editor(boolean editable) {
    	AmbitListOneItemEditor e = new AmbitListOneItemEditor(this,editable,"");
        e.setNoDataText("Click on <+> to add a new item");
        e.setEditable(editable);
        return e;
    }
}
