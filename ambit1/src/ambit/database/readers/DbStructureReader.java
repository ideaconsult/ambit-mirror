/* DbStructureReader.java
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

package ambit.database.readers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IMolecule;

import ambit.data.molecule.Compound;
import ambit.database.AmbitID;
import ambit.exceptions.AmbitException;

/**
 * Read structures from database, given an sql string or an already open {@link java.sql.ResultSet}.
 * Provides support for descendant classes and not really intended to be used directly.
 * The constructor is left public to facilitate database access by advansed users, who are acquainted with the database structure.
 * Expects
 * <pre>
 * ResultSet resultset; <br>
 * String cml = resultset.getString("ustructure");  //CML representation of the molecule, optional 
 * String cas = resultset.getString("casno");  //CAS registry number, optional 
 * String name = resultset.getString("name");  //Chemical name, optional 
 * String idstructure = resultset.getString("idstructure"); //unique structure identifier , mandatory 
 * String idsubstance = resultset.getString("idsubstance"); //unique substance identifier , optional 
 * </pre>
 * Note that most descendant of this class do not have "ustructure" field and therefore do not return complete structure.
 * This is by design and for efficient memory management. If the structure is necessary for further processing, pass the returned object through 
 * {@link ambit.database.processors.ReadStructureProcessor}
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-4-9
 */
public class DbStructureReader extends DbReader {
    PreparedStatement dbStrucReaderPS  = null;

    /**
     * 
     */
    public DbStructureReader() {
        super();
    }

    /**
     * @param resultset
     */
    public DbStructureReader(ResultSet resultset) {
        super(resultset);
    }
    public DbStructureReader(Connection connection , String sql) throws AmbitException {
        super();
        try {
            dbStrucReaderPS = connection.prepareStatement(sql,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
            setParameters(dbStrucReaderPS);
            setResultset(dbStrucReaderPS.executeQuery());
        } catch (SQLException x) {
            throw new AmbitException(x);
        }
    }
    public void setParameters(PreparedStatement ps) throws SQLException  {
    	
    }
    /* (non-Javadoc)
     * @see java.util.Iterator#next()
     */
    public Object next() {
        if (readIDOnly) {
            int idsubstance = -1;
            int idstructure = -1;
            try {
                idsubstance = getSubstance();
            } catch (Exception x) {
            }
            try {
                idstructure = getStructure();
            } catch (Exception x) {
            }            
            return new AmbitID(idsubstance,idstructure);
        } else return nextFat();
    }
    public Object nextFat() {
       try { 
           IMolecule mol = null;
           String cml = "";
           try {
		       cml = resultset.getString("ustructure");
		       mol = Compound.readMolecule(cml);
	       } catch (SQLException x) {
	           mol = new org.openscience.cdk.Molecule();
	       } catch (Exception x) {
	           System.out.println(x.getMessage() + cml);
	           mol = new org.openscience.cdk.Molecule();
	       }
           setPropertySubstance(mol);
           setPropertyStructure(mol);
	       try {
	           mol.setProperty(CDKConstants.CASRN,getCAS());
	       } catch (Exception x) {
	           
	       }
	       try {
	           mol.setProperty(CDKConstants.NAMES,getName());	       
	           
	       } catch (Exception x) {
	           
	       }
	       return mol;
       } catch (Exception x) {
           logger.error(x);
           return null;
       }
    }
    /* (non-Javadoc)
     * @see ambit.database.DbReader#close()
     */
    public void close() throws IOException {
        try {
        if (dbStrucReaderPS !=null) dbStrucReaderPS.close();
        } catch (SQLException x) {
            throw new IOException(x.getMessage());
            
        }
        super.close();
    }
}
