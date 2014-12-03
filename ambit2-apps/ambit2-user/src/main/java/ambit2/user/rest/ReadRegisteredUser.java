package ambit2.user.rest;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.restnet.db.aalocal.user.IDBConfig;
import net.idea.restnet.u.UserRegistration;
import net.idea.restnet.user.DBUser;
import net.idea.restnet.user.db.ReadUser;

public class ReadRegisteredUser   extends ReadUser<UserRegistration> implements IDBConfig {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6228939989116141217L;

	protected String databaseName = null;
	@Override
	public void setDatabaseName(String name) {
		databaseName = name;
	}
	@Override
	public String getDatabaseName() {
		return databaseName;
	}
	
	protected static String sql = 
		"SELECT users.iduser,username,user.title,firstname,lastname,institute,weblog,homepage,email,keywords,reviewer " +
		"FROM `%s`.user_registration r, users x "+ 
		"where r.user_name=x.user_name and r.status='confirmed' and user_name=? and email=?";
		
	public ReadRegisteredUser(DBUser user) {
		super(user);
	}


	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = null;
		if (getValue()==null || getValue().getUserName()==null || getValue().getEmail()==null) throw new AmbitException("Empty argument!");
		params = new ArrayList<QueryParam>();
		params.add(new QueryParam<String>(String.class, getValue().getUserName()));
		params.add(new QueryParam<String>(String.class, getValue().getEmail()));
		return params;
	}

	public String getSQL() throws AmbitException {
		if (getFieldname()==null) throw new AmbitException("Empty argument!");
		if (getDatabaseName()==null) throw new AmbitException("Database not specified!");
		return String.format(sql,getDatabaseName());
	}


}