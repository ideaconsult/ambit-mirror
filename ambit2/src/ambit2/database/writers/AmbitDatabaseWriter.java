/* AmbitDatabaseWriter.java
 * Author: Nina Jeliazkova
 * Date: 2006-7-1 
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

package ambit2.database.writers;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.DefaultChemObjectWriter;
import org.openscience.cdk.io.formats.IResourceFormat;

import ambit2.database.AmbitDatabaseFormat;

/**
 * Abstract class for writing objects into database
 * TODO refactor
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-7-1
 */
public abstract class AmbitDatabaseWriter extends DefaultChemObjectWriter {

    /**
     * 
     */
    public AmbitDatabaseWriter() {
        super();
    }

    /* (non-Javadoc)
     * @see org.openscience.cdk.io.IChemObjectWriter#setWriter(java.io.Writer)
     */
    public void setWriter(Writer writer) throws CDKException {
        throw new CDKException(getClass().getName() + " setting a writer not allowed");

    }

    /* (non-Javadoc)
     * @see org.openscience.cdk.io.IChemObjectWriter#setWriter(java.io.OutputStream)
     */
    public void setWriter(OutputStream writer) throws CDKException {
        throw new CDKException(getClass().getName() + " setting a writer not allowed");

    }

    /* (non-Javadoc)
     * @see org.openscience.cdk.io.IChemObjectIO#getFormat()
     */
    public IResourceFormat getFormat() {
        return new AmbitDatabaseFormat();
    }

    /* (non-Javadoc)
     * @see org.openscience.cdk.io.IChemObjectIO#accepts(java.lang.Class)
     */
    public boolean accepts(Class classObject) {
		Class[] interfaces = classObject.getInterfaces();
		for (int i=0; i<interfaces.length; i++) {
			if (IAtomContainer.class.equals(interfaces[i])) return true;
		}
		return false;
    }

    /* (non-Javadoc)
     * @see org.openscience.cdk.io.IChemObjectIO#close()
     */
    public void close() throws IOException {
        // TODO Auto-generated method stub

    }
    

}
