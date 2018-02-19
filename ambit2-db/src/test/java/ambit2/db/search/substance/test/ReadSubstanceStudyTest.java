package ambit2.db.search.substance.test;

import java.sql.ResultSet;

import ambit2.base.data.I5Utils;
import ambit2.base.data.study.IParams;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.ProtocolApplication;
import ambit2.db.search.test.QueryTest;
import ambit2.db.substance.study.ReadSubstanceStudy;
import junit.framework.Assert;

public class ReadSubstanceStudyTest extends QueryTest<ReadSubstanceStudy> {
	private static final String testSubstance = I5Utils.getPrefixedUUID("IUC4", "EFDB21BBE79F3286A988B6F6944D3734");

	@Override
	protected ReadSubstanceStudy createQuery() throws Exception {
		ReadSubstanceStudy q = new ReadSubstanceStudy();
		q.setFieldname(testSubstance);
		return q;
	}

	@Override
	protected void verify(ReadSubstanceStudy query, ResultSet rs) throws Exception {
		int count = 0;
		while (rs.next()) {
			count++;
			ProtocolApplication<Protocol, String, String, IParams, String> m = query.getObject(rs);
			Assert.assertNotNull(m);
			Assert.assertNull(m.getEffects());

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
			
			
			Assert.assertNotNull(m.getReliability());
			Assert.assertNotNull(m.getReliability().getValue());
			Assert.assertNotNull(m.getReliability().getStudyResultType());
			Assert.assertNotNull(m.getReliability().getPurposeFlag());
		}
		Assert.assertEquals(4, count);

	}

}