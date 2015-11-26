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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;

import net.idea.modbcum.i.IQueryObject;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.i.query.IQueryUpdate;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import ambit2.base.data.AmbitUser;
import ambit2.base.data.IFilteredColumns;
import ambit2.base.data.ISelectableRecords;
import ambit2.base.data.ISortableColumns;
import ambit2.base.data.Profile;
import ambit2.base.data.ProfileListModel;
import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.data.TypedListModel;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.data.MoleculeTools;
import ambit2.db.SessionID;
import ambit2.db.UpdateExecutor;
import ambit2.db.processors.ProcessorCreateQuery;
import ambit2.db.processors.StructureStatisticsProcessor;
import ambit2.db.readers.RetrieveAtomContainer;
import ambit2.db.readers.RetrieveDatasets;
import ambit2.db.readers.RetrieveField;
import ambit2.db.search.IStoredQuery;
import ambit2.db.search.QueryExecutor;
import ambit2.db.search.qlabel.QueryConsensus;
import ambit2.db.search.qlabel.QueryQLabel;
import ambit2.db.search.structure.AbstractStructureQuery;
import ambit2.db.search.structure.QuerySimilarityStructure;
import ambit2.db.search.structure.QueryStoredResults;
import ambit2.db.update.storedquery.SelectStoredQuery;
import ambit2.db.update.storedquery.UpdateSelectedRecords;

public class StoredQueryTableModel extends ResultSetTableModel implements ISelectableRecords, ISortableColumns, IFilteredColumns{
	/**
	 * 
	 */
	protected enum FIELDS {
		
	
		
		CONSENSUSLABEL {
	
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
			
		},
		STRUCTURELABEL {
			
			@Override
			protected IQueryRetrieval getQuery() {
				IQueryRetrieval q =  new QueryQLabel(new AmbitUser("comparison"),new StructureRecord());
				return q;
			}
			@Override
			protected void setParameters(IQueryRetrieval q,int idchemical, int idstructure) {
				((QueryQLabel)q).getValue().setIdstructure(idstructure);
			}
			@Override
			public String toString() {
				return "Quality label";
			}			
		};
	//	public abstract int getOffset();
		protected abstract IQueryRetrieval getQuery();
		protected abstract void setParameters(IQueryRetrieval q,int idchemical,int idstructure);

	}
	private static final long serialVersionUID = -7273714299667490819L;
	protected SELECTION_MODE selectionMode = SELECTION_MODE.NO_CHANGE;
	protected AbstractStructureQuery storedResults;
	
	
	//protected PreparedStatement structureRecords = null;
	//protected PreparedStatement structureField = null;	
	
	protected RetrieveAtomContainer retrieveMolecule = new RetrieveAtomContainer();
	protected RetrieveField<Object> retrieveField = new RetrieveField<Object>();
	protected TypedListModel<Property> fields;
	protected Hashtable<String,Profile<Property>> properties ;
	protected Hashtable<Integer,Boolean> order = new Hashtable<Integer, Boolean>();
	protected UpdateExecutor<IQueryUpdate> updateExecutor = new UpdateExecutor<IQueryUpdate>();
	protected QueryExecutor<RetrieveAtomContainer> queryMolecule;
	protected QueryExecutor<IQueryObject> queryExecutor;
	protected QueryExecutor<RetrieveField> queryFieldExecutor;
	protected PropertyChangeListener propertyListener;
	protected StructureStatisticsProcessor statsProcessor = new StructureStatisticsProcessor();
	protected IQueryRetrieval[] queryFields = new IQueryRetrieval[] { 
			FIELDS.CONSENSUSLABEL.getQuery(), FIELDS.DATASET.getQuery(), FIELDS.STRUCTURELABEL.getQuery() };
	protected StructureRecord workingRecord;
	
	public StoredQueryTableModel() {
		this(true);
	}
	public StoredQueryTableModel(boolean chemicalsOnly) {
		super();
		queryMolecule = new QueryExecutor<RetrieveAtomContainer>();
		queryMolecule.setCache(true);
		queryFieldExecutor = new QueryExecutor<RetrieveField>();
		queryFieldExecutor.setCache(true);
		retrieveMolecule.setValue(new StructureRecord());
		retrieveField.setValue(new StructureRecord());
		retrieveField.setSearchByAlias(true);
		order.put(new Integer(2), Boolean.FALSE);
		storedResults = new QueryStoredResults();
		storedResults.setChemicalsOnly(chemicalsOnly);
		queryExecutor = new QueryExecutor<IQueryObject>();
		queryExecutor.setResultTypeConcurency(ResultSet.CONCUR_READ_ONLY | ResultSet.TYPE_SCROLL_INSENSITIVE);
		properties = new Hashtable<String,Profile<Property>>();		
		propertyListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				fields = new ProfileListModel(properties,true);				
				fireTableStructureChanged();
			}
		};
	}
	public void findSimilar(SessionID session, int row, int col) throws AmbitException {

		ProcessorCreateQuery p = new ProcessorCreateQuery();
		try {
			IAtomContainerSet set = MoleculeTools.newMoleculeSet(SilentChemObjectBuilder.getInstance());
			IStructureRecord record =  getRecord(row, col);
			set.addAtomContainer(getAtomContainer(record));		
			QuerySimilarityStructure q = new QuerySimilarityStructure();
			q.setValue(set);			
			p.setConnection(getConnection());
			IStoredQuery newQuery = p.process(q);
			setQuery(q);
		} catch (Exception x) {
			throw new AmbitException(x);
		} finally {
			try {	p.close(); } catch (Exception x) {}
		}
	}
	public void setChemicalsOnly(boolean value) {
		storedResults.setChemicalsOnly(value);
		try {
		setQuery(getQuery());
		} catch (Exception x) {}
	}
	public boolean isChemicalsOnly() {
		return storedResults.isChemicalsOnly();
	}
	public AbstractStructureQuery getQuery() {
		return storedResults;
	}
	public Connection getConnection() {
		return connection;
	}
	public void setConnection(Connection connection) throws SQLException, DbAmbitException {

		setResultSet(null);

		statsProcessor.setConnection(connection);
		
		try {
			queryMolecule.closeResults(getResultSet());
			queryMolecule.close();
			queryMolecule.setConnection(connection);
		} catch (Exception x) {
			x.printStackTrace();
		}

		try {
			queryFieldExecutor.closeResults(getResultSet());
			queryFieldExecutor.close();
			queryFieldExecutor.setConnection(connection);
		} catch (Exception x) {
			x.printStackTrace();
		}
		
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
		try {
			if (this.connection != null) this.connection.close();

		} catch (Exception x) {
			//x.printStackTrace();
		} finally {
			this.connection = connection;
		}
	}		
	public void setQuery(AbstractStructureQuery query) throws SQLException, AmbitException {
		
		query.setChemicalsOnly(storedResults==null?false:storedResults.isChemicalsOnly());
		storedResults = query;
		setResultSet(null);
		if (getConnection() == null) throw new AmbitException("No connection!");
		try {
			storedResults.setPageSize(maxRecords);
			setResultSet(queryExecutor.process(storedResults));
			
		} catch (SQLException x) {
			setResultSet(null);
			throw new AmbitException(x);
		}
		
	}
	
	

	protected Object getField(IStructureRecord record,Property fieldname) throws SQLException, AmbitException {
		if (getConnection() == null) return null;
		
		retrieveField.setFieldname(fieldname);
		retrieveField.getValue().setIdchemical(record.getIdchemical());
		retrieveField.getValue().setIdstructure(record.getIdstructure());
		ResultSet rs = null;
		try {
			retrieveField.setChemicalsOnly(storedResults.isChemicalsOnly());
			rs = queryFieldExecutor.process(retrieveField);
			ArrayList list = new ArrayList();
			Object value = null;
			while (rs.next()) {
				try {
	    			value = retrieveField.getObject(rs);
	    			if (value != null) {
	    				if (list.indexOf(value)<0)
	    					list.add(value);
	    			    
	    			}
				} catch (Exception x) {
					value = null;
				}
		    }
			if (list.size()<=1) return value;
			else {
				StringBuilder b = new StringBuilder();
				for (Object item:list) {b.append(item.toString()); b.append("; "); }
				return b.toString();
			}
		} catch (Exception x) {
			x.printStackTrace();
			return null;
		} finally {
		    try {rs.close();} catch (Exception xx) {}
		}
	}	
	public IAtomContainer getAtomContainer(IStructureRecord record) throws SQLException, AmbitException {

		try {
			retrieveMolecule.setFieldname(isChemicalsOnly());
			retrieveMolecule.setValue(record);
			ResultSet rs = queryMolecule.process(retrieveMolecule);
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
	        				StructureRecord r = new StructureRecord();
	        				r.setIdstructure(records.getInt(AbstractStructureQuery.FIELD_NAMES.idstructure.toString()));
	        				r.setIdchemical(records.getInt(AbstractStructureQuery.FIELD_NAMES.idchemical.toString()));
	        				selectRecords(r,(Boolean)value);
	        				//records.updateBoolean("selected", (Boolean)value);
	        				//records.updateRow();
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
	public String getStatsAt(IStructureRecord record) {
		try {
	        return statsProcessor.process(record).toString();
		} catch (Exception x) {
			return "";
		}
	}
	public IStructureRecord getRecord(int rowIndex, int columnIndex) {
		try {
			if (workingRecord == null) workingRecord = new StructureRecord(-1,-1,null,null);
			workingRecord.setIdstructure(records.getInt(AbstractStructureQuery.FIELD_NAMES.idstructure.toString()));
			workingRecord.setIdchemical(records.getInt(AbstractStructureQuery.FIELD_NAMES.idchemical.toString()));
			return workingRecord;
		} catch (Exception x) {
			return null;
		}
	}
	public Object getValueAt(int rowIndex, int columnIndex) {
		try {
            records.first();
            records.relative(rowIndex);			
            if (records.isAfterLast()) return "";
            
            IStructureRecord record = getRecord(rowIndex, columnIndex);
            if (record == null) return null;
            //System.out.println("idstruc" +idstructure);
			switch (columnIndex) {
			//selected
			case 0: return records.getBoolean(AbstractStructureQuery.FIELD_NAMES.selected.toString()); 
			case 1: return getAtomContainer(record);
			case 2: return records.getFloat(AbstractStructureQuery.FIELD_NAMES.metric.toString());
			default: {
				int offset = getColumnCount()-FIELDS.values().length;
				if (columnIndex >= offset) {
					return getLabel(record,columnIndex-offset);
				} else if (columnIndex < (getColumnCount()-1)) {
					return (fields==null)?"":
						getField(record, ((Property)fields.getElementAt(columnIndex-3)));
				} else return getStatsAt(record);
				//return records.getInt("idstructure");
			}
			}
		} catch (Exception x) {
			return x.getMessage();
		}
	}

	protected String getLabel(IStructureRecord record,int offset) {
		if (offset>=queryFields.length) return "";
		ResultSet rs = null;
		try {
			
			IQueryRetrieval q = queryFields[offset];
			FIELDS field = FIELDS.values()[offset];
			field.setParameters(q, record.getIdchemical(),record.getIdstructure());
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
	                IAtomContainer m = null;
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
    		setQuery(storedResults);
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
        		setQuery(storedResults);
        		break;
        	}
        	case SHOW_ALL: {
        		storedResults.setValue(null);
        		setQuery(storedResults);
        		break;        		
        	}
        	case SHOW_SELECTIONS: {
        		storedResults.setValue(true);
        		setQuery(storedResults);
        		break;        		
        	}
        	}
    	} catch (Exception e) {
    		e.printStackTrace();
    	} finally {

    	}
    }
    protected void selectRecords(IStructureRecord record,boolean value) throws Exception,SQLException {
    	IStoredQuery query = null;
    	if (storedResults==null) return;
    	if (storedResults instanceof QueryStoredResults) query = ((QueryStoredResults)storedResults).getFieldname();
    	else if (storedResults instanceof IStoredQuery) query = (IStoredQuery) storedResults;
    	else return;
    	SelectStoredQuery r = new SelectStoredQuery(value);
    	r.setObject(record);
    	r.setGroup(query);
    	int updated = updateExecutor.process(r);
    	if (updated > 0)
    		setQuery(getQuery());    	
    }
    
    protected void selectRecords(SELECTION_MODE mode) throws Exception,SQLException {
    	IStoredQuery query = null;
    	if (storedResults==null) return;
    	if (storedResults instanceof QueryStoredResults) query = ((QueryStoredResults)storedResults).getFieldname();
    	else if (storedResults instanceof IStoredQuery) query = (IStoredQuery) storedResults;
    	else return;
    	
    	UpdateSelectedRecords r = new UpdateSelectedRecords();
    	r.setObject(mode);
    	r.setGroup(query);
    	int updated = updateExecutor.process(r);
    	if (updated > 0)
    		setQuery(getQuery());    	
    }
    public void dropFilter(int column) throws UnsupportedOperationException {
    	try {
        	IStoredQuery query = null;
        	if (storedResults==null) return;
        	if (storedResults instanceof QueryStoredResults) query = ((QueryStoredResults)storedResults).getFieldname();
        	else if (storedResults instanceof IStoredQuery) query = (IStoredQuery) storedResults;
        	else return;
        	
        	//((IStoredQuery)storedResults).getQuery().setValue(null);
    //		setQuery(storedResults);
    	} catch (Exception x) {
    		throw new UnsupportedOperationException(x);
    	}
    	
    }
    public void setFilter(int column, Object value)
    		throws UnsupportedOperationException {
    	throw new  UnsupportedOperationException();
    	
    }
    
}
