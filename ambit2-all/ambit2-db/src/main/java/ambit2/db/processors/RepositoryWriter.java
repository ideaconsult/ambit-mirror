/*
Copyright (C) 2007-2008  

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
*/

package ambit2.db.processors;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import ambit2.core.data.IStructureRecord;
import ambit2.core.data.StructureRecord;
import ambit2.db.SourceDataset;
import ambit2.db.exceptions.DbAmbitException;

/**
<pre>
tables chemicals, structure
</pre>  
 * @author nina
 *
 */
public class RepositoryWriter extends AbstractRepositoryWriter<IStructureRecord,List<IStructureRecord>> {
    /**
     * 
     */
    private static final long serialVersionUID = 6530309499663693100L;
    protected static final String select_chemical = "SELECT idchemical FROM CHEMICALS where idchemical=?";
    protected PreparedStatement ps_selectchemicals;

    protected static final String insert_chemical = "INSERT INTO CHEMICALS (idchemical) values (null)";
	protected PreparedStatement ps_chemicals;
	protected static final String insert_structure = "INSERT INTO STRUCTURE (idstructure,idchemical,structure,format,updated,user_name) values (null,?,compress(?),?,CURRENT_TIMESTAMP,SUBSTRING_INDEX(user(),'@',1))";
	protected PreparedStatement ps_structure;

	protected static final String insert_dataset = "insert into struc_dataset SELECT ?,id_srcdataset from src_dataset where name=?";
	protected PreparedStatement ps_dataset;	
	
	protected DbSrcDatasetWriter datasetWriter;
	protected SourceDataset dataset;
	
	public RepositoryWriter() {
		datasetWriter = new DbSrcDatasetWriter();
	}
	@Override
	public void open() throws DbAmbitException {
		super.open();
		datasetWriter.open();
	}
	@Override
	public synchronized void setConnection(Connection connection)
			throws DbAmbitException {
		super.setConnection(connection);
		datasetWriter.setConnection(connection);
	}
	public SourceDataset getDataset() {
		return dataset;
	}
	public void setDataset(SourceDataset dataset) {
		this.dataset = dataset;
	}
	public void writeDataset(IStructureRecord structure) throws SQLException {
		if (dataset == null) dataset = new SourceDataset("Default");
		datasetWriter.write(dataset);
		
		ps_dataset.clearParameters();
		ps_dataset.setInt(1,structure.getIdstructure());
		ps_dataset.setString(2,dataset.getName());
		ps_dataset.execute();
		
		
		
	}
	public List<IStructureRecord> write(IStructureRecord structure) throws SQLException {
        //find if a structure with specified idchemical exists
        if (structure.getIdchemical() > 0) {
            ps_selectchemicals.setInt(1,structure.getIdchemical());
            ResultSet rs = ps_selectchemicals.executeQuery();
            structure.setIdchemical(-1);
            structure.setIdstructure(-1);
            while (rs.next()) {
                structure.setIdchemical(rs.getInt(1));
            }
            rs.close();
        }
        List<IStructureRecord> sr = new ArrayList<IStructureRecord>();
        //add a new idchemical if idchemical <=0
        if (structure.getIdchemical() <= 0) {
    		ps_chemicals.executeUpdate();
    		ResultSet rs = ps_chemicals.getGeneratedKeys();
    		ps_structure.clearParameters();
    		while (rs.next()) {
                structure.setIdchemical(rs.getInt(1));
    		} 
    		rs.close();
        }
        //add a new entry in structure table
        ps_structure.setInt(1,structure.getIdchemical());
        ps_structure.setString(2,structure.getContent());
        ps_structure.setString(3,structure.getFormat());
        ps_structure.executeUpdate();
        ResultSet rss = ps_structure.getGeneratedKeys();
        while (rss.next())  {
        	StructureRecord record = new StructureRecord(structure.getIdchemical(),rss.getInt(1),null,structure.getFormat());
        	writeDataset(record);
            sr.add(record);
        }
        rss.close();
        
        return sr;
        
	}
	protected void prepareStatement(Connection connection) throws SQLException {
		 ps_chemicals = connection.prepareStatement(insert_chemical,Statement.RETURN_GENERATED_KEYS);
		 ps_structure = connection.prepareStatement(insert_structure,Statement.RETURN_GENERATED_KEYS);
         ps_selectchemicals = connection.prepareStatement(select_chemical);
         ps_dataset = connection.prepareStatement(insert_dataset);
         datasetWriter.prepareStatement(connection);
	}
	public void close() throws SQLException {
        try {
        if (ps_dataset != null)
        	ps_dataset.close();
        if (ps_chemicals != null)
            ps_chemicals.close();
        if (ps_structure != null)
            ps_structure.close();
        if (ps_selectchemicals != null)
            ps_selectchemicals.close();        
        if (datasetWriter != null)
        	datasetWriter.close();
        } catch (SQLException x) {
            logger.error(x);
        }
        super.close();
	}
}
