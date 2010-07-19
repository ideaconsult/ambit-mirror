package ambit2.rest.aa.opensso;

import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import ambit2.rest.aa.AAException;
import ambit2.rest.aa.AALogoutException;
import ambit2.rest.aa.AAServicesConfig;
import ambit2.rest.aa.AATokenValidationException;
import ambit2.rest.aa.AuthenticationServiceException;
import ambit2.rest.aa.AuthzException;

public class OpenSSOToken {
	
	protected static final String tokenReceived = "token.id=";
	protected static final String username_tag = "username";
	protected static final String password_tag = "password";
	
	protected static final String uri_tag = "uri";
	protected static final String subjectid_tag = "subjectid";
	protected static final String action_tag = "action";

	protected static final String boolean_true_result = "boolean=true";	
	
	public static final String tokenid = "tokenid";
	protected static final String headers_tag = "org.restlet.http.headers";
	
	/**
	 * Either as query param  , or in the header
	 * @param request
	 * @return
	 */
	public static String getToken(Request request) {
		Object token = request.getResourceRef().getQueryAsForm().getFirstValue(tokenid);
		if (token==null) {
			//try headers
			Form headers = (Form) request.getAttributes().get(headers_tag);  
			token = headers.getFirstValue(tokenid);
		}		
		return token==null?null:token.toString();
	}
	
	/**
	 * Add token to request headers
	 * @param token
	 * @param request
	 * @return
	 */
	public static Form addTokenToHeaders(String token,Request request) {
		if (token == null) return null;
		Form headers = (Form) request.getAttributes().get(headers_tag);  
		if (headers == null) headers =  new Form();
		headers.add(tokenid,token.toString());
		request.getAttributes().put(headers_tag,headers);
		return headers;
	}	
	/**
	 * 
	 * @param request
	 * @return
	 * @throws AAException
	 */
	protected static String getTokenByUserPass(Request request) throws AAException {
		Object username = request.getResourceRef().getQueryAsForm().getFirstValue(username_tag);
		Object pass = request.getResourceRef().getQueryAsForm().getFirstValue(password_tag);
		if (username==null) return null;
		return getTokenByUserPass(username.toString(),pass==null?"":pass.toString());
	}

	/**
	 * curl -i -v -d 'username=name' -d 'password=pass' 'http://opensso.in-silico.ch/opensso/identity/authenticate?uri=service=openldap'
	 * @param username
	 * @param pass
	 * @return
	 * @throws AAException
	 */
	public static String getTokenByUserPass(String username,String pass) throws AAException {

		Form form = new Form();
		form.add(username_tag, username);
		form.add(password_tag,pass);

		String ref =  AAServicesConfig.getSingleton().getAuthenticationService();

		ClientResource client = new ClientResource(ref);
		Representation r=null;
		try {
			r = client.post(form.getWebRepresentation());
			String text = r.getText().trim();
			return text.substring(text.indexOf(tokenReceived)+9);
		} catch (ResourceException x) {
			throw new AuthenticationServiceException(ref,x);
		} catch (Exception x) {
			throw new AuthenticationServiceException(ref,x);
		} finally {
			try {r.release();} catch (Exception x) {}
			try {client.release();} catch (Exception x) {}
		}
	}	
	
	public static boolean isValid(String token) throws AAException {
		System.out.println(token);
		Form form = new Form();		form.add(tokenid,token);
		String tokenvalidation = AAServicesConfig.getSingleton().getTokenValidationService();
		Reference ref = new Reference(tokenvalidation);
		ClientResource client = new ClientResource(ref);
		Representation r=null;
		try {
			r = client.post(form.getWebRepresentation());
			String text = r.getText();
			return text.indexOf(boolean_true_result)>=0;
		} catch (ResourceException x) {
			throw new AATokenValidationException(ref.toString(),x);
		} catch (Exception x) {
			throw new AATokenValidationException(ref.toString(),x);
		} finally {
			try {r.release();} catch (Exception x) {}
			try {client.release();} catch (Exception x) {}
		}
	}
	
	public static void logout(String token) throws AAException {

		Form form = new Form();
		form.add(tokenid,token);

		Reference ref = new Reference(AAServicesConfig.getSingleton().getLogout());
		ref.setQuery(form.getQueryString());
		ClientResource client = new ClientResource(ref);
		Representation r=null;
		try {
			r = client.post(form.getWebRepresentation(),MediaType.APPLICATION_WWW_FORM);
			//String text = r.getText();
			return; //text.indexOf(boolean_true_result)>=0;
		} catch (ResourceException x) {
			throw new AALogoutException(ref.toString(),x);
		} catch (Exception x) {
			throw new AALogoutException(ref.toString(),x);
		} finally {
			try {r.release();} catch (Exception x) {}
			try {client.release();} catch (Exception x) {}
		}
	}
	
	protected static boolean verifyWithSSOServer(String uri,Method method,String token) throws AAException {
		
		Reference reference = new Reference(uri);
		reference.setQuery("");
		Form form = new Form();
		form.add(uri_tag, reference.toString());
		form.add(subjectid_tag,token);
		form.add(action_tag,method.getName());
		Reference ref = new Reference(AAServicesConfig.getSingleton().getAuthorizationService());
		ref.setQuery(form.getQueryString());
		ClientResource client = new ClientResource(ref);
		Representation r=null;
		try {
			r = client.get();
			String text = r.getText();
			return text.indexOf(boolean_true_result)>=0;
		} catch (ResourceException x) {
			throw new AuthzException(ref.toString(),x);
		} catch (Exception x) {
			throw new AuthzException(ref.toString(),x);
		} finally {
			try {r.release();} catch (Exception x) {}
			try {client.release();} catch (Exception x) {}
		}
	}	
	
		
	public static Form credentials2Form(String user,String pass) throws AAException  {
		Form form = new Form();
		form.add(username_tag,user);
		form.add(password_tag,pass);
		return form;
	}
	
	public static Reference addTokenToReference(Reference ref, String token) {
		return ref.addQueryParameter(tokenid,token);
	}
	
	public static Status createPolicy(String policyName, String url, String username, String token) throws ResourceException {
		String policyService = AAServicesConfig.getSingleton().getPolicyService();
		String policy =
		"<!DOCTYPE Policies PUBLIC \"-//Sun Java System Access Manager7.1 2006Q3  Admin CLI DTD//EN\" \"jar://com/sun/identity/policy/policyAdmin.dtd\">\n"+
		"<Policies>\n"+
		"    <Policy name=\"%s\" referralPolicy=\"false\" active=\"true\">\n"+
		"        <Rule name=\"tr1\">\n"+
		"            <ServiceName name=\"iPlanetAMWebAgentService\"/>\n"+
		"            <ResourceName name=\"%s\"/>\n"+
		"            <AttributeValuePair>\n"+
		"                <Attribute name=\"POST\"/>\n"+
		"                <Value>allow</Value>\n"+
		"            </AttributeValuePair>\n"+
		"            <AttributeValuePair>\n"+
		"                <Attribute name=\"GET\"/>\n"+
		"                <Value>allow</Value>\n"+
		"            </AttributeValuePair>\n"+
		"        </Rule>\n"+
		"        <Subjects name=\"s1\" description=\"\">\n"+
		"            <Subject name=\"%s\" type=\"LDAPUsers\" includeType=\"inclusive\">\n"+
		"                <AttributeValuePair>\n"+
		"                    <Attribute name=\"Values\"/>\n"+
		"                    <Value>uid=%s,ou=people,dc=opentox,dc=org</Value>\n"+
		"                </AttributeValuePair>\n"+
		"            </Subject>\n"+
		"        </Subjects>\n"+
		"    </Policy>\n"+
		"</Policies>\n";
		
		String p = String.format(policy,policyName,url,username,username);
		Representation r = new StringRepresentation(p,MediaType.APPLICATION_XML);
		ClientResource client = new ClientResource(policyService);
		
		Form headers = new Form();  
		client.getRequest().getAttributes().put(headers_tag, headers);  
		headers.add(subjectid_tag, token);
		
		Representation response = null;
		try {
			
			response = client.post(r);
			return client.getStatus();
		} catch (Exception x) {
			try { System.out.println(response.getText()); } catch (Exception xx ) {x.printStackTrace();}
			throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,x.getMessage());
		} finally {
			try {response.release(); } catch (Exception x) {}
			try {r.release(); } catch (Exception x) {}
			try {client.release(); } catch (Exception x) {}
		}
		//curl -i -H 'Content-Type: application/xml' -T policy-nina.xml -X POST -H 'subjectid: AQIC5wM2LY4Sfcx1OKHtFWI53gDhXr5thikBu1%2BG6ze64Lg%3D%40AAJTSQACMDE%3D%23' 'http://opensso.in-silico.ch/Pol/opensso-pol'
	}	
}
