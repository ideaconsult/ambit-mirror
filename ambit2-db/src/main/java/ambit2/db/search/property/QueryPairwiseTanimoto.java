package ambit2.db.search.property;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ambit2.base.exceptions.AmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.BooleanCondition;
import ambit2.db.search.IStoredQuery;
import ambit2.db.search.QueryParam;

/**
 * Generatise pairwise similarities between structures in given queries
 * @author nina
 *
 */
public class QueryPairwiseTanimoto extends AbstractQuery<IStoredQuery, IStoredQuery, BooleanCondition, Double> implements IQueryRetrieval<Double> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5148418049120426363L;
	protected static final String sql = 
		"select Nab/(Na+Nb-Nab) as Tanimoto from\n"+
		"(\n"+
		"SELECT f1.idchemical as c1,f2.idchemical as c2,f1.bc as Na,f2.bc as Nb,\n"+
		"bit_count(f1.fp1 & f2.fp1) + bit_count(f1.fp2 & f2.fp2) + bit_count(f1.fp3 & f2.fp3) + bit_count(f1.fp4 & f2.fp4) +\n"+
		"bit_count(f1.fp5 & f2.fp5) + bit_count(f1.fp6 & f2.fp6) + bit_count(f1.fp7 & f2.fp7) + bit_count(f1.fp8 & f2.fp8) +\n"+
		"bit_count(f1.fp9 & f2.fp9) + bit_count(f1.fp10 & f2.fp10) + bit_count(f1.fp11 & f2.fp11) + bit_count(f1.fp12 & f2.fp12) +\n"+
		"bit_count(f1.fp13 & f2.fp13) + bit_count(f1.fp14 & f2.fp14) + bit_count(f1.fp15 & f2.fp15) + bit_count(f1.fp16 & f2.fp16) as Nab\n"+
		"FROM fp1024 as f1\n"+
		"join fp1024 as f2\n"+
		"where f1.idchemical != f2.idchemical\n"+
		"and f1.idchemical in (select s.idchemical from structure as s join query_results using(idstructure) where idquery=?)\n"+
		"and f2.idchemical in (select s.idchemical from structure as s join query_results using(idstructure) where idquery=?)\n"+
		") as c\n";
	
	public List<QueryParam> getParameters() throws AmbitException {
		IStoredQuery q1 = (getFieldname()!=null)?getFieldname():getValue();
		IStoredQuery q2 = (getValue()!=null)?getValue():getFieldname();
		
		if ((q1==null) && (q2==null)) throw new AmbitException("Queries not defined");
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, q1.getId()));
		params.add(new QueryParam<Integer>(Integer.class, q2.getId()));
		return params;	
	}

	public String getSQL() throws AmbitException {
		return sql;
	}
	public Double getObject(ResultSet rs) throws AmbitException {
		try {
			return rs.getDouble(1);
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
	}

	public double calculateMetric(Double object) {
		return 1;
	}

	public boolean isPrescreen() {
		return false;
	}

}
