/* SourceDatasetWriter.java
 * Author: Nina Jeliazkova
 * Date: May 5, 2008 
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

package ambit2.db.processors;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import ambit2.core.data.LiteratureEntry;
import ambit2.core.exceptions.AmbitException;
import ambit2.db.SourceDataset;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.readers.RetrieveDatasets;
import ambit2.db.search.QueryExecutor;

public class DbSrcDatasetWriter extends AbstractRepositoryWriter<SourceDataset, SourceDataset> {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -1299266238757646045L;
    protected static final String insert_dataset = "INSERT IGNORE INTO src_dataset (id_srcdataset, name,user_name,idreference) VALUES (null,?,SUBSTRING_INDEX(user(),'@',1),?)";
    protected PreparedStatement ps_dataset;
    protected DbReferenceWriter referenceWriter;
    protected RetrieveDatasets select_dataset;
    protected QueryExecutor<RetrieveDatasets> exec;

    public DbSrcDatasetWriter() {
		super();
		select_dataset = new RetrieveDatasets();
        exec = new QueryExecutor<RetrieveDatasets>();
	}
    
    public synchronized DbReferenceWriter getReferenceWriter() {
        return referenceWriter;
    }

    public synchronized void setReferenceWriter(DbReferenceWriter referenceWriter) {
        this.referenceWriter = referenceWriter;
    }
    @Override
    public void open() throws DbAmbitException {
    	super.open();
    	referenceWriter.open();
    }
    @Override
    protected void prepareStatement(Connection connection) throws SQLException {
        ps_dataset = connection.prepareStatement(insert_dataset,Statement.RETURN_GENERATED_KEYS);
       
    }
    @Override
    public synchronized void setConnection(Connection connection) throws DbAmbitException {
        super.setConnection(connection);
        if (referenceWriter == null) setReferenceWriter(new DbReferenceWriter());
        referenceWriter.setConnection(connection);
        exec.setConnection(connection);
    }

    @Override
    public SourceDataset write(SourceDataset dataset) throws SQLException {
        dataset.setId(-1);
        select_dataset.setValue(dataset);
        //Find if dataset already exists
        try {
            exec.open(); 
            ResultSet rs = exec.process(select_dataset);
            while (rs.next()) {
                SourceDataset ds = select_dataset.getObject(rs);
                dataset.setId(ds.getId());
                dataset.setUsername(ds.getName());
                dataset.setReference(ds.getReference());
            }
            rs.close();
            
            if (dataset.getId() > 0) return dataset;
            
            LiteratureEntry le = referenceWriter.write(dataset.getReference());
                    
            ps_dataset.clearParameters();
            ps_dataset.setString(1,dataset.getName());
            ps_dataset.setInt(2,le.getId());
            ps_dataset.executeUpdate();
            rs = ps_dataset.getGeneratedKeys();

            while (rs.next()) {
                dataset.setId(rs.getInt(1));
            } 
            rs.close();
            return dataset;            
        } catch (AmbitException x) {
            throw new SQLException(x.getMessage());
        }
        
    }
    
    public void close() throws SQLException {
        if (ps_dataset != null)
            ps_dataset.close();
        ps_dataset = null;
        super.close();
    }    
}
