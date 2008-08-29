package ambit2.db.search.test;

import java.sql.ResultSet;

import ambit2.core.exceptions.AmbitException;
import ambit2.db.search.IQueryObject;

public class QueryAquireTest extends QueryTest {

	@Override
	protected IQueryObject createQuery() throws Exception {
		throw new AmbitException("Not implemented");
	}

	@Override
	protected void verify(IQueryObject query, ResultSet rs) throws Exception {
		throw new AmbitException("Not implemented");
		
	}

}
