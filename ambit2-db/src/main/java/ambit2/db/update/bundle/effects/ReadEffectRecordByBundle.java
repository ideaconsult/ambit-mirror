package ambit2.db.update.bundle.effects;

import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.db.substance.study.ReadEffectRecordBySubstance;

public class ReadEffectRecordByBundle extends ReadEffectRecordBySubstance {
	protected SubstanceEndpointsBundle bundle;
	/**
	 * 
	 */
	private static final long serialVersionUID = -1885870102248748663L;

	private String b_sql = 
		"select p.document_prefix,hex(p.document_uuid) u,\n"+
		"p.topcategory,p.endpointcategory,guidance,params,reference,studyResultType,idresult,\n"+
		"e.endpoint as effectendpoint,hex(e.endpointhash) as hash,conditions,unit,loQualifier, loValue, upQualifier, upValue, textValue, err, errQualifier,p.endpoint as pendpoint\n"+ 
		"from substance s join substance_protocolapplication p on s.prefix=p.substance_prefix and s.uuid=p.substance_uuid\n"+
		"join substance_experiment e on p.document_prefix=e.document_prefix and p.document_uuid=e.document_uuid\n"+
		"join bundle_endpoints b on b.topcategory=e.topcategory and b.endpointcategory=e.endpointcategory\n"+
		"where p.substance_prefix =? and p.substance_uuid =unhex(?) and idbundle=?";
	
	public ReadEffectRecordByBundle(SubstanceEndpointsBundle bundle) {
		super();
		this.bundle = bundle;
	}
	@Override
	public String getSQL() throws AmbitException {
		return b_sql;
	}
	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		if (bundle==null || bundle.getID()<=0) throw new AmbitException("Bundle not defined");
		
		List<QueryParam> params = super.getParameters();
		params.add(new QueryParam<Integer>(Integer.class, bundle.getID()));
		return params;
	}
}
