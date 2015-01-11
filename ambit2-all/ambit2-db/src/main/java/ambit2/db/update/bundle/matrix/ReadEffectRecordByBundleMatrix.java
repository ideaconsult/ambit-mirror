package ambit2.db.update.bundle.matrix;

import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.db.substance.study.ReadEffectRecordBySubstance;

public class ReadEffectRecordByBundleMatrix extends ReadEffectRecordBySubstance {
	protected SubstanceEndpointsBundle bundle;
	/**
	 * 
	 */
	private static final long serialVersionUID = -1885870102248748663L;

	private String sql = 
		"select p.document_prefix,hex(p.document_uuid) u,\n"+
		"p.topcategory,p.endpointcategory,guidance,params,reference,idresult,\n"+
		"e.endpoint as effectendpoint,hex(e.endpointhash) as hash,conditions,unit,loQualifier, loValue, upQualifier, upValue, textValue, err, errQualifier,p.endpoint as pendpoint\n"+ 
		"from bundle_substance_protocolapplication p\n"+
		"join bundle_substance_experiment e on p.document_prefix=e.document_prefix and p.document_uuid=e.document_uuid\n"+
		"where p.idbundle=e.idbundle and p.substance_prefix =? and p.substance_uuid =unhex(?) and p.idbundle=?";
	
	public ReadEffectRecordByBundleMatrix(SubstanceEndpointsBundle bundle) {
		super();
		this.bundle = bundle;
	}
	@Override
	public String getSQL() throws AmbitException {
		return sql;
	}
	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		if (bundle==null || bundle.getID()<=0) throw new AmbitException("Bundle not defined");
		
		List<QueryParam> params = super.getParameters();
		params.add(new QueryParam<Integer>(Integer.class, bundle.getID()));
		return params;
	}
}
