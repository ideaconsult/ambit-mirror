/* MoleculeReader.java
 * Author: Nina Jeliazkova
 * Date: Aug 10, 2008 
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

package ambit2.core.processors.structure;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.MOL_TYPE;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.core.data.MoleculeTools;

public class MoleculeReader extends DefaultAmbitProcessor<IStructureRecord,IAtomContainer> {
	



    /**
     * 
     */
    private static final long serialVersionUID = 1811923574213153916L;

    public IAtomContainer process(IStructureRecord target) throws AmbitException {
    		if (target.getContent()==null) return null;
    		if (target.getFormat()==null)
    			throw new AmbitException("Unknown format "+target.getFormat());
            switch (MOL_TYPE.valueOf(target.getFormat())) {
            case SDF: {
            	try {
            		return MoleculeTools.readMolfile(target.getContent());
                } catch (Exception x) {
                    throw new AmbitException(x);
                }
                
            }
           case CML:     
        	   	try {
        	   		return MoleculeTools.readCMLMolecule(target.getContent());
                } catch (Exception x) {
                    throw new AmbitException(x);
                }
            default: {
            	 throw new AmbitException("Unknown format "+target.getFormat());
            }
            }
    }

}
