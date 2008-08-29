package ambit2.db.search.test;

import java.sql.ResultSet;

import ambit2.core.data.LiteratureEntry;
import ambit2.core.external.DescriptorMopacShell;
import ambit2.db.search.QueryMissingDescriptor;

public class QueryMissingDescriptorTest extends QueryTest<QueryMissingDescriptor> {
	public void test() throws Exception {
		assertEquals(
		"select ? as idquery,idchemical,idstructure,1 as selected,1 as metrics from structure where idstructure not in (select idstructure from dvalues join descriptors using(iddescriptor) join catalog_references using(idreference) where name=? and title=?)",
		query.getSQL());
	}
	@Override
	protected QueryMissingDescriptor createQuery() throws Exception {
		DescriptorMopacShell mopac = new DescriptorMopacShell();
		QueryMissingDescriptor q = new QueryMissingDescriptor();
		q.setValue(DescriptorMopacShell.EHOMO);
		q.setFieldname(new LiteratureEntry(mopac.getSpecification().getImplementationIdentifier()));
		return q;
	}

	@Override
	protected void verify(QueryMissingDescriptor query, ResultSet rs) throws Exception {
		int count = 0;
		while (rs.next()) {
			count++;
		}
		assertTrue(count>0);
		
	}
}
