package ambit2.db.search;

import java.util.ArrayList;
import java.util.List;

import ambit2.core.exceptions.AmbitException;

public class QueryStudyConditions extends QueryExperiment<String,StringCondition> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4412340359727465867L;

	public String getSQL() throws AmbitException {
		StringBuffer sql = new StringBuffer();
		sql.append("select ? as idquery,-1,idstructure,1 as selected,idexperiment as metric from experiment\n");
		//sql.append("select idstructure,idexperiment,T.name,S.name,C.value from experiment\n");
		sql.append("join\n");
		sql.append("(select idstudy,idtemplate,name from study) as S\n");
		sql.append("using(idstudy)");
		sql.append("join\n");
		sql.append("(select idtemplate,id_fieldname,name from template join template_def using(idtemplate) where template.name=?) as T\n");
		sql.append("using(idtemplate)\n");
		sql.append("join\n");
		sql.append("(select idstudy,id_fieldname,value from study_conditions join study_fieldnames using(id_fieldname) where name=? and value ");
		sql.append(getCondition().getSQL());
		sql.append(" ?) as C\n");
		sql.append("using(id_fieldname)");
		return sql.toString();
	}

	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getId()));
		params.add(new QueryParam<String>(String.class, getFieldname().getName()));
		params.add(new QueryParam<String>(String.class, getValue().getName()));
		params.add(new QueryParam<String>(String.class, getValue().getValue()));
		return params;
	}

}


/**
 * Test data 
 
insert into template (idtemplate,name,template) values (1,"template1",null);
insert into study (idstudy,idtemplate,name) values (1,1,"study1");
insert into study_fieldnames (id_fieldname,name,units,fieldtype,fieldmode) values (1,"field1","u1","numeric","result");
insert into study_fieldnames (id_fieldname,name,units,fieldtype,fieldmode) values (2,"field2","u2","string","condition");

insert into template_def  (idtemplate,id_fieldname) values (1,1);
insert into template_def (idtemplate,id_fieldname) values (1,2);

insert into study_conditions (idstudy,id_fieldname,value) values (1,2,"condition1");
insert into experiment (idexperiment,idstudy,idreference,idstructure) values (1,1,1,1);
insert into study_results (idexperiment,id_fieldname,value,value_num) values (1,1,"result1",1);
insert into experiment (idexperiment,idstudy,idreference,idstructure) values (2,1,1,2);
insert into study_results (idexperiment,id_fieldname,value,value_num) values (2,1,"result2",2);

insert into catalog_references (idreference,title,url) values (2,"study2","url2");
insert into experiment (idexperiment,idstudy,idreference,idstructure) values (3,1,2,1);
**/