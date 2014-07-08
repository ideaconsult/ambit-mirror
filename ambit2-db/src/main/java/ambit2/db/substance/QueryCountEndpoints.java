package ambit2.db.substance;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.db.substance.study.facet.SubstanceByEndpointFacet;
import ambit2.db.update.dataset.QueryCount;

public class QueryCountEndpoints  extends QueryCount<SubstanceByEndpointFacet> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3168195512257461022L;
	public QueryCountEndpoints(String facetURL) {
		super(facetURL);
		setPageSize(Integer.MAX_VALUE);
	}
	@Override
	protected SubstanceByEndpointFacet createFacet(String facetURL) {
		return new SubstanceByEndpointFacet(facetURL);
	}
	/**
	 * 
	 */
	
	protected static String sql = 
		//"SELECT e.endpoint,count(*),group_concat(distinct(unit)),hex(e.endpointhash) FROM substance_experiment e group by e.endpoint,e.endpointhash";
		"SELECT topcategory,count(*),endpointcategory," +
		"e.endpoint,hex(e.endpointhash),group_concat(distinct(unit)),group_concat(distinct(conditions)) FROM substance_experiment e \n"+
		"where topcategory=? and endpointcategory=?\n"+
		"group by e.endpoint,e.endpointhash";
	@Override
	public String getSQL() throws AmbitException {
		return sql;
	}
	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		if (getFieldname()==null || getValue()==null) throw new AmbitException("Endpoints not defined");
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<String>(String.class, getFieldname().toString()));
		params.add(new QueryParam<String>(String.class, getValue().toString()));
		return params;
	}
	@Override
	public SubstanceByEndpointFacet getObject(ResultSet rs) throws AmbitException {
		try {
			facet.getEffect().clear();
			facet.getEffect().setEndpoint(rs.getString(4));
			facet.getEffect().setSampleID(rs.getString(5));
			facet.getEffect().setUnit(rs.getString(6));
			facet.getEffect().setConditions(rs.getString(7));
			
			facet.setSubcategoryTitle(rs.getString(1));
			facet.setValue(rs.getString(3));
			facet.setCount(rs.getInt(2));
			return facet;
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
	}
}
