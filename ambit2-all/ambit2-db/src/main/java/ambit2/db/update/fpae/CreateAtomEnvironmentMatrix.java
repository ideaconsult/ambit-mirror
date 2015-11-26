package ambit2.db.update.fpae;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.data.HashIntDescriptorResult;
import ambit2.db.processors.FP1024Writer.FP1024_status;
import ambit2.descriptors.processors.FPTable;

public class CreateAtomEnvironmentMatrix extends
		AbstractUpdate<IStructureRecord, IStructureRecord> {
	protected FPTable fptable = FPTable.aematrix;

	public FPTable getFptable() {
		return fptable;
	}

	public void setFptable(FPTable fptable) {
		this.fptable = fptable;
	}

	public CreateAtomEnvironmentMatrix(FPTable fptable) {
		super();
		setFptable(fptable);
	}

	public CreateAtomEnvironmentMatrix() {
		this(FPTable.aematrix);
	}

	private static final String sql = "INSERT INTO %s (idchemical,bc,status,tags,updated,levels,factory) "
			+ "VALUES (?,?,?,?,CURRENT_TIMESTAMP(),?,?) ON DUPLICATE KEY UPDATE "
			+ "bc=values(bc),status=values(status),tags=values(tags), updated=CURRENT_TIMESTAMP(),levels=values(levels),factory=values(factory)";

	@Override
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getGroup()
				.getIdchemical()));

		FP1024_status status = FP1024_status.valid;
		Object value = null;
		int bc = -1;
		int maxlevels = -1;
		String factory = null;
		if (getGroup() == null ) {
			status = FP1024_status.error;
		} else {
			Property key = Property.getInstance(fptable.getProperty(), fptable.getProperty());
			value = getGroup().getRecordProperty(key);
			if (value == null) status = FP1024_status.error;
			if (value instanceof HashIntDescriptorResult) {
				bc = ((HashIntDescriptorResult)value).getSize();
				maxlevels = ((HashIntDescriptorResult)value).getMaxLevels();
				factory = ((HashIntDescriptorResult)value).getFactory();
			}
		}
		params.add(new QueryParam<Integer>(Integer.class, bc));
		params.add(new QueryParam<Integer>(Integer.class, status.ordinal() + 1));
		params.add(new QueryParam<String>(String.class, value==null?null:value.toString()));
		params.add(new QueryParam<Integer>(Integer.class, maxlevels));
		params.add(new QueryParam<String>(String.class, factory));
		return params;
	}

	@Override
	public String[] getSQL() throws AmbitException {
		return new String[] { String.format(sql, getFptable().getTable()) };
	}

	public void setID(int index, int id) {
	}

}
