package ambit2.dbui;

import java.net.URI;
import java.sql.Connection;

import ambit2.db.LoginInfo;

import com.jgoodies.binding.beans.Model;

public class LoginInfoBean extends Model {
	protected LoginInfo loginInfo;
	/**
	 * 
	 */
	private static final long serialVersionUID = -6318006747221999109L;
	public LoginInfoBean() {
		super();
		loginInfo = new LoginInfo();
		
	}
	
    public void setURI(URI uri) throws Exception {
        loginInfo.setURI(uri);
    }
    public void setURI(Connection connection) throws Exception {
    	loginInfo.setURI(connection);
    }
    
	public String getDatabase() {
		return loginInfo.getDatabase();
	}
	public void setDatabase(String database) {
		loginInfo.setDatabase(database);
	}
	public String getHostname() {
		return loginInfo.getHostname();
	}
	public void setHostname(String hostname) {
		loginInfo.setHostname(hostname);
	}
	public String getPassword() {
		return loginInfo.getPassword();
	}
	public void setPassword(String password) {
		loginInfo.setPassword(password);
	}
	public String getPort() {
		return loginInfo.getPort();
	}
	public void setPort(String port) {
		loginInfo.setPort(port);
	}
	public String getScheme() {
		return loginInfo.getScheme();
	}
	public void setScheme(String scheme) {
		loginInfo.setScheme(scheme);
	}
	public String getUser() {
		return loginInfo.getUser();
	}
	public void setUser(String user) {
		loginInfo.setUser(user);
	}
	@Override
	public String toString() {
		return loginInfo.toString();
	}	
}
