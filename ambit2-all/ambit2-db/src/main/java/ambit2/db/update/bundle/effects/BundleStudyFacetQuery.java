package ambit2.db.update.bundle.effects;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.facet.AbstractFacetQuery;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.db.search.StringCondition;
import ambit2.db.substance.study.facet.SubstanceStudyFacet;

public class BundleStudyFacetQuery extends AbstractFacetQuery<SubstanceEndpointsBundle,String,StringCondition,SubstanceStudyFacet>  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8910197148842469398L;
	private final static String sql = 
		"select p.topcategory,p.endpointcategory,count(*) from substance_protocolapplication p, bundle_substance s ,bundle_endpoints b\n"+ 
		"where p.substance_prefix=s.substance_prefix and p.substance_uuid=s.substance_uuid\n"+
		"and p.topcategory=b.topcategory and p.endpointcategory=b.endpointcategory and s.idbundle=b.idbundle and s.idbundle=?\n"+
		"group by p.topcategory,p.endpointcategory order by p.topcategory,p.endpointcategory\n";	
	
	protected SubstanceStudyFacet record;
	
	public BundleStudyFacetQuery(String facetURL) {
		super(facetURL);
		record = createFacet(facetURL);
	}
	
	@Override
	protected SubstanceStudyFacet createFacet(String facetURL) {
		return new SubstanceStudyFacet(facetURL);
	}
	@Override
	public boolean isPrescreen() {
		return false;
	}

	@Override
	public double calculateMetric(SubstanceStudyFacet object) {
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
	public SubstanceStudyFacet getObject(ResultSet rs) throws AmbitException {
		try {
			record.setValue(rs.getString(1));
			record.setSubcategoryTitle(rs.getString(2));
			record.setCount(rs.getInt(3));
			try {
				Protocol._categories category = Protocol._categories.valueOf(rs.getString(2));
				record.setSortingOrder(category.getSortingOrder());
				
			} catch (Exception x) {
				record.setSortingOrder(999);
			}
			return record;
		} catch (Exception x) {
			record.setValue(x.getMessage());
			record.setCount(-1);
			record.setSortingOrder(999);
			return record;
		}
	}
	
	
}
