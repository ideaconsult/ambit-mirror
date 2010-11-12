package org.opentox.aa;

public class OpenToxUser implements IOpenToxUser {
	protected String password;
	protected String username;
	
	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public void setUserName(String userName) {
		this.username = userName;
	}

}
