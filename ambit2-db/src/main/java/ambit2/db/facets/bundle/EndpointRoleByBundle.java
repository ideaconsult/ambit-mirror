package ambit2.db.facets.bundle;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.facet.AbstractFacetQuery;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.base.facet.BundleRoleFacet;
import ambit2.db.search.StringCondition;
import ambit2.db.substance.study.facet.SubstanceByCategoryFacet;

public class EndpointRoleByBundle  extends AbstractFacetQuery<SubstanceByCategoryFacet,SubstanceEndpointsBundle,StringCondition,BundleRoleFacet>  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2183619142564101671L;
	/**
	 * 
	 */
	
	private final static String sql = "SELECT idbundle,topcategory,endpointcategory,endpointhash from bundle_endpoints where idbundle=? and topcategory=? and endpointcategory=?"; 
	protected BundleRoleFacet record;
	
	public EndpointRoleByBundle(String facetURL) {
		super(facetURL);
		record = createFacet(facetURL);
		setPageSize(1);
	}
	@Override
	public boolean isPrescreen() {
		return false;
	}

	@Override
	public double calculateMetric(BundleRoleFacet object) {
		return 1;
	}

	@Override
	public void setFieldname(SubstanceByCategoryFacet fieldname) {
		super.setFieldname(fieldname);
	}
	@Override
	public String getSQL() throws AmbitException {
		return sql;
	}

	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		if ((getFieldname()==null) || (getFieldname().getEndpoint()==null || getFieldname().getSubcategoryTitle()==null)) throw new AmbitException("Endpoint not defined");
		if (getValue()==null || getValue().getID()<=0)  throw new AmbitException("Bundle not defined");
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class,getValue().getID()));		
		params.add(new QueryParam<String>(String.class,getFieldname().getSubcategoryTitle()));
		params.add(new QueryParam<String>(String.class,getFieldname().getEndpoint().name()));
		return params;
	}
	
	@Override
	protected BundleRoleFacet createFacet(String facetURL) {
		return new BundleRoleFacet(facetURL);
	}

	@Override
	public BundleRoleFacet getObject(ResultSet rs) throws AmbitException {
		if (record == null) {
			record = createFacet(null);
		} else record.clear();
		try {
			record.setTag("selected");
			record.setRemarks(rs.getString("endpointcategory"));
			record.setValue(getValue());
			record.setCount(1);
			return record;
		} catch (Exception x) {
			record.setValue(getValue());
			record.setCount(-1);
			return record;
		}
	}	
	
}