package ambit2.db.substance.study.facet;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.facet.AbstractFacetQuery;
import ambit2.base.data.I5Utils;
import ambit2.db.search.StringCondition;

public class OwnerSubstanceStats  extends AbstractFacetQuery<String,String,StringCondition,OwnerFacet> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8910197148842469398L;
	protected String sql = 
		"SELECT owner_prefix,hex(owner_uuid),owner_name,count(*) FROM substance group by owner_prefix,owner_uuid";
	
	private static String  owner_uuid = " owner_prefix=? and owner_uuid=unhex(?)";
	
	protected OwnerFacet record;
	
	public OwnerSubstanceStats(String facetURL) {
		super(facetURL);
		record = new OwnerFacet();
	}
	
	@Override
	protected OwnerFacet createFacet(String facetURL) {
		return new OwnerFacet();
	}
	@Override
	public boolean isPrescreen() {
		return false;
	}

	@Override
	public double calculateMetric(OwnerFacet object) {
		return 1;
	}

	@Override
	public String getSQL() throws AmbitException {
		if (getFieldname()==null) return sql;
		else return String.format(sql,"\nwhere" + owner_uuid);
	}

	protected String[] getOwnerUUID() {
		if (getFieldname()==null) return null;
		return I5Utils.splitI5UUID(getFieldname());
	}
	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		
		List<QueryParam> params1 = new ArrayList<QueryParam>();
		if (getFieldname()!=null) {
			String[] uuid = getOwnerUUID();
			params1.add(new QueryParam<String>(String.class, uuid[0]));
			params1.add(new QueryParam<String>(String.class, uuid[1].replace("-", "").toLowerCase()));
			return params1;
		} else return null;		
	}

	@Override
	public OwnerFacet getObject(ResultSet rs) throws AmbitException {
		try {
			record.setValue(rs.getString(1)+"-"+I5Utils.addDashes(rs.getString(2)));
			record.setName(rs.getString(3));
			record.setCount(rs.getInt(4));
			return record;
		} catch (Exception x) {
			record.setValue(x.getMessage());
			//record.setSubcategoryTitle("");
			record.setCount(-1);
			return record;
		}
	}
	
	
}

