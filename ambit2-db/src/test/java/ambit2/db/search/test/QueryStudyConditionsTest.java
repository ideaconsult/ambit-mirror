package ambit2.db.search.test;

import java.sql.ResultSet;

import ambit2.core.data.experiment.StudyTemplate;
import ambit2.db.search.QueryStudyConditions;
import ambit2.db.search.StringCondition;
import ambit2.db.search.TemplateFieldQuery;

public class QueryStudyConditionsTest extends QueryTest<QueryStudyConditions> {
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
		assertEquals(s,query.getSQL());
	}
	@Override
	protected QueryStudyConditions createQuery() throws Exception {
		TemplateFieldQuery<String> q = new TemplateFieldQuery<String>("field2","",false,false);
		q.setValue("condition1");
		
		QueryStudyConditions qs = new QueryStudyConditions();
		qs.setCondition(StringCondition.getInstance("="));
		qs.setFieldname(new StudyTemplate("template1"));
		qs.setValue(q);
		return qs;
	}
	@Override
	protected void verify(QueryStudyConditions query, ResultSet rs) throws Exception {
		int count = 0; 
		while (rs.next()) {
			count++;
			System.out.println(rs.getInt("idstructure"));
		}
		assertTrue(count>0);
		
	}
}
