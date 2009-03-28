package ambit2.db.search.test;

import java.sql.ResultSet;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import ambit2.db.search.NumberCondition;
import ambit2.db.search.QueryFieldNumeric;
import ambit2.db.search.QueryParam;

public class QueryDescriptorTest extends QueryTest<QueryFieldNumeric> {
	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		dbFile = "src/test/resources/ambit2/db/processors/test/dataset-properties.xml";
	}
	@Test
	public void test() throws Exception {
		QueryFieldNumeric qf = new QueryFieldNumeric();
		qf.setFieldname("name");
		qf.setValue(3.0);
		qf.setMaxValue(20.2);
		qf.setCondition(NumberCondition.getInstance("between"));
		qf.setId(1);
		System.out.println(qf.getSQL());
		Assert.assertEquals(
				"select ? as idquery,idchemical,idstructure,1 as selected,1 as metric from structure\n"+
				"join property_values using(idstructure) join property_number as f using (idvalue,idtype)\n"+
				"join properties using(idproperty) where\n"+
				"name=? and value between ? and ?",				
				qf.getSQL());
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
		System.out.println(qf.getSQL());
		Assert.assertEquals(
				"select ? as idquery,idchemical,idstructure,1 as selected,1 as metric from structure\n"+
				"join property_values using(idstructure) join property_number as f using (idvalue,idtype)\n"+
				"join properties using(idproperty) where\n"+
				"name=? and value = ?"
				, qf.getSQL());
		params = qf.getParameters();
		Assert.assertNotNull(params);
		Assert.assertEquals(3,params.size());		
	}

	@Override
	protected QueryFieldNumeric createQuery() throws Exception {
		QueryFieldNumeric query = new QueryFieldNumeric();
		query.setFieldname("Property 1");
		query.setValue(11.0);
		query.setMaxValue(12.0);
		query.setCondition(NumberCondition.getInstance("between"));		
		return query;
	}

	@Override
	protected void verify(QueryFieldNumeric query, ResultSet rs) throws Exception {
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
