package ambit2.db.search.test;

import java.sql.ResultSet;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.qsar.descriptors.molecular.XLogPDescriptor;

import ambit2.base.data.LiteratureEntry;
import ambit2.db.search.structure.QueryMissingDescriptor;

public class QueryMissingDescriptorTest extends QueryTest<QueryMissingDescriptor> {
	@Test
	public void test() throws Exception {
		Assert.assertEquals(
		QueryMissingDescriptor.MISSING_DESCRIPTOR,
		query.getSQL());
	}
	@Override
	protected QueryMissingDescriptor createQuery() throws Exception {
		XLogPDescriptor d = new XLogPDescriptor();
		QueryMissingDescriptor q = new QueryMissingDescriptor();
		q.setValue("XLogP");
		q.setFieldname(LiteratureEntry.getInstance(d.getSpecification().getImplementationIdentifier()));
		return q;
	}

	@Override
	protected void verify(QueryMissingDescriptor query, ResultSet rs) throws Exception {
		int count = 0;
		while (rs.next()) {
			count++;
		}
		Assert.assertEquals(4,count);
		
	}
}
