/*
Copyright (C) 2005-2008  

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

package ambit2.dbui;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.core.data.Profile;
import ambit2.core.data.Property;
import ambit2.core.data.StructureRecord;
import ambit2.core.exceptions.AmbitException;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.readers.AbstractStructureRetrieval;
import ambit2.db.readers.RetrieveAtomContainer;
import ambit2.db.readers.RetrieveField;
import ambit2.db.search.IStoredQuery;
import ambit2.db.search.QueryExecutor;
import ambit2.ui.PropertiesTableModel;
import ambit2.ui.table.ISortableColumns;

public class StoredQueryTableModel extends ResultSetTableModel implements ISortableColumns {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7273714299667490819L;
	protected IStoredQuery query;

	protected String countRecordsSQL = "SELECT count(*) FROM query_results where idquery=?";
	protected String selectRecordsSQL = "SELECT selected,idchemical,idstructure,metric FROM query_results where idquery=? order by metric ";
	protected PreparedStatement countRecords = null;
	protected PreparedStatement selectRecords = null;
	
	protected PreparedStatement structureRecords = null;
	protected PreparedStatement structureField = null;	
	protected RetrieveAtomContainer retrieveMolecule = new RetrieveAtomContainer();
	protected RetrieveField<String,String> retrieveField = new RetrieveField<String,String>();
	protected PropertiesTableModel fields;
	protected Hashtable<Integer,Boolean> order = new Hashtable<Integer, Boolean>();
	

	
	public StoredQueryTableModel() {
		super();
		retrieveMolecule.setValue(new StructureRecord());
		retrieveField.setValue(new StructureRecord());
		order.put(new Integer(2), Boolean.FALSE);
	}

	@Override
	protected int getRecordsNumber() throws SQLException {
		if (countRecords == null) countRecords = getConnection().prepareStatement(countRecordsSQL);
		countRecords.setInt(1,getQuery().getId());
		ResultSet count = countRecords.executeQuery();
		int mr = 0;
		while (count.next()) {
			mr = count.getInt(1);
		}
		count.close();
		return mr;
	}
	
	public IStoredQuery getQuery() {
		return query;
	}
	public Connection getConnection() {
		return connection;
	}
	public void setConnection(Connection connection) throws SQLException, DbAmbitException {
		this.connection = connection;
		setResultSet(null);
		if (countRecords != null)
			countRecords.close(); 
		countRecords = null;
		if (selectRecords != null)
			selectRecords.close();
		selectRecords = null;
		
		if (structureRecords != null)
			structureRecords.close();
		structureRecords = null;

		if (structureField != null)
			structureField.close();
		structureField = null;		
	}		
	public void setQuery(IStoredQuery query) throws SQLException, AmbitException {
		if (getConnection() == null) throw new AmbitException("No connection!");
		this.query = query;
		setResultSet(null);
		
		/**
		 * Execute query
		 */
		if (selectRecords == null) selectRecords = getConnection().prepareStatement(selectRecordsSQL + (order.get(new Integer(2))?"asc":"desc"));
		selectRecords.setInt(1,getQuery().getId());
		
		try {
			setResultSet(selectRecords.executeQuery());
		} catch (SQLException x) {
			setResultSet(null);
			throw new AmbitException(x);
		}
		
	}
	
	

	protected String getField(int idstructure,String fieldname) throws SQLException, AmbitException {
		if (getConnection() == null) return null;
		if (structureField == null) structureField = 
			getConnection().prepareStatement(retrieveField.getSQL());
		
		retrieveField.setFieldname(fieldname);
		retrieveField.getValue().setIdstructure(idstructure);
		structureField.clearParameters();
		QueryExecutor.setParameters(structureField, retrieveField.getParameters());
		
		try {
			ResultSet rs = structureField.executeQuery();
			String value = null;
			while (rs.next()) {
				try {
	    			value = retrieveField.getObject(rs);
	    			if (value != null)
	    			    break;
				} catch (Exception x) {
					value = null;
				}
		    }
			rs.close();
			return value;
		} catch (Exception x) {
			x.printStackTrace();
			return null;
		} finally {
				
		}
	}	
	protected IAtomContainer getAtomContainer(int idstructure) throws SQLException, AmbitException {
		if (structureRecords == null) structureRecords = 
			getConnection().prepareStatement(AbstractStructureRetrieval.sql);
		structureRecords.clearParameters();
		structureRecords.setInt(1,idstructure);
		try {
			ResultSet rs = structureRecords.executeQuery();
			IAtomContainer ac = null;
			while (rs.next()) {
				try {
	    			ac = retrieveMolecule.getObject(rs);
	    			if (ac != null)
	    			    break;
				} catch (Exception x) {
					ac = null;
				}
		    }
			rs.close();
			return ac;
		} catch (Exception x) {
			x.printStackTrace();
			return null;
		} finally {
				
		}
	}
	public int getColumnCount() {
		return ((fields==null)?3:(3+fields.getRowCount()));
	}

	public int getRowCount() {
		return maxRecords;
	}
	public Object getValueAt(int rowIndex, int columnIndex) {
		try {
            records.first();
            records.relative(rowIndex);			
            if (records.isAfterLast()) return "";
            int idstructure = records.getInt("idstructure");
			switch (columnIndex) {
			//selected
			case 0: return records.getBoolean("selected"); 
			case 1: return getAtomContainer(idstructure);
			case 2: return records.getFloat("metric");
			default:
				return (fields==null)?"":
					getField(idstructure, ((Property)fields.getValueAt(columnIndex-3,0)).getName());
				//return records.getInt("idstructure");
			}
		} catch (Exception x) {
			return x.getMessage();
		}
	}
/*
	public Object getValueAt(int rowIndex, int column) {
		int columnIndex = column +1;
        if (records == null) return "N/A";
        else try {
            int type = records.getMetaData().getColumnType(columnIndex);
            records.first();
            records.relative(rowIndex);

            if (records.getMetaData().getColumnName(columnIndex).equals("uncompress(structure)")) {
            	if (type == java.sql.Types.VARBINARY) {
	                InputStream s = records.getBinaryStream(columnIndex);
	                IMolecule m = null;
	                if ("SDF".equals(records.getString("format")))
	                	m = MoleculeTools.readMolfile(new InputStreamReader(s));
	                else
	                	m = MoleculeTools.readCMLMolecule(new InputStreamReader(s));
	                s = null;
	                return m;
            	} else {

            		return MoleculeTools.readCMLMolecule(records.getString(columnIndex));
            	}
                
            } else
                switch (type) {
                case java.sql.Types.INTEGER:  return records.getInt(columnIndex);
                case java.sql.Types.BOOLEAN:  return records.getBoolean(columnIndex);
                case java.sql.Types.TINYINT:  return records.getBoolean(columnIndex);
                case java.sql.Types.BIT:  return records.getBoolean(columnIndex);
                case java.sql.Types.CHAR:  return records.getString(columnIndex);
                case java.sql.Types.TIMESTAMP: return records.getDate(columnIndex);
                case java.sql.Types.VARCHAR:  return records.getString(columnIndex);
                default: {
                	System.out.println(type);
                	return records.getString(columnIndex);
                }
                }
        } catch (CDKException x) {
            x.printStackTrace();
            return null;
        } catch (SQLException x) {
            x.printStackTrace();
            return rowIndex + " " + x.getMessage();
        } catch (Exception x) {
            x.printStackTrace();
            return null;            
        }
	}
*/	
	@Override
	public Class<?> getColumnClass(int column) {
		switch (column) {
		case 0: return Boolean.class;
		case 1: return IAtomContainer.class;
		case 2: return Float.class;
		default: return String.class;
		}
	}
    @Override
    public String getColumnName(int column) {
		switch (column) {
		case 0: return "Select";
		case 1: return "Structure";
		case 2: return "Metric";
		default: {
			return (fields==null)?Integer.toString(column):
				fields.getValueAt(column-3,1).toString();
		}
		}
    }
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
    	return columnIndex==0;
    }
    
    public void setProfile(Profile profile) {
    	
    	fields = (profile==null)?null:new PropertiesTableModel(profile,true,2);
    }
    public void sort(int column, boolean ascending)
    		throws UnsupportedOperationException {
    	if (column != 2) throw new UnsupportedOperationException("Column "+getColumnName(column) + " is not sortable!");
    	order.put(new Integer(2),new Boolean(ascending));
    	try {
    		if (selectRecords != null)
    			selectRecords.close();
    		selectRecords = null;    		
    		setQuery(query);
    	} catch (Exception x) {
    		throw new UnsupportedOperationException(x);
    	}
    }
}
