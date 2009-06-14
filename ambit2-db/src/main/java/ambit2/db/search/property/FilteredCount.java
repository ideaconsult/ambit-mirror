package ambit2.db.search.property;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.Property;
import ambit2.base.data.PropertyStats;
import ambit2.base.data.Range;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.IStoredQuery;
import ambit2.db.search.QueryParam;

import com.jgoodies.binding.beans.Model;

public abstract class FilteredCount<T extends Comparable<T>> extends Model 
										implements IQueryRetrieval<PropertyStats<T>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9049403419636975159L;
	/**
	 * 
	 */
	protected Integer id;
	protected Property property;
	protected IStoredQuery query;
	protected Range<Double> metric;
	protected Range<T> value;
	
	public Range<T> getValue() {
		return value;
	}

	public void setValue(Range<T> value) {
		this.value = value;
	}

	public Range<Double> getMetric() {
		return metric;
	}

	public void setMetric(Range<Double> metric) {
		this.metric = metric;
	}

	public Property getProperty() {
		return property;
	}

	public void setProperty(Property property) {
		this.property = property;
	}

	public IStoredQuery getQuery() {
		return query;
	}

	public void setQuery(IStoredQuery query) {
		this.query = query;
	}

	public Integer getId() {
		return id;
	}

	public List<QueryParam> getParameters() throws AmbitException {
		if (getProperty() == null) throw new AmbitException("Property not defined");
		List<QueryParam> p = new ArrayList<QueryParam>();

		p.add(new QueryParam<String>(String.class,getProperty().getName()));
		if (getQuery() != null)
			p.add(new QueryParam<Integer>(Integer.class,getQuery().getId()));
		if (getMetric()!=null) {
			p.add(new QueryParam<Double>(Double.class,getMetric().getMinValue()));
			p.add(new QueryParam<Double>(Double.class,getMetric().getMaxValue()));
		}
		return p;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	public PropertyStats<T> getObject(ResultSet rs) throws AmbitException {
		try {
			PropertyStats<T> stats = new PropertyStats<T>();
			stats.setCount(rs.getLong(1));
			return stats;
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
	}
	public double calculateMetric(PropertyStats<T> object) {
		return 1;
	}
	public boolean isPrescreen() {
		return false;
	}
}
