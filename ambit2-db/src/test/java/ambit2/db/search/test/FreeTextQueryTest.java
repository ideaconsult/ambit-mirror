package ambit2.db.search.test;

import java.sql.ResultSet;

import junit.framework.Assert;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.structure.FreeTextQuery;

public class FreeTextQueryTest extends QueryTest<FreeTextQuery> {

	@Override
	public void setUp() throws Exception {

		setDbFile("src/test/resources/ambit2/db/processors/test/dataset-properties.xml");
		super.setUp();
	}
	@Override
	protected FreeTextQuery createQuery() throws Exception {
		FreeTextQuery q = new FreeTextQuery();
		q.setFieldname(new String[] {"Names","Property 2"});
		q.setValue(new String[] {"value"});
		return q;
	}

	@Override
	protected void verify(FreeTextQuery query, ResultSet rs) throws Exception {
		while (rs.next()) {
			IStructureRecord record = query.getObject(rs);
			Assert.assertEquals(11,record.getIdchemical());
		}
		
	}

}
