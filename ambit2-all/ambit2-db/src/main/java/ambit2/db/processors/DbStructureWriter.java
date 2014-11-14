/* DbStructureWriter.java
 * Author: nina
 * Date: Mar 28, 2009
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2009  Ideaconsult Ltd.
 * 
 * Contact: nina
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

package ambit2.db.processors;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.naming.OperationNotSupportedException;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import ambit2.base.data.SourceDataset;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.StringCondition;
import ambit2.db.update.dataset.DatasetAddStructure;
import ambit2.db.update.dataset.ReadDataset;
import ambit2.db.update.structure.CreateStructure;
import ambit2.db.update.structure.UpdateStructure;

public class DbStructureWriter extends AbstractRepositoryWriter<IStructureRecord, IStructureRecord> {
	protected DatasetAddStructure datasetAddStruc = new DatasetAddStructure();
	protected CreateStructure createStructure;
	protected UpdateStructure updateStructure;
	protected PropertyValuesWriter propertyWriter;
	protected SourceDataset dataset;	
	protected ReadDataset readDatasetQuery;
	/**
	 * 
	 */
	private static final long serialVersionUID = -5831358598266151159L;

	public DbStructureWriter(SourceDataset dataset) {
		super();
		propertyWriter = new PropertyValuesWriter();
		setDataset(dataset);
	}

	@Override
	public void open() throws DbAmbitException {
		super.open();
		propertyWriter.open();
	}
	@Override
	public synchronized void setConnection(Connection connection)
			throws DbAmbitException {
		super.setConnection(connection);
		propertyWriter.setConnection(connection);
	}	
	public SourceDataset getDataset() {
		return dataset;
	}
	public void setDataset(SourceDataset dataset) {
		this.dataset = dataset;
		propertyWriter.setDataset(dataset);
	}	
	@Override
	public IStructureRecord write(IStructureRecord structure) throws SQLException,AmbitException, OperationNotSupportedException {

		writeStructure(structure,false);
		return structure;
	}

	@Override
	public IStructureRecord update(IStructureRecord structure) throws SQLException,
			OperationNotSupportedException, AmbitException {
		writeStructure(structure,true);
		return structure;
	}
	public List<IStructureRecord> writeStructure(IStructureRecord structure, boolean update) throws SQLException, AmbitException, OperationNotSupportedException {
		 List<IStructureRecord> sr = new ArrayList<IStructureRecord>();

        if (structure.getIdstructure() <= 0) {
        	if (createStructure ==null) createStructure = new CreateStructure();
        	createStructure.setObject(structure);
	        exec.process(createStructure);
        	writeDataset(structure);
       		writeProperties(structure);
            sr.add(structure);
        } else {
        	try {
        		if (update) {
        			if (updateStructure==null) updateStructure = new UpdateStructure();
        			updateStructure.setObject(structure);
        			exec.process(updateStructure);
        		}
        		writeDataset(structure);
        		writeProperties(structure);
        		sr.add(structure);
        	} catch (AmbitException x) {
        		logger.log(java.util.logging.Level.WARNING,x.getMessage(),x);
        	}        	
        }
		return sr;
	}	

		
	public void close() throws Exception {
        try {

        if (propertyWriter != null)
        	propertyWriter.close();
     
         } catch (SQLException x) {
             logger.log(java.util.logging.Level.SEVERE,x.getMessage(),x);
        }
        super.close();
	}
	
	protected void writeDataset(IStructureRecord structure) throws SQLException, AmbitException, OperationNotSupportedException {
		if (getDataset() == null) setDataset(new SourceDataset("Default"));
		
		if (getDataset().getID()<=0) {
			if (readDatasetQuery==null) readDatasetQuery = new ReadDataset();
			readDatasetQuery.setValue(getDataset());
			readDatasetQuery.setCondition(StringCondition.getInstance(StringCondition.C_EQ));
			readDatasetQuery.setPageSize(2); readDatasetQuery.setPage(0);
			ResultSet rs = null;
			try {
				rs = queryexec.process(readDatasetQuery);
				int record = 0; //should be 1 only
				while (rs.next()) {
					SourceDataset d = readDatasetQuery.getObject(rs);
					getDataset().setID(d.getID());
					record++;
				}
				if (record>1) getDataset().setID(-1); //smth is wrong
			} catch (Exception x) {
				getDataset().setID(-1);
			}
		}
		datasetAddStruc.setObject(structure);
		datasetAddStruc.setGroup(dataset);
		try {
			exec.process(datasetAddStruc);
		} catch (AmbitException x) {
			logger.log(Level.SEVERE,String.format("Error %s adding structure /compound/%d/conformer/%d to dataset [%d] %s",
					x.getMessage(),
					structure.getIdchemical(),structure.getIdstructure(),dataset.getId(),dataset.getName()));
			throw x;
		}
		
		propertyWriter.setDataset(dataset); 
	}
	
	public void writeProperties(IStructureRecord structure) throws SQLException, AmbitException {
			propertyWriter.process(structure);
			structure.clearProperties();
	}	
	
}
