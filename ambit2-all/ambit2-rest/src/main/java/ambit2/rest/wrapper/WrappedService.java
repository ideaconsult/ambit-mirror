package ambit2.rest.wrapper;

import java.net.URI;

import org.apache.http.auth.Credentials;

public class WrappedService<C extends Credentials> {
	protected String handler;
	protected String query;
	protected String filterConfig;
	protected String name;
	protected String idkey;
	protected String propertykey;
	protected String valueKey;
	protected String dummyTopLevel;
	public String getDummyTopLevel() {
		return dummyTopLevel;
	}

	public void setDummyTopLevel(String dummyTopLevel) {
		this.dummyTopLevel = dummyTopLevel;
	}

	public String getValueKey() {
		return valueKey;
	}

	public void setValueKey(String valueKey) {
		this.valueKey = valueKey;
	}

	public String getIdkey() {
		return idkey;
	}

	public void setIdkey(String idkey) {
		this.idkey = idkey;
	}

	public String getPropertykey() {
		return propertykey;
	}

	public void setPropertykey(String propertykey) {
		this.propertykey = propertykey;
	}

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
		String delimiter="";
		try {
			b.append(getURI());
			if (getHandler() != null) 
				if (!getHandler().equals(getDummyTopLevel())) {
					b.append(getHandler());
					delimiter= "/";
				}
			
			if (getIdkey() != null) {
				b.append(delimiter);
				b.append(getIdkey());
				delimiter= "/";
			}
			if (getPropertykey() != null) {
				b.append(delimiter);
				b.append(getPropertykey());
				delimiter= "/";
			}	
			if (getValueKey() != null) {
				b.append(delimiter);
				b.append(getValueKey());
				delimiter= "/";
			}							
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
