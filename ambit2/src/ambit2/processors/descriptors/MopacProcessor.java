/* MopacProcessor.java
 * Author: Nina Jeliazkova
 * Date: 2006-4-9 
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

package ambit2.processors.descriptors;

import java.awt.Container;

import joelib.desc.result.DoubleArrayResult;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.IDescriptorResult;

import ambit2.external.DescriptorMopacShell;
import ambit2.ui.editors.DefaultProcessorEditor;
import ambit2.ui.editors.IAmbitEditor;
import ambit2.exceptions.AmbitException;
import ambit2.processors.DefaultAmbitProcessor;
import ambit2.processors.IAmbitResult;

/**
 * Calculates electronic parameters by invokig WinMopac by {@link ambit2.processors.descriptors.MopacShell}.
 * At this moment this works only for windows, but can be extended if corresponding mopac executable is provided.<br>
 * Assigns eHOMO,eLUMO,TOTAL ENERGY,FINAL HEAT OF FORMATION,IONIZATION POTENTIAL,ELECTRONIC ENERGY.
 * Doesn't update 3D coordinates even if optimization is set! (TODO fix it)
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-4-9
 */
public class MopacProcessor extends DefaultAmbitProcessor {
    protected DescriptorMopacShell mopac;
    protected Container frame = null;
    protected boolean interactive = true;
    
    /**
     * Creates {@link MopacProcessor} initialized with {@link MopacShell}
     */
    public MopacProcessor(DescriptorMopacShell mopac) {
        super();
        this.mopac = mopac; 
    }
//mysql> select count(*),name,status from dvalues join ddictionary using(iddescriptor) group by ddictionary.iddescriptor,s    tatus;
    /* (non-Javadoc)
     * @see ambit2.processors.IAmbitProcessor#process(java.lang.Object)
     */
    public Object process(Object object) throws AmbitException {
        if (object instanceof IAtomContainer) {
        	IAtomContainer mol = (IAtomContainer) object;
            try {
                DescriptorValue value = mopac.calculate(mol);
                IDescriptorResult r = value.getValue();
                String[] names = value.getNames();
                for (int i=0; i < names.length;i++) {
                	mol.setProperty(names[i],((DoubleArrayResult)r).getDoubleArray()[i]);
                }
                /*
                if (((Molecule) object).getProperty("eHOMO") == null) {
                    Exception x = new DescriptorCalculationException("MOPAC Error!");
                    if (interactive)
                        JOptionPane.showMessageDialog(frame,"WinMOPAC was not able to calculate electronic parameters!");
                    ((Molecule) object).setProperty("eHOMO", x);
                    ((Molecule) object).setProperty("eLUMO", x);
                    ((Molecule) object).setProperty("TOTAL ENERGY", x);
                    ((Molecule) object).setProperty("FINAL HEAT OF FORMATION", x);
                    ((Molecule) object).setProperty("IONIZATION POTENTIAL", x);
                    ((Molecule) object).setProperty("ELECTRONIC ENERGY", x);
                    
                    
                    
                }
                */
                return object;
            } catch (Exception x) {
            	throw new AmbitException(x);
            }
        }    
        else throw new AmbitException("Unsupported object "+object.getClass().getName());
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
    public String toString() {
    	return mopac.toString();
    }
    /* (non-Javadoc)
     * @see ambit2.processors.IAmbitProcessor#close()
     */
    public void close() {

    }
	public Container getFrame() {
		return frame;
	}
	public void setFrame(Container frame) {
		this.frame = frame;
	}
	   public IAmbitEditor getEditor() {

	    	return new DefaultProcessorEditor(this);
	    }
	  
    public synchronized boolean isInteractive() {
        return interactive;
    }
    public synchronized void setInteractive(boolean interactive) {
        this.interactive = interactive;
    }
    
}
