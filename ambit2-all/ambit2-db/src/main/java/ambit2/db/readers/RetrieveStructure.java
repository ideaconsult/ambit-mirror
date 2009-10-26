/* RetrieveStructure.java
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

import java.sql.ResultSet;
import java.sql.SQLException;

import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.smarts.CMLUtilities;

public class RetrieveStructure extends AbstractStructureRetrieval<IStructureRecord> {
    /**
     * 
     */
    private static final long serialVersionUID = 7257863166977468657L;
    protected static String s_idchemical = "idchemical";
    protected static String s_idstructure = "idstructure";
    protected static String s_ustructure = "ustructure";
    protected static String s_format = "format";
    

    public IStructureRecord getObject(ResultSet rs) throws AmbitException {
        try {
            IStructureRecord r = getValue();
            r.setIdchemical(rs.getInt(s_idchemical));
            r.setIdstructure(rs.getInt(s_idstructure));
            r.setContent(rs.getString(s_ustructure));
            r.setFormat(rs.getString(s_format));
            r.setProperty(Property.getInstance(CMLUtilities.SMARTSProp, CMLUtilities.SMARTSProp), rs.getString(6));
            return r;
        } catch (SQLException x){
            throw new AmbitException(x);
        }
    }

}
