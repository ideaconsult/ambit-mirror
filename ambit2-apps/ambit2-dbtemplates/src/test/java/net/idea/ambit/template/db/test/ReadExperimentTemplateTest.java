package net.idea.ambit.template.db.test;

import java.sql.ResultSet;

import ambit2.db.search.test.QueryTest;
import junit.framework.Assert;
import net.enanomapper.maker.TR;
import net.enanomapper.maker.TemplateMakerSettings;
import net.idea.ambit.templates.db.ReadExperimentTemplate;

public class ReadExperimentTemplateTest extends QueryTest<ReadExperimentTemplate> {

	@Override
	protected ReadExperimentTemplate createQuery() throws Exception {
		ReadExperimentTemplate q = new ReadExperimentTemplate();
		TemplateMakerSettings settings = new TemplateMakerSettings();
		settings.setQueryTemplateid("3e16dff8");
		q.setFieldname(settings);
		return q;
	}

	@Override
	protected void verify(ReadExperimentTemplate query, ResultSet rs) throws Exception {
		int count = 0;
		int units = 0;
		while (rs.next()) {
			count++;
			TR tr= query.getObject(rs);
			System.out.println(tr);
			if (tr.get("unit")!=null) units++;
		}
		Assert.assertTrue("No units - are your sure?",units>0);
		Assert.assertEquals(102, count);

	}

}
