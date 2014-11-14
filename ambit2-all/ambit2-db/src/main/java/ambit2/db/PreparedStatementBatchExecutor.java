package ambit2.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.i.query.IQueryUpdate;
import net.idea.modbcum.i.query.QueryParam;

public class PreparedStatementBatchExecutor<Q extends IQueryUpdate> extends StatementExecutor<Q, int[]> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6641899570004098671L;

	public void initBatch(Connection connection) throws DbAmbitException {
		Iterator<PreparedStatement> p = cache.values().iterator();
		while (p.hasNext())
			try {
				PreparedStatement ps = p.next();
				ps.clearParameters();
				ps.clearBatch();
			} catch (Exception x) {
				
			}
		if ((this.connection != null) && (this.connection != connection) && isCloseConnection()) try {
			close();
		} catch (Exception x) {
			logger.log(java.util.logging.Level.SEVERE,x.getMessage(),x);
        }
		this.connection = connection;
	}
	
	public int addBatch(Q query) throws SQLException,AmbitException {
		String[] sqls = query.getSQL();
		int n = 0;
		for (int i=0;i< sqls.length;i++) {
			PreparedStatement ps = getCachedStatement(sqls[i]);
			if (ps==null) {
				
				if (query.returnKeys(i))
					ps = connection.prepareStatement(sqls[i],Statement.RETURN_GENERATED_KEYS);
				else 
					ps = connection.prepareStatement(sqls[i]);

				addStatementToCache(sqls[i],ps);
			}
			
			List<QueryParam> params = query.getParameters(i);
			StatementExecutor.setParameters(ps, params);
			ps.addBatch();
			n++;
		}
		return n;
	}

	@Override
	protected int[] execute(Connection c, Q query) throws SQLException,AmbitException {
		String[] sqls = query.getSQL();
		int[] results = null;
		for (int i=0;i< sqls.length;i++) {
			PreparedStatement ps = getCachedStatement(sqls[i]);
			if (ps!=null) try {
				results = ps.executeBatch();
				//no meaningfull way to use generated keys in batch mode :(
			} catch (Exception x) {logger.log(Level.WARNING,x.getMessage(),x);}
		}
		return results;
	}
}
