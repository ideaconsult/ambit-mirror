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
import java.util.List;

import javax.naming.OperationNotSupportedException;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.processors.structure.key.CASKey;
import ambit2.core.processors.structure.key.PropertyKey;
import ambit2.core.processors.structure.key.SmilesKey;
import ambit2.db.SourceDataset;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.search.AbstractStructureQuery;
import ambit2.db.search.QueryField;
import ambit2.db.search.QueryFieldNumeric;
import ambit2.db.search.QueryStructure;
import ambit2.db.search.StringCondition;

/**
<pre>
tables chemicals, structure
</pre>  
http://bugs.mysql.com/bug.php?id=39191
 * @author nina
 *
 */
public class RepositoryWriter extends AbstractRepositoryWriter<IStructureRecord,List<IStructureRecord>> {
    /**
     * 
     */
    private static final long serialVersionUID = 6530309499663693100L;
	protected DbStructureWriter structureWriter;
	protected SmilesKey smilesKey;
	protected PropertyKey propertyKey;
	protected AbstractStructureQuery<String,String,StringCondition> query_chemicals;
	protected AbstractStructureQuery query_property;
	protected static final String seek_dataset = "SELECT idstructure,uncompress(structure) as s,format FROM structure join struc_dataset using(idstructure) join src_dataset using(id_srcdataset) where name=? and idchemical=?";
	protected PreparedStatement ps_seekdataset;		
	protected StructureNormalizer normalizer = new StructureNormalizer(); 
	
	protected final String idchemical_tag="idchemical";
	
	public RepositoryWriter() {
		structureWriter = new DbStructureWriter();
		smilesKey = new SmilesKey();
		setPropertyKey(new CASKey());
		query_chemicals = new QueryStructure();
		query_chemicals.setId(-1);
		query_chemicals.setFieldname(smilesKey.getKey());

	}
	
	
	public PropertyKey getPropertyKey() {
		return propertyKey;
	}
	public void setPropertyKey(PropertyKey propertyKey) {
		this.propertyKey = propertyKey;
		if ((propertyKey.getType() == Number.class) || (propertyKey.getType() == Integer.class) ||(propertyKey.getType() == Double.class))
			query_property = new QueryFieldNumeric();
		else
			query_property = new QueryField();
		
		query_property.setId(-1);

	}
	
	@Override
	public void open() throws DbAmbitException {
		super.open();
		structureWriter.open();
	}
	@Override
	public synchronized void setConnection(Connection connection)
			throws DbAmbitException {
		super.setConnection(connection);
		structureWriter.setConnection(connection);
		exec.setConnection(connection);
	}
	public SourceDataset getDataset() {
		return structureWriter.getDataset();
	}
	public void setDataset(SourceDataset dataset) {
		structureWriter.setDataset(dataset);
	}
	protected void findChemical(AbstractStructureQuery query, String key,Object value, IStructureRecord record) throws SQLException, AmbitException {
		System.out.println(query.getClass().getName() + " key=" + key + " value="+value);
		if (value == null) return;
		ResultSet rs = null;
		try {
			query.setValue(value);
			if (key != null)
				query.setFieldname(key);
			rs = queryexec.process(query);
			while (rs.next()) {
				record.setIdchemical(rs.getInt(2));
				System.out.println("found "+record);
/*
				ps_seekdataset.clearParameters();
				ps_seekdataset.setString(1,getDataset().getName());
				ps_seekdataset.setInt(2,record.getIdchemical());
				ResultSet strucs = ps_seekdataset.executeQuery();
				while (strucs.next()) 
					if (record.getFormat().equals(strucs.getString("format")) &&
						record.getWritableContent().equals(strucs.getString("s"))							
							) {
						
						record.setIdstructure(strucs.getInt(1));
						System.out.println("--found "+record + record.getContent());
					}

				if (strucs != null) strucs.close();
					*/
				break;
			}
		
		} catch (SQLException x) {
			throw x;
		} catch (AmbitException x) {
			throw x;
		} catch (Exception x) {
			logger.error(x);
			throw new AmbitException(x);
		} finally {
			try {
			 queryexec.closeResults(rs);
			} catch (Exception x) {logger.error(x);}
		}
		
	}
	public List<IStructureRecord> write(IStructureRecord structure) throws SQLException, AmbitException, OperationNotSupportedException {

		structure = normalizer.process(structure);

		//find if a structure with specified idchemical exists
        if (structure.getIdchemical() > 0) {
        	if (structure.getIdstructure()>0) {
        		//don't search for anything

        	} else {
        		try {
        			findChemical(query_chemicals, idchemical_tag, structure.getIdchemical(), structure);
        		} catch (Exception x) {
        			x.printStackTrace();
        			structure.setIdchemical(-1);
        		}
        	}
        } else {
        	//find by a property
        	Object property = null;
        	try {
        		property = propertyKey.process(structure);
        		if (property!=null)
        			findChemical(query_property,propertyKey.getKey(),property,structure);
        		else
	        		if (structure.getIdchemical() <= 0) {
	        			query_chemicals.setFieldname(smilesKey.getKey());        			
	        			findChemical(query_chemicals,null,structure.getSmiles(),structure);
	        		}
        	} catch (SQLException x) {
        		throw x;
        	} catch (Exception x) {
        		x.printStackTrace();
        		logger.warn(x);
            	//if not found, find by SMILES
            	if (structure.getIdchemical()<=0)
            		try {
            			findChemical(query_chemicals,null,structure.getSmiles(),structure);
            		} catch (Exception ex) {
                		x.printStackTrace();
                		logger.warn(x);
            		}
        	}

        }
        //add a new idchemical if idchemical <=0
        return structureWriter.writeStructure(structure);
        
	}
	protected void prepareStatement(Connection connection) throws SQLException {
        ps_seekdataset = connection.prepareStatement(seek_dataset);

	}
	public void close() throws SQLException {
        try {

        if (ps_seekdataset != null)
            ps_seekdataset.close();        
  
        if (exec != null)
        	exec.close();
        } catch (SQLException x) {
            logger.error(x);
        }
        super.close();
	}
}
