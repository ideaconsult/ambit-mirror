package ambit2.db.search.test;

import java.sql.ResultSet;

import junit.framework.Assert;
import ambit2.base.data.Property;
import ambit2.base.data.PropertyStats;
import ambit2.base.data.Range;
import ambit2.db.search.StoredQuery;
import ambit2.db.search.property.FilteredCountNominal;

/**
 * property_values are not correctly loaded by setup database/dbunit !!!
 * @author nina
 *
 */
public class FilteredCountNominalTest extends QueryTest<FilteredCountNominal> {

	@Override
	public void setUp() throws Exception {
		super.setUp();
		setDbFile("src/test/resources/ambit2/db/processors/test/query-datasets-string.xml");
	}
	@Override
	protected FilteredCountNominal createQuery() throws Exception {
		FilteredCountNominal q = new FilteredCountNominal();
		q.setQuery(new StoredQuery(1));
		q.setProperty(Property.getInstance("LLNA Class","Skinsens_dataset.sdf"));
		q.setMetric(new Range<Double>(0.0,1.0));
		q.setValue(new Range<String>("Weak"));
		return q;
	}

	@Override
	protected void verify(FilteredCountNominal query, ResultSet rs)
			throws Exception {
		while (rs.next()) {
			Assert.assertEquals(3,((PropertyStats<String>)query.getObject(rs)).getCount());
		}
		
	}

}
