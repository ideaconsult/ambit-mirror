package ambit2.user.aa;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.restnet.u.AbstractUpdateCredentials;
import net.idea.restnet.u.UserCredentials;
import net.idea.restnet.user.DBUser;

public class AdminUpdateCredentials extends AbstractUpdateCredentials<DBUser> {

	private final String sql = "update `%s`.users a, `%s`.user b set user_pass = %s(?) where a.user_name=b.username and iduser=? and user_name = ?";
	public AdminUpdateCredentials(UserCredentials c,DBUser ref, String dbname) {
		super(c,ref);
		setDatabaseName(dbname);
	}
	
	@Override
	public String getUserName(DBUser user) {
		return user.getUserName();
	}

	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		if ((getObject()==null) || (getUserName(getObject())==null) || (getObject().getID()<=0)) throw new AmbitException("Invalid input");
		
		params.add(new QueryParam<String>(String.class, getGroup().getNewpwd()));
		params.add(new QueryParam<Integer>(Integer.class, getObject().getID()));		
		params.add(new QueryParam<String>(String.class, getUserName(getObject())));
		return params;
	}

	public String[] getSQL() throws AmbitException {
		return new String[] { String.format(sql,getDatabaseName(),getDatabaseName(),getHash()) };
	}
}
