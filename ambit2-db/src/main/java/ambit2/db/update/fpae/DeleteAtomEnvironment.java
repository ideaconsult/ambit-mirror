package ambit2.db.update.fpae;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.QueryParam;
import ambit2.db.update.AbstractUpdate;

public class DeleteAtomEnvironment extends AbstractUpdate<IStructureRecord,IStructureRecord> {
	protected static String[] sql = new String[] {"DELETE from fpaechemicals where idchemical=?"};
	@Override
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getGroup().getIdchemical()));
		return params;
	}

	@Override
	public String[] getSQL() throws AmbitException {
		return sql;
	}

	@Override
	public void setID(int index, int id) {
	}

}
