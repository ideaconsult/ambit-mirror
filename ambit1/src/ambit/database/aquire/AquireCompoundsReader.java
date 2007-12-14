/* AquireCompoundsReader.java
 * Author: Nina Jeliazkova
 * Date: 2006-4-15 
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

package ambit.database.aquire;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;

import ambit.data.literature.ReferenceFactory;
import ambit.data.molecule.SourceDataset;
import ambit.database.ConnectionPool;
import ambit.database.readers.DbReader;
import ambit.misc.AmbitCONSTANTS;
import ambit.processors.IdentifiersProcessor;

/**
 * Reads CAS and Name from AQUIRE database.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-4-15
 */
public class AquireCompoundsReader extends DbReader {
    protected static final String sql = "select distinct(cas),Chemical_Name,Endpoint from chemicals join aquire on chemicals.cas=aquire.TestCAS join species_data on species_data.SpeciesID=aquire.SpeciesNumber ";
    protected Statement stmt = null;
    protected SourceDataset srcDataset;
    protected ConnectionPool aquire_pool = null;
    protected Connection aquire_connection = null;    
    
    /**
     * 
     */
    public AquireCompoundsReader(String endpoint, String species, int page, int pagesize) {
        super();
        try {
	        aquire_pool = new ConnectionPool("localhost","33060","aquire","root","",1,1);
	        aquire_connection = aquire_pool.getConnection();
	        srcDataset = new SourceDataset(AmbitCONSTANTS.AQUIRE,
	                ReferenceFactory.createDatasetReference("EPA Aquire database","http://mountain.epa.gov/ecotox/"));
	        stmt = aquire_connection.createStatement();
            ResultSet rs = stmt.executeQuery(prepareSQL(endpoint, species,page,pagesize));
            setResultset(rs);

        
        } catch (Exception x) {
            stmt = null;
            setResultset(null);
            aquire_connection = null;
        }
        
    }
    public AquireCompoundsReader(Connection connection,String endpoint, String species, int page, int pagesize) {
        super();
        
        srcDataset = new SourceDataset("AQUIRE",
                ReferenceFactory.createDatasetReference("EPA Aquire database","http://mountain.epa.gov/ecotox/"));
        try {
            ResultSet rs = stmt.executeQuery(prepareSQL(endpoint, species,page,pagesize));
            setResultset(rs);
        } catch (Exception x) {
            stmt = null;
            setResultset(null);
        }
    }

    /**
     * @param resultset
     */
    public AquireCompoundsReader(ResultSet resultset) {
        super(resultset);
    }
    
    protected String prepareSQL(String endpoint, String species,int page,int pagesize) {
    	String where = " where ";
    	StringBuffer sql1 = new StringBuffer();
    	sql1.append(sql);
        if (endpoint != null) { 
        	sql1.append(where);
        	where = " and ";
        	sql1.append(" Endpoint='");
        	sql1.append(endpoint);
        	sql1.append("'");
            
        }
        if (species != null) { 
        	sql1.append(where);
        	where = " and ";
        	sql1.append(" LatinName = '");
        	sql1.append(species);
        	sql1.append("'");
            
        }        
        sql1.append(" limit ");
        sql1.append(page);
        sql1.append(",");
        sql1.append(pagesize);
        System.out.println(sql1);
        return sql1.toString();
        
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#next()
     */
    public Object next() {
        try {
            IMolecule m = DefaultChemObjectBuilder.getInstance().newMolecule();
            m.setProperty(CDKConstants.NAMES,resultset.getString(2));            
            m.setProperty(CDKConstants.CASRN,
                    IdentifiersProcessor.hyphenateCAS(resultset.getString(1)));
            m.setProperty(AmbitCONSTANTS.DATASET,srcDataset);
            return m;
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
            resultset.close();
            stmt.close();
            if (aquire_pool != null) {    
            aquire_pool.returnConnection(aquire_connection);
            aquire_connection.close();
            aquire_connection = null;
            aquire_pool = null;
            }
        } catch (Exception x) {
            logger.error(x);
            
        }        
    }
    @Override
    public String toString() {
        return "Retrieve compounds with AQUIRE data for the specified endpoint";
    }
}
