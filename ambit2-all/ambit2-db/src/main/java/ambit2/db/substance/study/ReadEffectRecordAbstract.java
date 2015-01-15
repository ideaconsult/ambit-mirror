package ambit2.db.substance.study;

import java.sql.ResultSet;

import net.idea.modbcum.i.IParameterizedQuery;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import ambit2.base.data.study.EffectRecord;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.EQCondition;

public abstract class ReadEffectRecordAbstract<F, ER extends EffectRecord<String, String, String>> extends
	AbstractQuery<F, ER, EQCondition, ER> implements IQueryRetrieval<ER>, IParameterizedQuery<F, ER, EQCondition> {

    /**
	 * 
	 */
    private static final long serialVersionUID = 2277250526632665919L;
    protected final static String sql = "SELECT idresult,endpoint as effectendpoint,hex(endpointhash) as hash,conditions,unit,loQualifier,loValue,upQualifier,upValue,errQualifier,err,textValue from substance_experiment where document_prefix =? and document_uuid =unhex(?)";

    @Override
    public String getSQL() throws AmbitException {
	return sql;
    }

    protected ER createEffectRecord() {
	return (ER) new EffectRecord<String, String, String>();
    }

    @Override
    public ER getObject(ResultSet rs) throws AmbitException {
	ER record = createEffectRecord();
	try {
	    record.setSampleID(rs.getString("hash"));
	    record.setEndpoint(rs.getString("effectendpoint"));
	    record.setConditions(rs.getString("conditions"));
	    record.setUnit(rs.getString("unit"));
	    record.setLoQualifier(rs.getString("loQualifier"));
	    record.setUpQualifier(rs.getString("upQualifier"));
	    if (rs.getString("loValue") != null)
		record.setLoValue(rs.getDouble("loValue"));
	    if (rs.getString("upValue") != null)
		record.setUpValue(rs.getDouble("upValue"));
	    record.setErrQualifier(rs.getString("errQualifier"));
	    if (rs.getString("err") != null)
		record.setErrorValue(rs.getDouble("err"));
	    String text = rs.getString("textValue");
	    record.setTextValue(text);
	} catch (Exception x) {
	    x.printStackTrace();
	}
	return record;
    }

    @Override
    public double calculateMetric(ER object) {
	return 1;
    }

    @Override
    public boolean isPrescreen() {
	return false;
    }

}
