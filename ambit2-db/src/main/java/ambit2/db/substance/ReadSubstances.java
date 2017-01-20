package ambit2.db.substance;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.I5Utils;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.relation.composition.CompositionRelation;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;

/**
 * Retrieve set of substances given array of substance UUIDs
 * 
 * @author nina
 *
 */
public class ReadSubstances extends AbstractReadSubstance<CompositionRelation, String[]> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6355365745488149195L;
	protected SubstanceRecord record;

	public ReadSubstances() {
		this(null);
	}

	public ReadSubstances(String[] uuids) {
		setValue(uuids);
		record = new SubstanceRecord();
	}

	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params1 = new ArrayList<QueryParam>();
		if (getValue() != null) {
			for (String i5uuid : getValue()) {
				String[] uuid = I5Utils.splitI5UUID(i5uuid.trim());
				params1.add(new QueryParam<String>(String.class, uuid[1].replace("-", "").toLowerCase()));
			}
		}
		return params1;
	}

	@Override
	public String getSQL() throws AmbitException {
		if (getValue() != null) {
			StringBuilder b = new StringBuilder();
			b.append(sql);
			b.append("where uuid in (");
			String delimiter = "";
			for (int i = 0; i < getValue().length; i++) {
				b.append(delimiter);
				b.append("unhex(?) ");
				delimiter = ",";
			}
			b.append(")");
			return b.toString();
		}
		throw new AmbitException("Unspecified substance");
	}

	@Override
	protected SubstanceRecord getRecord() {
		return record;
	}

	@Override
	protected String stringify(String[] value) {
		return value==null?"":String.format("Substance UUIDs (%d)", value.length);
	}

}
