package ambit2.db.search.relation.test;

import java.sql.ResultSet;

import junit.framework.Assert;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.chemrelation.ReadStructureRelation;
import ambit2.db.search.test.QueryTest;

/**
 * Test for {@link ReadStructureRelation}
 * @author nina
 *
 */
public class ReadStructureRelationTest extends QueryTest<ReadStructureRelation> {

	@Override
	public void setUp() throws Exception {

		setDbFile("src/test/resources/ambit2/db/processors/test/descriptors-datasets.xml");
		super.setUp();
	}
	@Override
	protected ReadStructureRelation createQuery() throws Exception {
		ReadStructureRelation query = new ReadStructureRelation("test",7);
		return query;
	}

	@Override
	protected void verify(ReadStructureRelation query, ResultSet rs)
			throws Exception {
		int count = 0;
		while (rs.next()) {
			IStructureRecord record = query.getObject(rs);
			Assert.assertEquals(11,record.getIdchemical());
			for (Property p : record.getProperties()) {
				Assert.assertEquals("metric",p.getName());
				Assert.assertEquals(new Float(999.0),record.getProperty(p));
			}
			count++;
		}
		Assert.assertEquals(1,count);
		
	}

}
