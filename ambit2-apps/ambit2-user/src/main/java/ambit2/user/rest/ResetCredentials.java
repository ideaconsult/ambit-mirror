package ambit2.user.rest;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;
import net.idea.restnet.db.aalocal.user.IDBConfig;
import net.idea.restnet.u.UserRegistration;
import net.idea.restnet.user.DBUser;

/**
 * Set a new password if a valid code is provided (usually sent via email)
 * @author nina
 *
 */
public class ResetCredentials extends AbstractUpdate<UserRegistration,DBUser>  implements IDBConfig { 
	protected int hoursValid = 24;
	
	public int getHoursValid() {
		return hoursValid;
	}
	public String getHash() {
		return hash;
	}
	public void setHash(String hash) {
		this.hash = hash;
	}
	protected String hash = "md5";
	public void setHoursValid(int hoursValid) {
		this.hoursValid = hoursValid;
	}
	private String sql = 
		"update `%s`.users u, `%s`.user_registration r, `%s`.user xu set code=concat('PWDRST',rand(),CURRENT_TIMESTAMP()),confirmed=now(),user_pass=%s(?) " +
		"where xu.username=u.user_name and u.user_name=r.user_name and r.status='confirmed' and date_add(created,interval ? hour)>=now() " +
		"and code=?";

	public ResetCredentials(Integer hoursValid,UserRegistration reg) {
		super();
		setGroup(reg);
		setHoursValid(hoursValid);
	}
	
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		if ((getGroup().getConfirmationCode()==null)|| getGroup().getConfirmationCode().startsWith("PWDRST")) throw new AmbitException("Invalid code");
		if (getObject()==null) throw new AmbitException("Invalid input");
		if (getObject().getCredentials()==null || getObject().getCredentials().getNewpwd()==null) throw new AmbitException("Invalid input");
		params.add(new QueryParam<String>(String.class, getObject().getCredentials().getNewpwd()));
		params.add(new QueryParam<Integer>(Integer.class, getHoursValid()));
		params.add(new QueryParam<String>(String.class,  getGroup().getConfirmationCode()));
		return params;
	}
	
	public String[] getSQL() throws AmbitException {
		return new String[] { String.format(sql,getDatabaseName(),getDatabaseName(),getDatabaseName(),getHash()) };
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
