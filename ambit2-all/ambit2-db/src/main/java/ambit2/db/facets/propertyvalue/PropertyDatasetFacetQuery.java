package ambit2.db.facets.propertyvalue;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.Property;
import ambit2.base.data.SourceDataset;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.facet.IFacet;
import ambit2.db.facets.AbstractFacetQuery;
import ambit2.db.search.QueryParam;
import ambit2.db.search.StringCondition;

/**
 * Number of compounds grouped by value , given property and dataset
 * @author nina
 *
 */
public class PropertyDatasetFacetQuery extends AbstractFacetQuery<Property,SourceDataset,StringCondition,IFacet<String>>  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2156155776273258064L;
	protected static final String sql = 
		"SELECT ifnull(value_num,value) as v,count(distinct(property_values.idchemical)) as num_chemicals\n"+
		"FROM struc_dataset join property_values using(idstructure) " +
		"left join property_string using(idvalue_string) join properties using(idproperty)\n"+
		"where idproperty=? and id_srcdataset=?\n"+
		"group by v\n";	
	
	protected PropertyDatasetFacet<Property,SourceDataset> record;
	
	public PropertyDatasetFacetQuery(String url) {
		super(url);
		record = new PropertyDatasetFacet<Property,SourceDataset>(url);
		record.setProperty(getFieldname());
		record.setDataset(getValue());		
	}
	@Override
	public double calculateMetric(IFacet<String> object) {
		return 0;
	}

	@Override
	public boolean isPrescreen() {
		return false;
	}

	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		if (getValue()==null) return null;	
		List<QueryParam> params = new ArrayList<QueryParam>();
		if (getFieldname().getId()>0)
			params.add(new QueryParam<Integer>(Integer.class,getFieldname().getId()));
		else throw new AmbitException("Property not defined");
		if (getValue().getId()>0)
			params.add(new QueryParam<Integer>(Integer.class,getValue().getId()));
		else throw new AmbitException("Dataset not defined");
		return params;
	}

	@Override
	public String getSQL() throws AmbitException {
		return sql;
	}

	@Override
	public IFacet<String> getObject(ResultSet rs) throws AmbitException {
		if (record == null) {
			record = new PropertyDatasetFacet<Property,SourceDataset>(null);
			record.setProperty(getFieldname());
			record.setDataset(getValue());
		}
		try {
			record.setValue(rs.getString(1));
			record.setCount(rs.getInt(2));
			return record;
		} catch (Exception x) {
			record.setValue(x.getMessage());
			record.setCount(-1);
			return record;
		}
	}	
	
	@Override
	public String toString() {
		if ((getFieldname()!=null) && (getValue()!=null))
			return String.format("Property %d & Dataset %d",getFieldname().getId(),getValue().getId());
		else return "Undefined";
	}

}
