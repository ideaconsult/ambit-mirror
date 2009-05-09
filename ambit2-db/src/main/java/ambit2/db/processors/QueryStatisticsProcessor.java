package ambit2.db.processors;

import ambit2.base.exceptions.AmbitException;
import ambit2.db.search.IStoredQuery;

public class QueryStatisticsProcessor extends ConnectionStatisticsProcessor<IStoredQuery> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1847691478569935696L;
	public QueryStatisticsProcessor() {

	}
	protected String[] getSQL(IStoredQuery target) {
		String[] s = new String[] {
				
		
		"select template.name as template,properties.name as Property,count(*) as 'Number of entries' from template\n"+
		"join template_def using(idtemplate)\n"+
		"join properties using(idproperty)\n"+
		"join property_values using(idproperty)\n"+
		"join (SELECT idstructure FROM query_results where idquery=%s) as s\n"+
		"using(idstructure)\n"+
		"group by idtemplate,idproperty",
		
		"select properties.name as Property,count(distinct(value)) as 'Distinct values' from properties\n"+
		"join property_values using(idproperty)\n"+
		"join property_string using(idvalue,idtype)\n"+
		"join\n"+
		"(SELECT idstructure FROM query_results where idquery=%s) as s\n"+
		"using(idstructure)\n"+
		"group by properties.idproperty\n",
		
		"select idproperty,properties.name as property,min(value) as \"Min\",avg(value) as \"Average\",max(value) as \"Max\",count(idstructure) as \"#\" from properties\n"+
		"join property_values using(idproperty)\n"+
		"join property_int using(idvalue,idtype)\n"+
		"join\n"+
		"(SELECT idstructure FROM query_results where idquery=%s) as s\n"+
		"using(idstructure)\n"+
		"group by properties.idproperty\n"+
		"union\n"+
		"select idproperty,properties.name as property,min(value) As \"Min\",avg(value) as \"Average\",max(value) as \"Max\",count(idstructure) as \"#\" from properties\n"+
		"join property_values using(idproperty)\n"+
		"join property_number using(idvalue,idtype)\n"+
		"join\n"+
		"(SELECT idstructure FROM query_results where idquery=%s) as s\n"+
		"using(idstructure)\n"+
		"group by properties.idproperty\n"
		};
		for (int i=0; i < (s.length-1);i++)
			s[i] = String.format(s[i],target.getId());
		s[s.length-1] = String.format(s[s.length-1],target.getId(),target.getId());		
		return s;
	}
	@Override
	public StringBuffer process(IStoredQuery target) throws AmbitException {
		setSql(getSQL(target));
		return super.process(target);
	}
}
