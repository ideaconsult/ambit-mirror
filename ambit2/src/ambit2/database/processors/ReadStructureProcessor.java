/* ReadStructureProcessor.java
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

package ambit2.database.processors;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;

import ambit2.config.AmbitCONSTANTS;
import ambit2.database.AmbitID;
import ambit2.exceptions.AmbitException;
import ambit2.processors.IAmbitResult;
import ambit2.data.molecule.Compound;
import ambit2.data.molecule.MoleculeTools;

/**
 * Reads structures from the database
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-4-15
 */
public class ReadStructureProcessor extends DefaultDbProcessor {
	protected PreparedStatement ps;
	protected String queryID = AmbitCONSTANTS.AMBIT_IDSTRUCTURE;
    /**
     * 
     */
    public ReadStructureProcessor(Connection connection) throws AmbitException {
        super(connection);
    }
    /* (non-Javadoc)
     * @see ambit2.database.processors.DefaultDbProcessor#prepare(java.sql.Connection)
     */
    public void prepare(Connection connection) throws AmbitException {
    	try {
			ps = connection.prepareStatement(getSQL());
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
    }
    public String getSQL() {
    	final String sql = "select structure.idstructure,idsubstance,uncompress(structure) as ustructure from structure where structure.idstructure =?";
    	return sql;
    	
    }
    protected IAtomContainer readByID(IAtomContainer mol, Object id) throws Exception {
        if (id == null) return mol;
        int idstructure = -1;
        try {
            idstructure = ((Integer) id).intValue();
            if (idstructure <= 0) return mol;
        } catch (Exception x) {
            return mol;
        }
        ps.clearParameters();
        ps.setInt(1,idstructure);
        ResultSet resultset = ps.executeQuery();
        while (resultset.next()) {
            try {
               String cml = resultset.getString("ustructure");
               IMolecule newMol = MoleculeTools.readCMLMolecule(cml);
               newMol.setProperties(mol.getProperties());
               mol = newMol;
           } catch (SQLException x) {
               
           }
           try {
               String idsubstance = resultset.getString("idsubstance");
               if (idsubstance != null) 
                   mol.setProperty(AmbitCONSTANTS.AMBIT_IDSUBSTANCE,new Integer(idsubstance));
           } catch (SQLException x) {
               
           }         
           try {
               String idsubstance = resultset.getString("idstructure");
               if (idsubstance != null) 
                   mol.setProperty(AmbitCONSTANTS.AMBIT_IDSTRUCTURE,new Integer(idsubstance));
           } catch (SQLException x) {
               
           }                   
         
        }
           return mol;
    }
    /* (non-Javadoc)
     * @see ambit2.processors.IAmbitProcessor#process(java.lang.Object)
     */
    public Object process(Object object) throws AmbitException {
        if (ps == null) return object;
        
        try {
            if (object instanceof IAtomContainer) {
                IAtomContainer mol = (IAtomContainer) object;
                Object id = mol.getProperty(queryID);
                return readByID(mol,id);
            } else if (object instanceof Integer) {
                IAtomContainer ac = DefaultChemObjectBuilder.getInstance().newMolecule();
                ac.setProperty(queryID,object);
                return readByID(ac,((Integer)object).intValue());                
            } else if (object instanceof AmbitID) {
                IAtomContainer ac = DefaultChemObjectBuilder.getInstance().newMolecule();
                Integer id= new Integer(getID((AmbitID)object));
                ac.setProperty(queryID,id);
                return readByID(ac,id);            	
            }
        } catch (Exception x) {
            //logger.error(x);
            x.printStackTrace();
            return null;
        }
        return object;
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
    /* (non-Javadoc)
     * @see ambit2.processors.IAmbitProcessor#close()
     */
    public void close() {
        try {
            if (ps != null) ps.close();
        } catch (SQLException x) {
            
        }
    }
    /* (non-Javadoc)
     * @see ambit2.database.processors.DefaultDbProcessor#toString()
     */
    public String toString() {
        return "Reads structure from database by "+AmbitCONSTANTS.AMBIT_IDSTRUCTURE;
    }
    public synchronized String getQueryID() {
        return queryID;
    }
    
    public Integer getID(AmbitID id) {
    	return new Integer(id.getIdStructure());
    }    
}
