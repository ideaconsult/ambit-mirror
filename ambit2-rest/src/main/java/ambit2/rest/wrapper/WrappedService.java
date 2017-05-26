package ambit2.rest.wrapper;

import java.net.URI;

import org.apache.http.auth.Credentials;

public class WrappedService<C extends Credentials> {
	protected String handler;

	public String getHandler() {
		return handler;
	}

	public void setHandler(String handler) {
		this.handler = handler;
	}

	protected URI uri;
	protected C credentials;

	public URI getURI() {
		return uri;
	}

	public void setURI(URI uri) {
		this.uri = uri;
	}

	public C getCredentials() {
		return credentials;
	}

	public void setCredentials(C credentials) {
		this.credentials = credentials;
	}

	public URI getService() {
		try {
			return getHandler() == null ? getURI() : new URI(String.format("%s%s", getURI().toString(), getHandler()));
		} catch (Exception x) {
			x.printStackTrace();
			return getURI();
		}
	}
}
