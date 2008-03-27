/* DefaultAmbitProcessor.java
 * Author: Nina Jeliazkova
 * Date: 2006-4-15 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2006  Ideaconsult Ltd.
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

package ambit2.processors;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import ambit2.log.AmbitLogger;
import ambit2.ui.editors.DefaultProcessorEditor;
import ambit2.ui.editors.IAmbitEditor;
import ambit2.exceptions.AmbitException;

/**
 * Default (empty) implementation of {@link ambit2.processors.IAmbitProcessor}
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-4-15
 */
public class DefaultAmbitProcessor implements IAmbitProcessor {
	protected PropertyChangeSupport propertyChangeSupport = null;
	protected static AmbitLogger logger = new AmbitLogger(DefaultAmbitProcessor.class);
    protected boolean enabled=true;
    
    /**
     * 
     */
    public DefaultAmbitProcessor() {
        super();
        propertyChangeSupport=new PropertyChangeSupport(this);
    }

    /* (non-Javadoc)
     * @see ambit2.processors.IAmbitProcessor#process(java.lang.Object)
     */
    public Object process(Object object) throws AmbitException {
        return object;
    }

    /* (non-Javadoc)
     * @see ambit2.processors.IAmbitProcessor#createResult()
     */
    public IAmbitResult createResult() {
        return null;
    }

    /* (non-Javadoc)
     * @see ambit2.processors.IAmbitProcessor#getResult()
     */
    public IAmbitResult getResult() {
        return null;
    }

    /* (non-Javadoc)
     * @see ambit2.processors.IAmbitProcessor#setResult(ambit2.processors.IAmbitResult)
     */
    public void setResult(IAmbitResult result) {
    }

    /* (non-Javadoc)
     * @see ambit2.processors.IAmbitProcessor#close()
     */
    public void close() {

    }
    public IAmbitEditor getEditor() {

    	return new DefaultProcessorEditor(this);
    }
    public String toString() {
    	return "";
    }
    public synchronized boolean isEnabled() {
        return enabled;
    }
    public synchronized void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    public void addPropertyChangeListener(final String propertyName,
    	      final PropertyChangeListener listener)
   {
    	    propertyChangeSupport.addPropertyChangeListener(propertyName,
    	                                                    listener);
   }
    public void removePropertyChangeListener(final String propertyName,
    	      final PropertyChangeListener listener)
    {
    	    propertyChangeSupport.removePropertyChangeListener(propertyName,
    	                                                       listener);
    }
    public void addPropertyChangeListener(final PropertyChangeListener listener)
 {
  	    propertyChangeSupport.addPropertyChangeListener(listener);
 }
  public void removePropertyChangeListener(final PropertyChangeListener listener)
  {
  	    propertyChangeSupport.removePropertyChangeListener( listener);
  }


}
