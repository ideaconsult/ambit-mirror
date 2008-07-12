package ambit.database.search;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import ambit.database.readers.DbStructureReader;
import ambit.database.writers.QueryResults;
import ambit.exceptions.AmbitException;

public class DbQueryReader extends DbStructureReader {
	protected QueryResults query = null;
	protected PreparedStatement ps = null;	
	protected static final String sql=
		"SELECT idstructure,casno,iddsname FROM datasets right join cas using(idstructure) where iddsname=?";
	
	public DbQueryReader(Connection connection,QueryResults query, int page, int pagesize)  throws AmbitException {
		super();
		setPage(page);
		setPagesize(pagesize);
		ps = prepareSQLStatement(connection,query,page,pagesize);
		if (ps == null) throw new AmbitException("Error when preparing SQL statement!");
	
	}
	protected PreparedStatement prepareSQLStatement(Connection conn,
			QueryResults query,  int page, int pagesize) throws AmbitException {
		boolean hasQuery = false;
		try {
			if (page >=0)
				ps = conn.prepareStatement(sql+" limit "+page2Limit());
			else 
				ps = conn.prepareStatement(sql);
			ps.setInt(1,query.getId());
			setResultset(ps.executeQuery());
		} catch (SQLException x) {
				throw new AmbitException(x);
		}
			
		return ps;
	
	}
	

	public void close() throws IOException {
		super.close();
		if (ps == null) return;
		try {
			ps.close();
		} catch (SQLException x) {
			throw new IOException(x.getMessage());
			
		}
	}
	public String toString() {
		if (query == null) return super.toString();
		else return "Retrieve compounds from stored query "+ query.toString();
	}
	public QueryResults getQuery() {
		return query;
	}
	public void setQuery(QueryResults query) {
		this.query = query;
	}

}
