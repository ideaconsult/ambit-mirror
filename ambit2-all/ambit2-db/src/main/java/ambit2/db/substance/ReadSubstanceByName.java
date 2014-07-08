package ambit2.db.substance;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.SubstanceRecord;

public class ReadSubstanceByName  extends AbstractReadSubstance<String,String> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8701796200692548800L;
	/**
	 * 
	 */
	private SubstanceRecord record = new SubstanceRecord();
	private static final String _defaultType = "name";
	private static final String _defaultID = null;

	
	private static String sql_byname =
		"select substance.idsubstance,prefix,hex(uuid) as huuid,documentType,format,name,publicname,content,substanceType,rs_prefix,hex(rs_uuid) as rs_huuid,owner_prefix,hex(owner_uuid) as owner_huuid,owner_name "+ 
		"from substance where name regexp ? or publicname regexp ?";	

	public ReadSubstanceByName() {
		this(_defaultType,null);
	}
	public ReadSubstanceByName(String type,String id) {
		super();
		setFieldname(type);
		setValue(id);
	}	
	@Override
	public String getSQL() throws AmbitException {
		return sql_byname;
	}

	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		if (getValue()==null) throw new AmbitException("No search value");
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<String>(String.class, "^"+getValue()));
		params.add(new QueryParam<String>(String.class, "^"+getValue()));
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