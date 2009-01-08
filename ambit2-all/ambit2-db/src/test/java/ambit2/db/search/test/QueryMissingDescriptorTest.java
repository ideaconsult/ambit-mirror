package ambit2.db.search.test;

import java.sql.ResultSet;

import junit.framework.Assert;

import org.openscience.cdk.qsar.descriptors.molecular.XLogPDescriptor;

import ambit2.core.data.LiteratureEntry;
import ambit2.db.search.QueryMissingDescriptor;

public class QueryMissingDescriptorTest extends QueryTest<QueryMissingDescriptor> {
	public void test() throws Exception {
		Assert.assertEquals(
		"select ? as idquery,idchemical,idstructure,1 as selected,1 as metrics from structure where idstructure not in (select idstructure from dvalues join descriptors using(iddescriptor) join catalog_references using(idreference) where name=? and title=?)",
		query.getSQL());
	}
	@Override
	protected QueryMissingDescriptor createQuery() throws Exception {
		XLogPDescriptor d = new XLogPDescriptor();
		QueryMissingDescriptor q = new QueryMissingDescriptor();
		q.setValue("XLogP");
		q.setFieldname(new LiteratureEntry(d.getSpecification().getImplementationIdentifier()));
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
