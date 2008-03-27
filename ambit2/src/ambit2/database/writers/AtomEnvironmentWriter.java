/* AtomEnvironmentWriter.java
 * Author: Nina Jeliazkova
 * Date: 2006-5-16 
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

package ambit2.database.writers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemObject;

import ambit2.config.AmbitCONSTANTS;
import ambit2.exceptions.AmbitException;
import ambit2.data.AmbitUser;
import ambit2.data.descriptors.AtomEnvironment;
import ambit2.data.descriptors.AtomEnvironmentList;

/**
 * Writes atom enwironments to database. Atom enviromnents are expected as molecule properties as generated from {@link ambit2.processors.structure.AtomEnvironmentGenerator}.
 * Used in {@link ambit2.ui.actions.dbadmin.DBGenerator}
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-5-16
 */
public class AtomEnvironmentWriter extends AbstractDbStructureWriter {
    protected PreparedStatement psFPAE = null;

    /**
     * @param connection
     * @param user
     */
    public AtomEnvironmentWriter(Connection connection, AmbitUser user) {
        super(connection, user);
        try {
        setKeyCheck(connection,false);
        } catch (Exception x) {
            logger.error(x);
        }
    }

    /* (non-Javadoc)
     * @see ambit2.database.writers.AbstractDbStructureWriter#write(int, org.openscience.cdk.interfaces.ChemObject)
     */
    /* (non-Javadoc)
     * @see ambit2.database.writers.AbstractDbStructureWriter#write(org.openscience.cdk.interfaces.ChemObject)
     */
    public void write(IChemObject object) throws CDKException {
        try {
            int idsubstance = getIdSubstance(object);
            if (idsubstance < 0) throw new CDKException("Undefined "+AmbitCONSTANTS.AMBIT_IDSUBSTANCE);
            Object o = object.getProperty(AmbitCONSTANTS.AtomEnvironment);
            if ((o!=null) && (o instanceof AtomEnvironmentList))
                insertFPAE(idsubstance,(AtomEnvironmentList) o);
        } catch (AmbitException x) {
            throw new CDKException(x.getMessage());
        }
    }
    /* (non-Javadoc)
     * @see ambit2.database.writers.AbstractDbStructureWriter#write(int, org.openscience.cdk.interfaces.ChemObject)
     */
    public int write(int idstructure, IChemObject object) throws AmbitException {
        return 0;
    }
	public int insertFPAE(int idsubstance, AtomEnvironmentList aeList) throws AmbitException {
	    int rows = 0;
	    try {
		    prepareStatement();
		    connection.setAutoCommit(true);
		    
		    try {
			    for (int i=0; i < aeList.size();i++) {
			        	AtomEnvironment ae = (AtomEnvironment) aeList.get(i);
			        	//if (ae.getSublevel()<ae.getLevels()) continue;
                        String aestring = AtomEnvironment.atomFingerprintToString(ae.getAtom_environment(),'\t');
						psFPAE.clearParameters();
						psFPAE.setString(1,aestring);
						psFPAE.setString(2,ae.getCentral_atom());
						psFPAE.executeUpdate();
						
						ps.clearParameters();
						ps.setInt(1,idsubstance);
						ps.setLong(2,ae.getTime_elapsed());
						ps.setInt(3,ae.getStatus());
                        ps.setInt(4,ae.getFrequency());
                        ps.setInt(5,ae.getSublevel());
						ps.setString(6,aestring);
						ps.executeUpdate();
						rows++;
				}	
		    } catch (SQLException x) {
		    	//connection.rollback();
		    } finally {
		        //connection.commit();
		    }
	    } catch (SQLException x) {
	        throw new AmbitException(x);
	    }
	    return rows;
	}
    

    /* (non-Javadoc)
     * @see ambit2.database.writers.DefaultDbWriter#prepareStatement()
     */
    protected void prepareStatement() throws SQLException {
        if ((ps != null) && (psFPAE != null)) return;
    	final String AMBIT_insertFPAEID = 
    		"insert ignore into fpaeid (idsubstance,idfpae,time_elapsed,status,freq,level) select ?,idfpae,?,?,?,? from fpae where ae=?;";
    	ps = connection.prepareStatement(AMBIT_insertFPAEID);    	
    	final String AMBIT_insertFPAE = 
    		"insert ignore into fpae (ae,atom) values(?,?);";
    	psFPAE = connection.prepareStatement(AMBIT_insertFPAE);
    }
    /* (non-Javadoc)
     * @see ambit2.database.writers.DefaultDbWriter#close()
     */
    public void close() throws IOException {
        super.close();
        try {
            if (psFPAE !=null)
            psFPAE.close(); psFPAE = null;
            setKeyCheck(connection,true);
        } catch (SQLException x) {
            throw new IOException(x.getMessage());
        }
    }
    public void invalidateExisting() throws AmbitException {
    	try {
    		connection.setAutoCommit(true);
	    	Statement st = connection.createStatement();
	    	/*
	    	st.execute("truncate fpaeid;");
	    	st.execute("truncate fpae;");
	    	*/
	    	st.execute("update fpaeid set status='invalid'");

	    	st.close();
	    	st = null;
    	} catch (Exception x) {
    		throw new AmbitException(x);
    	}
    }
    /* (non-Javadoc)
     * @see ambit2.database.writers.AbstractDbStructureWriter#toString()
     */
    public String toString() {
        return "Writes atom environments to database";
    }
}
