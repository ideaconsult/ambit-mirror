package ambit2.db.substance;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
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
	
	private static final String sql = 
		"SELECT topcategory,count(*),endpointcategory,count(distinct(concat(substance_prefix,substance_uuid)))  FROM substance_protocolapplication %s group by topcategory,endpointcategory";
	
	/*
select p.topcategory,p.endpointcategory,count(*) from substance_protocolapplication p, bundle_endpoints b 
where p.topcategory=b.topcategory
and p.endpointcategory=b.endpointcategory
and idbundle=1
group by p.topcategory,p.endpointcategory
order by p.topcategory,p.endpointcategory
 
	 */
	private static final String w_topcategory = "topcategory=?";
	private static final String w_endpointcategory = "endpointcategory=?";
		
	@Override
	public String getSQL() throws AmbitException {
		StringBuilder b = new StringBuilder();
		String d = "\nwhere ";
		if (getFieldname()!=null) { b.append(d); b.append(w_topcategory); d = " and ";}
		if (getValue()!=null) { b.append(d); b.append(w_endpointcategory); d = " and ";}
		return String.format(sql,b.toString());
	}
	@Override
	public void setValue(String value) {
		if (value!=null && !value.endsWith("_SECTION")) super.setValue(value+"_SECTION");
		else super.setValue(value);
	}
	
	@Override
	protected SubstanceByCategoryFacet createFacet(String facetURL) {
		return new SubstanceByCategoryFacet(facetURL);
	}
	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		if (getFieldname()!=null) params.add(new QueryParam<String>(String.class, getFieldname().toString()));
		if (getValue()!=null) params.add(new QueryParam<String>(String.class, getValue().toString()));
		return params;
	}
	@Override
	public SubstanceByCategoryFacet getObject(ResultSet rs) throws AmbitException {
		try {
			facet.setSubcategoryTitle(rs.getString(1));
			facet.setValue(rs.getString(3));
			facet.setCount(rs.getInt(2));
			facet.setSubstancesCount(rs.getInt(4));
			
			return facet;
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
	}
	

}
