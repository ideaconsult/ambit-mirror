package ambit2.db.substance.study;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.I5Utils;
import ambit2.base.data.study.EffectRecord;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.EQCondition;
import ambit2.db.search.QueryParam;

/**
 * Reads effects records given document uuid
 * @author nina
 *
 */
public class ReadEffectRecord  extends AbstractQuery<String,EffectRecord<String, String, String>, EQCondition, EffectRecord> implements IQueryRetrieval<EffectRecord>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5430387137460722198L;
	protected EffectRecord record = new EffectRecord();
	public final static String sql = 
		"SELECT endpoint,conditions,unit,loQualifier,loValue,upQualifier,upvalue from substance_experiment where document_prefix =? and hex(document_uuid) u=?";
	
	@Override
	public String getSQL() throws AmbitException {
		return sql;
	}

	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		if (getFieldname()==null) throw new AmbitException("Empty substance id");
		String[] uuid = new String[]{null,getFieldname()};
		uuid = I5Utils.splitI5UUID(getFieldname());
		params.add(new QueryParam<String>(String.class, uuid[0]));
		params.add(new QueryParam<String>(String.class, uuid[1].replace("-", "").toLowerCase()));
		return params;
	}

	@Override
	public EffectRecord getObject(ResultSet rs) throws AmbitException {
		record.clear();
		try {
			record.setEndpoint(rs.getString("endpoint"));
			record.setConditions(rs.getString("conditions"));
			record.setUnit(rs.getString("unit"));
			record.setLoQualifier(rs.getString("loQualifier"));
			record.setUpQualifier(rs.getString("upQualifier"));
			record.setLoValue(rs.getDouble("loValue"));
			record.setUpValue(rs.getDouble("upValue"));
		} catch (Exception x) {
			x.printStackTrace();
		}
		return record;
	}

	@Override
	public boolean isPrescreen() {
		return false;
	}

	@Override
	public double calculateMetric(EffectRecord object) {
		return 1;
	}

	

}
