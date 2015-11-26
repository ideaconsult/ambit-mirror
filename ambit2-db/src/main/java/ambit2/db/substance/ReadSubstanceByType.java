package ambit2.db.substance;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.SubstanceRecord;

public class ReadSubstanceByType extends AbstractReadSubstance<String, String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2198459948257520650L;

	private SubstanceRecord record = new SubstanceRecord();

	private static String sql_bytype = "select substance.idsubstance,prefix,hex(uuid) as huuid,documentType,format,name,publicname,content,substanceType,rs_prefix,hex(rs_uuid) as rs_huuid,owner_prefix,hex(owner_uuid) as owner_huuid,owner_name "
			+ "from substance where substanceType = ?";

	public ReadSubstanceByType(String value) {
		super();
		setValue(value);
	}

	@Override
	public String getSQL() throws AmbitException {
		return sql_bytype;
	}

	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		if (getValue() == null)
			throw new AmbitException("No search value");
		List<QueryParam> params = new ArrayList<QueryParam>();

		params.add(new QueryParam<String>(String.class, getValue()));
		return params;
	}

	@Override
	protected SubstanceRecord getRecord() {
		return record;
	}

	@Override
	public String toString() {
		return String.format("substancetype = %s", getValue());
	}

}