package ambit2.db.substance.study;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.I5Utils;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.ProtocolEffectRecord;

public class ReadEffectRecordBySubstance extends
	ReadEffectRecordAbstract<SubstanceRecord, ProtocolEffectRecord<String, String, String>> {

    /**
	 * 
	 */
    private static final long serialVersionUID = -3708679134698941319L;
    protected SubstanceRecord record = new SubstanceRecord();

    // "SELECT idresult,endpoint as effectendpoint,conditions,unit,loQualifier,loValue,upQualifier,upValue,textValue from substance_experiment where document_prefix =? and hex(document_uuid) =?";

    private String _sql = "select p.document_prefix,hex(p.document_uuid) u,\n"
	    + "p.topcategory,p.endpointcategory,guidance,params,reference,idresult,studyResultType,interpretation_result,\n"
	    + "e.endpoint as effectendpoint,hex(endpointhash) as hash,conditions,unit,loQualifier, loValue, upQualifier, upValue, textValue, err, errQualifier,p.endpoint as pendpoint\n"
	    + "from substance s join substance_protocolapplication p on s.prefix=p.substance_prefix and s.uuid=p.substance_uuid\n"
	    + "join substance_experiment e on p.document_prefix=e.document_prefix and p.document_uuid=e.document_uuid\n"
	    + "where p.substance_prefix =? and p.substance_uuid =unhex(?)\n";

    @Override
    public String getSQL() throws AmbitException {
	return _sql;
    }

    @Override
    public List<QueryParam> getParameters() throws AmbitException {
	List<QueryParam> params = new ArrayList<QueryParam>();
	if (getFieldname() == null || getFieldname().getSubstanceUUID() == null)
	    throw new AmbitException("Empty substance id");
	String[] uuid = new String[] { null, getFieldname().getSubstanceUUID() };
	uuid = I5Utils.splitI5UUID(getFieldname().getSubstanceUUID());
	params.add(new QueryParam<String>(String.class, uuid[0]));
	params.add(new QueryParam<String>(String.class, uuid[1].replace("-", "").toLowerCase()));
	return params;
    }

    @Override
    protected ProtocolEffectRecord<String, String, String> createEffectRecord() {
	return new ProtocolEffectRecord<String, String, String>();
    }

    @Override
    public ProtocolEffectRecord<String, String, String> getObject(ResultSet rs) throws AmbitException {
	ProtocolEffectRecord<String, String, String> effect = super.getObject(rs);
	try {
	    effect.setIdresult(rs.getInt("idresult"));
	} catch (Exception x) {
	    effect.setIdresult(-1);
	}
	try {
	    effect.setInterpretationResult(rs.getString("interpretation_result"));
	} catch (Exception x) {
		x.printStackTrace();
	    effect.setInterpretationResult(null);
	}
	try {
	    effect.setStudyResultType(rs.getString("studyResultType"));
	} catch (Exception x) {
	    effect.setStudyResultType(null);
	}		
	try {
	    try {
		effect.setDocumentUUID(rs.getString("document_prefix") + "-"
			+ I5Utils.addDashes(rs.getString("u").toString().toLowerCase()));
	    } catch (Exception xx) {
		effect.setDocumentUUID(null);
	    }
	    Protocol protocol = new Protocol(rs.getString("pendpoint"));
	    protocol.addGuideline(rs.getString("guidance"));
	    protocol.setCategory(rs.getString("endpointcategory"));
	    protocol.setTopCategory(rs.getString("topcategory"));
	    effect.setProtocol(protocol);

	    /*
	     * String topcategory = rs.getString("topcategory"); String
	     * endpointcategory = rs.getString("endpointcategory"); String
	     * guidance = rs.getString("guidance"); String conditions =
	     * rs.getString("conditions");
	     * 
	     * String key = (topcategory==null?"":topcategory)+
	     * (endpointcategory==null?"":endpointcategory)+
	     * (guidance==null?"":guidance)+ (conditions==null?"":conditions);
	     * effect
	     * .setSampleID(UUID.nameUUIDFromBytes(key.getBytes()).toString());
	     */
	} catch (Exception x) {
	    logger.log(Level.WARNING, x.getMessage());
	}

	return effect;
    }

}
