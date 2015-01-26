package ambit2.db.facets.compounds;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.facet.AbstractFacetQuery;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.base.facet.BundleRoleFacet;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.StringCondition;

public class ChemicalRoleByBundle extends
	AbstractFacetQuery<IStructureRecord, SubstanceEndpointsBundle, StringCondition, BundleRoleFacet> {
    /**
	 * 
	 */
    private static final long serialVersionUID = -4741289697057206809L;
    private final static String sql = "SELECT idbundle,idchemical,tag,remarks from bundle_chemicals where idbundle=? and idchemical=?";
    protected BundleRoleFacet record;

    public ChemicalRoleByBundle(String facetURL) {
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
	if ((getFieldname() == null) || (getFieldname().getIdchemical() <= 0))
	    throw new AmbitException("Chemical not defined");
	if (getValue() == null || getValue().getID() <= 0)
	    throw new AmbitException("Bundle not defined");
	List<QueryParam> params = new ArrayList<QueryParam>();
	params.add(new QueryParam<Integer>(Integer.class, getValue().getID()));
	params.add(new QueryParam<Integer>(Integer.class, getFieldname().getIdchemical()));
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
	    record.setTag(rs.getString("tag"));
	    record.setRemarks(rs.getString("remarks"));
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