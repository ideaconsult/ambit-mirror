package ambit2.db.update.bundle.effects;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.facet.AbstractFacetQuery;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.db.search.StringCondition;
import ambit2.db.substance.study.facet.SubstanceByCategoryFacet;

public class BundleStudyFacetQuery extends AbstractFacetQuery<SubstanceEndpointsBundle,String,StringCondition,SubstanceByCategoryFacet>  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8910197148842469398L;
	private final static String sql = 
		"select p.topcategory,count(*),p.endpointcategory,count(distinct(p.substance_uuid)) from substance_protocolapplication p, bundle_substance s ,bundle_endpoints b\n"+ 
		"where p.substance_prefix=s.substance_prefix and p.substance_uuid=s.substance_uuid\n"+
		"and p.topcategory=b.topcategory and p.endpointcategory=b.endpointcategory and s.idbundle=b.idbundle and s.idbundle=?\n"+
		"group by p.topcategory,p.endpointcategory order by p.topcategory,p.endpointcategory\n";	
	
	protected SubstanceByCategoryFacet facet;
	
	public BundleStudyFacetQuery(String facetURL) {
		super(facetURL);
		facet = createFacet(facetURL);
	}
	
	@Override
	protected SubstanceByCategoryFacet createFacet(String facetURL) {
		return new SubstanceByCategoryFacet(facetURL);
	}
	@Override
	public boolean isPrescreen() {
		return false;
	}

	@Override
	public double calculateMetric(SubstanceByCategoryFacet object) {
		return 1;
	}

	@Override
	public String getSQL() throws AmbitException {
		return sql;
	}

	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		if (getFieldname()==null || getFieldname().getID()<=0) throw new AmbitException("Bundle not defined");
		
		List<QueryParam> params1 =  new ArrayList<QueryParam>();
		params1.add(new QueryParam<Integer>(Integer.class, getFieldname().getID()));
		return params1;
	}

	@Override
	public SubstanceByCategoryFacet getObject(ResultSet rs) throws AmbitException {
		try {
			facet.setSubcategoryTitle(rs.getString(1));
			facet.setValue(rs.getString(3));
			facet.setCount(rs.getInt(2));
			facet.setSubstancesCount(rs.getInt(4));
			
			return facet;
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}
	
	
}
