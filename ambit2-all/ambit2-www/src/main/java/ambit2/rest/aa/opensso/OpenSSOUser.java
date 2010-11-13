package ambit2.rest.aa.opensso;

import org.opentox.aa.IOpenToxUser;
import org.restlet.security.User;

public class OpenSSOUser extends User implements IOpenToxUser {

	@Override
	public String getPassword() {
		return getSecret().toString();
	}

	@Override
	public String getUsername() {
		return getIdentifier();
	}

	@Override
	public void setPassword(String secret) {
		setSecret(secret.toCharArray());
	}

	@Override
	public void setUserName(String name) {
		super.setIdentifier(name);
		
	}

}
