package ambit2.db.processors;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.OperationNotSupportedException;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import ambit2.base.data.Property;
import ambit2.base.data.SourceDataset;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.PreparedStatementBatchExecutor;
import ambit2.db.processors.AbstractPropertyWriter.mode;
import ambit2.db.readers.PropertyValue;
import ambit2.db.search.property.RetieveFeatures;
import ambit2.db.update.property.CreateProperty;
import ambit2.db.update.value.UpdateStructurePropertyIDNumber;
import ambit2.db.update.value.UpdateStructurePropertyIDString;

public class ValueWriterNew extends AbstractRepositoryWriter<IStructureRecord,IStructureRecord> {
	/**
	 * 
	 */
	
	
	private static final long serialVersionUID = 8373222804070419878L;
	
	protected SourceDataset dataset;
	public SourceDataset getDataset() {
		return dataset;
	}
	public void setDataset(SourceDataset dataset) {
		this.dataset = dataset;
	}

	/**
	 * Tuples
	*/
	protected PreparedStatementBatchExecutor tuples_batchExecutor = new PreparedStatementBatchExecutor();

	@Override
	public IStructureRecord write(IStructureRecord recordToStore) throws SQLException,
			OperationNotSupportedException, AmbitException {

		try {
			retrieveProperties(recordToStore);
			prepareBatchProperties(recordToStore, getConnection());
			int[] n = (int[]) property_batchExecutor.process(qProperty);
			retrieveProperties(recordToStore);			
			prepareBatchValues(recordToStore, getConnection());
			pnum_batchExecutor.process(qNumber);
			pstring_batchExecutor.process(qString);


		} catch (AmbitException x) {
			throw x;
		} catch (java.lang.Exception x) {
			throw new AmbitException(x);
		} finally {

		}		
		return recordToStore;
	}
	
	@Override
	public void close() throws Exception {
		try { property_batchExecutor.close();} catch (Exception x) {}
		try {pnum_batchExecutor.close(); } catch (Exception x) {}
		try {pstring_batchExecutor.close();} catch (Exception x) {}
		try {queryexec.close();} catch (Exception x) {}
		super.close();
	}

	/**
	 * Prepared statement for writing {@link Property} objects 
	 */
	protected PreparedStatementBatchExecutor property_batchExecutor = new PreparedStatementBatchExecutor();
	protected CreateProperty qProperty = new CreateProperty();
	protected void prepareBatchProperties(IStructureRecord record, Connection connection) throws Exception {
		property_batchExecutor.setCloseConnection(false);
		property_batchExecutor.initBatch(connection);
		
		Iterable<Property> properties = record.getProperties(); 

		for (Property property : properties) {
			//properties --> table properties& catalog_reference
			if (property.getId()<=0) { //have to create the property

				property.setLabel(Property.guessLabel(property.getName().toLowerCase()));
				qProperty.setObject(property);
				int n = property_batchExecutor.addBatch(qProperty);

			}
		}
	}

	/**
	 * Prepared statements for writing string and numeric values into property_values table
	 */
	protected PreparedStatementBatchExecutor pstring_batchExecutor = new PreparedStatementBatchExecutor();
	protected PreparedStatementBatchExecutor pnum_batchExecutor = new PreparedStatementBatchExecutor();
	protected void prepareBatchValues(IStructureRecord record, Connection connection) throws Exception {
		pnum_batchExecutor.setCloseConnection(false);
		pstring_batchExecutor.setCloseConnection(false);
		pnum_batchExecutor.initBatch(connection);
		pstring_batchExecutor.initBatch(connection);
		
		Iterable<Property> properties = record.getProperties(); 

		for (Property property : properties) {
			//values --> table property_values && property_string

			if (property.getId()>0) { //only properties that exist
				prepareStatementValues(record, property);
			}
			
		}
	}
	
	/**
	 * Prepared statements for writing string and numeric values into property_values table
	 */
	protected UpdateStructurePropertyIDNumber qNumber = new UpdateStructurePropertyIDNumber() {
		public void setID(int index, int id) {
			
		};
	};
	protected UpdateStructurePropertyIDString qString = new UpdateStructurePropertyIDString() {
		
	};
	protected void prepareStatementValues(IStructureRecord record, Property property) throws Exception {
		Object o = record.getProperty(property);
		mode error = mode.UNKNOWN;
		Number value = null;
		boolean numeric = false;
		
		try {
			if (o instanceof String) {
				value = Integer.parseInt(o.toString());
				numeric = true;
			}
			else if (o instanceof Number) {

				if (Double.isNaN( ((Number)o).doubleValue())) { value = null;error = mode.ERROR;}
				else value = (Number) o;				
					
				numeric = true;
			}
		} catch (Exception x) {
			try {
				value = Double.parseDouble(o.toString());
				if (Double.isNaN( ((Number)value).doubleValue())) {
					error = mode.ERROR;
					value = null;
				}
				numeric = true;
			} catch (Exception xx) { //string
				value = null;
				numeric = false;
			}
		}
		
		if (numeric) { //numeric
			
			qNumber.setGroup(record);
//			qNumber.setError(mode);
			qNumber.setObject(new PropertyValue<Number>(property, value,error));
			int n = pnum_batchExecutor.addBatch(qNumber);
		
			
		} else if (o != null) { 
			qString.setGroup(record);
			qString.setObject(new PropertyValue<String>(property, o.toString()));
			int n = pstring_batchExecutor.addBatch(qString);
		}
	}	
	
	/**
	 * Looks for all {@link Property}, assigned to this structure and retrieves IDs from properties table.
	 */
	protected RetieveFeatures features = new RetieveFeatures();
	protected void retrieveProperties(IStructureRecord record) throws Exception {
		
		features.setValue(record);
		ResultSet rs = queryexec.process(features);
		try {
			while (rs.next()) {
				Property property = features.getObject(rs);
				Object value = record.getProperty(property);
				if (value != null) {
					record.setProperty(property, null);
					record.setProperty(property, value); 
				}
	
			}
		} finally {
			rs.close();
		}
	}
	
	public synchronized void setConnection(Connection connection)  throws DbAmbitException  {
        super.setConnection(connection);
        property_batchExecutor.setConnection(connection);
        pnum_batchExecutor.setConnection(connection);
        pstring_batchExecutor.setConnection(connection);
    } 
	
}


