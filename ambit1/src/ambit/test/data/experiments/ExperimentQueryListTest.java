package ambit.test.data.experiments;

import junit.framework.TestCase;
import ambit.database.query.ExperimentQuery;
import ambit.database.query.TemplateFieldQuery;

public class ExperimentQueryListTest extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(ExperimentQueryListTest.class);
	}
	public void testSQLGeneration() {
		TemplateFieldQuery q = new TemplateFieldQuery("LC50");
		q.setId(10);
		q.setCondition("between");
		q.setValue(new Double(5));
		String s = q.toSQL("logP");
		//System.out.println(s);
		assertEquals("select idexperiment from study_results where id_fieldname=10 and value_num between 0 and 1",s);
		
		s = q.id2SQL("logP.");
		//System.out.println(s);
		assertEquals("logP.id_fieldname=10 and logP.value_num between 0 and 1",s.trim());
	}

	public void testListSQLGeneration() {
		TemplateFieldQuery q = new TemplateFieldQuery("LC50");
		q.setResult(true);
		q.setId(10);
		q.setCondition("between");
		q.setNumeric(true);
		q.setMinValue(2);
		q.setMaxValue(3);
		q.setEnabled(true);
		
		TemplateFieldQuery q1 = new TemplateFieldQuery("TOXINDEX");
		q1.setResult(true);
		q1.setId(16);
		q1.setNumeric(true);
		q1.setCondition("<");
		q1.setValue(new Double(100));
		q1.setEnabled(true);

		TemplateFieldQuery q2 = new TemplateFieldQuery("LC50NOTE");
		q2.setResult(true);
		q2.setId(13);
		q2.setNumeric(false);
		q2.setCondition("ALL");
		q2.setValue("REACTIVE");
		q2.setEnabled(true);
		
		ExperimentQuery l = new ExperimentQuery();
		l.setCombineWithAND(true);
		l.addItem(q);
		l.addItem(q1);
		l.addItem(q2);
		String s = l.toSQL(null,0,100);
		/*
		System.out.println("---");
		System.out.println(s);
		System.out.println("---");
		*/
		assertEquals(
				"select experiment.idstructure,experiment.idexperiment,idref,idstudy,D0.idexperiment,D0.value_num,D1.value_num,D2.value"+
				"\nfrom"+
				"\nstudy_results  as D0"+
				"\njoin study_results  as D1 using(idexperiment)"+
				"\njoin study_results  as D2 using(idexperiment)"+
				"\njoin experiment using(idexperiment)"+
				"\nwhere  D0.id_fieldname=10 and D0.value_num between 2 and 3"+
				"\nand  D1.id_fieldname=16 and D1.value_num < 100"+
				"\nand  D2.id_fieldname=13"+
				"\norder by idstructure"+
				"\nlimit 0,100"
		        ,s.trim());
		l.setCombineWithAND(false);
		s = l.toSQL(null,1,100);
		
		//System.out.println(s);
		assertEquals(
		        "select experiment.idstructure,experiment.idexperiment,idref from ("+
		        "\nselect idexperiment from study_results where id_fieldname=10 and value_num between 2 and 3"+
		        "\nunion"+
		        "\nselect idexperiment from study_results where id_fieldname=16 and value_num < 100"+
		        "\nunion"+
		        "\nselect idexperiment from study_results "+
		        "\n) as T"+
		        "\njoin experiment using(idexperiment)"+
		        "\n"+
		        "\nlimit 100,100"
		        ,s.trim());
		
	}
	
}
