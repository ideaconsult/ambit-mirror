package ambit2.db.search.test;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import ambit2.base.data.Property;
import ambit2.db.search.structure.QueryFieldMultiple;

public class QueryFieldMultipleTest extends  QueryTest<QueryFieldMultiple>  {
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		setDbFile("src/test/resources/ambit2/db/processors/test/query-datasets-string.xml");
	}
	@Test
	public void test() throws Exception {
	
	}


	@Override
	protected QueryFieldMultiple createQuery() throws Exception {
		QueryFieldMultiple qf = new QueryFieldMultiple();
		qf.setFieldname(Property.getNameInstance());
		qf.setSearchByAlias(true);
		List<String> values = new ArrayList<String>();
		values.add("abietic acid");
		values.add("2-acetylcyclohexanone");
		qf.setValue(values);
		qf.setId(1);
		return qf;
	}

	@Override
	protected void verify(QueryFieldMultiple query, ResultSet rs) throws Exception {
		int count = 0;
		while (rs.next()) {
			Assert.assertEquals(query.getId().intValue(),rs.getInt(1));
			//Assert.assertEquals(1,rs.getInt(2));
			//Assert.assertEquals(1,rs.getInt(3));
			count++;
			//assertEquals(query.getValue(),rs.getInt(2));
		}
		Assert.assertEquals(2,count);
	}
}
