package ambit2.db.update.bundle.matrix;

import java.sql.ResultSet;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.study.ProtocolEffectRecord;
import ambit2.base.data.study.ProtocolEffectRecordMatrix;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.db.substance.study.ReadEffectRecordBySubstance;

public class ReadEffectRecordByBundleMatrix extends ReadEffectRecordBySubstance {
    protected SubstanceEndpointsBundle bundle;
    public enum _matrix {matrix_working,matrix_final,deleted_values};
    protected _matrix matrix = _matrix.matrix_working;
    /**
	 * 
	 */
    private static final long serialVersionUID = -1885870102248748663L;

    private String sql_working = "select p.document_prefix,hex(p.document_uuid) u,\n"
	    + "p.topcategory,p.endpointcategory,guidance,params,reference,idresult,interpretation_result,\n"
	    + "e.endpoint as effectendpoint,hex(e.endpointhash) as hash,conditions,unit,loQualifier, loValue, upQualifier, upValue, textValue, err, errQualifier,p.endpoint as pendpoint,e.copied,e.deleted,e.remarks\n"
	    + "from bundle_substance_protocolapplication p\n"
	    + "join bundle_substance_experiment e on p.document_prefix=e.document_prefix and p.document_uuid=e.document_uuid\n"
	    + "where p.idbundle=e.idbundle and p.substance_prefix =? and p.substance_uuid =unhex(?) and p.idbundle=?";

    private String sql_final = "select p.document_prefix,hex(p.document_uuid) u,\n"
	    + "p.topcategory,p.endpointcategory,guidance,params,reference,idresult,interpretation_result,\n"
	    + "e.endpoint as effectendpoint,hex(e.endpointhash) as hash,conditions,unit,loQualifier, loValue, upQualifier, upValue, textValue, err, errQualifier,p.endpoint as pendpoint,e.copied,e.deleted,e.remarks\n"
	    + "from bundle_final_protocolapplication p\n"
	    + "join bundle_final_experiment e on p.document_prefix=e.document_prefix and p.document_uuid=e.document_uuid\n"
	    + "where p.idbundle=e.idbundle and p.substance_prefix =? and p.substance_uuid =unhex(?) and p.idbundle=?";

    
    public ReadEffectRecordByBundleMatrix(SubstanceEndpointsBundle bundle, _matrix matrix) {
	super();
	this.bundle = bundle;
	this.matrix = matrix;
    }

    @Override
    public String getSQL() throws AmbitException {
	switch(matrix) {
	case matrix_final: return sql_final;
	default: return sql_working;	
	}
	
    }

    @Override
    public List<QueryParam> getParameters() throws AmbitException {
	if (bundle == null || bundle.getID() <= 0)
	    throw new AmbitException("Bundle not defined");

	List<QueryParam> params = super.getParameters();
	params.add(new QueryParam<Integer>(Integer.class, bundle.getID()));
	return params;
    }

    @Override
    protected ProtocolEffectRecord<String, String, String> createEffectRecord() {
	return new ProtocolEffectRecordMatrix<String, String, String>();
    }

    @Override
    public ProtocolEffectRecord<String, String, String> getObject(ResultSet rs) throws AmbitException {
	ProtocolEffectRecord<String, String, String> effect = super.getObject(rs);
	if (effect instanceof ProtocolEffectRecordMatrix)
	    try {
		((ProtocolEffectRecordMatrix)effect).setCopied(rs.getBoolean("copied"));
		((ProtocolEffectRecordMatrix)effect).setDeleted(rs.getBoolean("deleted"));
		((ProtocolEffectRecordMatrix)effect).setRemarks(rs.getString("remarks"));
	    } catch (Exception x) {
	    }
	return effect;
    }
}
