package ambit2.db.search.test;

import java.sql.ResultSet;
import java.util.List;

import ambit2.core.data.StructureRecord;
import ambit2.db.search.NumberCondition;
import ambit2.db.search.QueryParam;
import ambit2.db.search.QueryStructureByID;

public class QueryStructureByIDTest extends QueryTest<QueryStructureByID> {
	public void test() throws Exception {
		QueryStructureByID qf = new QueryStructureByID();
		qf.setValue(new StructureRecord(-1,2,"",""));
		qf.setCondition(NumberCondition.getInstance("<="));
		qf.setId(1);
		

		assertEquals(QueryStructureByID.sqlField + qf.getCondition() + " ?", qf.getSQL());
		List<QueryParam> params = qf.getParameters();
		assertNotNull(params);
		assertEquals(2,params.size());
		assertEquals(Integer.class,params.get(0).getType());
		assertEquals(Integer.class,params.get(1).getType());
		assertEquals(1,params.get(0).getValue());
		assertEquals(2,params.get(1).getValue());
	}
	@Override
	protected QueryStructureByID createQuery() throws Exception {
		return new QueryStructureByID(2);
	}
	@Override
	protected void verify(QueryStructureByID query, ResultSet rs) throws Exception {
		System.out.println(query.getSQL());

		while (rs.next()) {
			assertEquals(query.getId().intValue(),rs.getInt(1));
			assertEquals(query.getValue().getIdstructure(),rs.getInt(2));
		}
		
	}
}
