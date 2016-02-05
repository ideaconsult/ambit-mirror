package ambit2.db.search.substance.test;

import java.sql.ResultSet;

import junit.framework.Assert;
import net.idea.modbcum.i.bucket.Bucket;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.EffectRecord;
import ambit2.db.search.test.QueryTest;
import ambit2.db.substance.study.SubstanceStudyFlatQuery;

public class SubstanceStudyFlatQueryTest extends
		QueryTest<SubstanceStudyFlatQuery> {

	@Override
	protected SubstanceStudyFlatQuery createQuery() throws Exception {
		//String json = "{\"params\" : {\":all\" : { \"type\" : \"boolean\", \"value\": false}, \":s_prefix\" : { \"type\" : \"String\", \"value\": \"IUC4\"},\":s_uuid\" : { \"type\" : \"String\", \"value\":\"EFDB21BBE79F3286A988B6F6944D3734\"}	}}";
		SubstanceRecord record = new SubstanceRecord();
		record.setSubstanceUUID("IUC4-EFDB21BBE79F3286A988B6F6944D3734");
		return new SubstanceStudyFlatQuery(record);
	}

	@Override
	protected void verify(SubstanceStudyFlatQuery query, ResultSet rs)
			throws Exception {
		int count = 0;
		while (rs.next()) {
			Bucket record = query.getObject(rs);
			//System.out.println(record.asJSON());
			for (String header : record.getHeader()) {
				
				if ("owner_name".equals(header))
					continue;
				if ("params".equals(header))
					continue;
				if ("interpretation_result".equals(header))
					continue;
				if ("interpretation_criteria".equals(header))
					continue;
				if (EffectRecord._fields.conditions.name().equals(header))
					continue;
				if (EffectRecord._fields.loQualifier.name().equals(header))
					continue;
				if (EffectRecord._fields.upQualifier.name().equals(header))
					continue;
				if (EffectRecord._fields.upValue.name().equals(header))
					continue;
				if (EffectRecord._fields.textValue.name().equals(header))
					continue;
				if ("document_prefix".equals(header)) continue;
				if ("s_prefix".equals(header)) continue;
				if ("errQualifier".equals(header))
					continue;
				if ("reference_year".equals(header))
					continue;
				if ("err".equals(header))
					continue;
				if ("hash".equals(header))
					continue;
				if ("content".equals(header))
					continue;
				if (EffectRecord._fields.unit.name().equals(header))
					continue;
				Assert.assertNotNull(header, record.get(header));
			}
			count++;
		}
		Assert.assertEquals(4, count);
	}

}
