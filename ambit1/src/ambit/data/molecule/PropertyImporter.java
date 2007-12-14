/* PropertyImporter.java
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

package ambit.data.molecule;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Hashtable;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.io.DefaultChemObjectWriter;
import org.openscience.cdk.io.formats.IResourceFormat;

/**
 * Imports properties from the object passed into {@link #write(IChemObject)} to 
 * {@link IAtomContainersList} set via {@link #setData(IAtomContainersList)}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Nov 8, 2006
 */
public class PropertyImporter extends DefaultChemObjectWriter {
    protected IAtomContainersList data = null;
    protected PropertyTranslator properties;
    protected Object lookupProperty= CDKConstants.CASRN;
    
    public PropertyImporter(IAtomContainersList data, PropertyTranslator properties, Object lookupProperty) {
        super();
        setData(data);
        setProperties(properties);
        setLookupProperty(lookupProperty);
    }
    public PropertyImporter(IAtomContainersList data) {
        super();
        setData(data);
    }


    public void write(IChemObject object) throws CDKException {
        if (object == null) return;
        Object value = object.getProperty(lookupProperty);
        if (value == null) return;
        if (properties == null) throw new CDKException("Descriptors to import not defined!");
        //lookup
        try {
            int index = data.indexOf(lookupProperty, value);
            if (index >= 0) {
                Hashtable descriptors = properties.getDescriptorProperties();
                if (descriptors.size() == 0) throw new CDKException("Descriptors to import not defined!");
                Enumeration e = descriptors.keys();
                while (e.hasMoreElements()) {
                    Object property = e.nextElement();
                    Object newValue = ((ChemObject) object).getProperty(property);
                    Object newName = descriptors.get(property);
                    if (newValue==null) newValue = "";
                    data.setProperty(index, newName, newValue);
                }
            } else {
                System.out.println("Can't find "+ value);
            }
            return;
        } catch (Exception x) {
            throw new CDKException(x.getMessage());
        }
    }
    public void close() throws IOException {
        // TODO Auto-generated method stub
        
    }
    public IResourceFormat getFormat() {
        // TODO Auto-generated method stub
        return null;
    }
    public boolean accepts(Class classObject) {
        Class[] interfaces = classObject.getInterfaces();
        for (int i=0; i<interfaces.length; i++) {
            if (IAtomContainer.class.equals(interfaces[i])) return true;
        }
        return false;
    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.io.IChemObjectWriter#setWriter(java.io.OutputStream)
     */
    public void setWriter(OutputStream writer) throws CDKException {
        throw new CDKException(getClass().getName() + "setting a writer not allowed");

    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.io.IChemObjectWriter#setWriter(java.io.Writer)
     */
    public void setWriter(Writer writer) throws CDKException {
        throw new CDKException(getClass().getName() + "setting a writer not allowed");

    }
    public String toString() {
        
        return "Imports descriptors";
    }

    public synchronized IAtomContainersList getData() {
        return data;
    }
    public synchronized void setData(IAtomContainersList data) {
        this.data = data;
    }

    public synchronized Object getLookupProperty() {
        return lookupProperty;
    }

    public synchronized void setLookupProperty(Object lookupProperty) {
        if (lookupProperty==null) this.lookupProperty = CDKConstants.CASRN;
        else
            this.lookupProperty = lookupProperty;
    }

    public synchronized PropertyTranslator getProperties() {
        return properties;
    }

    public synchronized void setProperties(PropertyTranslator properties) {
        this.properties = properties;
    }

}
