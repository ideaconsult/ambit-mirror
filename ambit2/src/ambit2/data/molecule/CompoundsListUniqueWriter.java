/* CompoundsListUniqueWriter.java
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

package ambit2.data.molecule;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMolecule;

import ambit2.config.AmbitCONSTANTS;

/**
 * @deprecated
 * A {@link org.openscience.cdk.io.DefaultChemObjectWriter} descendant to add compounds to {@link ambit2.data.molecule.CompoundsList}. 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-4-15
 */
public class CompoundsListUniqueWriter extends CompoundsListWriter {
    protected Object idstructure = null;
    /**
     * 
     * @param list
     */
    public CompoundsListUniqueWriter(CompoundsList list) {
        super(list);
    }
	public void write(IChemObject arg0) throws CDKException {
		if (list == null) return;
		try {
		    Object id = arg0.getProperty(AmbitCONSTANTS.AMBIT_IDSTRUCTURE);
		    if ((id == null) || (idstructure ==null)) list.addItem(new Compound((IMolecule) arg0));
		    else if (idstructure.equals(id)) return;
		    else  list.addItem(new Compound((IMolecule) arg0));
		    idstructure = id;
		} catch (Exception x) {
			throw new CDKException(x.getMessage());
		}
	}    

}
