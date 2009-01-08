package ambit2.db.search.test;

import java.sql.ResultSet;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.junit.Before;
import org.junit.Test;

import ambit2.db.processors.test.DbUnitTest;
import ambit2.db.search.IQueryObject;
import ambit2.db.search.QueryExecutor;


public abstract class QueryTest<T extends IQueryObject> extends DbUnitTest {
	protected T query;
	protected QueryExecutor<T> executor;
	protected String dbFile = "src/test/resources/ambit2/db/processors/test/descriptors-datasets.xml";	
	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		query = createQuery();
		query.setId(999);
		executor = new QueryExecutor<T>();
	}
	
	@Test
	public void testSelect() throws Exception {
		setUpDatabase(dbFile);
		IDatabaseConnection c = getConnection();
		executor.setConnection(c.getConnection());
		executor.open();
		ResultSet rs = executor.process(query); 
		Assert.assertNotNull(rs);
		verify(query,rs);
		rs.close();
		c.close();
	}
	protected abstract T createQuery() throws Exception;
	protected abstract void verify(T query, ResultSet rs) throws Exception ;
}
