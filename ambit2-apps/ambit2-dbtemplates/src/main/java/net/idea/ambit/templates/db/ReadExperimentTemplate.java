package net.idea.ambit.templates.db;

import java.sql.ResultSet;
import java.util.List;

import net.enanomapper.maker.TR;
import net.enanomapper.maker.TemplateMakerSettings;
import net.idea.modbcum.i.IParameterizedQuery;
import net.idea.modbcum.i.IQueryCondition;
import net.idea.modbcum.i.IQueryObject;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.conditions.EQCondition;
import net.idea.restnet.db.aalocal.user.IDBConfig;

public class ReadExperimentTemplate implements IQueryObject<TR>,
		IParameterizedQuery<TemplateMakerSettings, TR, IQueryCondition>, IQueryRetrieval<TR>, IDBConfig {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8662401515570688215L;
	protected ExperimentTemplateQuery flatQuery;
	
	// protected ReadEffectRecord effectQuery = new ReadEffectRecord();
	// protected ReadSubstanceStudy paQuery = new ReadSubstanceStudy();
	protected TemplateMakerSettings fieldname;

	public ReadExperimentTemplate() {
		flatQuery = null;
	}

	@Override
	public String getSQL() throws AmbitException {
		return flatQuery == null ? null : String.format(flatQuery.getSQL(), getDatabaseName());
	}

	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		return flatQuery == null ? null : flatQuery.getParameters();
	}

	/**
	 * protected ProtocolApplication<Protocol, String, String, IParams, String>
	 * record;
	 */
	@Override
	public TR getObject(ResultSet rs) throws AmbitException {
		TR record = new TR();
		try {
			TR.hix.endpoint.set(record, rs.getString("endpoint"));
			TR.hix.Row.set(record, rs.getInt("row"));
			TR.hix.Column.set(record, rs.getInt("col"));
			TR.hix.id.set(record, rs.getString("idtemplate"));
			TR.hix.JSON_LEVEL1.set(record, rs.getString("level1"));
			TR.hix.JSON_LEVEL2.set(record, rs.getString("level2"));
			TR.hix.JSON_LEVEL3.set(record, rs.getString("level3"));
			TR.hix.Value.set(record, rs.getString("value"));
			TR.hix.cleanedvalue.set(record, rs.getString("value_clean"));
			TR.hix.header1.set(record, rs.getString("header1"));
			TR.hix.Sheet.set(record, rs.getString("sheet"));
			TR.hix.Folder.set(record, rs.getString("folder"));
			TR.hix.File.set(record, rs.getString("file"));
			TR.hix.Annotation.set(record, rs.getString("annotation"));
		} catch (Exception x) {
			x.printStackTrace();
		}

		return record;

	}

	@Override
	public boolean isPrescreen() {
		return flatQuery == null ? null : flatQuery.isPrescreen();
	}

	@Override
	public double calculateMetric(TR object) {
		return 1;
	}

	@Override
	public TR getValue() {
		return null;
	}

	@Override
	public void setValue(TR value) {

	}

	@Override
	public EQCondition getCondition() {
		return flatQuery == null ? null : flatQuery.getCondition();
	}

	@Override
	public void setCondition(IQueryCondition condition) {

	}

	@Override
	public TemplateMakerSettings getFieldname() {
		return fieldname;
	}

	@Override
	public void setFieldname(TemplateMakerSettings fieldname) {
		try {
			flatQuery = new ExperimentTemplateQuery(fieldname);
		} catch (Exception x) {
			flatQuery = null;
		}

	}

	@Override
	public Integer getId() {
		return flatQuery == null ? 0 : flatQuery.getId();
	}

	@Override
	public void setId(Integer id) {
		if (flatQuery != null)
			flatQuery.setId(id);

	}

	@Override
	public int getPage() {
		return flatQuery == null ? 0 : flatQuery.getPage();
	}

	@Override
	public void setPage(int page) {
		if (flatQuery != null)
			flatQuery.setPage(page);
	}

	@Override
	public long getPageSize() {
		return flatQuery == null ? 0 : flatQuery.getPageSize();
	}

	@Override
	public void setPageSize(long records) {
		if (flatQuery != null)
			flatQuery.setPageSize(records);
	}

	@Override
	public String getKey() {
		return flatQuery == null ? null : flatQuery.getKey();
	}

	@Override
	public String getCategory() {
		return flatQuery == null ? null : flatQuery.getCategory();
	}

	@Override
	public boolean supportsPaging() {
		return flatQuery == null ? null : flatQuery.supportsPaging();
	}

	@Override
	public void setDatabaseName(String name) {
		flatQuery.setDatabaseName(name);

	}

	@Override
	public String getDatabaseName() {
		return flatQuery.getDatabaseName();
	}
}