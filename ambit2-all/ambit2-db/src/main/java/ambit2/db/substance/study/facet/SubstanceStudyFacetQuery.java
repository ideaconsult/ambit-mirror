package ambit2.db.substance.study.facet;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.facet.AbstractFacetQuery;
import ambit2.base.data.I5Utils;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.Protocol;
import ambit2.base.facet.SubstanceStudyFacet;
import ambit2.db.search.StringCondition;

public class SubstanceStudyFacetQuery
		extends
		AbstractFacetQuery<SubstanceRecord, String, StringCondition, SubstanceStudyFacet> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8910197148842469398L;
	private final static String sql = "select topcategory,endpointcategory,count(*),null from substance_protocolapplication\n";
	private final static String sql_ir = "select topcategory,endpointcategory,count(*),interpretation_result from substance_protocolapplication\n";
	private final static String group = "group by topcategory,endpointcategory with rollup";
	private final static String group_ir = "group by topcategory,endpointcategory,interpretation_result";

	private final static String substance_uuid = " substance_prefix=? and substance_uuid=unhex(?)\n";
	private final static String endpointhash = " document_uuid in (select document_uuid from substance_experiment where endpointhash =unhex(?))\n";
	private final static String where_topcategory = " topcategory=?\n";
	private final static String where_category = " topcategory=? and endpointcategory=?\n";

	protected SubstanceStudyFacet record;
	protected boolean groupByInterpretationResult = false;
	protected String topcategory = null;
	protected String facetURL;
	protected boolean reuseRecord = true;

	public boolean isReuseRecord() {
		return reuseRecord;
	}

	public void setReuseRecord(boolean reuseRecord) {
		this.reuseRecord = reuseRecord;
	}

	public String getTopcategory() {
		return topcategory;
	}

	public void setTopcategory(String topcategory) {
		this.topcategory = topcategory;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	protected String category = null;

	public boolean isGroupByInterpretationResult() {
		return groupByInterpretationResult;
	}

	public void setGroupByInterpretationResult(
			boolean groupByInterpretationResult) {
		this.groupByInterpretationResult = groupByInterpretationResult;
	}

	public SubstanceStudyFacetQuery(String facetURL) {
		super(facetURL);
		this.facetURL = facetURL;
		record =  null;
	}

	public SubstanceStudyFacet getRecord() {
		return record;
	}

	public void setRecord(SubstanceStudyFacet record) {
		this.record = record;
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
		b.append(isGroupByInterpretationResult() ? sql_ir : sql);
		String c = "\nwhere ";
		if (getFieldname() != null && getFieldname().getSubstanceUUID() != null) {
			b.append(c);
			b.append(substance_uuid);
			c = "\nand ";
		}
		if (getValue() != null) {
			b.append(c);
			b.append(endpointhash);
		}
		if (getTopcategory() != null) {
			b.append(c);
			if (getCategory() != null) {
				b.append(where_category);
			} else
				b.append(where_topcategory);
		}
		b.append(isGroupByInterpretationResult() ? group_ir : group);
		return b.toString();
	}

	protected String[] getSubstanceUUID() {
		if (getFieldname() != null && getFieldname().getSubstanceUUID() != null)
			return I5Utils.splitI5UUID(getFieldname().getSubstanceUUID());
		else
			return null;
	}

	@Override
	public List<QueryParam> getParameters() throws AmbitException {

		List<QueryParam> params1 = null;
		if (getFieldname() != null && getFieldname().getSubstanceUUID() != null) {
			String[] uuid = getSubstanceUUID();
			if (params1 == null)
				params1 = new ArrayList<QueryParam>();
			params1.add(new QueryParam<String>(String.class, uuid[0]));
			params1.add(new QueryParam<String>(String.class, uuid[1].replace(
					"-", "").toLowerCase()));

		}
		if (getValue() != null) {
			String[] uuid = getSubstanceUUID();
			if (params1 == null)
				params1 = new ArrayList<QueryParam>();
			params1.add(new QueryParam<String>(String.class, getValue()));
		}

		if (getTopcategory() != null) {
			params1.add(new QueryParam<String>(String.class, getTopcategory()));
			if (getCategory() != null) {
				params1.add(new QueryParam<String>(String.class, getCategory()));
			}
		}
		return params1;
	}

	@Override
	public SubstanceStudyFacet getObject(ResultSet rs) throws AmbitException {
		try {
			if (!reuseRecord || record==null) record = createFacet(facetURL);

			record.setValue(rs.getString(1));
			record.setSubcategoryTitle(rs.getString(2));
			record.setCount(rs.getInt(3));
			record.setInterpretation_result(rs.getString(4));
			try {
				Protocol._categories category = Protocol._categories.valueOf(rs
						.getString(2));
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
