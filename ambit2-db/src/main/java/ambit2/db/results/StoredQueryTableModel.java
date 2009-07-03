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

package ambit2.db.results;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.base.data.IFilteredColumns;
import ambit2.base.data.ISelectableRecords;
import ambit2.base.data.ISortableColumns;
import ambit2.base.data.Profile;
import ambit2.base.data.ProfileListModel;
import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.data.TypedListModel;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.UpdateExecutor;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.processors.StructureStatisticsProcessor;
import ambit2.db.readers.AbstractStructureRetrieval;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.readers.RetrieveAtomContainer;
import ambit2.db.readers.RetrieveDatasets;
import ambit2.db.readers.RetrieveField;
import ambit2.db.search.IQueryObject;
import ambit2.db.search.IStoredQuery;
import ambit2.db.search.QueryExecutor;
import ambit2.db.search.qlabel.QueryConsensus;
import ambit2.db.search.structure.AbstractStructureQuery;
import ambit2.db.search.structure.QueryStoredResults;
import ambit2.db.update.IQueryUpdate;
import ambit2.db.update.storedquery.UpdateSelectedRecords;

public class StoredQueryTableModel extends ResultSetTableModel implements ISelectableRecords, ISortableColumns, IFilteredColumns{
	/**
	 * 
	 */
	protected enum FIELDS {
		CONSENSUSLABEL {
			@Override
			public int getOffset() {
				return 1;
			}
			@Override
			protected IQueryRetrieval getQuery() {
				IQueryRetrieval q =  new QueryConsensus(new StructureRecord(),null);
				return q;
			}
			@Override
			protected void setParameters(IQueryRetrieval q,int arg0, int arg1) {
				((QueryConsensus)q).getFieldname().setIdchemical(arg0);
				((QueryConsensus)q).getFieldname().setIdstructure(arg1);
			}
			@Override
			public String toString() {
				return "Consensus label";
			}
		},
		DATASET {
			@Override
			public int getOffset() {
				return 1;
			}
			@Override
			protected IQueryRetrieval getQuery() {
				IQueryRetrieval q =  new RetrieveDatasets(new StructureRecord(),null);
				return q;
			}
			@Override
			protected void setParameters(IQueryRetrieval q,int arg0, int arg1) {
				((RetrieveDatasets)q).getFieldname().setIdstructure(arg1);
				
			}
			@Override
			public String toString() {
				return "Source";
			}
			
		};
		public abstract int getOffset();
		protected abstract IQueryRetrieval getQuery();
		protected abstract void setParameters(IQueryRetrieval q,int idchemical,int idstructure);

	}
	private static final long serialVersionUID = -7273714299667490819L;
	protected SELECTION_MODE selectionMode = SELECTION_MODE.NO_CHANGE;
	protected QueryStoredResults storedResults;
	
	
	protected PreparedStatement structureRecords = null;
	protected PreparedStatement structureField = null;	
	
	protected RetrieveAtomContainer retrieveMolecule = new RetrieveAtomContainer();
	protected RetrieveField retrieveField = new RetrieveField();
	protected TypedListModel<Property> fields;
	protected Hashtable<String,Profile<Property>> properties ;
	protected Hashtable<Integer,Boolean> order = new Hashtable<Integer, Boolean>();
	protected UpdateExecutor<IQueryUpdate> updateExecutor = new UpdateExecutor<IQueryUpdate>();
	protected QueryExecutor<IQueryObject> queryExecutor;
	protected PropertyChangeListener propertyListener;
	protected StructureStatisticsProcessor statsProcessor = new StructureStatisticsProcessor();
	protected IQueryRetrieval[] queryFields = new IQueryRetrieval[] { FIELDS.CONSENSUSLABEL.getQuery(), FIELDS.DATASET.getQuery() };

	
	public StoredQueryTableModel() {
		super();
		retrieveMolecule.setValue(new StructureRecord());
		retrieveField.setValue(new StructureRecord());
		retrieveField.setSearchByAlias(true);
		order.put(new Integer(2), Boolean.FALSE);
		storedResults = new QueryStoredResults();
		queryExecutor = new QueryExecutor<IQueryObject>();
		queryExecutor.setResultTypeConcurency(ResultSet.CONCUR_UPDATABLE);
		properties = new Hashtable<String,Profile<Property>>();		
		propertyListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				fields = new ProfileListModel(properties,true);				
				fireTableStructureChanged();
			}
		};
	}

	public IStoredQuery getQuery() {
		return storedResults.getFieldname();
	}
	public Connection getConnection() {
		return connection;
	}
	public void setConnection(Connection connection) throws SQLException, DbAmbitException {
		this.connection = connection;
		setResultSet(null);

		statsProcessor.setConnection(connection);
		
		if (structureRecords != null)
			structureRecords.close();
		structureRecords = null;

		if (structureField != null)
			structureField.close();
		structureField = null;
		try {
			queryExecutor.closeResults(getResultSet());
			queryExecutor.close();
			queryExecutor.setConnection(connection);
		} catch (Exception x) {
			x.printStackTrace();
		}		
		try {
			updateExecutor.close();
			updateExecutor.setConnection(connection);
		} catch (Exception x) {
			x.printStackTrace();
		}
	}		
	public void setQuery(IStoredQuery query) throws SQLException, AmbitException {
		
		storedResults.setFieldname(query);
		query.setRows(0);
		setResultSet(null);
		if (getConnection() == null) throw new AmbitException("No connection!");
		try {
			setResultSet(queryExecutor.process(storedResults));
			query.setRows(maxRecords);		
		} catch (SQLException x) {
			setResultSet(null);
			throw new AmbitException(x);
		}
		
	}
	
	

	protected Object getField(int idstructure,Property fieldname) throws SQLException, AmbitException {
		if (getConnection() == null) return null;
		if (structureField == null) structureField = 
			getConnection().prepareStatement(retrieveField.getSQL());
		
		retrieveField.setFieldname(fieldname);
		retrieveField.getValue().setIdstructure(idstructure);
		structureField.clearParameters();
		QueryExecutor.setParameters(structureField, retrieveField.getParameters());
		
		try {
			ResultSet rs = structureField.executeQuery();
			ArrayList list = new ArrayList();
			Object value = null;
			while (rs.next()) {
				try {
	    			value = retrieveField.getObject(rs);
	    			if (value != null) {
	    				if (list.indexOf(value)<0)
	    					list.add(value);
	    			    break;
	    			}
				} catch (Exception x) {
					value = null;
				}
		    }
			rs.close();
			if (list.size()<=1) return value;
			else {
				StringBuilder b = new StringBuilder();
				for (Object item:list) {b.append(item.toString()); b.append(","); }
				return b.toString();
			}
		} catch (Exception x) {
			x.printStackTrace();
			return null;
		} finally {
				
		}
	}	
	public IAtomContainer getAtomContainer(int idstructure) throws SQLException, AmbitException {
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
		return ((fields==null)?3+FIELDS.values().length:(4+fields.getSize()+FIELDS.values().length));
	}

	public int getRowCount() {
		return maxRecords;
	}
	public void update(int record,IStructureRecord struc)  {
		try {
			if (record >=0) {
	            records.first();
	            records.relative(record);
	            if (!records.isAfterLast()) {	
					struc.setIdstructure(records.getInt("idstructure"));
					struc.setIdchemical(records.getInt("idchemical"));
					return;
	            }
			}
		} catch (Exception x) {

		}
		struc.setIdchemical(-1);
		struc.setIdstructure(-1);		
	}
	@Override
	public void setValueAt(Object value, int record, int columnIndex) {
		try {
			if (record >=0) {
	            records.first();
	            records.relative(record);
	
	            if (!records.isAfterLast() && !records.isAfterLast()) {	
	        		switch (columnIndex) {
	        		case 0: {
	        			try {
	        				records.updateBoolean("selected", (Boolean)value);
	        				records.updateRow();
	        				break;
	        			} catch (Exception x) {
	        				x.printStackTrace();
	        			}
	        		}
	        		default: return;
	        		}
	            }
			}	
		} catch (Exception x) {
			x.printStackTrace();
		}
		

	}
	public String getStatsAt(int idstructure) {
		try {
	        IStructureRecord record = new StructureRecord();
	        record.setIdstructure(records.getInt(AbstractStructureQuery.FIELD_NAMES.idstructure.toString()));
	        return statsProcessor.process(record).toString();
		} catch (Exception x) {
			return "";
		}
	}
	public Object getValueAt(int rowIndex, int columnIndex) {
		try {
            records.first();
            records.relative(rowIndex);			
            if (records.isAfterLast()) return "";
            
            int idstructure = records.getInt(AbstractStructureQuery.FIELD_NAMES.idstructure.toString());
            //System.out.println("idstruc" +idstructure);
			switch (columnIndex) {
			//selected
			case 0: return records.getBoolean(AbstractStructureQuery.FIELD_NAMES.selected.toString()); 
			case 1: return getAtomContainer(idstructure);
			case 2: return records.getFloat(AbstractStructureQuery.FIELD_NAMES.metric.toString());
			default: {
				int offset = getColumnCount()-FIELDS.values().length;
				if (columnIndex >= offset) {
					int idchemical = records.getInt(AbstractStructureQuery.FIELD_NAMES.idchemical.toString());
					return getLabel(idchemical,idstructure,columnIndex-offset);
				} else if (columnIndex < (getColumnCount()-1)) {
					return (fields==null)?"":
						getField(idstructure, ((Property)fields.getElementAt(columnIndex-3)));
				} else return getStatsAt(idstructure);
				//return records.getInt("idstructure");
			}
			}
		} catch (Exception x) {
			return x.getMessage();
		}
	}
	protected String getLabel(int idchemical,int idstructure,int offset) {
		ResultSet rs = null;
		try {
			IQueryRetrieval q = queryFields[offset];
			FIELDS field = FIELDS.values()[offset];
			field.setParameters(q, idchemical, idstructure);
			rs = queryExecutor.process(q);
			StringBuilder b = new StringBuilder();
			String delimiter = "";
			while (rs.next()) {
				b.append(q.getObject(rs).toString());
				b.append(delimiter);
				delimiter = ",";
			}
			return b.toString();
		} catch (Exception x) {
			x.printStackTrace();
			return "Unknown";
		} finally {
			try {queryExecutor.closeResults(rs);} catch (Exception xx) {}			
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
			int offset = getColumnCount()-FIELDS.values().length;
			if (column >= offset) 
				return FIELDS.values()[column-offset].toString();
			else
				return (fields==null)?Integer.toString(column):
				fields.getElementAt(column-3).getName();
		}
		}
    }
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
    	return columnIndex==0;
    }
    
    public void setProfile(String key,Profile<Property> profile) {
    	Profile<Property> oldProfile = properties.get(key);
    	if (oldProfile != null) oldProfile.removePropertyChangeListener(Profile.profile_property_change,propertyListener);
    	if (profile != null) {
	    	properties.put(key, profile);
	    	profile.addPropertyChangeListener(Profile.profile_property_change,propertyListener);
    	}
    	fields = new ProfileListModel(properties,true);
    	fireTableStructureChanged();
    }
    public void sort(int column, boolean ascending)
    		throws UnsupportedOperationException {
    	if (storedResults.getFieldname()==null) return;
    	String col = getColumnName(column);
    	if (!AbstractStructureQuery.FIELD_NAMES.metric.toString().equals(col.toLowerCase())) throw new UnsupportedOperationException("Column "+getColumnName(column) + " is not sortable!");
    	order.put(new Integer(column),new Boolean(ascending));
    	try {
    		queryExecutor.closeResults(getResultSet());
    		setResultSet(null);
    		storedResults.setOrder_descendant(!ascending);
    		setQuery(storedResults.getFieldname());
    	} catch (Exception x) {
    		throw new UnsupportedOperationException(x);
    	}
    }
    public SELECTION_MODE getSelection() {
    	return selectionMode ;
    }
    public void setSelection(SELECTION_MODE mode) {
    	if (mode == null) return;
    	this.selectionMode = mode;
    	try {
        	switch (mode) {
        	case UNSELECT_ALL: {
        		selectRecords(mode);
        		break;
        	}
        	case SELECT_ALL: {
        		selectRecords(mode);
        		break;
        	}
        	case INVERT_SELECTIONS: {
        		selectRecords(mode);
        		break;
        	} 
        	case HIDE_SELECTIONS: {
        		storedResults.setValue(false);
        		setQuery(storedResults.getFieldname());
        		break;
        	}
        	case SHOW_ALL: {
        		storedResults.setValue(null);
        		setQuery(storedResults.getFieldname());
        		break;        		
        	}
        	case SHOW_SELECTIONS: {
        		storedResults.setValue(true);
        		setQuery(storedResults.getFieldname());
        		break;        		
        	}
        	}
    	} catch (Exception e) {
    		e.printStackTrace();
    	} finally {

    	}
    }
    protected void selectRecords(SELECTION_MODE mode) throws AmbitException,SQLException {
    	if (storedResults.getFieldname()==null) return;
    	UpdateSelectedRecords r = new UpdateSelectedRecords();
    	r.setObject(mode);
    	r.setGroup(getQuery());
    	int updated = updateExecutor.process(r);
    	if (updated > 0)
    		setQuery(getQuery());    	
    }
    public void dropFilter(int column) throws UnsupportedOperationException {
    	try {
    		storedResults.setValue(null);
    		setQuery(storedResults.getFieldname());
    	} catch (Exception x) {
    		throw new UnsupportedOperationException(x);
    	}
    	
    }
    public void setFilter(int column, Object value)
    		throws UnsupportedOperationException {
    	throw new  UnsupportedOperationException();
    	
    }
 
}
