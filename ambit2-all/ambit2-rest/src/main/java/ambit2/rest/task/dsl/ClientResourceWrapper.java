package ambit2.rest.task.dsl;

import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.resource.ClientResource;



public class ClientResourceWrapper extends ClientResource {
	protected static final String subjectid = "subjectid";
	private static ThreadLocal<IAuthToken> tokenFactory = null;
	
	public static IAuthToken getTokenFactory() {
		return tokenFactory==null?null:tokenFactory.get();
	}
	public static void setTokenFactory(IAuthToken tokenFactory) {
		if (ClientResourceWrapper.tokenFactory==null) ClientResourceWrapper.tokenFactory =  new ThreadLocal<IAuthToken> ();
		ClientResourceWrapper.tokenFactory.set(tokenFactory);
	}
	public ClientResourceWrapper(Reference uri, String token) {
		super(uri);
		if (token !=null) addToken2Header(token);
	}
	public ClientResourceWrapper(String uri, String token) {
		super(uri);
		if (token !=null) addToken2Header(token);
	}
	public ClientResourceWrapper(Reference uri) {
		super(uri);
		String token = getToken();
		if (token !=null) addToken2Header(token);
	}
	public ClientResourceWrapper(String uri) {
		super(uri);
		String token = getToken();
		if (token !=null) addToken2Header(token); 
	}
	protected void addToken2Header(String token) {
	    Form extraHeaders = new Form();
        extraHeaders.add(subjectid, token);
        getRequest().getAttributes().put("org.restlet.http.headers",extraHeaders);
	}
	protected String getToken() {
		IAuthToken tokenFactory = getTokenFactory();
		return tokenFactory==null?null:tokenFactory.getToken();
	}
	
}
