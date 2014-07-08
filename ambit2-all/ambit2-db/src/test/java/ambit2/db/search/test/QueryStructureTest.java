package ambit2.db.search.test;

import java.sql.ResultSet;
import java.util.List;

import junit.framework.Assert;
import net.idea.modbcum.i.query.QueryParam;

import org.junit.Test;

import ambit2.core.processors.structure.key.ExactStructureSearchMode;
import ambit2.db.search.StringCondition;
import ambit2.db.search.structure.QueryStructure;

public class QueryStructureTest extends QueryTest<QueryStructure>{
	@Test
	public void test() throws Exception {
		QueryStructure qf = new QueryStructure();
		qf.setFieldname(ExactStructureSearchMode.smiles);
		qf.setValue("CC");
		qf.setCondition(StringCondition.getInstance("="));
		qf.setId(1);
		//Assert.assertEquals(String.format(QueryStructure.sqlSMILES, "", qf.getFieldname(),qf.getCondition(),qf.getFieldname(),qf.getCondition(),"")
			//	,qf.getSQL());
		List<QueryParam> params = qf.getParameters();
		Assert.assertNotNull(params);
		Assert.assertEquals(3,params.size());
		Assert.assertEquals(Integer.class,params.get(0).getType());
		Assert.assertEquals(String.class,params.get(1).getType());
		Assert.assertEquals(1,params.get(0).getValue());
		Assert.assertEquals("CC",params.get(1).getValue());
	}

	@Override
	protected QueryStructure createQuery() throws Exception {
		QueryStructure qf = new QueryStructure();
		qf.setFieldname(ExactStructureSearchMode.smiles);
		qf.setValue("F.[F-].[Na+]");
		//qf.setValue("[F-].F.[Na+]");
		qf.setCondition(StringCondition.getInstance("="));
		return qf;
	}

	@Override
	protected void verify(QueryStructure query, ResultSet rs) throws Exception {

			int records = 0;
			while (rs.next()) {
				records ++;
				Assert.assertEquals(query.getId().intValue(),rs.getInt(1));
				Assert.assertEquals(10,rs.getInt(2));
				Assert.assertEquals(100214,rs.getInt(3));
				Assert.assertEquals(1,rs.getInt(4));
				Assert.assertEquals(9999,rs.getInt(5));			

			}
			Assert.assertEquals(1,records);
		}
}
