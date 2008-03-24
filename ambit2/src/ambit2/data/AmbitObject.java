/**
 * Filename: AmbitObject.java 
 * @author Nina Jeliazkova
 * Created 2005-3-23
 *
 * Copyright (C) 2005  AMBIT project http://luna.acad.bg/nina/projects
 *
 * Contact: mailto:nina@acad.bg
 * 
 */

package ambit2.data;

import javax.swing.event.EventListenerList;

import ambit2.exceptions.AmbitException;

/**
 * TODO refactor
 * A parent object to all AMBIT basic data objects 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class AmbitObject implements  AmbitInterface {
    
	/**
	 * 
	 */
	private static final long serialVersionUID = -3648629080370650757L;
	protected transient EventListenerList aoListeners = new EventListenerList();
	protected transient AmbitObjectChanged changeEvent = null;
	
	protected boolean editable = true;
	protected int id = -1;
	protected String name = "";
	protected boolean modified = false;
	protected boolean selected = false;
	/**
	 * 
	 */
	public AmbitObject() {
		this("");
	}
	public AmbitObject(String name) {
		this(name,-1);
	}
	public AmbitObject(String name, int id) {
		this.name = name;
		this.id = id;
		aoListeners = new EventListenerList();
	}	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		if (equals(o)) {
			return 0;
		} else {
			return 1;
		}
	}
	public Object clone()  throws CloneNotSupportedException {
	    try {
			Object obj = createObject(getClass().getName());
			((AmbitObject) obj).id = id;
			((AmbitObject) obj).name = name;
			return obj;
	    } catch (AmbitException x) {
	        throw new CloneNotSupportedException(x.getCause().getMessage());
	    }
	}
	public String toString() {
		return name;
	}
	public boolean equals(Object obj) {
		if (obj == null) { return false; }
		else if (obj instanceof AmbitObject) {
			return name.equalsIgnoreCase(((AmbitObject) obj).name);
		}
		else { return false;}
	}
	/**
	 * TODO define hashcode in aggreement with equals
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	public static Object createObject(String className) throws AmbitException {
	      Object object = null;
	      try {
	          Class classDefinition = Class.forName(className);
	          object = classDefinition.newInstance();
	      } catch (InstantiationException e) {
	          throw new AmbitException(e);
	      } catch (IllegalAccessException e) {
	          throw new AmbitException(e);
	      } catch (ClassNotFoundException e) {
	          throw new AmbitException(e);
	      }
	      return object;
	   }
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		modified =  (this.id != id);
		this.id = id;
		if (id > -1) editable = false;
	}
	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		boolean m =  !this.name.equals(name);
		this.name = name;
		setModified(m);
	}
	public boolean hasID() {
		return (id > -1);
	}
	public void clear() {
		id = -1;
		name="";
		setModified(true);
	}
	public boolean isPredefined() {
		return false;
	}
	public String[] predefinedvalues() {
		return null;
	}
	public void setType(String type) {
	}
	public String getType() {
		return "";
	}
		
	/**
	 * @return Returns the editable.
	 */
	public boolean isEditable() {
		return editable;
	}
	/**
	 * @param editable The editable to set.
	 */
	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	public boolean isModified() {
		return modified;
	}
	public void setModified(boolean m) {
		this.modified |= m;
		if (m) { fireAmbitObjectEvent(); }
	}
	public void setNotModified() {
		this.modified = false;
	}
	public void fireAmbitObjectEvent() {
		fireAmbitObjectEvent(this);
	}
	 public void fireAmbitObjectEvent(AmbitObject object) {
	     // Guaranteed to return a non-null array
	     Object[] listeners = aoListeners.getListenerList();
	     // Process the listeners last to first, notifying
	     // those that are interested in this event
	     for (int i = listeners.length-1; i>=0; i-=1) {
	         if (listeners[i] instanceof IAmbitObjectListener) {
	             if (changeEvent == null) {
	            	 changeEvent = new AmbitObjectChanged(this,object);
	             }
	             ((IAmbitObjectListener)listeners[i]).ambitObjectChanged(changeEvent);
	         }
	     }
        changeEvent = null;
	 }
	 
	public synchronized void addAmbitObjectListener(IAmbitObjectListener listener) 
	{
	    	if (aoListeners == null) aoListeners = new EventListenerList();
		     aoListeners.add(IAmbitObjectListener.class, listener);
	}

	public void removeAmbitObjectListener(IAmbitObjectListener listener) 
	{
	    if (aoListeners == null) aoListeners = new EventListenerList();
			aoListeners.remove(IAmbitObjectListener.class, listener);
	}	

	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
		setModified(true);
	}
}
