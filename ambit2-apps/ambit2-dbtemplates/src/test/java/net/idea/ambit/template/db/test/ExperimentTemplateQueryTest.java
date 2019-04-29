package net.idea.ambit.template.db.test;

import java.sql.ResultSet;

import ambit2.db.search.test.QueryTest;
import junit.framework.Assert;
import net.enanomapper.maker.TemplateMakerSettings;
import net.idea.ambit.templates.db.ExperimentTemplateQuery;
import net.idea.modbcum.i.bucket.Bucket;

public class ExperimentTemplateQueryTest extends QueryTest<ExperimentTemplateQuery> {
	
	@Override
	protected ExperimentTemplateQuery createQuery() throws Exception {
		TemplateMakerSettings settings = new TemplateMakerSettings();
		settings.setQueryTemplateid("3e16dff8");
		ExperimentTemplateQuery q = new ExperimentTemplateQuery(settings);

		return q;
	}

	@Override
	protected void verify(ExperimentTemplateQuery query, ResultSet rs) throws Exception {
		int count = 0;
		
		while (rs.next()) {
			count++;
			Bucket bucket= query.getObject(rs);
			System.out.println(bucket);
		}
		Assert.assertEquals(102, count);
	}

}
