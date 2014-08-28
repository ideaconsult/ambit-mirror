package ambit2.db.substance;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.study.EffectRecord;
import ambit2.db.substance.study.facet.ResultsCountFacet;
import ambit2.db.update.dataset.QueryCount;

public class QueryCountEndpoints<E extends EffectRecord<String,Object,String>>  extends QueryCount<ResultsCountFacet<E>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3168195512257461022L;
	protected String endpoint;
	public String getEndpoint() {
		return endpoint;
	}
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	public QueryCountEndpoints(String facetURL) {
		super(facetURL);
		setPageSize(Integer.MAX_VALUE);
	}
	@Override
	protected ResultsCountFacet createFacet(String facetURL) {
		return new ResultsCountFacet(facetURL, new EffectRecord<String,Object,String>());
	}
	/**
	 * 
	 */
	
	private static String sql = 
		"SELECT topcategory,count(*),endpointcategory,endpoint FROM substance_experiment e\n" +
		"where topcategory=? %s %s\n"+
		"group by topcategory,endpointcategory,endpoint";
	private static String sql_endpointcategory = "and endpointcategory=?";
	private static String sql_endpoint = "and endpoint regexp ?";
	@Override
	public String getSQL() throws AmbitException {
		return String.format(sql,
				getValue()==null?"":sql_endpointcategory,
				getEndpoint()==null?"":sql_endpoint);
	}
	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		if (getFieldname()==null) setFieldname("TOX");
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<String>(String.class, getFieldname().toString()));
		if (getValue()!=null)
			params.add(new QueryParam<String>(String.class, getValue().toString()));
		if (getEndpoint()!=null)
			params.add(new QueryParam<String>(String.class, "^"+getEndpoint()));
		return params;
	}
	@Override
	public ResultsCountFacet getObject(ResultSet rs) throws AmbitException {
		try {
			facet.getResult().clear();
			facet.getResult().setEndpoint(rs.getString(4));
			//facet.getEffect().setSampleID(rs.getString(5));
			//facet.getEffect().setUnit(rs.getString(6));
			//facet.getEffect().setConditions(rs.getString(7));
			
			facet.setSubcategoryTitle(rs.getString(1));
			facet.setValue(rs.getString(3));
			facet.setCount(rs.getInt(2));
			return facet;
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
	}
}
