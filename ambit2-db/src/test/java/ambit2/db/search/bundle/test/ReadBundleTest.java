package ambit2.db.search.bundle.test;

import java.sql.ResultSet;

import junit.framework.Assert;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.db.search.test.QueryTest;
import ambit2.db.update.bundle.ReadBundle;

public class ReadBundleTest extends QueryTest<ReadBundle> {

	@Override
	protected ReadBundle createQuery() throws Exception {
		ReadBundle q = new ReadBundle();
		SubstanceEndpointsBundle b = new SubstanceEndpointsBundle();
		b.setID(1);
		q.setValue(b);
		return q;
	}

	@Override
	protected void verify(ReadBundle query, ResultSet rs) throws Exception {
		int count = 0;
		while (rs.next()) {
			SubstanceEndpointsBundle record = query.getObject(rs);
			Assert.assertEquals(1,record.getID());
			Assert.assertEquals("Assessment",record.getName());
			Assert.assertEquals("http://creativecommons.org/publicdomain/zero/1.0/",record.getLicenseURI());
			count++;
		}
		Assert.assertEquals(1,count);
		
	}

}
