package ambit2.db.search.substance.test;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import ambit2.base.data.I5Utils;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.study.IParams;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.ProtocolApplication;
import ambit2.db.search.test.QueryTest;
import ambit2.db.substance.study.ReadSubstanceStudyFlat;

public class ReadSubstanceStudyFlatTest extends
		QueryTest<ReadSubstanceStudyFlat> {
	private static final String testSubstance = I5Utils.getPrefixedUUID("IUC4",
			"EFDB21BBE79F3286A988B6F6944D3734");

	@Override
	protected ReadSubstanceStudyFlat createQuery() throws Exception {
		ReadSubstanceStudyFlat q = new ReadSubstanceStudyFlat();
		SubstanceRecord r = new SubstanceRecord();
		r.setSubstanceUUID(testSubstance);
		q.setFieldname(r);
		return q;
	}

	@Override
	protected void verify(ReadSubstanceStudyFlat query, ResultSet rs)
			throws Exception {
		int count = 0;
		query.setRecord(new ArrayList<ProtocolApplication>());
		while (rs.next()) {
			count++;
			List<ProtocolApplication> measurements = query.getObject(rs);
			Assert.assertNotNull(measurements);
			Assert.assertEquals(measurements.size(), count);
			// System.out.println(measurements);
			Assert.assertNotNull(measurements.get(count - 1).getEffects());
			Assert.assertEquals(1, measurements.get(count - 1).getEffects()
					.size());
			for (ProtocolApplication<Protocol, IParams, String, IParams, String> m : measurements) {
				Assert.assertNotNull(m.getSubstanceUUID());
				Assert.assertEquals(testSubstance, m.getSubstanceUUID());
				Assert.assertNotNull(m.getProtocol());
				Assert.assertNotNull(m.getProtocol().getCategory());
				Assert.assertNotNull(m.getProtocol().getTopCategory());
				Assert.assertNotNull(m.getProtocol().getGuideline());
				Assert.assertEquals(1, m.getProtocol().getGuideline().size());
				Assert.assertNotNull(m.getParameters());
				Assert.assertNotNull(m.getReference());
				Assert.assertNotNull(m.getReferenceOwner());
				Assert.assertNotNull(m.getEffects());
				for (EffectRecord<String, IParams, String> effect : m
						.getEffects()) {
					Assert.assertNotNull(effect.getEndpoint());
					Assert.assertNotNull(effect.getLoValue());
					Assert.assertNotNull(effect.getConditions());
				}
			}
		}
		Assert.assertEquals(4, count);
	}

}
