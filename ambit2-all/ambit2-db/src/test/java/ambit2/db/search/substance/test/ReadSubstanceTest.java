package ambit2.db.search.substance.test;

import java.sql.ResultSet;

import junit.framework.Assert;
import ambit2.base.data.SubstanceRecord;
import ambit2.db.search.test.QueryTest;
import ambit2.db.substance.ReadSubstance;

public class ReadSubstanceTest extends QueryTest<ReadSubstance> {

	@Override
	protected ReadSubstance createQuery() throws Exception {
		ReadSubstance q = new ReadSubstance();
		q.setValue(new SubstanceRecord(1));
		return q;
	}

	@Override
	protected void verify(ReadSubstance query, ResultSet rs) throws Exception {
		int count = 0;
		while (rs.next()) {
			SubstanceRecord record = query.getObject(rs);
			Assert.assertEquals(1,record.getIdsubstance());
			Assert.assertEquals("testname",record.getCompanyName());
			Assert.assertEquals("publictestname",record.getPublicName());
			Assert.assertEquals("i5._4.",record.getFormat());
			Assert.assertEquals("IUC4-efdb21bb-e79f-3286-a988-b6f6944d3734",record.getCompanyUUID());
			Assert.assertEquals("test",record.getContent());
			Assert.assertEquals("Monoconstituent",record.getSubstancetype());
			Assert.assertNotNull(record.getOwnerUUID());
			count++;
		}
		Assert.assertEquals(1,count);
		
	}

}
