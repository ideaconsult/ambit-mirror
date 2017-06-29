package ambit2.db.substance;

import java.sql.ResultSet;
import java.util.List;

import ambit2.base.data.substance.ParticleTypes;
import ambit2.base.facet.AbstractFacet;
import ambit2.db.update.dataset.QueryCount;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;

public class QueryCountSubstanceTypes extends QueryCount<SubstanceTypefacet> {

	public QueryCountSubstanceTypes(String facetURL) {
		super(facetURL);
		setPageSize(10000);
		setPage(0);
	}

	/**
	 * 
	 */

	protected static String sql_substancetype = "SELECT substanceType,count(*) FROM substance where substanceType is not null group by substanceType\n";

	@Override
	public String getSQL() throws AmbitException {
		return sql_substancetype;
	}

	@Override
	protected int getParam(String key) throws AmbitException {
		return -1;
	}
	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		return null;
	}

	@Override
	protected SubstanceTypefacet createFacet(String facetURL) {
		return new SubstanceTypefacet(facetURL);
	}
	@Override
	public SubstanceTypefacet getObject(ResultSet rs) throws AmbitException {
		//substance?type=substancetype&search=NPO_1892
		SubstanceTypefacet facet = super.getObject(rs);
		
		return facet;
	}
}

class SubstanceTypefacet extends AbstractFacet<String> {
	public SubstanceTypefacet() {
		super();
	}
	public SubstanceTypefacet(String url) {
		super(url);
	}	
	@Override
	public String getSubCategoryURL(String... params) {
		// TODO Auto-generated method stub
		return super.getSubCategoryURL(params);
	}
	@Override
	public String getResultsURL(String... params) {
		if (params==null || params.length==0 ) return super.getResultsURL(params);
		return String.format("%s/substance?type=substancetype&search=%s",params[0],getValue());
	}
	@Override
	public String getSubcategoryTitle() {
		try {
			return ParticleTypes.valueOf(getValue()).getName();
		} catch (Exception x) {
			return getValue();
		}
	}
	@Override
	public String getTitle() {
		return "Substance type";
	}
}