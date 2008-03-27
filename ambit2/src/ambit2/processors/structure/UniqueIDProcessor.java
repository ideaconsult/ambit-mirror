/* UniqueIDProcessor.java
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

package ambit2.processors.structure;

import org.openscience.cdk.interfaces.IChemObject;

import ambit2.ui.editors.DefaultProcessorEditor;
import ambit2.ui.editors.IAmbitEditor;
import ambit2.exceptions.AmbitException;
import ambit2.processors.DefaultAmbitProcessor;
import ambit2.processors.IAmbitResult;

/**
 * UniqueIDProcessor 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-4-15
 */
public class UniqueIDProcessor extends DefaultAmbitProcessor  {
    protected Object uniqueID = null;
    protected Object id_previous = null;
    /**
     * 
     */
    public UniqueIDProcessor(Object uniqueID) {
        super();
        this.uniqueID = uniqueID;
    }

    /* (non-Javadoc)
     * @see ambit2.processors.IAmbitProcessor#process(java.lang.Object)
     */
    public Object process(Object object) throws AmbitException {
		try {
		    if (object instanceof IChemObject) {
		        Object r = null;
			    Object id = ((IChemObject) object).getProperty(uniqueID.toString());
			    if ((id == null) || (id_previous ==null)) r = object;
			    else if (!id_previous.equals(id)) r = object;
			    id_previous = id;
			    return r;
		    } else return object;
		} catch (Exception x) {
			throw new AmbitException(x.getMessage());
		}
    }

    /* (non-Javadoc)
     * @see ambit2.processors.IAmbitProcessor#createResult()
     */
    public IAmbitResult createResult() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see ambit2.processors.IAmbitProcessor#getResult()
     */
    public IAmbitResult getResult() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see ambit2.processors.IAmbitProcessor#setResult(ambit2.processors.IAmbitResult)
     */
    public void setResult(IAmbitResult result) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see ambit2.processors.IAmbitProcessor#close()
     */
    public void close() {
        // TODO Auto-generated method stub

    }
    public IAmbitEditor getEditor() {

    	return new DefaultProcessorEditor(this);
    }
}
