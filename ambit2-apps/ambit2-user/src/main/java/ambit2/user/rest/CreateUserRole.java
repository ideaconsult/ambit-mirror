package ambit2.user.rest;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;
import net.idea.restnet.db.aalocal.DBRole;
import net.idea.restnet.db.aalocal.user.IDBConfig;
import net.idea.restnet.db.aalocal.user.IUser;

public class CreateUserRole extends AbstractUpdate<DBRole,IUser> implements IDBConfig {

	public CreateUserRole(DBRole role,IUser user) {
		super();
		setGroup(role);
		setObject(user);
	}
	
	@Override
	public String[] getSQL() throws AmbitException {
		return new String[] {
			String.format("insert ignore into %suser_roles values (?,?)",databaseName==null?"":String.format("`%s`.",databaseName))
		};
	}

	@Override
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		switch (index) {
		case 0: {
			params.add(new QueryParam<String>(String.class, getObject().getUserName()));
			params.add(new QueryParam<String>(String.class, getGroup().getName()));
			break;
		}
		}
		return params;
	}

	@Override
	public void setID(int index, int id) {

	}

	protected String databaseName = null;
	@Override
	public void setDatabaseName(String name) {
		databaseName = name;
	}
	@Override
	public String getDatabaseName() {
		return databaseName;
	}

}