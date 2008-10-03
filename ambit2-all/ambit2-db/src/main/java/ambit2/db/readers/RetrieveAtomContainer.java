/* RetrieveAtomContainer.java
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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.ResultSet;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;

import ambit2.core.data.IStructureRecord;
import ambit2.core.data.MoleculeTools;
import ambit2.core.exceptions.AmbitException;

public class RetrieveAtomContainer extends AbstractStructureRetrieval<IAtomContainer> {
    protected MoleculeReader transform;
    /**
     * 
     */
    private static final long serialVersionUID = 6145574959713490136L;
    public RetrieveAtomContainer() {
        super();
        transform = new MoleculeReader();
    }
    public IAtomContainer getObject(ResultSet rs) throws AmbitException {
        try {
            IStructureRecord r = getValue();
            r.setIdchemical(rs.getInt(ID_idchemical));
            r.setIdstructure(rs.getInt(ID_idstructure));
            r.setFormat(rs.getString(ID_format));
            /**
             * Structure
             */
            int type = rs.getMetaData().getColumnType(ID_structure);
            if (type == java.sql.Types.VARBINARY) {
	              InputStream s = rs.getBinaryStream(ID_structure);
	              IMolecule m = null;
	              if ("SDF".equals(r.getFormat()))
	                	m = MoleculeTools.readMolfile(new InputStreamReader(s));
	              else
	            	    m = MoleculeTools.readCMLMolecule(new InputStreamReader(s));
	                s = null;
	                return m;
            } else {
            		return MoleculeTools.readCMLMolecule(rs.getString(ID_structure));
            }        	
            //return transform.process(r);
        } catch (Exception x){
            throw new AmbitException(x);
        }
    }

}
