package org.opentox.aa;

import java.util.Hashtable;

public abstract class OpenToxToken {

	public static final String MSG_EMPTY_TOKEN = "Empty token";
	protected static final String MSG_EMPTY_USERNAME = "Empty user name";
	
	protected String token;
	protected String authService;

	public OpenToxToken(String authService) {
		this.authService = authService;
		this.token = null;
	}
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	public abstract boolean login(IOpenToxUser user) throws Exception;
	public abstract boolean login(String username, String password) throws Exception;
	public abstract boolean logout() throws Exception;
	public abstract boolean isTokenValid() throws Exception;
	public abstract boolean authorize(String uri, String action) throws Exception;
	public abstract boolean getAttributes(String[] attributeNames,Hashtable<String,String> attributes) throws Exception;

	@Override
	public String toString() {
		return token;
	}
	
	
}
