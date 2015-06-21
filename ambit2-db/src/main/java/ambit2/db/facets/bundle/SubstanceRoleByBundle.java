package ambit2.db.facets.bundle;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.facet.AbstractFacetQuery;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.base.facet.BundleRoleFacet;
import ambit2.db.search.StringCondition;

public class SubstanceRoleByBundle
		extends
		AbstractFacetQuery<SubstanceRecord, SubstanceEndpointsBundle, StringCondition, BundleRoleFacet> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2880103630366030217L;
	private final static String sql = "SELECT idbundle,idsubstance,tag,remarks from bundle_substance where idbundle=? and idsubstance=?";
	protected BundleRoleFacet record;

	public SubstanceRoleByBundle(String facetURL) {
		super(facetURL);
		record = createFacet(facetURL);
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
	public String getSQL() throws AmbitException {
		return sql;
	}

	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		if ((getFieldname() == null) || (getFieldname().getIdsubstance() <= 0))
			throw new AmbitException("Substance not defined");
		if (getValue() == null || getValue().getID() <= 0)
			throw new AmbitException("Bundle not defined");
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getValue().getID()));
		params.add(new QueryParam<Integer>(Integer.class, getFieldname()
				.getIdsubstance()));
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
		} else
			record.clear();
		try {
			record.setValue(getValue());
			record.setCount(1);
		} catch (Exception x) {
			record.setValue(getValue());
			record.setCount(-1);

		}
		try {
			String tag = rs.getString("tag");
			record.setTag(tag==null?"selected":tag);
			record.setRemarks(rs.getString("remarks"));
		} catch (Exception x) {
			record.setTag("selected");
			record.setRemarks(null);
		}
		return record;
	}

}