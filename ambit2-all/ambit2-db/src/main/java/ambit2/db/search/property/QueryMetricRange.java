package ambit2.db.search.property;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.Range;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.IStoredQuery;
import ambit2.db.search.NumberCondition;

/**
 
 * @author nina
 *
 * @param <T>
 */
public class QueryMetricRange extends AbstractQuery<IStoredQuery, Integer, NumberCondition, Range<Double>> 
								implements	IQueryRetrieval<Range<Double>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 415109938169483765L;
	String sql = 
	"select m1+d*(m2-m1)/? as a, m1+(d+1)*(m2-m1)/? as b from\n"+
	"(select min(metric) as m1,max(metric) as m2 from query_results where idquery=?) as L\n"+
	"join (\n"+
	"select 0 as d\n%s"+
	") as M	\n";

	public QueryMetricRange(IStoredQuery query, Integer bins) {
		setValue(bins);
		setFieldname(query);
	}
	public QueryMetricRange() {
		this(null,5);
	}
	public double calculateMetric(Range<Double> object) {
		return 1;
	}

	public boolean isPrescreen() {
		return false;
	}

	public List<QueryParam> getParameters() throws AmbitException {
		if (getFieldname()==null) throw new AmbitException("Query not defined");
		if (getValue()==null) setValue(5);
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class,getValue()));
		params.add(new QueryParam<Integer>(Integer.class,getValue()));
		params.add(new QueryParam<Integer>(Integer.class,getFieldname().getId()));
		return params;
	}

	public String getSQL() throws AmbitException {
		return String.format(sql, getBinsSql());
	}
	public String getBinsSql() {
		StringBuilder b = new StringBuilder();
		for (int i=1; i < getValue();i++)
			b.append(String.format("union select %d as d\n",i));
		return b.toString();
	}

	public Range<Double> getObject(ResultSet rs) throws AmbitException {
		try {
			return new Range<Double>(rs.getDouble(1),rs.getDouble(2));
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
	}

}
