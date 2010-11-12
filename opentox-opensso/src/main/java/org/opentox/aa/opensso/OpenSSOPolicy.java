package org.opentox.aa.opensso;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Hashtable;

import org.opentox.aa.IOpenToxUser;
import org.opentox.aa.OTAAParams;
import org.opentox.aa.OpenToxPolicy;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

public class OpenSSOPolicy extends OpenToxPolicy<OpenSSOToken,String> {
	protected static final String headers_tag = "org.restlet.http.headers";
	
	protected String policyActionTemplate =
		"            <AttributeValuePair>\n"+
		"                <Attribute name=\"%s\"/>\n"+
		"                <Value>allow</Value>\n"+
		"            </AttributeValuePair>\n";
	
	protected String policyGroupTemplate = 
	"<!DOCTYPE Policies PUBLIC \"-//Sun Java System Access Manager7.1 2006Q3  Admin CLI DTD//EN\" \"jar://com/sun/identity/policy/policyAdmin.dtd\">\n"+
	"<Policies>\n"+
	"    <Policy name=\"%s\" referralPolicy=\"false\" active=\"true\">\n"+
	"        <Rule name=\"tr1\">\n"+
	"            <ServiceName name=\"iPlanetAMWebAgentService\"/>\n"+
	"            <ResourceName name=\"%s\"/>\n"+
	"            %s\n"+	
	"        </Rule>\n"+
	"        <Subjects name=\"s1\" description=\"\">\n"+
	"            <Subject name=\"%s\" type=\"LDAPGroups\" includeType=\"inclusive\">\n"+
	"                <AttributeValuePair>\n"+
	"                    <Attribute name=\"Values\"/>\n"+
	"                    <Value>cn=%s, ou=groups, dc=opentox, dc=org</Value>\n"+
	"                </AttributeValuePair>\n"+
	"            </Subject>\n"+
	"        </Subjects>\n"+
	"    </Policy>\n"+
	"</Policies>\n";
	
	protected String policyUserTemplate = 
		"<!DOCTYPE Policies PUBLIC \"-//Sun Java System Access Manager7.1 2006Q3  Admin CLI DTD//EN\" \"jar://com/sun/identity/policy/policyAdmin.dtd\">\n"+
		"<Policies>\n"+
		"    <Policy name=\"%s\" referralPolicy=\"false\" active=\"true\">\n"+
		"        <Rule name=\"tr1\">\n"+
		"            <ServiceName name=\"iPlanetAMWebAgentService\"/>\n"+
		"            <ResourceName name=\"%s\"/>\n"+
		"            %s\n"+	
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
	public OpenSSOPolicy(String policyService) {
		super(policyService);
	}


	@Override
	public int createGroupPolicy(String group,OpenSSOToken token, String uri, String[] methods) throws Exception {

		StringBuffer b = new StringBuffer();
		b.append(uri.replace(":","").replace("/",""));
		for (String method: methods) b.append(method);
		return createUserPolicy(group,token, uri, methods,b.toString());
	}
	
	@Override
	public int createGroupPolicy(String group,OpenSSOToken token, String uri, String[] methods, String policyId) throws Exception {
		if ((token==null) || (token.getToken()==null)) throw new Exception(OpenSSOToken.MSG_EMPTY_TOKEN,null);
		if (policyId==null) throw new Exception(MSG_EMPTY_POLICYID,null);
		
		if (group == null) {
			throw new Exception("No group id");
		}
		
		StringBuffer actions = new StringBuffer();
		for (String method: methods) {
			actions.append(String.format(policyActionTemplate,method));
		}
		String p = String.format(policyGroupTemplate,policyId,uri,actions,group,group);
		return sendPolicy(token,p);
		
	}	
	
	@Override
	public int createUserPolicy(String user,OpenSSOToken token, String uri, String[] methods) throws Exception {

		StringBuffer b = new StringBuffer();
		b.append(uri.replace(":","").replace("/",""));
		for (String method: methods) b.append(method);
		return createUserPolicy(user,token, uri, methods,b.toString());
	}
	@Override
	public int createUserPolicy(String user,OpenSSOToken token, String uri, String[] methods, String policyId) throws Exception {
		if ((token==null) || (token.getToken()==null)) throw new Exception(OpenSSOToken.MSG_EMPTY_TOKEN,null);
		if (policyId==null) throw new Exception(MSG_EMPTY_POLICYID,null);
		
		if (user == null) {
			//get username and other attributes
			Hashtable<String, String> results = new Hashtable<String, String>();
			if (!token.getAttributes(new String[] {"uid"},results))
				throw new Exception("Can't retrieve user name",null);
			
			user = results.get("uid");
		}
		
		StringBuffer actions = new StringBuffer();
		for (String method: methods) {
			actions.append(String.format(policyActionTemplate,method));
		}
		String p = String.format(policyUserTemplate,policyId,uri,actions,user,user);
		return sendPolicy(token,p);
		
	}	
	
	protected int sendPolicy(OpenSSOToken token,String xml) throws Exception {

		
		Representation r = new StringRepresentation(xml,MediaType.APPLICATION_XML);
		ClientResource client = new ClientResource(policyService);
		
		Form headers = new Form();  
		client.getRequest().getAttributes().put(headers_tag, headers);  
		headers.add(OTAAParams.subjectid.toString(), token.getToken());
		
		Representation response = null;
		try {
			
			response = client.post(r);
			return client.getStatus().getCode();
			
		} catch (Exception x) {
			try { System.out.println(response.getText()); } catch (Exception xx ) {x.printStackTrace();}
			throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,x.getMessage());
		} finally {
			try {response.release(); } catch (Exception x) {}
			try {r.release(); } catch (Exception x) {}
			try {client.release(); } catch (Exception x) {}
		}
		
	}
	
	@Override
	public int deletePolicy(OpenSSOToken token, String policyId) throws Exception {
		if ((token==null) || (token.getToken()==null)) throw new Exception(OpenSSOToken.MSG_EMPTY_TOKEN,null);
		if (policyId==null) throw new Exception(MSG_EMPTY_POLICYID,null);
		if (!token.isTokenValid()) throw new Exception("Invalid token",null);
		Form headers = new Form();  
		headers.add(OTAAParams.subjectid.toString(), token.getToken());
		headers.add(OTAAParams.id.toString(), policyId);
		
		Representation response = null;
		ClientResource client = new ClientResource(policyService);
		try {
			client.getRequest().getAttributes().put(headers_tag, headers);  
			response = client.delete();
			
			return client.getStatus().getCode();
			
		} catch (Exception x) {
			throw x;
		} finally {
			try {response.release(); } catch (Exception x) {}
		//	try {r.release(); } catch (Exception x) {}
			try {client.release(); } catch (Exception x) {}
		}		
	}

	@Override
	public int getURIOwner(OpenSSOToken token, String uri, IOpenToxUser user) throws Exception {
		return getURIOwner(token, uri, user,null);
	}

	
	@Override
	public int getURIOwner(OpenSSOToken token, String uri,IOpenToxUser user, Hashtable<String, String> policies) throws Exception {
		if ((token==null) || (token.getToken()==null)) throw new Exception(OpenSSOToken.MSG_EMPTY_TOKEN,null);
		if (uri==null) throw new Exception(MSG_EMPTY_URI,null);
		
		if (!token.isTokenValid()) throw new Exception("Invalid token",null);
			
		Form headers = new Form();  
		headers.add(OTAAParams.subjectid.toString(), token.getToken());
		headers.add(OTAAParams.uri.toString(), uri);
		headers.add(OTAAParams.polnames.toString(), Boolean.toString(policies!=null));
		
		
		
		Representation response = null;
		ClientResource client = new ClientResource(policyService);
		try {
			client.getRequest().getAttributes().put(headers_tag, headers);  
			response = client.get();
			
			if (Status.SUCCESS_OK.equals(client.getStatus())) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(response.getStream()));
				int count = 0;
				String line = null;
				while ((line = reader.readLine())!=null) {
					if (count==0) user.setUserName(line.trim());
					else {
						if (policies==null) break;
						else policies.put(line,line);
					}
					count++;
				}
			} 
			return client.getStatus().getCode();
		
		} catch (Exception x) {
			throw x;
		} finally {
			try {response.release(); } catch (Exception x) {}
		//	try {r.release(); } catch (Exception x) {}
			try {client.release(); } catch (Exception x) {}
		}	
	}


	@Override
	public int listPolicy(OpenSSOToken token, String policyId, Hashtable<String, String> policies) throws Exception {
		if ((token==null) || (token.getToken()==null)) throw new Exception(OpenSSOToken.MSG_EMPTY_TOKEN,null);
		if (policyId==null) throw new Exception(MSG_EMPTY_POLICYID,null);
		if (!token.isTokenValid()) throw new Exception("Invalid token",null);
		Form headers = new Form();  
		headers.add(OTAAParams.subjectid.toString(), token.getToken());
		headers.add(OTAAParams.id.toString(), policyId);
		
		Representation response = null;
		ClientResource client = new ClientResource(policyService);
		try {
			client.getRequest().getAttributes().put(headers_tag, headers);  
			response = client.get();
			
			if (Status.SUCCESS_OK.equals(client.getStatus())) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(response.getStream()));
				String line = null;
				StringBuffer b = new StringBuffer();
				while ((line = reader.readLine())!=null) {
					b.append(line);
					b.append("\n");
				}
				policies.put(policyId, b.toString());
				
			} 
			return client.getStatus().getCode();
		
		} catch (Exception x) {
			throw x;
		} finally {
			try {response.release(); } catch (Exception x) {}
		//	try {r.release(); } catch (Exception x) {}
			try {client.release(); } catch (Exception x) {}
		}		
	}
	
	@Override
	/**
	 *Currently, the owner is in the first row, then the policy names follow row by row (if requested).
	 */
	public int listPolicies(OpenSSOToken token,Hashtable<String, String> policies)  throws Exception{
		if (!token.isTokenValid()) throw new Exception("Invalid token",null);
		Form headers = new Form();  
		headers.add(OTAAParams.subjectid.toString(), token.getToken());
		
		Representation response = null;
		ClientResource client = new ClientResource(policyService);
		try {
			client.getRequest().getAttributes().put(headers_tag, headers);  
			response = client.get();
			
			if (Status.SUCCESS_OK.equals(client.getStatus())) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(response.getStream()));
				String line = null;
	
				while ((line = reader.readLine())!=null) {
					policies.put(line,line);
				}
				
			} 
			return client.getStatus().getCode();
		
		} catch (Exception x) {
			throw x;
		} finally {
			try {response.release(); } catch (Exception x) {}
		//	try {r.release(); } catch (Exception x) {}
			try {client.release(); } catch (Exception x) {}
		}		
	}

}
