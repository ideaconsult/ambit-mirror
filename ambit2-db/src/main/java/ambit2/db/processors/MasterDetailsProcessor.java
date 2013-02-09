package ambit2.db.processors;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import ambit2.base.exceptions.AmbitException;
import ambit2.db.AbstractDBProcessor;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.readers.IMultiRetrieval;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.IParameterizedQuery;
import ambit2.db.search.IQueryCondition;
import ambit2.db.search.IQueryObject;
import ambit2.db.search.QueryExecutor;

public abstract class MasterDetailsProcessor<Master,Detail,C extends IQueryCondition> extends AbstractDBProcessor<Master, Master> {
	protected IQueryRetrieval<Detail> query;
	protected QueryExecutor<IQueryObject<Detail>> exec;
	
	public MasterDetailsProcessor(IQueryRetrieval<Detail> query) {
		super();
		setQuery(query);
	}	
	public IQueryRetrieval<Detail> getQuery() {
		return query;
	}

	public void setQuery(IQueryRetrieval<Detail> query) {
		this.query = query;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 2086055435347535113L;

	@Override
	public void setConnection(Connection connection) throws DbAmbitException {
		super.setConnection(connection);
		if (exec==null) exec = new QueryExecutor<IQueryObject<Detail>>();
		exec.setCloseConnection(false);
		exec.setConnection(connection);
	}

	protected void configureQuery(Master target,IParameterizedQuery<Master,Detail,C> query) throws AmbitException { 
		query.setFieldname(target);
	}

	@Override
	public Master process(Master target) throws AmbitException {
		
		if (query instanceof IParameterizedQuery) {
	        configureQuery(target,(IParameterizedQuery<Master,Detail,C>)query);
		}
        ResultSet rs = null;
        try { 
        	rs = exec.process(query);
        	if (query instanceof IMultiRetrieval) 
        		return processDetail(target,((IMultiRetrieval<Detail>) query).getMultiObject(rs));
        	else	
        		return selectResult(target, rs);
        } catch (Exception x) {
        	throw new AmbitException(query.getSQL(),x);
        } finally {
	        try {
	            exec.closeResults(rs);
	        } catch (SQLException x) {
	        	logger.log(Level.WARNING,x.getMessage(),x);
	        } 
        }

	}
	protected abstract Master processDetail(Master target,Detail detail) throws Exception;
	
	protected Master selectResult(Master target,ResultSet rs) throws Exception {
    	Master result = target;
    	while (rs.next()) {
    		result = processDetail(target, query.getObject(rs));
    	}
    	return result;
	}

	public void open() throws DbAmbitException {
	}

}