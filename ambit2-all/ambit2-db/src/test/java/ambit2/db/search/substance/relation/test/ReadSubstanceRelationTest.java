package ambit2.db.search.substance.relation.test;

import java.sql.ResultSet;

import junit.framework.Assert;
import ambit2.base.data.Property;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.relation.STRUCTURE_RELATION;
import ambit2.db.chemrelation.ReadStructureRelation;
import ambit2.db.search.test.QueryTest;
import ambit2.db.substance.relation.ReadSubstanceRelation;

/**
 * Test for {@link ReadStructureRelation}
 * @author nina
 *
 */
public class ReadSubstanceRelationTest extends QueryTest<ReadSubstanceRelation> {

	@Override
	public void setUp() throws Exception {

		setDbFile("src/test/resources/ambit2/db/processors/test/descriptors-datasets.xml");
		super.setUp();
	}
	@Override
	protected ReadSubstanceRelation createQuery() throws Exception {
		ReadSubstanceRelation query = new ReadSubstanceRelation(STRUCTURE_RELATION.HAS_ADDITIVE,
				new SubstanceRecord("IUC4-efdb21bb-e79f-3286-a988-b6f6944d3734"));
		return query;
	}

	@Override
	protected void verify(ReadSubstanceRelation query, ResultSet rs)
			throws Exception {
		int count = 0;
		while (rs.next()) {
			IStructureRecord record = query.getObject(rs);
			Assert.assertEquals(11,record.getIdchemical());
			for (Property p : record.getProperties()) {
				//Assert.assertEquals("metric",p.getName());
				//Assert.assertEquals(new Float(10.0),record.getProperty(p));
			}
			count++;
		}
		Assert.assertEquals(1,count);
		
	}

}
