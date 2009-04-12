package ambit2.db.search;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.processors.ProcessorException;
import ambit2.db.StatementExecutor;
import ambit2.db.exceptions.DbAmbitException;

public class QueryExecutor<Q extends IQueryObject> extends StatementExecutor<Q,ResultSet> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5821244671560506456L;
	protected PreparedStatement sresults=null;
	protected Statement statement=null;
	protected int maxRecords = 0;
	//ResultSet.TYPE_FORWARD_ONLY, ResultSet.TYPE_SCROLL_INSENSITIVE
	protected int resultType = ResultSet.TYPE_SCROLL_INSENSITIVE;
	public int getResultType() {
		return resultType;
	}

	public void setResultType(int resultType) {
		this.resultType = resultType;
	}

	public int getResultTypeConcurency() {
		return resultTypeConcurency;
	}

	public void setResultTypeConcurency(int resultTypeConcurency) {
		this.resultTypeConcurency = resultTypeConcurency;
	}
	//one of ResultSet.CONCUR_READ_ONLY or ResultSet.CONCUR_UPDATABLE
	protected int resultTypeConcurency = ResultSet.CONCUR_READ_ONLY;

	public int getMaxRecords() {
		return maxRecords;
	}

	public void setMaxRecords(int maxRecords) {
		this.maxRecords = maxRecords;
	}

	public void open() throws DbAmbitException {
	}

	public ResultSet process(Q target) throws AmbitException {
		Connection c = getConnection();		
		if (c == null) throw new AmbitException("no connection");
		try {
				List<QueryParam> params = target.getParameters();
				if (params == null) {
					statement = c.createStatement(getResultType(),getResultTypeConcurency());
					String sql = target.getSQL();
					if (maxRecords > 0)
						sql = sql + " limit " + Integer.toString(maxRecords);
					ResultSet rs = statement.executeQuery(sql);
					return rs;
				} else {
					String sql = target.getSQL();
					if (maxRecords > 0)
						sql = sql + " limit " + Integer.toString(maxRecords);					
					sresults = c.prepareStatement(sql,getResultType(),getResultTypeConcurency());					
					QueryExecutor.setParameters(sresults, params);
					logger.debug(sresults);
					ResultSet rs = sresults.executeQuery();
					return rs;
				}
		} catch (Exception x) {
			x.printStackTrace();
			throw new ProcessorException(this,x);
		}
	}
	@Override
	protected ResultSet execute(Connection c,Q target) throws SQLException, AmbitException {
		String sql = getSQL(target); 
		List<QueryParam> params = target.getParameters();		
		if (params == null) {
			statement = c.createStatement();
			ResultSet rs = statement.executeQuery(sql);
			return rs;
		} else {
			sresults = c.prepareStatement(sql);					
			setParameters(sresults, params);
			logger.debug(sresults);
			ResultSet rs = sresults.executeQuery();
			return rs;
		}
	}
	protected String getSQL(Q target) throws AmbitException {
		String sql = target.getSQL();
		if (maxRecords > 0)
			sql = sql + " limit " + Integer.toString(maxRecords);
		return sql;
	}
	@Override
	public void closeResults(ResultSet rs) throws SQLException {
		if (rs != null) rs.close();
		if (sresults != null) sresults.close(); sresults = null;
		if (statement != null) statement.close();statement = null;		
	}

}
