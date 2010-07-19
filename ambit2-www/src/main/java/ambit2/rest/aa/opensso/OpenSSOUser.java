package ambit2.rest.aa.opensso;

import org.restlet.security.User;

public class OpenSSOUser extends User {
	protected String token;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
