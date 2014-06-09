package ambit2.db.substance.study;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.I5Utils;
import ambit2.base.data.study.EffectRecord;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.search.QueryParam;
import ambit2.db.update.AbstractUpdate;

public class UpdateEffectRecords extends AbstractUpdate<String,EffectRecord> {

	public static final String[] create_sql = {
		"INSERT INTO substance_experiment (document_prefix,document_uuid,endpoint,conditions,unit,loQualifier,loValue,upQualifier,upValue,textValue,errQualifier,err,endpointhash)\n"+
		"values(?,unhex(replace(?,'-','')),?,?,?,?,?,?,?,?,?,?,unhex(sha1(concat(ifnull(?,''),ifnull(?,''),ifnull(?,'')))))"
		
		//unhex(sha1(concat(ifnull(endpoint,""),ifnull(unit,""),ifnull(conditions,""))))
	};
	

	public UpdateEffectRecords(String documentuuid, EffectRecord effect) {
		super();
		setGroup(documentuuid);
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
		
		String o_uuid = getGroup();
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
		return params1;
	}
}