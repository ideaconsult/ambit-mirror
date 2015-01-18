package ambit2.db.facets.bundle;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.facet.AbstractFacetQuery;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.db.search.StringCondition;

public class BundleSummaryQuery extends AbstractFacetQuery<SubstanceEndpointsBundle,String,StringCondition,BundleSummaryFacet>  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2156155776273258064L;
	private static final String sql = 
		"select \"compound\",count(idchemical) from bundle_chemicals where idbundle=? "+
		"union select \"substance\",count(idsubstance) from bundle_substance where idbundle=? "+
		"union select \"property\",count(*) from bundle_endpoints where idbundle=? "+
		"union select \"matrix\",count(*)>0 FROM bundle_substance_protocolapplication where idbundle=?" +
		"union select \"matrix/final\",count(*)>0 FROM bundle_final_protocolapplication where idbundle=?";
	
	protected BundleSummaryFacet record;
	
	public BundleSummaryQuery(String url) {
		super(url);
		record = createFacet(url);
	}

	public double calculateMetric(BundleSummaryFacet arg0) { return 1; }
	
	@Override
	public boolean isPrescreen() {
		return false;
	}

	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		if (getFieldname()!=null && getFieldname().getID()>0) {
			params.add(new QueryParam<Integer>(Integer.class,getFieldname().getID()));
			params.add(new QueryParam<Integer>(Integer.class,getFieldname().getID()));
			params.add(new QueryParam<Integer>(Integer.class,getFieldname().getID()));
			params.add(new QueryParam<Integer>(Integer.class,getFieldname().getID()));
		}	
		else throw new AmbitException("Bundle not defined");
		return params;
	}

	@Override
	public String getSQL() throws AmbitException {
		return sql;
	}
	@Override
	protected BundleSummaryFacet createFacet(String facetURL) {
		BundleSummaryFacet facet = new BundleSummaryFacet(facetURL);
		return facet;
	}
	
	@Override
	public BundleSummaryFacet getObject(ResultSet rs) throws AmbitException {
		if (record == null) {
			record = createFacet(null);
		} else { 

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
			return String.format("Bundle %d summary",getFieldname().getID());
		else return "Undefined";
	}

}
