package ambit2.db.search.dataset.test;

import java.sql.ResultSet;

import junit.framework.Assert;
import net.idea.modbcum.i.IQueryRetrieval;
import ambit2.base.data.Property;
import ambit2.base.data.SourceDataset;
import ambit2.db.search.property.PropertiesByDataset;
import ambit2.db.search.test.QueryTest;

public class PropertiesByDatasetTest extends QueryTest<IQueryRetrieval<Property>> {

	@Override
	public void setUp() throws Exception {
		super.setUp();
		setDbFile("src/test/resources/ambit2/db/processors/test/src-datasets.xml");
	}
	@Override
	protected IQueryRetrieval<Property> createQuery() throws Exception {
		PropertiesByDataset q = new PropertiesByDataset();
		q.setValue(new SourceDataset("Dataset 1"));
		return q;
	}

	@Override
	protected void verify(IQueryRetrieval<Property> query, ResultSet rs) throws Exception {
		int count = 0;
		while (rs.next()) {
			Property record = query.getObject(rs);
			count++;
		}
		Assert.assertEquals(0,count);
		
	}

}
