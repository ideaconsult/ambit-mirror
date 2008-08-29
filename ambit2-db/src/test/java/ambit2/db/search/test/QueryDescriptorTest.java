package ambit2.db.search.test;

import java.util.List;

import junit.framework.TestCase;
import ambit2.db.search.NumberCondition;
import ambit2.db.search.QueryDescriptor;
import ambit2.db.search.QueryParam;

public class QueryDescriptorTest extends TestCase {
	public void test() throws Exception {
		QueryDescriptor qf = new QueryDescriptor();
		qf.setFieldname("name");
		qf.setValue(3.0);
		qf.setMaxValue(20.2);
		qf.setCondition(NumberCondition.getInstance("between"));
		qf.setId(1);
		assertEquals(QueryDescriptor.sqlField  +  qf.getCondition() + " ? and ?", qf.getSQL());
		List<QueryParam> params = qf.getParameters();
		assertNotNull(params);
		assertEquals(4,params.size());
		assertEquals(Integer.class,params.get(0).getType());
		assertEquals(String.class,params.get(1).getType());
		assertEquals(Double.class,params.get(2).getType());
		assertEquals(Double.class,params.get(2).getType());
		assertEquals(1,params.get(0).getValue());
		assertEquals("name",params.get(1).getValue());
		assertEquals(3.0,params.get(2).getValue());
		assertEquals(20.2,params.get(3).getValue());
		
		qf.setCondition(NumberCondition.getInstance("="));
		assertEquals(QueryDescriptor.sqlField  +  qf.getCondition() + " ?", qf.getSQL());
		params = qf.getParameters();
		assertNotNull(params);
		assertEquals(3,params.size());		
	}
}
