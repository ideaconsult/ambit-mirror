package ambit2.db.update.storedquery;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;
import ambit2.base.data.Template;
import ambit2.db.search.IStoredQuery;

public class QueryDeleteTemplate extends AbstractUpdate<IStoredQuery, Template> {

	public static final String[] update_sql_id =	
		{"update query set idtemplate=null where idquery=?"};
	
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getGroup().getId()));
		return params;
	}

	public String[] getSQL() throws AmbitException {
		return update_sql_id;
	}

	public void setID(int index, int id) {
	
	}
	@Override
	public boolean returnKeys(int index) {
		return false;
	}
}
