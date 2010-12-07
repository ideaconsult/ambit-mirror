package org.opentox.aa.opensso;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Hashtable;

import org.opentox.aa.IOpenToxUser;
import org.opentox.aa.OTAAParams;
import org.opentox.aa.OpenToxToken;
import org.opentox.aa.exception.AAException;
import org.opentox.aa.exception.AALogoutException;
import org.opentox.aa.exception.AATokenValidationException;
import org.opentox.aa.exception.AAUnauthorizedException;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

/**
 * http://opentox.org/dev/apis/api-1.2/AA
 * 
 * @author nina
 *
 */
public class OpenSSOToken extends OpenToxToken {
	//services
	protected static final String authn = "%s/authenticate?uri=service=openldap";
	protected static final String authz = "%s/authorize";
	protected static final String attributes = "%s/attributes";
	protected static final String token_validation = "%s/isTokenValid";
	protected static final String logout = "%s/logout"; 
	//parsing helpers
	private static final String tokenReceived = "token.id=";
	private static final String boolean_true_result = "boolean=true";
	
	public static final String authz_result_ok = "boolean=true";
	public static final String authz_result_bad = "boolean=false";
	
	public OpenSSOToken(String authService) {
		super(authService);
	}

	@Override
	public boolean login(IOpenToxUser user) throws Exception {
		return login(user.getUsername(),user.getPassword());
	}

	@Override
	public boolean login(String username, String password) throws Exception {
		if (username==null) throw new Exception(MSG_EMPTY_USERNAME,null);
		
		Form form = new Form();
		form.add(OTAAParams.username.toString(), username );
		form.add(OTAAParams.password.toString(),password);
		ClientResource client = new ClientResource(String.format(authn, authService));
		Representation r=null;
		try {
			r = client.post(form.getWebRepresentation());
			Status status = client.getStatus();
			if (Status.SUCCESS_OK.equals(status)) {
				String text = r.getText().trim();
				this.token = text.substring(text.indexOf(tokenReceived)+9);
				return token != null;
			} else if (Status.CLIENT_ERROR_UNAUTHORIZED.equals(status)) {
				throw new AAUnauthorizedException(authn);
			} else  {
				throw new AAException(client.getStatus(),authn,null);
			}

		} catch (Exception x) {
			if  (Status.CLIENT_ERROR_UNAUTHORIZED.equals(client.getStatus())) return false;
			else throw x;

		} finally {
			try {r.release();} catch (Exception x) {}
			try {client.release();} catch (Exception x) {}
		}
	}

	@Override
	public boolean logout() throws Exception {
		if (token==null) throw new Exception(MSG_EMPTY_TOKEN,null);
		
		Form form = new Form();
		form.add(OTAAParams.subjectid.toString(),token);
		String logoutService = String.format(logout, authService);
		ClientResource client = new ClientResource(logoutService);
		Representation r=null;
		try {
			r = client.post(form.getWebRepresentation(),MediaType.APPLICATION_WWW_FORM);
			Status status = client.getStatus();
			if (Status.SUCCESS_OK.equals(status)) {
				setToken(null);
				return true;
			} else  {
				throw new AALogoutException(logoutService,new ResourceException(client.getStatus()));
			}
		} catch (Exception x) {
			if  (Status.CLIENT_ERROR_UNAUTHORIZED.equals(client.getStatus())) return false;
			else throw x;
		} finally {
			try {r.release();} catch (Exception x) {}
			try {client.release();} catch (Exception x) {}
		}
	}
	
	@Override
	public boolean isTokenValid() throws Exception {
		if (token==null) throw new Exception(MSG_EMPTY_TOKEN,null);
		
		Form form = new Form();		form.add(OTAAParams.tokenid.toString(),token);
		String tokenValidationService = String.format(token_validation, authService);
		ClientResource client = new ClientResource(tokenValidationService);
		Representation r=null;
		try {
			r = client.post(form.getWebRepresentation());
			String text = r.getText();
			return text.indexOf(boolean_true_result)>=0;
		} catch (ResourceException x) {
			if  (Status.CLIENT_ERROR_UNAUTHORIZED.equals(client.getStatus())) return false;
			else throw new AATokenValidationException(tokenValidationService,x);
		} catch (Exception x) {
			throw new AATokenValidationException(tokenValidationService,x);
		} finally {
			try {r.release();} catch (Exception x) {}
			try {client.release();} catch (Exception x) {}
		}
	}

	@Override
	public boolean authorize(String uri, String action) throws Exception {
		if (token==null) throw new Exception(MSG_EMPTY_TOKEN,null);
		Form form = new Form();		
		form.add(OTAAParams.subjectid.toString(),token);
		form.add(OTAAParams.uri.toString(),uri);
		form.add(OTAAParams.action.toString(),action);
		
		String authorizationService = String.format(authz, authService);
		ClientResource client = new ClientResource(authorizationService);
		Representation r=null;
		
		try {
			r = client.post(form.getWebRepresentation());
			String content = r.getText();
			if (Status.SUCCESS_OK.equals(client.getStatus())) {
				return (content!=null) && authz_result_ok.equals(content.trim());
			} else if (Status.CLIENT_ERROR_UNAUTHORIZED.equals(client.getStatus())) {
				return false;
			} else  {
				throw new AAException(client.getStatus(),authz,null);
			}
		} catch (ResourceException x) {
			if  (Status.CLIENT_ERROR_UNAUTHORIZED.equals(client.getStatus())) return false;
			else throw x;
		} finally {
			try {r.release();} catch (Exception x) {}
			try {client.release();} catch (Exception x) {}
		}
	}
	
	/**
	 * http://developers.sun.com/identity/reference/techart/id-svcs.html
	 */
	@Override
	public boolean getAttributes(String[] attributeNames,Hashtable<String, String> results)
			throws Exception {
		if (token==null) throw new Exception(MSG_EMPTY_TOKEN,null);
		
		
		Reference attrService = new Reference(String.format(attributes, authService));
		attrService.addQueryParameter(OTAAParams.subjectid.toString(), token);
		if (attributeNames!=null)
			for (String aName : attributeNames) 
				attrService.addQueryParameter(OTAAParams.attributes_names.toString(), aName);
			
		
		ClientResource client = new ClientResource(attrService);
		Representation r=null;
		
		try {
			r = client.get();
			
			if (Status.SUCCESS_OK.equals(client.getStatus())) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(r.getStream()));

				String line = null;
				String name_tag = "userdetails.attribute.name";
				String value_tag = "userdetails.attribute.value";
				String name = null;
				String value = null;
				while ((line = reader.readLine())!=null) {
					if (line.startsWith(name_tag)) {
						name = line.substring(name_tag.length()+1);
					} else if ((name!=null) && line.startsWith(value_tag)) {
						value = line.substring(value_tag.length()+1);
						if (results != null) results.put(name,value);
					}
				}
				return true;
			} else return false;
		} catch (ResourceException x) {
			if  (Status.CLIENT_ERROR_UNAUTHORIZED.equals(client.getStatus())) return false;
			else throw x;
		} finally {
			try {r.release();} catch (Exception x) {}
			try {client.release();} catch (Exception x) {}
		}
		
	}
}
