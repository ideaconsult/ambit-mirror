package ambit2.db.substance;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.SubstanceRecord;


public class ReadSubstanceByExternalIDentifier extends AbstractReadSubstance<String,String> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5352529378365800440L;
	private SubstanceRecord record = new SubstanceRecord();
	private static final String _defaultType = "other:CompTox";
	private static final String _defaultID = "Ambit Transfer";
	
	private static String sql_byexternaltype_and_id =
		"select substance.idsubstance,prefix,hex(uuid) as huuid,documentType,format,name,publicname,content,substanceType,rs_prefix,hex(rs_uuid) as rs_huuid,owner_prefix,hex(owner_uuid) as owner_huuid,owner_name "+ 
		"from substance join substance_ids using(prefix,uuid) where type = ? and id = ?";
	
	private static String sql_byexternalid =
		"select substance.idsubstance,prefix,hex(uuid) as huuid,documentType,format,name,publicname,content,substanceType,rs_prefix,hex(rs_uuid) as rs_huuid,owner_prefix,hex(owner_uuid) as owner_huuid,owner_name "+ 
		"from substance join substance_ids using(prefix,uuid) where id = ?";	

	public ReadSubstanceByExternalIDentifier() {
		this(_defaultType,_defaultID);
	}
	public ReadSubstanceByExternalIDentifier(String type,String id) {
		super();
		setFieldname(type);
		setValue(id);
	}	
	@Override
	public String getSQL() throws AmbitException {
		if (getFieldname()!=null)
			return sql_byexternaltype_and_id;
		else
			return sql_byexternalid;
	}

	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		if (getFieldname()!=null)
			params.add(new QueryParam<String>(String.class, getFieldname()));
		params.add(new QueryParam<String>(String.class, getValue()==null?_defaultID:getValue()));
		return params;
	}

	@Override
	protected SubstanceRecord getRecord() {
		return record;
	}
	
	@Override
	public String toString() {
		return String.format("%s=%s", getFieldname()==null?_defaultType:getFieldname(),getValue()==null?_defaultID:getValue());
	}

}
