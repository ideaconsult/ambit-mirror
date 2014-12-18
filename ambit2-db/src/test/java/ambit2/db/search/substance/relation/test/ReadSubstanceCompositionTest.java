package ambit2.db.search.substance.relation.test;

import java.sql.ResultSet;

import junit.framework.Assert;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.relation.STRUCTURE_RELATION;
import ambit2.base.relation.composition.CompositionRelation;
import ambit2.db.search.test.QueryTest;
import ambit2.db.substance.relation.ReadSubstanceComposition;

public class ReadSubstanceCompositionTest extends QueryTest<ReadSubstanceComposition> {

	@Override
	public void setUp() throws Exception {

		setDbFile("src/test/resources/ambit2/db/processors/test/descriptors-datasets.xml");
		super.setUp();
	}
	@Override
	protected ReadSubstanceComposition createQuery() throws Exception {
		ReadSubstanceComposition query = new ReadSubstanceComposition();
		query.setFieldname(new SubstanceRecord(1));
		return query;
	}

	@Override
	protected void verify(ReadSubstanceComposition query, ResultSet rs)
			throws Exception {
		int count = 0;
		while (rs.next()) {
			CompositionRelation record = query.getObject(rs);
			Assert.assertEquals(1,record.getFirstStructure().getIdsubstance());
			Assert.assertEquals(11,record.getSecondStructure().getIdchemical());
			Assert.assertEquals(STRUCTURE_RELATION.HAS_ADDITIVE,record.getRelationType());
			Assert.assertEquals(10.0,record.getRelation().getTypical_value());
			Assert.assertEquals("=",record.getRelation().getTypical());
			Assert.assertEquals("%",record.getRelation().getTypical_unit());
			Assert.assertNotNull(record.getCompositionUUID());
			Assert.assertEquals("composition name 12345",record.getName());
			count++;
		}
		Assert.assertEquals(1,count);
		
	}

}