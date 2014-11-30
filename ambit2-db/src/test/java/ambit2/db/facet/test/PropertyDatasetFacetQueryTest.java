package ambit2.db.facet.test;

import java.sql.ResultSet;
import java.util.Hashtable;

import junit.framework.Assert;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.facet.IFacet;
import ambit2.base.data.Property;
import ambit2.base.data.SourceDataset;
import ambit2.db.facets.propertyvalue.PropertyDatasetFacet;
import ambit2.db.facets.propertyvalue.PropertyDatasetFacetQuery;
import ambit2.db.search.test.QueryTest;

/**
 * Test for {@link PropertyDatasetFacetQuery}
 * @author nina
 *
 */
public class PropertyDatasetFacetQueryTest extends QueryTest<IQueryRetrieval<PropertyDatasetFacet<Property,SourceDataset>>> {
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		setDbFile("src/test/resources/ambit2/db/processors/test/query-datasets-string.xml");
	}
	@Override
	protected IQueryRetrieval<PropertyDatasetFacet<Property, SourceDataset>> createQuery()
			throws Exception {
		PropertyDatasetFacetQuery q = new PropertyDatasetFacetQuery(null);
		Property p = new Property("");
		p.setId(48);
		q.setFieldname(p);
		SourceDataset d = new SourceDataset();
		d.setId(1);
		q.setValue(d);
		return q;
	}
	
	
	/**
	 */

	@Override
	protected void verify(
			IQueryRetrieval<PropertyDatasetFacet<Property, SourceDataset>> query,
			ResultSet rs) throws Exception {
		Hashtable<String, Integer> expected = new Hashtable<String, Integer>();
		expected.put("Extreme", 1);
		expected.put("Moderate", 6);
		expected.put("Non", 2);
		expected.put("Strong", 1);
		expected.put("Weak", 5);
		int count = 0;
		while (rs.next()) {
			IFacet<String> record = query.getObject(rs);
			Assert.assertEquals(expected.get(record.getValue()).intValue(),record.getCount());
			count++;
		}
		Assert.assertEquals(5,count);
		
	}

}
