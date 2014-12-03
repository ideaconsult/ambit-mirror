package ambit2.user.rest;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;
import net.idea.restnet.db.aalocal.user.IDBConfig;
import net.idea.restnet.u.UserCredentials;
import net.idea.restnet.u.UserRegistration;
import net.idea.restnet.user.DBUser;

public class ForgottenPasswordReset extends
		AbstractUpdate<UserCredentials, DBUser> implements IDBConfig {
	protected String dbName;
	protected UserRegistration reg;

	public ForgottenPasswordReset(DBUser user, UserRegistration reg,
			String dbname) {
		super();
		this.reg = reg;
		setObject(user);
		setDatabaseName(dbname);
	}

	@Override
	public String[] getSQL() throws AmbitException {
		return new String[] { String
				.format("update `%s`.user_registration r, `%s`.user x set r.code=?,r.created=now() where r.user_name=x.username and r.status='confirmed' and x.username=? and x.email=?",
						getDatabaseName(),getDatabaseName()) };
	}

	@Override
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> params1 = new ArrayList<QueryParam>();
		params1.add(new QueryParam<String>(String.class, reg
				.getConfirmationCode()));
		params1.add(new QueryParam<String>(String.class, getObject()
				.getUserName()));
		params1.add(new QueryParam<String>(String.class, getObject().getEmail()));
		return params1;
	}

	@Override
	public void setID(int index, int id) {

	}

	@Override
	public boolean returnKeys(int index) {
		return false;
	}

	@Override
	public void setDatabaseName(String name) {
		this.dbName = name;
	}

	@Override
	public String getDatabaseName() {
		return dbName;
	}
}
