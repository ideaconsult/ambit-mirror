package ambit2.db.substance;

import java.util.List;

import ambit2.base.facet.AbstractFacet;
import ambit2.db.update.dataset.QueryCount;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;

public class QueryCountSubstanceTypes extends QueryCount<AbstractFacet<String>> {

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

}
