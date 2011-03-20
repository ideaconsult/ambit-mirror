package ambit2.db.search.test;

import java.sql.ResultSet;

import junit.framework.Assert;
import ambit2.db.search.property.RetrieveFieldNamesByType;

public class RetrieveFieldNamesByTypeTest  extends QueryTest<RetrieveFieldNamesByType>  {

	@Override
	public void setUp() throws Exception {
		super.setUp();
		setDbFile("src/test/resources/ambit2/db/processors/test/dataset-properties.xml");			
	}
	@Override
	protected RetrieveFieldNamesByType createQuery() throws Exception {
		RetrieveFieldNamesByType r = new RetrieveFieldNamesByType(false);
		return r;
	}

	@Override
	protected void verify(RetrieveFieldNamesByType query, ResultSet rs)
			throws Exception {
		int count = 0;
		while (rs.next()) {
			count++;
		}
		Assert.assertEquals(3,count);		
	}

}
