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
		"INSERT INTO substance_experiment (document_prefix,document_uuid,endpoint,conditions,unit,loQualifier,loValue,upQualifier,upvalue)\n"+
		"values(?,unhex(replace(?,'-','')),?,?,?,?,?,?,?)"
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
		if (getObject().getEndpoint() == null) throw new AmbitException("No endpoint");
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

		params1.add(new QueryParam<String>(String.class, getObject().getEndpoint().toString()));
		params1.add(new QueryParam<String>(String.class, getObject().getConditions()==null?null:getObject().getConditions().toString()));
		params1.add(new QueryParam<String>(String.class,getObject().getUnit()==null?null:getObject().getUnit().toString()));
		params1.add(new QueryParam<String>(String.class,getObject().getLoQualifier()==null?null:getObject().getLoQualifier().toString()));
		params1.add(new QueryParam<Double>(Double.class,getObject().getLoValue()));
		params1.add(new QueryParam<String>(String.class,getObject().getUpQualifier()==null?null:getObject().getUpQualifier().toString()));
		params1.add(new QueryParam<Double>(Double.class,getObject().getUpValue()));
		return params1;
	}
}