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

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import ambit2.base.data.Property;
import ambit2.base.data.SourceDataset;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.MOL_TYPE;
import ambit2.core.processors.StructureNormalizer;
import ambit2.core.processors.structure.key.CASKey;
import ambit2.core.processors.structure.key.IStructureKey;
import ambit2.core.processors.structure.key.NoneKey;
import ambit2.core.processors.structure.key.PubchemSID;
import ambit2.db.search.structure.AbstractStructureQuery;
import ambit2.db.search.structure.AbstractStructureQuery.FIELD_NAMES;
import ambit2.db.search.structure.QueryByIdentifierWithStructureFallback;
import ambit2.db.search.structure.QueryFieldNumeric;

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
	protected QueryByIdentifierWithStructureFallback query;
	protected QueryFieldNumeric queryNum;
	//protected IStructureKey propertyKey;
	protected static final String seek_dataset = "SELECT idstructure,uncompress(structure) as s,format FROM structure join struc_dataset using(idstructure) join src_dataset using(id_srcdataset) where name=? and idchemical=?";
	protected PreparedStatement ps_seekdataset;		
	protected StructureNormalizer normalizer = new StructureNormalizer(); 
	protected boolean useExistingStructure = false;
	
	public boolean isUseExistingStructure() {
		return useExistingStructure;
	}


	public void setUseExistingStructure(boolean useExistingStructure) {
		this.useExistingStructure = useExistingStructure;
	}


	public boolean isBuild2D() {
		return normalizer.isBuild2D();
	}


	public void setBuild2D(boolean build2d) {
		normalizer.setBuild2D(build2d);
	}


	protected final String idchemical_tag="idchemical";
	
	public RepositoryWriter() {
		super();
		structureWriter = new DbStructureWriter(null);
		query = new QueryByIdentifierWithStructureFallback();
		queryNum = new QueryFieldNumeric();
		setPropertyKey(new CASKey());
		queryexec.setCache(true);
	}
	
	
	public IStructureKey getPropertyKey() {
		return query==null?null:query.getFieldname();
	}
	public void setPropertyKey(IStructureKey propertyKey) {
		query.setFieldname(propertyKey);
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
	
	protected AbstractStructureQuery getQuery(IStructureRecord record) {
		if (getPropertyKey() instanceof PubchemSID) {
			PubchemSID psid = (PubchemSID)getPropertyKey();
			queryNum.setFieldname((Property)psid.getKey());
			queryNum.setSearchByAlias(false);
			try {
				queryNum.setValue(psid.process(record));
				return queryNum;
			} catch (Exception x) {}
		}
		
		query.setValue(record);
		query.setPageSize(1);
		return query;
	}
	protected void findChemical(IStructureRecord record) throws SQLException, AmbitException {
		if (record == null) return;
		ResultSet rs = null;
		try {
			AbstractStructureQuery chemQuery = getQuery(record);
			rs = queryexec.process(chemQuery);
			while (rs.next()) {
				record.setIdchemical(rs.getInt(FIELD_NAMES.idchemical.name()));
				if (useExistingStructure)  
					record.setIdstructure(rs.getInt(FIELD_NAMES.idstructure.name()));
				break;
			}
		
		} catch (SQLException x) {
			throw x;
		} catch (AmbitException x) {
			throw x;
		} catch (Exception x) {
			logger.log(java.util.logging.Level.SEVERE,x.getMessage(),x);
			throw new AmbitException(x);
		} finally {
			try {
			 queryexec.closeResults(rs);
			} catch (Exception x) {
				logger.log(java.util.logging.Level.SEVERE,x.getMessage(),x);
			}
		}
		
	}
	public List<IStructureRecord> write(IStructureRecord structure) throws SQLException, AmbitException, OperationNotSupportedException {
		boolean cantreadstructure ;
		try {
			structure = normalizer.process(structure);
			cantreadstructure = false;
		} catch (Exception x) {
			structure.setContent(null);
			structure.setFormat(null);
			cantreadstructure = true;
		}
		
		if ((structure.getFormat()!=null) && MOL_TYPE.NANO.name().equals(structure.getFormat()))
			; //TODO don't do lookup (for now)
		else if (useExistingStructure && (structure.getIdchemical()>0) && (structure.getIdstructure()>0) ) { //all set
			
		} else {
		//find the chemical
			if (getPropertyKey() instanceof NoneKey) ; //do nothing 
			else	
				findChemical(structure);
			if (!structure.usePreferedStructure()) structure.setIdstructure(-1);
		}
        return structureWriter.writeStructure(structure,OP.UPDATE==getOperation());
        
	}
	
	protected void prepareStatement(Connection connection) throws SQLException {
        ps_seekdataset = connection.prepareStatement(seek_dataset);

	}
	@Override
	public void close() throws Exception {
        try {
        	if (ps_seekdataset != null) {
        		ps_seekdataset.close();
        		ps_seekdataset = null;
        	}
        } catch (Exception x) {
        	logger.log(java.util.logging.Level.WARNING,x.getMessage(),x);
        }
        try {
        	if (structureWriter!=null) {
        		structureWriter.close();
            	structureWriter = null;
        	}
        } catch (Exception x) {
        	logger.log(java.util.logging.Level.WARNING,x.getMessage(),x);
        }
        super.close();
	}
	
	@Override
	public List<IStructureRecord> update(IStructureRecord arg0)
			throws SQLException, OperationNotSupportedException, AmbitException {
		setOperation(OP.UPDATE);
		structureWriter.setOperation(OP.UPDATE);
		return write(arg0);
	}
	
	@Override
	public List<IStructureRecord> create(IStructureRecord arg0)
			throws SQLException, OperationNotSupportedException, AmbitException {
		setOperation(OP.CREATE);
		structureWriter.setOperation(OP.CREATE);
		return write(arg0);
	}
}
