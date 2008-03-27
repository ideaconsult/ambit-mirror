/* ReadIdentifierProcessor.java
 * Author: Nina Jeliazkova
 * Date: Apr 1, 2007 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2006  Nina Jeliazkova
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

import org.openscience.cdk.interfaces.IChemObject;

import ambit2.config.AmbitCONSTANTS;
import ambit2.database.AmbitID;
import ambit2.exceptions.AmbitException;
import ambit2.log.AmbitLogger;

public abstract class ReadIdentifierProcessor extends DefaultDbProcessor {
    protected static AmbitLogger logger = new AmbitLogger(ReadIdentifierProcessor.class);
    protected PreparedStatement psSubstance, psStructure;    
    protected String identifierLabel = "identifier";
    public ReadIdentifierProcessor(Connection connection) throws AmbitException {
        super(connection);

    }
    public abstract Object getIdentifierValue(ResultSet rs) throws Exception;
    
    protected ResultSet readIdentifier(AmbitID id) throws Exception {
        if (id.getIdSubstance() != -1) return readIdentifierBySubstance(id.getIdSubstance());
        else
            if (id.getIdStructure() != -1) return readIdentifierByStructure(id.getIdStructure());
            else return null;
    }
    protected ResultSet readIdentifierBySubstance(Object idsubstance) throws Exception {
        if (idsubstance != null) {
            int id  = ((Integer) idsubstance).intValue();
            if (id > 0) {
                psSubstance.clearParameters();
                psSubstance.setInt(1,id);
                return psSubstance.executeQuery();
            }
        } 
        return null;
    }
    protected ResultSet readIdentifierByStructure(Object idstructure) throws Exception {
        if (idstructure != null) {
            int id  = ((Integer) idstructure).intValue();
            if (id > 0) {
                psStructure.clearParameters();
                psStructure.setInt(1,id);
                return psStructure.executeQuery();
            }
        }
        return null;
    }
    public synchronized String getIdentifierLabel() {
        return identifierLabel;
    }
    public synchronized void setIdentifierLabel(String identifierLabel) {
        this.identifierLabel = identifierLabel;
    }    
    public Object process(Object object) throws AmbitException {
        try {
        ResultSet rs = null;
            if (object instanceof IChemObject) {
                   IChemObject mol = (IChemObject) object;
                    
                    rs = readIdentifierBySubstance(mol.getProperty(AmbitCONSTANTS.AMBIT_IDSUBSTANCE));
                    if (rs == null) 
                        rs = readIdentifierByStructure(mol.getProperty(AmbitCONSTANTS.AMBIT_IDSTRUCTURE));
                    
                    if (rs != null) {
                        setIdentifierValue(mol,rs);
                        rs.close();
                        rs = null;
                    }   
            } else if (object instanceof AmbitID) {
                
                rs = readIdentifier((AmbitID)object);
            }
            return object;
        } catch (Exception x) {
            throw new AmbitException(x);
        }
    }    
    protected void setIdentifierValue(IChemObject mol, ResultSet rs)  throws Exception {
        while (rs.next()) {
            mol.setProperty(getIdentifierLabel(),getIdentifierValue(rs));
            break;
        }
    }
}
