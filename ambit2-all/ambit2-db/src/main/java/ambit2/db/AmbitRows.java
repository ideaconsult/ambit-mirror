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

package ambit2.db;

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

import javax.sql.RowSet;
import javax.sql.RowSetEvent;
import javax.sql.RowSetListener;
import javax.sql.RowSetMetaData;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetWarning;
import javax.sql.rowset.spi.SyncProvider;
import javax.sql.rowset.spi.SyncProviderException;

import ambit2.core.exceptions.AmbitException;
import ambit2.db.search.IQueryObject;
import ambit2.db.search.QueryExecutor;

public abstract class AmbitRows<T, Q extends IQueryObject> implements CachedRowSet{
	protected Q query;
	protected CachedRowSet rowset;
	
    public AmbitRows() throws SQLException {
        super();
        rowset = CachedRowSetFactory.getCachedRowSet();
    }
    public abstract T getObject() throws AmbitException ;
    
    public void open(Connection connection) throws AmbitException {
    	if (query == null) throw new AmbitException("Query not defined!");
    	QueryExecutor<Q> executor = new QueryExecutor<Q>();
    	executor.setConnection(connection);
    	executor.open();
    	try {
    		populate(executor.process(query));
    	} catch (SQLException x) {
    		throw new AmbitException(x);
    	}
    }
	public Q getQuery() {
		return query;
	}
	public void setQuery(Q query) {
		this.query = query;
	}
    public void populate(ResultSet data) throws SQLException {
        rowset.populate(data);
        
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
    public boolean nextPage() throws SQLException {
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
        rowset.setTimestamp(parameterIndex, x, cal);
        
    }
    public void setTimestamp(int parameterIndex, Timestamp x)
            throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal)
            throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void setTransactionIsolation(int level) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void setType(int type) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void setUrl(String url) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void setUsername(String name) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public boolean absolute(int row) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }
    public void afterLast() throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void beforeFirst() throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void cancelRowUpdates() throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void clearWarnings() throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void close() throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void deleteRow() throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public int findColumn(String columnName) throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }
    public boolean first() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }
    public Array getArray(int i) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public Array getArray(String colName) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public InputStream getAsciiStream(int columnIndex) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public InputStream getAsciiStream(String columnName) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public BigDecimal getBigDecimal(String columnName) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public BigDecimal getBigDecimal(int columnIndex, int scale)
            throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public BigDecimal getBigDecimal(String columnName, int scale)
            throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public InputStream getBinaryStream(int columnIndex) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public InputStream getBinaryStream(String columnName) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public Blob getBlob(int i) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public Blob getBlob(String colName) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public boolean getBoolean(int columnIndex) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }
    public boolean getBoolean(String columnName) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }
    public byte getByte(int columnIndex) throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }
    public byte getByte(String columnName) throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }
    public byte[] getBytes(int columnIndex) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public byte[] getBytes(String columnName) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public Reader getCharacterStream(int columnIndex) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public Reader getCharacterStream(String columnName) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public Clob getClob(int i) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public Clob getClob(String colName) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public int getConcurrency() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }
    public String getCursorName() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public Date getDate(int columnIndex) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public Date getDate(String columnName) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public Date getDate(int columnIndex, Calendar cal) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public Date getDate(String columnName, Calendar cal) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public double getDouble(int columnIndex) throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }
    public double getDouble(String columnName) throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }
    public int getFetchDirection() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }
    public int getFetchSize() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }
    public float getFloat(int columnIndex) throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }
    public float getFloat(String columnName) throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }
    public int getInt(int columnIndex) throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }
    public int getInt(String columnName) throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }
    public long getLong(int columnIndex) throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }
    public long getLong(String columnName) throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }
    public ResultSetMetaData getMetaData() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public Object getObject(int columnIndex) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public Object getObject(String columnName) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public Object getObject(int i, Map<String, Class<?>> map)
            throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public Object getObject(String colName, Map<String, Class<?>> map)
            throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public Ref getRef(int i) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public Ref getRef(String colName) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public int getRow() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }
    public short getShort(int columnIndex) throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }
    public short getShort(String columnName) throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }
    public Statement getStatement() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public String getString(int columnIndex) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public String getString(String columnName) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public Time getTime(int columnIndex) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public Time getTime(String columnName) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public Time getTime(String columnName, Calendar cal) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public Timestamp getTimestamp(String columnName) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public Timestamp getTimestamp(int columnIndex, Calendar cal)
            throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public Timestamp getTimestamp(String columnName, Calendar cal)
            throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public int getType() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }
    public URL getURL(int columnIndex) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public URL getURL(String columnName) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public InputStream getUnicodeStream(int columnIndex) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public InputStream getUnicodeStream(String columnName) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public SQLWarning getWarnings() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public void insertRow() throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public boolean isAfterLast() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }
    public boolean isBeforeFirst() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }
    public boolean isFirst() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }
    public boolean isLast() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }
    public boolean last() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }
    public void moveToCurrentRow() throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void moveToInsertRow() throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public boolean next() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }
    public boolean previous() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }
    public void refreshRow() throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public boolean relative(int rows) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }
    public boolean rowDeleted() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }
    public boolean rowInserted() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }
    public boolean rowUpdated() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }
    public void setFetchDirection(int direction) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void setFetchSize(int rows) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void updateArray(int columnIndex, Array x) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void updateArray(String columnName, Array x) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void updateAsciiStream(int columnIndex, InputStream x, int length)
            throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void updateAsciiStream(String columnName, InputStream x, int length)
            throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void updateBigDecimal(int columnIndex, BigDecimal x)
            throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void updateBigDecimal(String columnName, BigDecimal x)
            throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void updateBinaryStream(int columnIndex, InputStream x, int length)
            throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void updateBinaryStream(String columnName, InputStream x, int length)
            throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void updateBlob(int columnIndex, Blob x) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void updateBlob(String columnName, Blob x) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void updateBoolean(String columnName, boolean x) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void updateByte(int columnIndex, byte x) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void updateByte(String columnName, byte x) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void updateBytes(int columnIndex, byte[] x) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void updateBytes(String columnName, byte[] x) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void updateCharacterStream(int columnIndex, Reader x, int length)
            throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void updateCharacterStream(String columnName, Reader reader,
            int length) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void updateClob(int columnIndex, Clob x) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void updateClob(String columnName, Clob x) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void updateDate(int columnIndex, Date x) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void updateDate(String columnName, Date x) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void updateDouble(int columnIndex, double x) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void updateDouble(String columnName, double x) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void updateFloat(int columnIndex, float x) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void updateFloat(String columnName, float x) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void updateInt(int columnIndex, int x) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void updateInt(String columnName, int x) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void updateLong(int columnIndex, long x) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void updateLong(String columnName, long x) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void updateNull(int columnIndex) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void updateNull(String columnName) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void updateObject(int columnIndex, Object x) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void updateObject(String columnName, Object x) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void updateObject(int columnIndex, Object x, int scale)
            throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void updateObject(String columnName, Object x, int scale)
            throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void updateRef(int columnIndex, Ref x) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void updateRef(String columnName, Ref x) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void updateRow() throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void updateShort(int columnIndex, short x) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void updateShort(String columnName, short x) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void updateString(int columnIndex, String x) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void updateString(String columnName, String x) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void updateTime(int columnIndex, Time x) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void updateTime(String columnName, Time x) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void updateTimestamp(int columnIndex, Timestamp x)
            throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void updateTimestamp(String columnName, Timestamp x)
            throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public boolean wasNull() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }
    public int[] getMatchColumnIndexes() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public String[] getMatchColumnNames() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    public void setMatchColumn(int columnIdx) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void setMatchColumn(int[] columnIdxes) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void setMatchColumn(String columnName) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void setMatchColumn(String[] columnNames) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void unsetMatchColumn(int columnIdx) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void unsetMatchColumn(int[] columnIdxes) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void unsetMatchColumn(String columnName) throws SQLException {
        // TODO Auto-generated method stub
        
    }
    public void unsetMatchColumn(String[] columnName) throws SQLException {
        // TODO Auto-generated method stub
        
    }
	

}
