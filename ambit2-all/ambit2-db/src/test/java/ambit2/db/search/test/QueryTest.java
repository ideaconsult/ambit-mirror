package ambit2.db.search.test;

import java.sql.Connection;
import java.sql.ResultSet;

import ambit2.db.search.IQueryObject;
import ambit2.db.search.QueryExecutor;


public abstract class QueryTest<T extends IQueryObject> extends ambit2.db.test.RepositoryTest {
	protected T query;
	protected QueryExecutor<T> executor;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		query = createQuery();
		query.setId(999);
		executor = new QueryExecutor<T>();
	}
	public void testSelect() throws Exception {
		Connection c = datasource.getConnection();
		executor.setConnection(c);
		executor.open();
		ResultSet rs = executor.process(query); 
		assertNotNull(rs);
		verify(query,rs);
		rs.close();
	}
	protected abstract T createQuery() throws Exception;
	protected abstract void verify(T query, ResultSet rs) throws Exception ;
}
