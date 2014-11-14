package ambit2.db.update.storedquery;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;
import ambit2.base.data.Property;
import ambit2.base.data.Range;
import ambit2.db.search.IStoredQuery;

public class FilteredSelectStoredQuery extends AbstractUpdate<IStoredQuery, Property> {
	protected String sql = 	
		"update query_results, properties, property_values %s\n"+
		"set selected = 1 where\n"+
		"properties.idproperty=property_values.idproperty\n"+
		"and query_results.idstructure = property_values.idstructure\n"+
		"%s\n"+
		"and name=?\n %s %s %s";
	protected String sql_table_string = ", property_string";
	protected String sql_where_string = 	
		"and property_string.idvalue_string = property_values.idvalue_string\n"+		
		"and property_values.idvalue_string is not null\n";		
	protected String sql_where_num = 	
		"and property_values.value_num is not null\n";
	
	protected String sql_query="and idquery=?";
	protected String sql_metric= "and metric > ? and metric <= ?";
	
	protected String sql_value_string = "and value = ?";
	protected String sql_value_numeric = "and value_num > ? and value_num <= ?";
	
	
	public String[] getSQL() throws AmbitException {
		boolean num = (getValue()!=null)&&(getValue().getMinValue() instanceof Number);
		return new String[] {
				String.format(sql,
				num?"":sql_table_string,
				num?sql_where_num:sql_where_string,
				getGroup() == null?"":sql_query,
				getMetric() == null?"":sql_metric,
				getValue()==null?"":(num?sql_value_numeric:sql_value_string)
				)
		};
	}	
	protected Range<Double> metric;
	protected Range value;
	
	public Range<Double> getMetric() {
		return metric;
	}
	public void setMetric(Range<Double> metric) {
		this.metric = metric;
	}

	public Range getValue() {
		return value;
	}
	public void setValue(Range value) {
		this.value = value;
	}
	public List<QueryParam> getParameters(int index) throws AmbitException {
		if (getObject() == null) throw new AmbitException("Property not defined");
		List<QueryParam> p = new ArrayList<QueryParam>();

		p.add(new QueryParam<String>(String.class,getObject().getName()));
		if (getGroup() != null)
			p.add(new QueryParam<Integer>(Integer.class,getGroup().getId()));
		if (getMetric()!=null) {
			p.add(new QueryParam<Double>(Double.class,getMetric().getMinValue()));
			p.add(new QueryParam<Double>(Double.class,getMetric().getMaxValue()));
		}
		if (getValue()!= null)
			if ((getValue()!=null)&&(getValue().getMinValue() instanceof Number)) {
				p.add(new QueryParam<Double>(Double.class,(Double)getValue().getMinValue()));
				p.add(new QueryParam<Double>(Double.class,(Double)getValue().getMaxValue()));
			}
			else
				p.add(new QueryParam<String>(String.class,getValue().getMinValue().toString()));
		return p;
	}	

	public void setID(int index, int id) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean returnKeys(int index) {
		return false;
	}
}
