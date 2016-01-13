package ambit2.db.substance.study;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IParameterizedQuery;
import net.idea.modbcum.i.IQueryCondition;
import net.idea.modbcum.i.IQueryObject;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.conditions.EQCondition;
import ambit2.base.data.I5Utils;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.study.ProtocolApplication;

public class ReadSubstanceStudyFlat implements
		IQueryObject<List<ProtocolApplication>>,
		IParameterizedQuery<SubstanceRecord,List<ProtocolApplication>, IQueryCondition>,
		IQueryRetrieval<List<ProtocolApplication>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8662401515570688215L;
	protected SubstanceStudyFlatQuery flatQuery;
	protected ReadEffectRecord effectQuery = new ReadEffectRecord();
	protected ReadSubstanceStudy paQuery = new ReadSubstanceStudy();
	protected List<ProtocolApplication> measurements;
	protected SubstanceRecord fieldname;

	public List<ProtocolApplication> getRecord() {
		return measurements;
	}

	public void setRecord(List<ProtocolApplication> record) {
		this.measurements = record;
	}

	public ReadSubstanceStudyFlat() {
		flatQuery = null;
	}

	@Override
	public String getSQL() throws AmbitException {
		return flatQuery == null ? null : flatQuery.getSQL();
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
	public List<ProtocolApplication> getObject(ResultSet rs)
			throws AmbitException {

		if (measurements == null) {
			measurements = new ArrayList<ProtocolApplication>();
		}
		ProtocolApplication thisPA = null;
		try {
			String documentUUID = 
					I5Utils.getPrefixedUUID(
							rs.getString("document_prefix"), rs.getString("document_uuid"));
			for (ProtocolApplication pa : measurements)
				if (pa.getDocumentUUID().equals(documentUUID)) {
					thisPA = pa;
					break;
				}
			if (thisPA == null) {
				paQuery.setRecord(null);
				thisPA = paQuery.getObject(rs);
				thisPA.setDocumentUUID(documentUUID);

				try {
					thisPA.setSubstanceUUID(
							I5Utils.getPrefixedUUID(
									rs.getString("s_prefix"), rs.getString("s_uuid")));
				} catch (Exception xx) {
					xx.printStackTrace();
					thisPA.setSubstanceUUID(null);
				}
				measurements.add(thisPA);
			}
			EffectRecord effect = effectQuery.getObject(rs);
			thisPA.addEffect(effect);

		} catch (Exception xx) {
			xx.printStackTrace();
		}

		return measurements;
	}

	@Override
	public boolean isPrescreen() {
		return flatQuery == null ? null : flatQuery.isPrescreen();
	}

	@Override
	public double calculateMetric(List<ProtocolApplication> object) {
		return 1;
	}

	@Override
	public List<ProtocolApplication> getValue() {
		return null;
	}
	@Override
	public void setValue(List<ProtocolApplication> value) {
		
	}

	@Override
	public EQCondition getCondition() {
		return flatQuery == null ? null : flatQuery.getCondition();
	}

	@Override
	public void setCondition(IQueryCondition condition) {

	}

	@Override
	public SubstanceRecord getFieldname() {
		return fieldname;
	}

	@Override
	public void setFieldname(SubstanceRecord fieldname) {
		try {
			flatQuery = new SubstanceStudyFlatQuery(fieldname);
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
}
