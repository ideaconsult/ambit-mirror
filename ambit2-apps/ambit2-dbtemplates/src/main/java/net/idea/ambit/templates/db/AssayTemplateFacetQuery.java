package net.idea.ambit.templates.db;

import java.sql.ResultSet;
import java.util.List;

import net.enanomapper.maker.TR;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.conditions.StringCondition;
import net.idea.modbcum.q.facet.AbstractFacetQuery;
import net.idea.restnet.db.aalocal.user.IDBConfig;


public class AssayTemplateFacetQuery extends AbstractFacetQuery<TR, String, StringCondition, AssayTemplateFacet>
		implements IDBConfig {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3158355313226120851L;
	protected final static String _sql = "SELECT idtemplate,endpoint,assay,module,count(*) c FROM %s.assay_template group by idtemplate";
	public AssayTemplateFacetQuery(String facetURL) {
		super(facetURL);
	}

	protected String dbname;

	public String getDatabaseName() {
		return dbname;
	}

	public void setDatabaseName(String arg0) {
		this.dbname = arg0;

	}

	@Override
	public double calculateMetric(AssayTemplateFacet arg0) {
		return 1;
	}

	@Override
	public boolean isPrescreen() {
		return false;
	}

	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		return null;
	}

	@Override
	public String getSQL() throws AmbitException {
		return String.format(_sql,getDatabaseName());
	}

	@Override
	public AssayTemplateFacet getObject(ResultSet rs) throws AmbitException {
		try {
			AssayTemplateFacet facet = createFacet(null);
			TR record = new TR();
			facet.setValue(rs.getString("idtemplate"));
			TR.hix.id.set(record, facet.getValue());
			TR.hix.endpoint.set(record, rs.getString("endpoint"));
			TR.hix.Sheet.set(record, rs.getString("assay"));
			facet.setCount(rs.getInt("c"));
			return facet;
		} catch (Exception x) {
			return null;
		}

	}

	@Override
	protected AssayTemplateFacet createFacet(String arg0) {
		return new AssayTemplateFacet();
	}
}
