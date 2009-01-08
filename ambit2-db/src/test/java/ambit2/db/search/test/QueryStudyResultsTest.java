package ambit2.db.search.test;

import java.sql.ResultSet;

import junit.framework.Assert;
import ambit2.core.data.experiment.StudyTemplate;
import ambit2.db.search.QueryStudyResults;
import ambit2.db.search.StringCondition;
import ambit2.db.search.TemplateFieldQuery;

public class QueryStudyResultsTest extends QueryTest<QueryStudyResults> {

	public void test() throws Exception {
		
		String s = 
		"select ? as idquery,-1 as idchemical,idstructure,1 as selected,experiment.idexperiment as metric from experiment\n"+
		"join\n"+
		"(select idexperiment,id_fieldname,value from study_results join study_fieldnames using(id_fieldname) where name=? and value = ?) as C\n"+
		"using(idexperiment)\n"+
		"join\n"+
		"(select idstudy,idtemplate,id_fieldname from study join template_def using(idtemplate) join template using(idtemplate) where template.name=?) as T\n"+
		"using(idstudy)";
		Assert.assertEquals(s,query.getSQL());
	}
	@Override
	protected QueryStudyResults createQuery() throws Exception {
		TemplateFieldQuery<String> q = new TemplateFieldQuery<String>("field1","",false,false);
		q.setValue("result1");
		
		QueryStudyResults qs = new QueryStudyResults();
		qs.setCondition(StringCondition.getInstance("="));
		qs.setFieldname(new StudyTemplate("template1"));
		qs.setValue(q);
		return qs;
	}
	@Override
	protected void verify(QueryStudyResults query, ResultSet rs) throws Exception {
		int count = 0; 
		while (rs.next()) {
			count++;
			System.out.println(rs.getInt("idstructure"));
		}
		Assert.assertTrue(count>0);
		
	}
}	
