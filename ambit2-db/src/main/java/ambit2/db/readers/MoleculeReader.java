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

package ambit2.db.readers;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.core.data.IStructureRecord;
import ambit2.core.data.MoleculeTools;
import ambit2.core.exceptions.AmbitException;
import ambit2.core.processors.DefaultAmbitProcessor;

public class MoleculeReader extends DefaultAmbitProcessor<IStructureRecord,IAtomContainer> {
	public enum MOL_TYPE {SDF,CML,CSV};



    /**
     * 
     */
    private static final long serialVersionUID = 1811923574213153916L;

    public IAtomContainer process(IStructureRecord target) throws AmbitException {
        try {
            IAtomContainer ac = null;
            if (MOL_TYPE.SDF == MOL_TYPE.valueOf(target.getFormat()))
                ac = MoleculeTools.readMolfile(target.getContent());
            else     
                ac = MoleculeTools.readCMLMolecule(target.getContent());
            return ac;
        } catch (Exception x) {
            throw new AmbitException(x);
        }
    }

}
