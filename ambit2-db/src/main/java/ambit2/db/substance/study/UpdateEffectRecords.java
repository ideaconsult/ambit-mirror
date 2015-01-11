package ambit2.db.substance.study;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;
import ambit2.base.data.I5Utils;
import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.study.Params;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.ProtocolApplication;

public class UpdateEffectRecords extends AbstractUpdate<ProtocolApplication<Protocol, Params,String,Params,String>,EffectRecord> {
	protected String substanceUUID = null;
	
	public String getSubstanceUUID() {
		return substanceUUID;
	}
	public void setSubstanceUUID(String substanceUUID) {
		this.substanceUUID = substanceUUID;
	}
	
	private static final String[] create_sql = {
		"INSERT INTO substance_experiment (document_prefix,document_uuid,endpoint,conditions,unit,loQualifier,loValue,upQualifier,upValue,textValue,errQualifier,err,endpointhash,topcategory,endpointcategory,substance_prefix,substance_uuid)\n"+
		"values(?,unhex(replace(?,'-','')),?,?,?,?,?,?,?,?,?,?,unhex(sha1(concat(ifnull(?,''),ifnull(?,''),ifnull(?,'')))),?,?,?,unhex(replace(?,'-','')))"
	};
	

	public UpdateEffectRecords(String substanceUUID,ProtocolApplication<Protocol, Params,String,Params,String> papp, EffectRecord effect) {
		super();
		setSubstanceUUID(substanceUUID);
		setGroup(papp);
		setObject(effect);
	}
	public void setID(int index, int id) {
	}

	protected void check() throws AmbitException  {
		if (getGroup()==null) throw new AmbitException("No document UUID");
		if (getObject() == null) throw new AmbitException("No measurement");
		//if (getObject().getEndpoint() == null) throw new AmbitException("No endpoint");
	}
	public String[] getSQL() throws AmbitException {
		check();
		return create_sql;
	}
	@Override
	public List<QueryParam> getParameters(int index) throws AmbitException {
		check();
		List<QueryParam> params1 = new ArrayList<QueryParam>();
		addParameters(index, params1);
		return params1;
	}

	public void addParameters(int index,List<QueryParam> params1) throws AmbitException {
		String o_uuid = getGroup().getDocumentUUID();
		String[] cmp_uuid = {null,o_uuid==null?null:o_uuid.toString()};
		if (o_uuid!=null) cmp_uuid = I5Utils.splitI5UUID(o_uuid.toString());
		params1.add(new QueryParam<String>(String.class, cmp_uuid[0]));
		params1.add(new QueryParam<String>(String.class, cmp_uuid[1]));		

		String endpoint = getObject().getEndpoint() ==null?"":truncate(getObject().getEndpoint().toString(),64);
		String conditions =  getObject().getConditions()==null?null:getObject().getConditions().toString();
		params1.add(new QueryParam<String>(String.class, endpoint));
		params1.add(new QueryParam<String>(String.class, conditions));
		Object unit = getObject().getUnit();
		if (unit!=null && unit.toString().length()>45) unit = unit.toString().substring(0,45);
		params1.add(new QueryParam<String>(String.class,unit==null?null:unit.toString()));
		Object lq = getObject().getLoQualifier();
		if (lq!=null && lq.toString().length()>6) lq = lq.toString().substring(0,5);
		params1.add(new QueryParam<String>(String.class,lq==null?null:lq.toString()));
		params1.add(new QueryParam<Double>(Double.class,getObject().getLoValue()));
		Object uq = getObject().getUpQualifier();
		if (uq!=null && uq.toString().length()>6) uq = uq.toString().substring(0,5);
		params1.add(new QueryParam<String>(String.class,uq==null?null:uq.toString()));
		params1.add(new QueryParam<Double>(Double.class,getObject().getUpValue()));
		
		params1.add(new QueryParam<String>(String.class,getObject().getTextValue()==null?null:getObject().getTextValue().toString()));
		

		params1.add(new QueryParam<String>(String.class,truncate(getObject().getErrQualifier(),6)));
		params1.add(new QueryParam<Double>(Double.class,getObject().getErrorValue()));
		
		//hash
		params1.add(new QueryParam<String>(String.class, endpoint));
		params1.add(new QueryParam<String>(String.class,unit==null?null:unit.toString()));
		params1.add(new QueryParam<String>(String.class,conditions));
		
		params1.add(new QueryParam<String>(String.class,getGroup().getProtocol().getTopCategory()));
		params1.add(new QueryParam<String>(String.class,getGroup().getProtocol().getCategory()));
		
		Object s_uuid = getSubstanceUUID();
		String[] subst_uuid = {null,s_uuid==null?null:s_uuid.toString()};
		if (subst_uuid==null || subst_uuid.length<2) throw new AmbitException("Invalid UUID "+s_uuid.toString());
		if (s_uuid!=null) subst_uuid = I5Utils.splitI5UUID(s_uuid.toString());
		params1.add(new QueryParam<String>(String.class, subst_uuid[0]));
		params1.add(new QueryParam<String>(String.class, subst_uuid[1]));

	}
}