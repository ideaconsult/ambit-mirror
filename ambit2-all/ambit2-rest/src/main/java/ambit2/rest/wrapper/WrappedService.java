package ambit2.rest.wrapper;

import java.net.URI;

import org.apache.http.auth.Credentials;

public class WrappedService<C extends Credentials> {
	protected String handler;
	protected String query;
	protected String filterConfig;
	protected String name;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFilterConfig() {
		return filterConfig;
	}

	public void setFilterConfig(String filterConfig) {
		this.filterConfig = filterConfig;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

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
		StringBuilder b = new StringBuilder();
		try {
			b.append(getURI());
			if (getHandler() != null)
				b.append(getHandler());
			if (getQuery() != null) {
				b.append("?");
				b.append(getQuery());
			}

			return new URI(b.toString());
		} catch (Exception x) {
			x.printStackTrace();
			return getURI();
		}
	}
}
