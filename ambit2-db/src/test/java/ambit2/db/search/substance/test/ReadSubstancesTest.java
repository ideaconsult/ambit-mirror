package ambit2.db.search.substance.test;

import java.sql.ResultSet;

import ambit2.base.data.SubstanceRecord;
import ambit2.db.search.test.QueryTest;
import ambit2.db.substance.ReadSubstances;
import junit.framework.Assert;

public class ReadSubstancesTest extends QueryTest<ReadSubstances> {

	@Override
	protected ReadSubstances createQuery() throws Exception {
		ReadSubstances q = new ReadSubstances();
		q.setValue(new String[] {"IUC4-efdb21bb-e79f-3286-a988-b6f6944d3734","IUC4-efdb21bb-e79f-3286-a988-b6f6944d3734"});
		return q;
	}

	@Override
	protected void verify(ReadSubstances query, ResultSet rs) throws Exception {
		int count = 0;
		while (rs.next()) {
			SubstanceRecord record =  query.getObject(rs);
			Assert.assertEquals(1,record.getIdsubstance());
			Assert.assertEquals("testname",record.getSubstanceName());
			Assert.assertEquals("publictestname",record.getPublicName());
			Assert.assertEquals("i5._4.",record.getFormat());
			Assert.assertEquals("IUC4-efdb21bb-e79f-3286-a988-b6f6944d3734",record.getSubstanceUUID());
			Assert.assertEquals("test",record.getContent());
			Assert.assertEquals("Monoconstituent",record.getSubstancetype());
			Assert.assertNotNull(record.getOwnerUUID());
			count++;
		}
		Assert.assertEquals(1,count);
	}

}