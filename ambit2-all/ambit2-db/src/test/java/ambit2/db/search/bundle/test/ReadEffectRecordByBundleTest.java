package ambit2.db.search.bundle.test;

import java.sql.ResultSet;

import junit.framework.Assert;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.ProtocolEffectRecord;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.db.search.test.QueryTest;
import ambit2.db.update.bundle.effects.ReadEffectRecordByBundle;

public class ReadEffectRecordByBundleTest extends
		QueryTest<ReadEffectRecordByBundle> {

	@Override
	protected ReadEffectRecordByBundle createQuery() throws Exception {
		SubstanceEndpointsBundle bundle = new SubstanceEndpointsBundle();
		bundle.setID(1);
		ReadEffectRecordByBundle q = new ReadEffectRecordByBundle(bundle);
		SubstanceRecord record = new SubstanceRecord();
		record.setSubstanceUUID("IUC4-efdb21bb-e79f-3286-a988-b6f6944d3734");
		q.setFieldname(record);
		return q;
	}

	@Override
	protected void verify(ReadEffectRecordByBundle query, ResultSet rs)
			throws Exception {
		int count = 0;
		while (rs.next()) {
			ProtocolEffectRecord<String, String, String> record = query
					.getObject(rs);
			Assert.assertNotNull(record.getEndpoint());

			count++;
		}
		Assert.assertEquals(3, count);

	}

}
