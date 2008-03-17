/* ObjectWithPropertyChangeSupport.java
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

package com.microworkflow.events;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import com.microworkflow.process.Activity;

public class ObjectWithPropertyChangeSupport {
    protected PropertyChangeSupport psp;
    public ObjectWithPropertyChangeSupport() {
        psp = new PropertyChangeSupport(this);
    }
    /**
     * Add a PropertyChangeListener to the listener list. 
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        psp.addPropertyChangeListener(listener);
    }
    /**
     * Add a PropertyChangeListener for a specific property.
     * @param propertyName
     * @param listener
     */
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        psp.addPropertyChangeListener(propertyName,listener);
    }
    /**
     *     Fire an existing PropertyChangeEvent to any registered listeners.
     * @param evt
     */
    protected  void firePropertyChange(PropertyChangeEvent evt) {
        psp.firePropertyChange(evt);
    }
    
    /**
     * Report a boolean bound property update to any registered listeners.
     * @param propertyName
     * @param oldValue
     * @param newValue
     */
    protected  void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        psp.firePropertyChange(propertyName,oldValue,newValue);
    }
    /**
     * Report an int bound property update to any registered listeners.
     * @param propertyName
     * @param oldValue
     * @param newValue
     */ 
    protected  void firePropertyChange(String propertyName, int oldValue, int newValue) {
        psp.firePropertyChange(propertyName,oldValue,newValue);
    }
    /**
     * Report a bound property update to any registered listeners. 
     * @param propertyName
     * @param oldValue
     * @param newValue
     */
    protected  void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        psp.firePropertyChange(propertyName,oldValue,newValue);
    }

    /**
     * Returns an array of all the listeners that were added to the PropertyChangeSupport object with addPropertyChangeListener(). 
     * @return
     */
    public  PropertyChangeListener[] getPropertyChangeListeners() {
        return psp.getPropertyChangeListeners();
    }
    /**
     *  Returns an array of all the listeners which have been associated with the named property. 
     * @param propertyName
     * @return
     */
    public PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
        return psp.getPropertyChangeListeners(propertyName);
    }
    /**
     * Check if there are any listeners for a specific property.
     * @param propertyName
     * @return
     */
    public boolean hasListeners(String propertyName) {
        return psp.hasListeners(propertyName);
    }
    /**
     * Remove a PropertyChangeListener from the listener list.
     * @param listener
     */    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        psp.removePropertyChangeListener(listener);
    }
    /**
     * Remove a PropertyChangeListener for a specific property.
     * @param propertyName
     * @param listener
     */  
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        psp.removePropertyChangeListener(propertyName,listener);
    }
         
        
    
        
}
