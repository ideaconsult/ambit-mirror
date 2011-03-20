package ambit2.db.search.test;

import java.sql.ResultSet;

import junit.framework.Assert;
import ambit2.base.data.Property;
import ambit2.base.data.PropertyStats;
import ambit2.base.data.Range;
import ambit2.db.search.StoredQuery;
import ambit2.db.search.property.FilteredCountNumeric;

public class FilteredCountNumericTest extends QueryTest<FilteredCountNumeric> {

	@Override
	public void setUp() throws Exception {
		super.setUp();
		setDbFile("src/test/resources/ambit2/db/processors/test/query-datasets.xml");
	}
	@Override
	protected FilteredCountNumeric createQuery() throws Exception {
		FilteredCountNumeric q = new FilteredCountNumeric();
		q.setQuery(new StoredQuery(5));
		q.setProperty(Property.getInstance("LLNA EC3","Skinsens_dataset.sdf"));
		q.setMetric(new Range<Double>(0.75,1.0));
		q.setValue(new Range<Double>(0.0,5.0));
		return q;
	}

	@Override
	protected void verify(FilteredCountNumeric query, ResultSet rs)
			throws Exception {
		while (rs.next()) {
			Assert.assertEquals(3L,((PropertyStats<Double>)query.getObject(rs)).getCount());
		}
		
	}

}
