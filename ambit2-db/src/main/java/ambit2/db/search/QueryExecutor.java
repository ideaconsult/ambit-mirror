package ambit2.db.search;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;

import net.idea.modbcum.i.IQueryObject;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.processors.ProcessorException;
import ambit2.db.IStoredProcStatement;
import ambit2.db.StatementExecutor;

/**
 * Executes arbitrary {@link IQueryObject}
 * 
 * @author nina
 * 
 * @param <Q>
 */
public class QueryExecutor<Q extends IQueryObject> extends
		StatementExecutor<Q, ResultSet> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5821244671560506456L;
	protected PreparedStatement sresults = null;
	protected Statement statement = null;
	protected boolean cache = false;
	// protected String limit = "%s limit %d";
	protected String paged_limit = "%s limit %d,%d";
	protected String LIMIT = "limit";

	public QueryExecutor() {

	}

	public boolean isCache() {
		return cache;
	}

	public void setCache(boolean cache) {
		this.cache = cache;
	}

	// ResultSet.TYPE_FORWARD_ONLY, ResultSet.TYPE_SCROLL_INSENSITIVE
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

	// one of ResultSet.CONCUR_READ_ONLY or ResultSet.CONCUR_UPDATABLE
	protected int resultTypeConcurency = ResultSet.CONCUR_READ_ONLY;

	public void open() throws DbAmbitException {
	}

	public synchronized ResultSet process(Q target) throws AmbitException {

		long now = System.currentTimeMillis();
		Connection c = getConnection();
		if (c == null)
			throw new AmbitException("no connection");

		ResultSet rs = null;
		try {
			List<QueryParam> params = target.getParameters();
			if (params == null) {
				statement = c.createStatement(getResultType(),
						getResultTypeConcurency());
				String sql = getSQL(target);

				rs = statement.executeQuery(sql);

			} else {
				String sql = getSQL(target);
				sresults = getCachedStatement(sql);

				if (sresults == null) {
					if (target instanceof IStoredProcStatement) {
						sresults = c.prepareCall(sql);
						
					} else {
						sresults = c.prepareStatement(sql, getResultType(),getResultTypeConcurency());
						sresults.setFetchDirection(ResultSet.FETCH_FORWARD);
						sresults.setFetchSize(Integer.MIN_VALUE);
					}
					if (cache)
						addStatementToCache(sql, sresults);
				} else {
					sresults.clearParameters();
				}

				QueryExecutor.setParameters(sresults, params);
				//logger.log(Level.FINEST,sql);

				rs = sresults.executeQuery();

			}
		} catch (Exception x) {
			try {
				logger.log(Level.SEVERE,x.getMessage() + " " + sresults);
			} catch (Exception xx) {
			}
			throw new ProcessorException(this, x);
		} catch (Throwable x) {
			throw new ProcessorException(this, x.getMessage());
		} finally {
		}
		return rs;
	}

	@Override
	protected synchronized ResultSet execute(Connection c, Q target)
			throws SQLException, AmbitException {
		String sql = getSQL(target);
		List<QueryParam> params = target.getParameters();
		if (params == null) {
			statement = c.createStatement();
			ResultSet rs = statement.executeQuery(sql);
			return rs;
		} else {
			sresults = c.prepareStatement(sql);
			setParameters(sresults, params);
			ResultSet rs = sresults.executeQuery();
			return rs;
		}
	}

	public String getSQL(Q target) throws AmbitException {
		String sql = target.getSQL();
		if (sql.indexOf(LIMIT) >= 0)
			return sql;

		if (target instanceof IStoredProcStatement)
			return sql;

		int page = target.getPage();
		long psize = target.getPageSize();

		if ((target instanceof IQueryRetrieval)
				&& !((IQueryRetrieval) target).supportsPaging()) {
			page = 0;
			psize = (psize * 100) > 10000 ? 10000 : (psize * 100);
		}

		return (target.getPageSize() > 0 ? String.format(paged_limit, sql,
				page == 0 ? 0 : page * psize, psize) : sql);
	}

	@Override
	public void closeResults(ResultSet rs) throws SQLException {
		if (rs != null)
			rs.close();
		if (sresults != null) {
			if (!cache)
				sresults.close();
			sresults = null;
		}
		if (statement != null)
			statement.close();
		statement = null;
	}

}
