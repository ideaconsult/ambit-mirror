package ambit2.db.substance.study.facet;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.I5Utils;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.facet.IFacet;
import ambit2.db.facets.AbstractFacetQuery;
import ambit2.db.search.QueryParam;
import ambit2.db.search.StringCondition;

public class SubstanceStudyFacetQuery  extends AbstractFacetQuery<String,String,StringCondition,IFacet<String>>  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8910197148842469398L;
	protected String sql = 
		"select topcategory,endpointcategory,count(*) from substance_protocolapplication %s group by topcategory,endpointcategory with rollup";
	
	private static String  substance_uuid = " substance_prefix=? and hex(substance_uuid)=?";
	
	protected SubstanceStudyFacet record;
	
	public SubstanceStudyFacetQuery(String facetURL) {
		super(facetURL);
		record = new SubstanceStudyFacet(facetURL);
	}
	@Override
	public boolean isPrescreen() {
		return false;
	}

	@Override
	public double calculateMetric(IFacet<String> object) {
		return 1;
	}

	@Override
	public String getSQL() throws AmbitException {
		if (getFieldname()==null) return sql;
		else return String.format(sql,"\nwhere" + substance_uuid);
	}

	protected String[] getSubstanceUUID() {
		if (getFieldname()==null) return null;
		return I5Utils.splitI5UUID(getFieldname());
	}
	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		
		List<QueryParam> params1 = new ArrayList<QueryParam>();
		if (getFieldname()!=null) {
			String[] uuid = getSubstanceUUID();
			params1.add(new QueryParam<String>(String.class, uuid[0]));
			params1.add(new QueryParam<String>(String.class, uuid[1].replace("-", "").toLowerCase()));
			return params1;
		} else return null;		
	}

	@Override
	public SubstanceStudyFacet getObject(ResultSet rs) throws AmbitException {
		try {
			record.setValue(rs.getString(1));
			record.setSubcategoryTitle(rs.getString(2));
			record.setCount(rs.getInt(3));
			return record;
		} catch (Exception x) {
			record.setValue(x.getMessage());
			record.setCount(-1);
			return record;
		}
	}
	
	
}
