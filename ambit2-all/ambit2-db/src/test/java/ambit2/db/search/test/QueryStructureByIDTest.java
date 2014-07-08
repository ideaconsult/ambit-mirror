package ambit2.db.search.test;

import java.sql.ResultSet;
import java.util.List;

import junit.framework.Assert;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.StructureRecord;
import ambit2.db.search.NumberCondition;
import ambit2.db.search.structure.QueryStructureByID;

public class QueryStructureByIDTest extends QueryTest<QueryStructureByID> {
	public void test() throws Exception {
		QueryStructureByID qf = new QueryStructureByID();
		qf.setValue(new StructureRecord(-1,2,"",""));
		qf.setCondition(NumberCondition.getInstance("<="));
		qf.setId(1);
		

		Assert.assertEquals(QueryStructureByID.sqlField + qf.getCondition() + " ?", qf.getSQL());
		List<QueryParam> params = qf.getParameters();
		Assert.assertNotNull(params);
		Assert.assertEquals(2,params.size());
		Assert.assertEquals(Integer.class,params.get(0).getType());
		Assert.assertEquals(Integer.class,params.get(1).getType());
		Assert.assertEquals(1,params.get(0).getValue());
		Assert.assertEquals(2,params.get(1).getValue());
	}
	@Override
	protected QueryStructureByID createQuery() throws Exception {
		return new QueryStructureByID(100211);
	}
	@Override
	protected void verify(QueryStructureByID query, ResultSet rs) throws Exception {
		int records = 0;
		while (rs.next()) {
			records++;
			Assert.assertEquals(query.getId().intValue(),rs.getInt(1));
			Assert.assertEquals(7,rs.getInt(2));			
			Assert.assertEquals(query.getValue().getIdstructure(),rs.getInt(3));
		}
		Assert.assertEquals(1,records);
		
	}
}
