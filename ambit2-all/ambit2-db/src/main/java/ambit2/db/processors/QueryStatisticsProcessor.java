package ambit2.db.processors;

import net.idea.modbcum.i.exceptions.AmbitException;
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
				
		"select min(Nab/(Na+Nb-Nab)) as minTanimoto, max(Nab/(Na+Nb-Nab)) as maxTanimoto, avg(Nab/(Na+Nb-Nab)) as averageTanimoto, std(Nab/(Na+Nb-Nab)) as \"Standard deviation\" from\n"+
		"(\n"+
		"SELECT f1.idchemical as c1,f2.idchemical as c2,f1.bc as Na,f2.bc as Nb,\n"+
		"bit_count(f1.fp1 & f2.fp1) + bit_count(f1.fp2 & f2.fp2) + bit_count(f1.fp3 & f2.fp3) + bit_count(f1.fp4 & f2.fp4) +\n"+
		"bit_count(f1.fp5 & f2.fp5) + bit_count(f1.fp6 & f2.fp6) + bit_count(f1.fp7 & f2.fp7) + bit_count(f1.fp8 & f2.fp8) +\n"+
		"bit_count(f1.fp9 & f2.fp9) + bit_count(f1.fp10 & f2.fp10) + bit_count(f1.fp11 & f2.fp11) + bit_count(f1.fp12 & f2.fp12) +\n"+
		"bit_count(f1.fp13 & f2.fp3) + bit_count(f1.fp14 & f2.fp14) + bit_count(f1.fp15 & f2.fp15) + bit_count(f1.fp16 & f2.fp16) as Nab\n"+
		"FROM fp1024 as f1\n"+
		"join fp1024 as f2\n"+
		"where f1.idchemical != f2.idchemical\n"+
		"and f1.idchemical in (select s.idchemical from structure as s join query_results using(idstructure) where idquery=%s)\n"+
		"and f2.idchemical in (select s.idchemical from structure as s join query_results using(idstructure) where idquery=%s)\n"+
		") as c",				
		
		"select template.name as template,properties.name as Property,count(*) as 'Number of entries' from template\n"+
		"join template_def using(idtemplate)\n"+
		"join properties using(idproperty)\n"+
		"join property_values using(idproperty)\n"+
		"join (SELECT idstructure FROM query_results where idquery=%s) as s\n"+
		"using(idstructure)\n"+
		"group by idtemplate,idproperty",
		
		"select properties.name as Property,count(distinct(value)) as 'Distinct values' from properties\n"+
		"join property_values using(idproperty)\n"+
		"join property_string using(idvalue_string)\n"+
		"join\n"+
		"(SELECT idstructure FROM query_results where idquery=%s) as s\n"+
		"using(idstructure)\n"+
		"group by properties.idproperty\n",
		
		"select idproperty,properties.name as property,min(value_num) as \"Min\",avg(value_num) as \"Average\",max(value_num) as \"Max\",count(idstructure) as \"#\" from properties\n"+
		"join property_values using(idproperty)\n"+
		"join\n"+
		"(SELECT idstructure FROM query_results where idquery=%s) as s\n"+
		"using(idstructure)\n"+
		"group by properties.idproperty\n"
		};
		s[0] = String.format(s[0],target.getId(),target.getId(),target.getId());	
		for (int i=1; i < (s.length-1);i++)
			s[i] = String.format(s[i],target.getId());
		s[s.length-1] = String.format(s[s.length-1],target.getId(),target.getId());		
		return s;
	}
	@Override
	public StringBuffer process(IStoredQuery target) throws AmbitException {
		if (target != null) setSql(getSQL(target));
		return super.process(target);
	}
}
