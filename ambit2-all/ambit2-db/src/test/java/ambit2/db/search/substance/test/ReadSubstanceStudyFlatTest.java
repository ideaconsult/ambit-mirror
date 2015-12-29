package ambit2.db.search.substance.test;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.ProtocolApplication;
import ambit2.db.search.test.QueryTest;
import ambit2.db.substance.study.ReadSubstanceStudyFlat;

public class ReadSubstanceStudyFlatTest extends QueryTest<ReadSubstanceStudyFlat> {

	@Override
	protected ReadSubstanceStudyFlat createQuery() throws Exception {
		ReadSubstanceStudyFlat q = new ReadSubstanceStudyFlat();
		SubstanceRecord r = new SubstanceRecord();
		r.setSubstanceUUID("IUC4-EFDB21BBE79F3286A988B6F6944D3734");
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
			Assert.assertEquals(measurements.size(),count);
			//System.out.println(measurements);
			Assert.assertNotNull(measurements.get(count-1).getEffects());
			Assert.assertEquals(1,measurements.get(count-1).getEffects().size());
		}
		Assert.assertEquals(4, count);
	}

}
