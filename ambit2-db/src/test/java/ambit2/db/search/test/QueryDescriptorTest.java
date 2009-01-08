package ambit2.db.search.test;

import java.sql.ResultSet;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import ambit2.db.search.NumberCondition;
import ambit2.db.search.QueryDescriptor;
import ambit2.db.search.QueryParam;

public class QueryDescriptorTest extends QueryTest<QueryDescriptor> {
	@Test
	public void test() throws Exception {
		QueryDescriptor qf = new QueryDescriptor();
		qf.setFieldname("name");
		qf.setValue(3.0);
		qf.setMaxValue(20.2);
		qf.setCondition(NumberCondition.getInstance("between"));
		qf.setId(1);
		Assert.assertEquals(QueryDescriptor.sqlField  +  qf.getCondition() + " ? and ?", qf.getSQL());
		List<QueryParam> params = qf.getParameters();
		Assert.assertNotNull(params);
		Assert.assertEquals(4,params.size());
		Assert.assertEquals(Integer.class,params.get(0).getType());
		Assert.assertEquals(String.class,params.get(1).getType());
		Assert.assertEquals(Double.class,params.get(2).getType());
		Assert.assertEquals(Double.class,params.get(2).getType());
		Assert.assertEquals(1,params.get(0).getValue());
		Assert.assertEquals("name",params.get(1).getValue());
		Assert.assertEquals(3.0,params.get(2).getValue());
		Assert.assertEquals(20.2,params.get(3).getValue());
		
		qf.setCondition(NumberCondition.getInstance("="));
		Assert.assertEquals(QueryDescriptor.sqlField  +  qf.getCondition() + " ?", qf.getSQL());
		params = qf.getParameters();
		Assert.assertNotNull(params);
		Assert.assertEquals(3,params.size());		
	}

	@Override
	protected QueryDescriptor createQuery() throws Exception {
		QueryDescriptor query = new QueryDescriptor();
		query.setFieldname("XLogP");
		query.setValue(1.0);
		query.setMaxValue(2.2);
		query.setCondition(NumberCondition.getInstance("between"));		
		return query;
	}

	@Override
	protected void verify(QueryDescriptor query, ResultSet rs) throws Exception {
		int records = 0;
		while (rs.next()) {
			records ++;
			Assert.assertEquals(query.getId().intValue(),rs.getInt(1));
			Assert.assertEquals(11,rs.getInt(2));
			Assert.assertEquals(100215,rs.getInt(3));
			Assert.assertEquals(1,rs.getInt(4));
			Assert.assertEquals(1,rs.getInt(5));			

		}
		Assert.assertEquals(1,records);
	}
}
