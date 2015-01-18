package ambit2.db.search.bundle.test;

import java.sql.ResultSet;

import junit.framework.Assert;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.db.facets.bundle.BundleSummaryFacet;
import ambit2.db.facets.bundle.BundleSummaryQuery;
import ambit2.db.search.test.QueryTest;

public class ReadBundleSummaryTest  extends QueryTest<BundleSummaryQuery> {

	@Override
	protected BundleSummaryQuery createQuery() throws Exception {
		BundleSummaryQuery q = new BundleSummaryQuery("");
		SubstanceEndpointsBundle b = new SubstanceEndpointsBundle();
		b.setID(1);
		q.setFieldname(b);
		return q;
	}

	@Override
	protected void verify(BundleSummaryQuery query, ResultSet rs) throws Exception {
		int count = 0;
		while (rs.next()) {
			BundleSummaryFacet record = query.getObject(rs);
			Assert.assertNotNull(record.getURL());
			count++;
		}
		Assert.assertEquals(5,count);
		
	}

}
