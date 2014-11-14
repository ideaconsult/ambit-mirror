package ambit2.db.search.property;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.Property;
import ambit2.base.data.Range;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.NumberCondition;

/**
 * Returns binned range of property (defined by Fieldname), number of bins is given by setValue()
 * @author nina
 *
 */
public class PropertyRange  extends AbstractQuery<Property,Integer, NumberCondition, Range> 
										   implements	IQueryRetrieval<Range> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2385531620133212366L;
	String sql = 
		"select distinct(value),null  from property_values join property_string as f using (idvalue_string)\n"+
		"join properties using(idproperty) where name=? and idvalue_string is not null limit 25\n"+
		"union\n"+
		"select m1+d*(m2-m1)/? as a, m1+(d+1)*(m2-m1)/? as b from\n"+
		"(select min(value_num) as m1,max(value_num) as m2 from property_values join properties using(idproperty) where name=? and value_num is not null) as L\n"+
		"join (\n"+
		"select 0 as d\n%s"+
		") as M\n";
	
	public double calculateMetric(Range object) {
		return 1;
	}

	public boolean isPrescreen() {
		return false;
	}
	public List<QueryParam> getParameters() throws AmbitException {
		if (getFieldname()==null) throw new AmbitException("Property not defined");
		if (getValue()==null) setValue(5);
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<String>(String.class,getFieldname().getName()));
		params.add(new QueryParam<Integer>(Integer.class,getValue()));
		params.add(new QueryParam<Integer>(Integer.class,getValue()));
		params.add(new QueryParam<String>(String.class,getFieldname().getName()));
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

	public Range getObject(ResultSet rs) throws AmbitException {
		try {
			Range range = null;
			Object o2 = rs.getObject(2);
			if (o2 == null) {
				Object o1 = rs.getObject(1);
				range = new Range<String>(o1.toString());
			} else {
				range = new Range<Double>((Double)rs.getDouble(1),(Double)rs.getDouble(2));
			}
			return range;
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
	}

}
