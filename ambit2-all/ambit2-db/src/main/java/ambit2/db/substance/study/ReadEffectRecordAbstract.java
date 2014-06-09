package ambit2.db.substance.study;

import java.sql.ResultSet;

import ambit2.base.data.study.EffectRecord;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.EQCondition;
import ambit2.db.search.IParameterizedQuery;

public abstract class ReadEffectRecordAbstract<F> extends AbstractQuery<F,EffectRecord<String, String, String>, EQCondition, EffectRecord<String, String, String>> 
															implements IQueryRetrieval<EffectRecord<String, String, String>>,
															IParameterizedQuery<F, EffectRecord<String, String, String>, EQCondition>{


	/**
	 * 
	 */
	private static final long serialVersionUID = 2277250526632665919L;
	protected final static String sql = 
		"SELECT idresult,endpoint as effectendpoint,hex(endpointhash) as hash,conditions,unit,loQualifier,loValue,upQualifier,upValue,errQualifier,err,textValue from substance_experiment where document_prefix =? and hex(document_uuid) =?";
	
	
	@Override
	public String getSQL() throws AmbitException {
		return sql;
	}
	@Override
	public EffectRecord<String, String, String> getObject(ResultSet rs)
			throws AmbitException {
		EffectRecord<String, String, String> record = new EffectRecord<String, String, String>();
		try {
			record.setSampleID(rs.getString("hash"));
			record.setEndpoint(rs.getString("effectendpoint"));
			record.setConditions(rs.getString("conditions"));
			record.setUnit(rs.getString("unit"));
			record.setLoQualifier(rs.getString("loQualifier"));
			record.setUpQualifier(rs.getString("upQualifier"));
			if (rs.getString("loValue")!=null)
				record.setLoValue(rs.getDouble("loValue"));
			if (rs.getString("upValue")!=null)
				record.setUpValue(rs.getDouble("upValue"));
			record.setErrQualifier(rs.getString("errQualifier"));
			if (rs.getString("err")!=null)
				record.setErrorValue(rs.getDouble("err"));			
			String text = rs.getString("textValue");
			record.setTextValue(text);
		} catch (Exception x) {
			x.printStackTrace();
		}
		return record;
	}
	
	@Override
	public double calculateMetric(EffectRecord<String, String, String> object) {
		return 1;
	}
	@Override
	public boolean isPrescreen() {
		return false;
	}
	
	

}
