/* SourceDatasetWriter.java
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

package ambit.database.writers;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.io.formats.IResourceFormat;

import ambit.data.molecule.SourceDataset;
import ambit.database.AmbitDatabaseFormat;
import ambit.database.DbConnection;
import ambit.database.core.DbSrcDataset;
import ambit.exceptions.AmbitException;
import ambit.misc.AmbitCONSTANTS;

/**
 * Writes the correspondence between a structure and dataset of origin to the database. If necessary, 
 * writes the description of the dataset of origin to the database. 
 * This concerns tables src_dataset and struc_dataset.<br>
 * Expects object.getProperty(AmbitCONSTANTS.DATASET) to be {@link ambit.data.molecule.SourceDataset} type
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-4-15
 */
public class SourceDatasetWriter extends AbstractDbStructureWriter {
	protected SourceDataset defaultDataset =null;
    protected DbSrcDataset srcDataset = null;
    protected PreparedStatement pssubstance = null;
	protected static final String AMBIT_insertStrucSrc = "INSERT IGNORE INTO struc_dataset (idstructure,id_srcdataset) VALUES (?,?)";
	protected static final String AMBIT_insertSubstanceSrc = "INSERT IGNORE INTO struc_dataset (idstructure,id_srcdataset) select idstructure,? from structure where idsubstance=?";
    /**
     * 
     */
	public SourceDatasetWriter(DbConnection conn) {
		this(null,conn);
	}
    public SourceDatasetWriter(SourceDataset dataset, DbConnection conn) {
        super(conn.getConn(),conn.getUser());
        setDefaultDataset(dataset);
        srcDataset = new DbSrcDataset(conn);
        try {
            prepareStatement();
        } catch (SQLException x) {
            x.printStackTrace();
            ps = null;
        }
    }
    protected void prepareStatement() throws SQLException {
    	if (ps == null) ps = connection.prepareStatement(AMBIT_insertStrucSrc);
    	
    }
	protected void insertSrc(int idstructure, int idsrcdataset) throws SQLException {
		if (ps == null) return;
		ps.clearParameters();
		ps.setInt(1,idstructure);
		ps.setInt(2,idsrcdataset);
		ps.executeUpdate();
	}	 
	protected void insertSubstanceSrc(int idsubstance, int idsrcdataset) throws SQLException {
		if (pssubstance == null) {
			 pssubstance = connection.prepareStatement(AMBIT_insertSubstanceSrc);
		}
		pssubstance.clearParameters();
		pssubstance.setInt(1,idsrcdataset);
		pssubstance.setInt(2,idsubstance);
		pssubstance.executeUpdate();
	}	  	
	public void write(IChemObject object) throws CDKException {
        try {
        	int idstructure = getIdStructure(object);
        	write(idstructure,object);
        } catch (AmbitException x) {
        	try {
        		int idsubstance = getIdSubstance(object);
        		writeSubstance(idsubstance, object);
        	} catch (AmbitException xx) {
        		throw new CDKException(x.getMessage());
        	}
        }
	}	
	public int writeSubstance(int idsubstance, IChemObject object) throws AmbitException {
        if (object ==null) return -1;
        Object dataset = object.getProperty(AmbitCONSTANTS.DATASET);
        if (dataset == null) dataset = defaultDataset;
        if (dataset == null) throw new AmbitException("Source dataset not defined!");
        try {
            int idd = -1;
	        if (dataset instanceof SourceDataset) {
	            idd = srcDataset.addSourceDataSet((SourceDataset) dataset);
	        }
	        if (idd == -1) throw new AmbitException("Invalid "+AmbitCONSTANTS.DATASET);
	        insertSubstanceSrc(idsubstance,idd);
        } catch (SQLException x) {
            throw new AmbitException(x);
        }
        return idsubstance;
	}	
	public int write(int idstructure, IChemObject object) throws AmbitException {
        if (object ==null) return -1;
        Object dataset = object.getProperty(AmbitCONSTANTS.DATASET);
        if (dataset == null) dataset = defaultDataset;
        if (dataset == null) throw new AmbitException("Source dataset not defined!");
        try {
            int idd = -1;
	        if (dataset instanceof SourceDataset) {
	            idd = srcDataset.addSourceDataSet((SourceDataset) dataset);
	        }
	        if (idd == -1) throw new AmbitException("Invalid "+AmbitCONSTANTS.DATASET);
	        insertSrc(idstructure,idd);
        } catch (SQLException x) {
            throw new AmbitException(x);
        }
        return idstructure;
	}
	@Override
	public void close() throws IOException {
		try {
		if (pssubstance != null) pssubstance.close();
		} catch (Exception x) {
			
			logger.error(x);
		}
		pssubstance = null;
		super.close();
	}
    /* (non-Javadoc)
     * @see org.openscience.cdk.io.ChemObjectIO#getFormat()
     */
    public IResourceFormat getFormat() {
        return new AmbitDatabaseFormat();
    }

	public SourceDataset getDefaultDataset() {
		return defaultDataset;
	}
	public void setDefaultDataset(SourceDataset defaultDataset) {
		this.defaultDataset = defaultDataset;
	}

	public String toString() {
		return "Write information about source dataset to database";
	}

}
