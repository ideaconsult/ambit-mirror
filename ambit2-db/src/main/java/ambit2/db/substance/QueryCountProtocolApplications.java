package ambit2.db.substance;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.facet.IFacet;
import ambit2.db.search.QueryParam;
import ambit2.db.substance.study.facet.SubstanceByCategoryFacet;
import ambit2.db.update.dataset.QueryCount;

public class QueryCountProtocolApplications   extends QueryCount<SubstanceByCategoryFacet> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6001454887114379950L;
	/**
	 * 
	 */
	
	public QueryCountProtocolApplications(String facetURL) {
		super(facetURL);
		setPageSize(Integer.MAX_VALUE);
	}
	/**
	 * 
	 */
	
	protected static String sql = 
		"SELECT topcategory,count(*),endpointcategory FROM substance_protocolapplication group by topcategory,endpointcategory";
	@Override
	public String getSQL() throws AmbitException {
		return sql;
	}
	
	@Override
	protected SubstanceByCategoryFacet createFacet(String facetURL) {
		return new SubstanceByCategoryFacet(facetURL);
	}
	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		return null;
	}
	@Override
	public SubstanceByCategoryFacet getObject(ResultSet rs) throws AmbitException {
		try {
			facet.setSubcategoryTitle(rs.getString(1));
			facet.setValue(rs.getString(3));
			facet.setCount(rs.getInt(2));
			
			return facet;
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
	}
}
