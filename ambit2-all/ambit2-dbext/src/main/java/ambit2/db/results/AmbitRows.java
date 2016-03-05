/* AmbitRows.java
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

package ambit2.db.results;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;

import javax.sql.RowSet;
import javax.sql.RowSetEvent;
import javax.sql.RowSetListener;
import javax.sql.RowSetMetaData;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetWarning;
import javax.sql.rowset.spi.SyncProvider;
import javax.sql.rowset.spi.SyncProviderException;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.p.AbstractDBProcessor;
import ambit2.db.CachedRowSetFactory;
import ambit2.db.search.QueryExecutor;

public class AmbitRows<T> extends AbstractDBProcessor<T,IQueryRetrieval> implements 
									 PropertyChangeListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4124120461604514413L;
	protected IQueryRetrieval<T> query;
	protected CachedRowSet rowset;
	protected QueryExecutor<IQueryRetrieval<T>> executor = new QueryExecutor<IQueryRetrieval<T>>();
	protected String propertyname = getClass().getName();
	protected boolean ready = false;
    
	public CachedRowSet getRowSet() {
		return rowset;
	}
	public synchronized boolean isReady() {
		return ready;
	}

    
	public synchronized void setReady(boolean ready) {
		boolean status = this.ready;
		this.ready = ready;
		propertyChangeSupport.firePropertyChange("status", status,ready);
	}

	public String getPropertyname() {
		return propertyname;
	}

	public synchronized void setPropertyname(String propertyname) {
		this.propertyname = propertyname;
	}

	public AmbitRows() {
        super();
        rowset = null;
    }

    protected synchronized IQueryRetrieval createNewQuery(T target) throws AmbitException {
    	return null;
    }
    public synchronized IQueryRetrieval process(T target) throws AmbitException {
    	IQueryRetrieval q = createNewQuery(target);
    	propertyChangeSupport.firePropertyChange(new QueryChangeEvent(this,getPropertyname(),null,q));
    	return q;
    };
    
	public synchronized T getObject() throws AmbitException {
		//return getQuery().getObject(this);
		return getQuery().getObject(rowset);
	}
	public synchronized void setObject(T object) throws AmbitException {
		//throw new AmbitException("N/A");
	}
	
	public synchronized void open() throws DbAmbitException {
    	if (query == null) throw new DbAmbitException("Query not defined!");
    	setReady(false);
    	try {
    		close();
    		//rowset.close();
    	} catch (Exception x) { logger.log(Level.WARNING,x.getMessage(),x);}

    	if (getConnection() != null)
    		executor.setConnection(connection);
	
    	executor.open();
    	try {
    		populate(executor.process(query));

    	} catch (AmbitException x) {
    		throw new DbAmbitException(x);
    	} catch (SQLException x) {
    		throw new DbAmbitException(x);
    	} finally {
    		setReady(true);
    	}
    	
    }
    @Override
    protected void finalize() throws Throwable {
    	try { close(); } catch (Exception x) {}
    	super.finalize();
    }
	public synchronized IQueryRetrieval<T> getQuery() {
		return query;
	}
	public synchronized void setQuery(IQueryRetrieval<T> query)  throws AmbitException {
		this.query = query;
		open();
		
	}
    public synchronized void populate(ResultSet data) throws SQLException {
    	if (rowset == null) rowset = CachedRowSetFactory.getCachedRowSet();
        rowset.populate(data);
        executor.closeResults(data);
    }
    public void populate(ResultSet rs, int startRow) throws SQLException {
        rowset.populate(rs, startRow);
        
    }
    public void acceptChanges() throws SyncProviderException {
        rowset.acceptChanges();
        
    }
    public void acceptChanges(Connection con) throws SyncProviderException {
        rowset.acceptChanges();
        
    }
    public boolean columnUpdated(int idx) throws SQLException {
        return rowset.columnUpdated(idx);
    }
    public boolean columnUpdated(String columnName) throws SQLException {
        return rowset.columnUpdated(columnName);
    }
    public void commit() throws SQLException {
        rowset.commit();
        
    }
    public CachedRowSet createCopy() throws SQLException {
        return rowset.createCopy();
    }
    public CachedRowSet createCopyNoConstraints() throws SQLException {
        return rowset.createCopyNoConstraints();
    }
    public CachedRowSet createCopySchema() throws SQLException {
        return rowset.createCopySchema();
    }
    public RowSet createShared() throws SQLException {
        return rowset.createShared();
    }
    public void execute(Connection conn) throws SQLException {
        rowset.execute(conn);
        
    }
    public int[] getKeyColumns() throws SQLException {
        return rowset.getKeyColumns();
        
    }
    public ResultSet getOriginal() throws SQLException {
        return rowset.getOriginal();
    }
    public ResultSet getOriginalRow() throws SQLException {
        return rowset.getOriginalRow();
    }
    public int getPageSize() {
        return rowset.getPageSize();
    }
    public RowSetWarning getRowSetWarnings() throws SQLException {
        return rowset.getRowSetWarnings();
    }
    public boolean getShowDeleted() throws SQLException {
        return rowset.getShowDeleted();
    }
    public SyncProvider getSyncProvider() throws SQLException {
        return rowset.getSyncProvider();
    }
    public String getTableName() throws SQLException {
        return rowset.getTableName();
    }
    public synchronized boolean  nextPage() throws SQLException {
        return rowset.nextPage();
    }
    public boolean previousPage() throws SQLException {
        return rowset.previousPage();
    }
    public void release() throws SQLException {
        rowset.release();
        
    }
    public void restoreOriginal() throws SQLException {
        rowset.restoreOriginal();
        
    }
    public void rollback() throws SQLException {
        rowset.rollback();
        
    }
    public void rollback(Savepoint s) throws SQLException {
        rowset.rollback(s);
        
    }
    public void rowSetPopulated(RowSetEvent event, int numRows)
            throws SQLException {
        rowset.rowSetPopulated(event, numRows);
        
    }
    public void setKeyColumns(int[] keys) throws SQLException {
        rowset.setKeyColumns(keys);
        
    }
    public void setMetaData(RowSetMetaData md) throws SQLException {
        rowset.setMetaData(md);
        
    }
    public void setOriginalRow() throws SQLException {
        rowset.setOriginalRow();
        
    }
    public void setPageSize(int size) throws SQLException {
        rowset.setPageSize(size);
        
    }
    public void setShowDeleted(boolean b) throws SQLException {
        rowset.setShowDeleted(b);
        
    }
    public void setSyncProvider(String provider) throws SQLException {
        rowset.setSyncProvider(provider);
        
    }
    public void setTableName(String tabName) throws SQLException {
        rowset.setTableName(tabName);
        
    }
    public int size() {
    	if (!isReady()) return 0;
    	if (rowset == null) return 0;
        return rowset.size();
    }
    public Collection<?> toCollection() throws SQLException {
        return rowset.toCollection();
    }
    public Collection<?> toCollection(int column) throws SQLException {
        return rowset.toCollection(column);
    }
    public Collection<?> toCollection(String column) throws SQLException {
        return rowset.toCollection(column);
    }
    public void undoDelete() throws SQLException {
        rowset.undoDelete();
        
    }
    public void undoInsert() throws SQLException {
        rowset.undoInsert();
        
    }
    public void undoUpdate() throws SQLException {
        rowset.undoUpdate();
        
    }
    public void addRowSetListener(RowSetListener listener) {
        rowset.addRowSetListener(listener);
        
    }
    public void clearParameters() throws SQLException {
        rowset.clearParameters();
        
    }
    public void execute() throws SQLException {
        rowset.execute();
        
    }
    public String getCommand() {
        return rowset.getCommand();
    }
    public String getDataSourceName() {
        return rowset.getDataSourceName();
    }
    public boolean getEscapeProcessing() throws SQLException {
        return rowset.getEscapeProcessing();
    }
    public int getMaxFieldSize() throws SQLException {
        return rowset.getMaxFieldSize();
    }
    public int getMaxRows() throws SQLException {
        return rowset.getMaxRows();
    }
    public String getPassword() {
        return rowset.getPassword();
    }
    public int getQueryTimeout() throws SQLException {
        return rowset.getQueryTimeout();
    }
    public int getTransactionIsolation() {
        return rowset.getTransactionIsolation();
    }
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return rowset.getTypeMap();
    }
    public String getUrl() throws SQLException {
        return rowset.getUrl();
    }
    public String getUsername() {
        return rowset.getUsername();
    }
    public boolean isReadOnly() {
        return rowset.isReadOnly();
    }
    public void removeRowSetListener(RowSetListener listener) {
        rowset.removeRowSetListener(listener);
        
    }
    public void setArray(int i, Array x) throws SQLException {
        rowset.setArray(i, x);
        
    }
    public void setAsciiStream(int parameterIndex, InputStream x, int length)
            throws SQLException {
        rowset.setAsciiStream(parameterIndex, x, length);
        
    }
    public void setBigDecimal(int parameterIndex, BigDecimal x)
            throws SQLException {
        rowset.setBigDecimal(parameterIndex, x);
        
    }
    public void setBinaryStream(int parameterIndex, InputStream x, int length)
            throws SQLException {
        rowset.setBinaryStream(parameterIndex, x, length);
        
    }
    public void setBlob(int i, Blob x) throws SQLException {
        rowset.setBlob(i, x);
        
    }
    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        rowset.setBoolean(parameterIndex, x);
        
    }
    public void setByte(int parameterIndex, byte x) throws SQLException {
        rowset.setByte(parameterIndex, x);
        
    }
    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        rowset.setBytes(parameterIndex, x);
        
    }
    public void setCharacterStream(int parameterIndex, Reader reader, int length)
            throws SQLException {
        rowset.setCharacterStream(parameterIndex, reader, length);
        
    }
    public void setClob(int i, Clob x) throws SQLException {
        rowset.setClob(i, x);
        
    }
    public void setCommand(String cmd) throws SQLException {
        rowset.setCommand(cmd);     
        
    }
    public void setConcurrency(int concurrency) throws SQLException {
        rowset.setConcurrency(concurrency);
        
    }
    public void setDataSourceName(String name) throws SQLException {
        rowset.setDataSourceName(name);
        
    }
    public void setDate(int parameterIndex, Date x) throws SQLException {
        rowset.setDate(parameterIndex, x);
        
    }
    public void setDate(int parameterIndex, Date x, Calendar cal)
            throws SQLException {
        rowset.setDate(parameterIndex, x, cal);
        
    }
    public void setDouble(int parameterIndex, double x) throws SQLException {
        rowset.setDouble(parameterIndex, x);
        
    }
    public void setEscapeProcessing(boolean enable) throws SQLException {
        rowset.setEscapeProcessing(enable);
        
    }
    public void setFloat(int parameterIndex, float x) throws SQLException {
        rowset.setFloat(parameterIndex, x);
        
    }
    public void setInt(int parameterIndex, int x) throws SQLException {
        rowset.setInt(parameterIndex, x);
        
    }
    public void setLong(int parameterIndex, long x) throws SQLException {
        rowset.setLong(parameterIndex, x);
        
    }
    public void setMaxFieldSize(int max) throws SQLException {
        rowset.setMaxFieldSize(max);
        
    }
    public void setMaxRows(int max) throws SQLException {
        rowset.setMaxRows(max);
        
    }
    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        rowset.setNull(parameterIndex, sqlType);
        
    }
    public void setNull(int paramIndex, int sqlType, String typeName)
            throws SQLException {
        rowset.setNull(paramIndex, sqlType, typeName);
        
    }
    public void setObject(int parameterIndex, Object x) throws SQLException {
        rowset.setObject(parameterIndex, x);
        
    }
    public void setObject(int parameterIndex, Object x, int targetSqlType)
            throws SQLException {
        rowset.setObject(parameterIndex, x, targetSqlType);
        
    }
    public void setObject(int parameterIndex, Object x, int targetSqlType,
            int scale) throws SQLException {
        rowset.setObject(parameterIndex, x, targetSqlType, scale);
        
    }
    public void setPassword(String password) throws SQLException {
        rowset.setPassword(password);
        
    }
    public void setQueryTimeout(int seconds) throws SQLException {
        rowset.setQueryTimeout(seconds);
        
    }
    public void setReadOnly(boolean value) throws SQLException {
        rowset.setReadOnly(value);
        
    }
    public void setRef(int i, Ref x) throws SQLException {
        rowset.setRef(i, x);
        
    }
    public void setShort(int parameterIndex, short x) throws SQLException {
        rowset.setShort(parameterIndex, x);
        
    }
    public void setString(int parameterIndex, String x) throws SQLException {
        rowset.setString(parameterIndex, x);
        
    }
    public void setTime(int parameterIndex, Time x) throws SQLException {
        rowset.setTime(parameterIndex, x);
        
    }
    public void setTime(int parameterIndex, Time x, Calendar cal)
            throws SQLException {
        rowset.setTime(parameterIndex, x, cal);
        
    }
    public void setTimestamp(int parameterIndex, Timestamp x)
            throws SQLException {
        rowset.setTimestamp(parameterIndex, x);
        
    }
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal)
            throws SQLException {
       rowset.setTimestamp(parameterIndex, x, cal);
        
    }
    public void setTransactionIsolation(int level) throws SQLException {
        rowset.setTransactionIsolation(level);
        
    }
    public void setType(int type) throws SQLException {
        rowset.setType(type);   
        
    }
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        rowset.setTypeMap(map);
        
    }
    public void setUrl(String url) throws SQLException {
        rowset.setUrl(url);
        
    }
    public void setUsername(String name) throws SQLException {
        rowset.setUsername(name);
        
    }
    public boolean absolute(int row) throws SQLException {
        return rowset.absolute(row);
    }
    public void afterLast() throws SQLException {
        rowset.afterLast();
        
    }
    public void beforeFirst() throws SQLException {
        rowset.beforeFirst();
        
    }
    public void cancelRowUpdates() throws SQLException {
        rowset.cancelRowUpdates();
        
    }
    public void clearWarnings() throws SQLException {
        rowset.clearWarnings();
        
    }
    public synchronized void close() throws SQLException {
  	
    	executor.closeResults(null);
    	if (rowset != null)  rowset.close();
        rowset = null;
    }
    public void deleteRow() throws SQLException {
        rowset.deleteRow();
        
    }
    public int findColumn(String columnName) throws SQLException {
        return rowset.findColumn(columnName);
    }
    public boolean first() throws SQLException {
        return rowset.first()  ;
    }
    public Array getArray(int i) throws SQLException {
        return rowset.getArray(i);
    }
    public Array getArray(String colName) throws SQLException {
        return rowset.getArray(colName);
    }
    public InputStream getAsciiStream(int columnIndex) throws SQLException {
        return rowset.getAsciiStream(columnIndex);
    }
    public InputStream getAsciiStream(String columnName) throws SQLException {
        return rowset.getAsciiStream(columnName);
    }
    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        return rowset.getBigDecimal(columnIndex);
    }
    public BigDecimal getBigDecimal(String columnName) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    @SuppressWarnings("deprecation")
    public BigDecimal getBigDecimal(int columnIndex, int scale)
            throws SQLException {
        return rowset.getBigDecimal(columnIndex, scale);
    }
    public BigDecimal getBigDecimal(String columnName, int scale)
            throws SQLException {
        return getBigDecimal(columnName, scale);
    }
    public InputStream getBinaryStream(int columnIndex) throws SQLException {
        return rowset.getBinaryStream(columnIndex);
    }
    public InputStream getBinaryStream(String columnName) throws SQLException {
        return rowset.getBinaryStream(columnName);
    }
    public Blob getBlob(int i) throws SQLException {
        return rowset.getBlob(i);
    }
    public Blob getBlob(String colName) throws SQLException {

        return rowset.getBlob(colName);
    }
    public boolean getBoolean(int columnIndex) throws SQLException {
        return rowset.getBoolean(columnIndex);
    }
    public boolean getBoolean(String columnName) throws SQLException {
        return rowset.getBoolean(columnName);
    }
    public byte getByte(int columnIndex) throws SQLException {
        return rowset.getByte(columnIndex);
    }
    public byte getByte(String columnName) throws SQLException {
        return rowset.getByte(columnName);
    }
    public byte[] getBytes(int columnIndex) throws SQLException {
        return rowset.getBytes(columnIndex);
    }
    public byte[] getBytes(String columnName) throws SQLException {
        return rowset.getBytes(columnName);
    }
    public Reader getCharacterStream(int columnIndex) throws SQLException {
        return rowset.getCharacterStream(columnIndex);
    }
    public Reader getCharacterStream(String columnName) throws SQLException {
        return rowset.getCharacterStream(columnName);
    }
    public Clob getClob(int i) throws SQLException {
        return rowset.getClob(i);
    }
    public Clob getClob(String colName) throws SQLException {
        return rowset.getClob(colName);
    }
    public int getConcurrency() throws SQLException {
        return rowset.getConcurrency();
    }
    public String getCursorName() throws SQLException {
        return rowset.getCursorName();
    }
    public Date getDate(int columnIndex) throws SQLException {
        return rowset.getDate(columnIndex);
    }
    public Date getDate(String columnName) throws SQLException {
        return rowset.getDate(columnName);
    }
    public Date getDate(int columnIndex, Calendar cal) throws SQLException {
        return rowset.getDate(columnIndex, cal);
    }
    public Date getDate(String columnName, Calendar cal) throws SQLException {
        return rowset.getDate(columnName, cal);
    }
    public double getDouble(int columnIndex) throws SQLException {
        return rowset.getDouble(columnIndex);
    }
    public double getDouble(String columnName) throws SQLException {
        return rowset.getDouble(columnName);
    }
    public int getFetchDirection() throws SQLException {
        return rowset.getFetchDirection();
    }
    public int getFetchSize() throws SQLException {
        return rowset.getFetchSize();
    }
    public float getFloat(int columnIndex) throws SQLException {
        return rowset.getFloat(columnIndex);
    }
    public float getFloat(String columnName) throws SQLException {
        return rowset.getFloat(columnName);
    }
    public int getInt(int columnIndex) throws SQLException {
        return rowset.getInt(columnIndex);
    }
    public int getInt(String columnName) throws SQLException {
        return rowset.getInt(columnName);
    }
    public long getLong(int columnIndex) throws SQLException {
        return rowset.getLong(columnIndex);
        
    }
    public long getLong(String columnName) throws SQLException {
        return rowset.getLong(columnName);
    }
    public ResultSetMetaData getMetaData() throws SQLException {
        return rowset.getMetaData();
    }
    public Object getObject(int columnIndex) throws SQLException {
        return rowset.getObject(columnIndex);
    }
    public Object getObject(String columnName) throws SQLException {
        return rowset.getObject(columnName);
    }
    public Object getObject(int i, Map<String, Class<?>> map)
            throws SQLException {
        return rowset.getObject(i, map);
    }
    public Object getObject(String colName, Map<String, Class<?>> map)
            throws SQLException {
        return rowset.getObject(colName, map);
    }
    public Ref getRef(int i) throws SQLException {
        return rowset.getRef(i);
    }
    public Ref getRef(String colName) throws SQLException {
        return rowset.getRef(colName);
    }
    public int getRow() throws SQLException {
        return rowset.getRow();
    }
    public short getShort(int columnIndex) throws SQLException {
        return rowset.getShort(columnIndex);
    }
    public short getShort(String columnName) throws SQLException {
        return rowset.getShort(columnName);
    }
    public Statement getStatement() throws SQLException {
        return rowset.getStatement();
    }
    public String getString(int columnIndex) throws SQLException {
        return rowset.getString(columnIndex);
    }
    public String getString(String columnName) throws SQLException {
        return rowset.getString(columnName);
    }
    public Time getTime(int columnIndex) throws SQLException {
        return rowset.getTime(columnIndex);
    }
    public Time getTime(String columnName) throws SQLException {
        return rowset.getTime(columnName);
    }
    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        return rowset.getTime(columnIndex, cal);
    }
    public Time getTime(String columnName, Calendar cal) throws SQLException {
        return rowset.getTime(columnName, cal);
    }
    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        return rowset.getTimestamp(columnIndex);
    }
    public Timestamp getTimestamp(String columnName) throws SQLException {
        return rowset.getTimestamp(columnName);
    }
    public Timestamp getTimestamp(int columnIndex, Calendar cal)
            throws SQLException {
        return rowset.getTimestamp(columnIndex, cal);
    }
    public Timestamp getTimestamp(String columnName, Calendar cal)
            throws SQLException {
        return rowset.getTimestamp(columnName, cal);
    }
    public int getType() throws SQLException {
        return rowset.getType();
    }
    public URL getURL(int columnIndex) throws SQLException {
        return rowset.getURL(columnIndex);
    }
    public URL getURL(String columnName) throws SQLException {
        return rowset.getURL(columnName);
    }
    @SuppressWarnings("deprecation")
    public InputStream getUnicodeStream(int columnIndex) throws SQLException {
        return rowset.getUnicodeStream(columnIndex);
    }
    public InputStream getUnicodeStream(String columnName) throws SQLException {
        return rowset.getUnicodeStream(columnName);
    }
    public SQLWarning getWarnings() throws SQLException {
        return rowset.getWarnings();
    }
    public void insertRow() throws SQLException {
        rowset.insertRow();
    }
    public boolean isAfterLast() throws SQLException {
        return rowset.isAfterLast();
    }
    public boolean isBeforeFirst() throws SQLException {
        return rowset.isBeforeFirst();
    }
    public boolean isFirst() throws SQLException {
        return rowset.isFirst();
    }
    public boolean isLast() throws SQLException {
        return rowset.isLast();
    }
    public boolean last() throws SQLException {
        return rowset.last();
    }
    public void moveToCurrentRow() throws SQLException {
        rowset.moveToCurrentRow();
    }
    public void moveToInsertRow() throws SQLException {
        rowset.moveToInsertRow();
    }
    public synchronized boolean  next() throws SQLException {
        return rowset.next();
    }
    public boolean previous() throws SQLException {
        return rowset.previous();
    }
    public void refreshRow() throws SQLException {
        rowset.refreshRow();
    }
    public boolean relative(int rows) throws SQLException {
        return rowset.relative(rows);
    }
    public boolean rowDeleted() throws SQLException {
        return rowset.rowDeleted();
    }
    public boolean rowInserted() throws SQLException {
        return rowset.rowInserted();
    }
    public boolean rowUpdated() throws SQLException {
        return rowset.rowUpdated();
    }
    public void setFetchDirection(int direction) throws SQLException {
        rowset.setFetchDirection(direction);
        
    }
    public void setFetchSize(int rows) throws SQLException {
        rowset.setFetchSize(rows);
        
    }
    public void updateArray(int columnIndex, Array x) throws SQLException {
        rowset.updateArray(columnIndex, x);
        
    }
    public void updateArray(String columnName, Array x) throws SQLException {
        rowset.updateArray(columnName, x);
        
    }
    public void updateAsciiStream(int columnIndex, InputStream x, int length)
            throws SQLException {
        rowset.updateAsciiStream(columnIndex, x, length);
        
    }
    public void updateAsciiStream(String columnName, InputStream x, int length)
            throws SQLException {
        rowset.updateAsciiStream(columnName, x, length);
        
    }
    public void updateBigDecimal(int columnIndex, BigDecimal x)
            throws SQLException {
        rowset.updateBigDecimal(columnIndex, x);
    }
    public void updateBigDecimal(String columnName, BigDecimal x)
            throws SQLException {
        rowset.updateBigDecimal(columnName, x);
        
    }
    public void updateBinaryStream(int columnIndex, InputStream x, int length)
            throws SQLException {
        rowset.updateBinaryStream(columnIndex, x, length);
        
    }
    public void updateBinaryStream(String columnName, InputStream x, int length)
            throws SQLException {
        rowset.updateBinaryStream(columnName, x, length);
        
    }
    public void updateBlob(int columnIndex, Blob x) throws SQLException {
        rowset.updateBlob(columnIndex, x);
        
    }
    public void updateBlob(String columnName, Blob x) throws SQLException {
        rowset.updateBlob(columnName, x);
        
    }
    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
        rowset.updateBoolean(columnIndex, x);
        
    }
    public void updateBoolean(String columnName, boolean x) throws SQLException {
        rowset.updateBoolean(columnName, x);
        
    }
    public void updateByte(int columnIndex, byte x) throws SQLException {
        rowset.updateByte(columnIndex, x);
        
    }
    public void updateByte(String columnName, byte x) throws SQLException {
        rowset.updateByte(columnName, x);
        
    }
    public void updateBytes(int columnIndex, byte[] x) throws SQLException {
        rowset.updateBytes(columnIndex, x);
        
    }
    public void updateBytes(String columnName, byte[] x) throws SQLException {
        rowset.updateBytes(columnName, x);
        
    }
    public void updateCharacterStream(int columnIndex, Reader x, int length)
            throws SQLException {
        rowset.updateCharacterStream(columnIndex, x, length);
        
    }
    public void updateCharacterStream(String columnName, Reader reader,
            int length) throws SQLException {
        rowset.updateCharacterStream(columnName, reader, length);
        
    }
    public void updateClob(int columnIndex, Clob x) throws SQLException {
        rowset.updateClob(columnIndex, x);
        
    }
    public void updateClob(String columnName, Clob x) throws SQLException {
        rowset.updateClob(columnName, x);
        
    }
    public void updateDate(int columnIndex, Date x) throws SQLException {
        rowset.updateDate(columnIndex, x);
        
    }
    public void updateDate(String columnName, Date x) throws SQLException {
        rowset.updateDate(columnName, x);
        
    }
    public void updateDouble(int columnIndex, double x) throws SQLException {
        rowset.updateDouble(columnIndex, x);
        
    }
    public void updateDouble(String columnName, double x) throws SQLException {
        rowset.updateDouble(columnName, x);
        
    }
    public void updateFloat(int columnIndex, float x) throws SQLException {
        rowset.updateFloat(columnIndex, x);
        
    }
    public void updateFloat(String columnName, float x) throws SQLException {
        rowset.updateFloat(columnName, x);
        
    }
    public void updateInt(int columnIndex, int x) throws SQLException {
        rowset.updateInt(columnIndex, x);
        
    }
    public void updateInt(String columnName, int x) throws SQLException {
        rowset.updateInt(columnName, x);
        
    }
    public void updateLong(int columnIndex, long x) throws SQLException {
        rowset.updateLong(columnIndex, x);
        
    }
    public void updateLong(String columnName, long x) throws SQLException {
        rowset.updateLong(columnName, x);
        
    }
    public void updateNull(int columnIndex) throws SQLException {
        rowset.updateNull(columnIndex);
        
    }
    public void updateNull(String columnName) throws SQLException {
        rowset.updateNull(columnName);
        
    }
    public void updateObject(int columnIndex, Object x) throws SQLException {
        rowset.updateObject(columnIndex, x);
        
    }
    public void updateObject(String columnName, Object x) throws SQLException {
        rowset.updateObject(columnName, x);
        
    }
    public void updateObject(int columnIndex, Object x, int scale)
            throws SQLException {
        rowset.updateObject(columnIndex, x,scale);
        
    }
    public void updateObject(String columnName, Object x, int scale)
            throws SQLException {
        rowset.updateObject(columnName, x, scale);
        
    }
    public void updateRef(int columnIndex, Ref x) throws SQLException {
        rowset.updateRef(columnIndex, x);
        
    }
    public void updateRef(String columnName, Ref x) throws SQLException {
        rowset.updateRef(columnName, x);
        
    }
    public void updateRow() throws SQLException {
        rowset.updateRow();
        
    }
    public void updateShort(int columnIndex, short x) throws SQLException {
        rowset.updateShort(columnIndex, x);
        
    }
    public void updateShort(String columnName, short x) throws SQLException {
        rowset.updateShort(columnName, x);
        
    }
    public void updateString(int columnIndex, String x) throws SQLException {
        rowset.updateString(columnIndex, x);
        
    }
    public void updateString(String columnName, String x) throws SQLException {
        rowset.updateString(columnName, x);
        
    }
    public void updateTime(int columnIndex, Time x) throws SQLException {
        rowset.updateTime(columnIndex, x);
        
    }
    public void updateTime(String columnName, Time x) throws SQLException {
        rowset.updateTime(columnName, x);
        
    }
    public void updateTimestamp(int columnIndex, Timestamp x)
            throws SQLException {
        rowset.updateTimestamp(columnIndex, x);
        
    }
    public void updateTimestamp(String columnName, Timestamp x)
            throws SQLException {
        rowset.updateTimestamp(columnName, x);
        
    }
    public boolean wasNull() throws SQLException {
        return rowset.wasNull();
    }
    public int[] getMatchColumnIndexes() throws SQLException {
        return rowset.getMatchColumnIndexes();
    }
    public String[] getMatchColumnNames() throws SQLException {
        return rowset.getMatchColumnNames();
    }
    public void setMatchColumn(int columnIdx) throws SQLException {
        rowset.setMatchColumn(columnIdx);
        
    }
    public void setMatchColumn(int[] columnIdxes) throws SQLException {
        rowset.setMatchColumn(columnIdxes);
        
    }
    public void setMatchColumn(String columnName) throws SQLException {
        rowset.setMatchColumn(columnName);
        
    }
    public void setMatchColumn(String[] columnNames) throws SQLException {
        rowset.setMatchColumn(columnNames);
        
    }
    public void unsetMatchColumn(int columnIdx) throws SQLException {
        rowset.unsetMatchColumn(columnIdx);
        
    }
    public void unsetMatchColumn(int[] columnIdxes) throws SQLException {
        rowset.unsetMatchColumn(columnIdxes);
        
    }
    public void unsetMatchColumn(String columnName) throws SQLException {
        rowset.unsetMatchColumn(columnName);
        
    }
    public void unsetMatchColumn(String[] columnName) throws SQLException {
        rowset.unsetMatchColumn(columnName);
        
    }

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt instanceof QueryChangeEvent) 
			try {
				
				setQuery(((QueryChangeEvent)evt).getNewQuery());
			} catch (Exception x) {
				logger.log(Level.SEVERE,getPropertyname(),x);
			}
		else {
			logger.log(Level.FINE,getPropertyname() + ":"+evt.getNewValue());
		}
		
	}
	

}
