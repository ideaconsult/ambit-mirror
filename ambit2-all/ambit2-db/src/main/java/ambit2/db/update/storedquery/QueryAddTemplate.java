package ambit2.db.update.storedquery;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;
import ambit2.base.data.Template;
import ambit2.db.search.IStoredQuery;

public class QueryAddTemplate extends AbstractUpdate<IStoredQuery, Template> {
	public static final String update_sql_id =	
			"update query set idtemplate=? where idquery=?";
	public static final String update_sql_name =	
			"insert query (idquery,idsessions,name,content,idtemplate)\n"+
			"select idquery,idsessions,query.name,content,template.idtemplate from template\n"+
			"join query	where template.name=? and idquery=?\n"+
			"on duplicate key update idtemplate=values(idtemplate)\n";

	
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		if (getObject().getId()>0)
			params.add(new QueryParam<Integer>(Integer.class, getObject().getId()));
		else
			params.add(new QueryParam<String>(String.class, getObject().getName()));
		params.add(new QueryParam<Integer>(Integer.class, getGroup().getId()));
		return params;
	}

	public String[] getSQL() throws AmbitException {
		if (getObject().getId()>0)
			return new String[] {update_sql_id};
		else
			return new String[] {update_sql_name};
	}

	public void setID(int index, int id) {
	
	}
	@Override
	public boolean returnKeys(int index) {
		return false;
	}
}
