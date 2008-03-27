/* QSARDatasetWriter.java
 * Author: Nina Jeliazkova
 * Date: 2006-6-23 
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

package ambit2.domain;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.DefaultChemObjectWriter;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.setting.IOSetting;

import ambit2.data.descriptors.DescriptorsList;
import ambit2.data.molecule.Compound;

/**
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-6-23
 */
public class QSARDatasetWriter extends DefaultChemObjectWriter {
    protected QSARDataset dataset;
    protected DescriptorsList descriptors;
    protected IOSetting[] settings;
    /**
     * 
     */
    public QSARDatasetWriter(QSARDataset dataset) {
        super();
        this.dataset = dataset;
        dataset.getModel().clear();
        dataset.getData().clear();
        descriptors = null;

        this.settings = new IOSetting[1];
    }

    /* (non-Javadoc)
     * @see org.openscience.cdk.io.ChemObjectWriter#write(org.openscience.cdk.interfaces.ChemObject)
     */
	public void write(IChemObject arg0) throws CDKException {
		if (dataset == null) return;
		try {
		    AllData data = dataset.getData();
		    if (arg0 instanceof IMolecule) {
		        IMolecule mol = (IMolecule) arg0;
		        /*
		        if (descriptors == null) {
		            descriptors = ((MolPropertiesIOSetting) getIOSettings()[0]).getSelectedProperties();
		            data.initialize(descriptors);
		        }
		        */
		        data.addRow(new Compound(mol),descriptors,mol.getProperties(),false);
		    }
			
		} catch (Exception x) {
			throw new CDKException(x.getMessage());
		}
	}

 

    /* (non-Javadoc)
     * @see org.openscience.cdk.io.ChemObjectIO#close()
     */
    public void close() throws IOException {
        if (dataset !=null)
        dataset.data.first();
        dataset.readonly = true;
        dataset.coverage.clear();
		
        dataset.setModified(true);
        dataset.syncWithModel(dataset.getModel());
    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.io.DefaultChemObjectWriter#getIOSettings()
     */
    public IOSetting[] getIOSettings() {
        return settings;
    }
    

    public synchronized DescriptorsList getDescriptors() {
        return descriptors;
    }
    public synchronized void setDescriptors(DescriptorsList descriptors) {
        this.descriptors = descriptors;
        dataset.getData().initialize(descriptors);
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
    /* (non-Javadoc)
     * @see org.openscience.cdk.io.IChemObjectIO#getFormat()
     */
    public IResourceFormat getFormat() {
        // TODO Auto-generated method stub
        return null;
    }
}
