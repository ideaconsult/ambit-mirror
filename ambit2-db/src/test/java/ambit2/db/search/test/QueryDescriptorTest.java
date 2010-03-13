package ambit2.db.search.test;

import java.sql.ResultSet;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import ambit2.base.data.Property;
import ambit2.db.search.NumberCondition;
import ambit2.db.search.QueryParam;
import ambit2.db.search.structure.QueryFieldNumeric;

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
		qf.setFieldname(Property.getInstance("name","ref"));
		qf.setValue(3.0);
		qf.setMaxValue(20.2);
		qf.setCondition(NumberCondition.getInstance("between"));
		qf.setId(1);
		Assert.assertEquals(
				"SELECT ? as idquery,idchemical,idstructure,if(type_structure='NA',0,1) as selected,value_num as metric,null as text FROM properties join property_values using(idproperty) join structure using(idstructure) where value_num is not null and value_num between ?  and ? and name=? \n", 
				qf.getSQL());
		List<QueryParam> params = qf.getParameters();
		Assert.assertNotNull(params);
		Assert.assertEquals(4,params.size());
		for (int i=0; i < 1;i++) {
			Assert.assertEquals(Integer.class,params.get(i*4).getType());
			Assert.assertEquals(Double.class,params.get(i*4+1).getType());
			Assert.assertEquals(Double.class,params.get(i*4+2).getType());
			Assert.assertEquals(String.class,params.get(i*4+3).getType());			
			Assert.assertEquals(1,params.get(i*4).getValue());	
			Assert.assertEquals(3.0,params.get(i*4+1).getValue());
			Assert.assertEquals(20.2,params.get(i*4+2).getValue());			
		}
		
		

		
		qf.setCondition(NumberCondition.getInstance("="));
		Assert.assertEquals(
				"SELECT ? as idquery,idchemical,idstructure,if(type_structure='NA',0,1) as selected,value_num as metric,null as text FROM properties join property_values using(idproperty) join structure using(idstructure) where value_num is not null and value_num = ? and name=?\n",

				qf.getSQL());
		params = qf.getParameters();
		Assert.assertNotNull(params);
		Assert.assertEquals(3,params.size());		
		
		qf.setFieldname(null);
		qf.setCondition(NumberCondition.getInstance(">"));
		Assert.assertEquals(
				"SELECT ? as idquery,idchemical,idstructure,if(type_structure='NA',0,1) as selected,value_num as metric,null as text FROM properties join property_values using(idproperty) join structure using(idstructure) where value_num is not null and value_num > ?   \n"
				, qf.getSQL());
		params = qf.getParameters();
		Assert.assertNotNull(params);
		Assert.assertEquals(2,params.size());		
	}

	@Override
	protected QueryFieldNumeric createQuery() throws Exception {
		QueryFieldNumeric query = new QueryFieldNumeric();
		query.setFieldname(Property.getInstance("Property 1","ref"));
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
			Assert.assertEquals(0,rs.getInt(4));
			Assert.assertEquals(12.0,rs.getDouble(5));			

		}
		Assert.assertEquals(1,records);
	}
}
