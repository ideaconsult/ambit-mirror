package ambit2.db.search;

import java.util.ArrayList;
import java.util.List;

import ambit2.core.exceptions.AmbitException;

/**
 * structures with given template name and study results value
 * @author Nina Jeliazkova nina@acad.bg
 *
 */
public class QueryStudyResultsNumeric extends QueryExperiment<Number,NumberCondition> {
	public String getSQL() throws AmbitException {
		StringBuffer sql = new StringBuffer();
		sql.append("select ? as idquery,-1 as idchemical,idstructure,1 as selected,experiment.idexperiment as metric from experiment\n");
		sql.append("join\n");
		sql.append("(select idexperiment,id_fieldname,value from study_results join study_fieldnames using(id_fieldname) where name=? and value_num ");
		sql.append(getCondition().getSQL());
		sql.append(" ?) as C\n");
		sql.append("using(idexperiment)\n");
		sql.append("join\n");
		sql.append("(select idstudy,idtemplate,id_fieldname from study join template_def using(idtemplate) join template using(idtemplate) where template.name=?) as T\n");
		sql.append("using(idstudy)");		
		
		return sql.toString();
	}

	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getId()));
		params.add(new QueryParam<String>(String.class, getValue().getName()));
		params.add(new QueryParam<Number>(Number.class, getValue().getValue()));
		params.add(new QueryParam<String>(String.class, getFieldname().getName()));		
		return params;
	}
}		
