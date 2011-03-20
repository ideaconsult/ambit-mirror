package ambit2.db.search.test;

import java.sql.ResultSet;

import junit.framework.Assert;

import org.junit.Test;

import ambit2.db.search.StoredQuery;
import ambit2.db.search.property.QueryMetricRange;

public class QueryMetricRangeTest extends QueryTest<QueryMetricRange> {

	@Override
	public void setUp() throws Exception {
		super.setUp();
		setDbFile("src/test/resources/ambit2/db/processors/test/query-datasets.xml");
	}
	@Override
	protected QueryMetricRange createQuery() throws Exception {
		QueryMetricRange q = new QueryMetricRange();
		q.setFieldname(new StoredQuery(4));
		q.setValue(10);
		return q;
	}
	
	@Test
	public void testSql() throws Exception {
		String expected = 
			"select m1+d*(m2-m1)/? as a, m1+(d+1)*(m2-m1)/? as b from\n"+
			"(select min(metric) as m1,max(metric) as m2 from query_results where idquery=?) as L\n"+
			"join (\n"+
			"select 0 as d\n"+
			"union select 1 as d\n"+
			"union select 2 as d\n"+
			"union select 3 as d\n"+
			"union select 4 as d\n"+
			"union select 5 as d\n"+
			"union select 6 as d\n"+
			"union select 7 as d\n"+
			"union select 8 as d\n"+
			"union select 9 as d\n"+
			") as M\t\n";
		Assert.assertEquals(expected,query.getSQL());
	}

	@Override
	protected void verify(QueryMetricRange query, ResultSet rs)
			throws Exception {
		int count = 0;
		while (rs.next()) {
			count++;
		}
		Assert.assertEquals(query.getValue().intValue(),count);
		
	}
}
