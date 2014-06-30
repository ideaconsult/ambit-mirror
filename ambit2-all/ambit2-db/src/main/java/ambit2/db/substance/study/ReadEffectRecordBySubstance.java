package ambit2.db.substance.study;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ambit2.base.data.I5Utils;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.EffectRecord;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.search.QueryParam;

public class ReadEffectRecordBySubstance extends ReadEffectRecordAbstract<SubstanceRecord> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3708679134698941319L;
	protected SubstanceRecord record = new SubstanceRecord();
	
	
	//	"SELECT idresult,endpoint as effectendpoint,conditions,unit,loQualifier,loValue,upQualifier,upValue,textValue from substance_experiment where document_prefix =? and hex(document_uuid) =?";

	
	private String sql = 
			"select p.document_prefix,hex(p.document_uuid) u,\n"+
			"p.topcategory,p.endpointcategory,guidance,params,reference,idresult,\n"+
			"e.endpoint as effectendpoint,hex(endpointhash) as hash,conditions,unit,loQualifier, loValue, upQualifier, upValue, textValue, err, errQualifier\n"+ 
			"from substance s join substance_protocolapplication p on s.prefix=p.substance_prefix and s.uuid=p.substance_uuid\n"+
			"join substance_experiment e on p.document_prefix=e.document_prefix and p.document_uuid=e.document_uuid\n"+
			"where p.substance_prefix =? and hex(p.substance_uuid) =?\n"+
			"order by p.document_prefix,p.document_uuid";
	
	@Override
	public String getSQL() throws AmbitException {
		return sql;
	}

	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		if (getFieldname()==null) throw new AmbitException("Empty substance id");
		String[] uuid = new String[]{null,getFieldname().getCompanyUUID()};
		uuid = I5Utils.splitI5UUID(getFieldname().getCompanyUUID());
		params.add(new QueryParam<String>(String.class, uuid[0]));
		params.add(new QueryParam<String>(String.class, uuid[1].replace("-", "").toLowerCase()));
		return params;
	}

	@Override
	public EffectRecord<String, String, String> getObject(ResultSet rs)
			throws AmbitException {
		EffectRecord<String, String, String> effect = super.getObject(rs);
		try {
			/*
			String topcategory = rs.getString("topcategory");
			String endpointcategory = rs.getString("endpointcategory");
			String guidance = rs.getString("guidance");
			String conditions = rs.getString("conditions");

			String key = 
				(topcategory==null?"":topcategory)+
				(endpointcategory==null?"":endpointcategory)+
				(guidance==null?"":guidance)+
				(conditions==null?"":conditions);
			effect.setSampleID(UUID.nameUUIDFromBytes(key.getBytes()).toString());
			*/
		} catch (Exception x) {
			x.printStackTrace();
		}
		
		return effect;
	}

}
