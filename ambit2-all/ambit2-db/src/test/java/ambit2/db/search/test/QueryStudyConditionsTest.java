package ambit2.db.search.test;

import java.sql.ResultSet;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import ambit2.core.data.experiment.StudyTemplate;
import ambit2.db.search.QueryStudyConditions;
import ambit2.db.search.StringCondition;
import ambit2.db.search.TemplateFieldQuery;

public class QueryStudyConditionsTest extends QueryTest<QueryStudyConditions> {
	
	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		dbFile = "src/test/resources/ambit2/db/processors/test/experiments-datasets.xml";		
	}
	@Test
	public void test() throws Exception {
		
		String s = 
		"select ? as idquery,-1,idstructure,1 as selected,idexperiment as metric from experiment\n"+
		"join\n"+
		"(select idstudy,idtemplate,name from study) as S\n"+
		"using(idstudy)join\n"+
		"(select idtemplate,id_fieldname,name from template join template_def using(idtemplate) where template.name=?) as T\n"+
		"using(idtemplate)\n"+
		"join\n"+
		"(select idstudy,id_fieldname,value from study_conditions join study_fieldnames using(id_fieldname) where name=? and value = ?) as C\n"+
		"using(id_fieldname)";
		Assert.assertEquals(s,query.getSQL());
	}
	@Override
	protected QueryStudyConditions createQuery() throws Exception {
		TemplateFieldQuery<String> q = new TemplateFieldQuery<String>("Species scientific name","",false,false);
		q.setValue("Cyprinodon variegatus");
		
		QueryStudyConditions qs = new QueryStudyConditions();
		qs.setCondition(StringCondition.getInstance("="));
		qs.setFieldname(new StudyTemplate("BCF"));
		qs.setValue(q);
		return qs;
	}
	@Override
	protected void verify(QueryStudyConditions query, ResultSet rs) throws Exception {
		System.out.println(query.getSQL());
		System.out.println(query.getParameters());
		int count = 0; 
		while (rs.next()) {
			count++;
			System.out.println(rs.getInt("idstructure"));
		}
		Assert.assertTrue(count>0);
		
	}
}
