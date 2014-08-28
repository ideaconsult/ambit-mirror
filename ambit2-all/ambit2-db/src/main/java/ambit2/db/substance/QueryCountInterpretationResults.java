package ambit2.db.substance;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.study.Params;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.ProtocolApplication;
import ambit2.db.substance.study.facet.ResultsCountFacet;
import ambit2.db.update.dataset.QueryCount;

public class QueryCountInterpretationResults  extends QueryCount<
				ResultsCountFacet<ProtocolApplication<Protocol,Params,String,Params,String>>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3168195512257461022L;
	protected String interpretation_result;

	public String getInterpretation_result() {
		return interpretation_result;
	}
	public void setInterpretation_result(String interpretation_result) {
		this.interpretation_result = interpretation_result;
	}
	public QueryCountInterpretationResults(String facetURL) {
		super(facetURL);
		setPageSize(100);
	}
	@Override
	protected ResultsCountFacet createFacet(String facetURL) {
		return new ResultsCountFacet(facetURL, new ProtocolApplication<Protocol,Params,String,Params,String>(new Protocol("")));
	}
	/**
	 * 
	 */
	
	private static String sql = 
		"SELECT topcategory,count(*),endpointcategory,interpretation_result FROM substance_protocolapplication e\n" +
		"where topcategory=? %s %s\n"+
		"group by topcategory,endpointcategory,interpretation_result";
	private static String sql_endpointcategory = "and endpointcategory=?";
	private static String sql_result = "and interpretation_result regexp ?";
	private static String sql_resultnull = "and interpretation_result is null";
	@Override
	public String getSQL() throws AmbitException {
		return String.format(sql,
				getValue()==null?"":sql_endpointcategory,
				getInterpretation_result()==null?"":
				("null".equals(interpretation_result))?sql_resultnull:sql_result);
	}
	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		if (getFieldname()==null) setFieldname("TOX");
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<String>(String.class, getFieldname().toString()));
		if (getValue()!=null)
			params.add(new QueryParam<String>(String.class, getValue().toString()));
		if (getInterpretation_result()!=null) {
			if (!"null".equals(interpretation_result))
				params.add(new QueryParam<String>(String.class, "^"+getInterpretation_result()));
		}
		return params;
	}
	@Override
	public ResultsCountFacet getObject(ResultSet rs) throws AmbitException {
		try {
			facet.getResult().clear();
			facet.getResult().setInterpretationResult(rs.getString(4));
			//facet.getResult().getProtocol().setTopCategory(rs.getString(1));
			//facet.getResult().getProtocol().setCategory(rs.getString(3));
			facet.setSubcategoryTitle(rs.getString(1));
			facet.setValue(rs.getString(3));
			facet.setCount(rs.getInt(2));
			return facet;
		} catch (Exception x) {
			x.printStackTrace();
			throw new AmbitException(x);
		}
	}
}
