package ambit2.db.search.test;

import java.util.List;

import junit.framework.TestCase;
import ambit2.db.search.QueryParam;
import ambit2.db.search.QueryStructure;
import ambit2.db.search.StringCondition;

public class QueryStructureTest extends TestCase {
	public void test() throws Exception {
		QueryStructure qf = new QueryStructure();
		qf.setFieldname("smiles");
		qf.setValue("CC");
		qf.setCondition(StringCondition.getInstance("="));
		qf.setId(1);
		assertEquals(QueryStructure.sqlSMILES + qf.getFieldname() + " " + qf.getCondition() + " ?",qf.getSQL());
		List<QueryParam> params = qf.getParameters();
		assertNotNull(params);
		assertEquals(2,params.size());
		assertEquals(Integer.class,params.get(0).getType());
		assertEquals(String.class,params.get(1).getType());
		assertEquals(1,params.get(0).getValue());
		assertEquals("CC",params.get(1).getValue());
	
	}
}
