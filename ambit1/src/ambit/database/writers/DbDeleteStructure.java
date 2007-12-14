/* DbDeleteStructure.java
 * Author: Nina Jeliazkova
 * Date: 2006-4-16 
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

package ambit.database.writers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.io.formats.IResourceFormat;

import ambit.database.AmbitDatabaseFormat;
import ambit.exceptions.AmbitException;
import ambit.misc.AmbitCONSTANTS;

/**
 * Delete structures from a database. Structures are expected to have object.getProperty(AmbitCONSTANTS.AMBIT_IDSTRUCTURE) identifier.
 * The structure with this identifier in the database is deleted. 
 * TODO junit test
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-4-16
 */
public class DbDeleteStructure extends AmbitDatabaseWriter {
    PreparedStatement psValues = null;
    PreparedStatement psStructure = null;
    PreparedStatement psCAS = null;
    protected final static String sqlCAS = 
        "delete cas from structure,cas where structure.idstructure=cas.idstructure and structure.idstructure=?";
    
    protected final static String sqlValues = 
        "delete dvalues from structure,dvalues where structure.idstructure=dvalues.idstructure and structure.idstructure=?";
    protected final static String sqlStructure = 
        "delete structure,substance from substance,structure where substance.idsubstance=structure.idsubstance and idstructure=?";
    /**
     * 
     */
    public DbDeleteStructure(Connection connection) throws AmbitException {
        super();
        try {
            psCAS = connection.prepareStatement(sqlCAS);
            psValues = connection.prepareStatement(sqlValues);
            psStructure = connection.prepareStatement(sqlStructure);
        } catch (SQLException x) {
            throw new AmbitException(x);
        }
    }

    /* (non-Javadoc)
     * @see org.openscience.cdk.io.ChemObjectWriter#write(org.openscience.cdk.interfaces.ChemObject)
     */
    public void write(IChemObject arg0) throws CDKException {
        Object id = arg0.getProperty(AmbitCONSTANTS.AMBIT_IDSTRUCTURE);
        if (id == null) throw new CDKException("Undefined "+AmbitCONSTANTS.AMBIT_IDSTRUCTURE);
        int idstructure;
        try {
            //idstructure = Integer.parseInt(id.toString());
            idstructure = ((Integer) id).intValue();
        } catch (Exception x) {
            throw new CDKException(x.getMessage());
        }
        try {
            psCAS.clearParameters();
            psCAS.setInt(1,idstructure);
            psCAS.executeUpdate();            
            psValues.clearParameters();
            psValues.setInt(1,idstructure);
            psValues.executeUpdate();
            psStructure.clearParameters();
            psStructure.setInt(1,idstructure);
            psStructure.executeUpdate();            
        } catch (Exception x) {
            throw new CDKException(x.getMessage());
        }

    }


    public IResourceFormat getFormat() {
        return new AmbitDatabaseFormat();
    }

    /* (non-Javadoc)
     * @see org.openscience.cdk.io.ChemObjectIO#close()
     */
    public void close() throws IOException {
        try {
        psValues.close();
        psCAS.close();
        psStructure.close();
        } catch (Exception x) {
            
        }
     

    }

}
