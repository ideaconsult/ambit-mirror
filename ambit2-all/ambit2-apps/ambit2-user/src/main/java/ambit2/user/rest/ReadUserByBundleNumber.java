package ambit2.user.rest;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.restnet.db.aalocal.user.IDBConfig;
import net.idea.restnet.user.DBUser;
import net.idea.restnet.user.db.ReadUser;

public class ReadUserByBundleNumber  extends ReadUser<String> implements IDBConfig {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6228939989116141217L;

	protected String databaseName = null;
	protected boolean allowWrite = false;
	public boolean isAllowWrite() {
	    return allowWrite;
	}
	public void setAllowWrite(boolean allowWrite) {
	    this.allowWrite = allowWrite;
	}
	@Override
	public void setDatabaseName(String name) {
		databaseName = name;
	}
	@Override
	public String getDatabaseName() {
		return databaseName;
	}
	
	protected static String sql = 
		"SELECT iduser,username,user.title,firstname,lastname,institute,weblog,homepage,email,keywords,reviewer " +
		"FROM %s.user_roles join %s.user on user_name=username where role_name=?\n"+
		"union select null,concat('g_',role_name),'',IF(role_name='ambit_user','All',role_name),'',null,null,null,null,null,0 from policy where resource = ? and role_name != ? %s";
		
	public ReadUserByBundleNumber(DBUser user) {
		super(user);
	}

	public ReadUserByBundleNumber() {
		super();
	}
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = null;
		if (getFieldname()==null) throw new AmbitException("Empty argument!");
		params = new ArrayList<QueryParam>();
		String id = getFieldname().toUpperCase();
		String rolename = "B."+id.replace("-", "") + (isAllowWrite()?".W":".R");
		String resource = "/bundle/"+id;
		params.add(new QueryParam<String>(String.class, rolename));
		params.add(new QueryParam<String>(String.class, resource));
		params.add(new QueryParam<String>(String.class, rolename));
		return params;
	}

	public String getSQL() throws AmbitException {
		if (getFieldname()==null) throw new AmbitException("Empty argument!");
		if (getDatabaseName()==null) throw new AmbitException("Database not specified!");
		return String.format(sql,getDatabaseName(),getDatabaseName(),isAllowWrite()?"and (mput=1 or mpost=1)":"and mget=1");
	}


}