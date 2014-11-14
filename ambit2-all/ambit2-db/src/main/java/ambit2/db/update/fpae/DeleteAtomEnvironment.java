package ambit2.db.update.fpae;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;
import ambit2.base.interfaces.IStructureRecord;

public class DeleteAtomEnvironment extends AbstractUpdate<IStructureRecord,IStructureRecord> {
	protected static String[] sql = new String[] {"DELETE from fpaechemicals where idchemical=? and user_name=(SUBSTRING_INDEX(user(),'@',1))"};
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
