package ambit2.db.substance.study.facet;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.facet.AbstractFacetQuery;
import ambit2.base.data.I5Utils;
import ambit2.base.data.study.Protocol;
import ambit2.db.search.StringCondition;

public class SubstanceStudyFacetQuery  extends AbstractFacetQuery<String,String,StringCondition,SubstanceStudyFacet>  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8910197148842469398L;
	private final static String sql = 
		"select topcategory,endpointcategory,count(*) from substance_protocolapplication\n";
	private final static String group = "group by topcategory,endpointcategory with rollup";
	
	private final static String substance_uuid = " substance_prefix=? and substance_uuid=unhex(?)\n";
	private final static String endpointhash = " document_uuid in (select document_uuid from substance_experiment where endpointhash =unhex(?))\n";
	
	protected SubstanceStudyFacet record;
	
	public SubstanceStudyFacetQuery(String facetURL) {
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
		StringBuilder b = new StringBuilder();
		b.append(sql);
		String c = "\nwhere ";
		if (getFieldname()!=null) {
			b.append(c);
			b.append(substance_uuid);
			c = "\nand ";
		}
		if (getValue()!=null) {
			b.append(c);
			b.append(endpointhash);
		}
		b.append(group);
		return b.toString();
	}

	protected String[] getSubstanceUUID() {
		if (getFieldname()==null) return null;
		return I5Utils.splitI5UUID(getFieldname());
	}
	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		
		List<QueryParam> params1 = null;
		if (getFieldname()!=null) {
			String[] uuid = getSubstanceUUID();
			if (params1==null) params1 = new ArrayList<QueryParam>();
			params1.add(new QueryParam<String>(String.class, uuid[0]));
			params1.add(new QueryParam<String>(String.class, uuid[1].replace("-", "").toLowerCase()));
			
		}
		if (getValue()!=null) {
			String[] uuid = getSubstanceUUID();
			if (params1==null) params1 = new ArrayList<QueryParam>();
			params1.add(new QueryParam<String>(String.class, getValue()));
		} 		
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
