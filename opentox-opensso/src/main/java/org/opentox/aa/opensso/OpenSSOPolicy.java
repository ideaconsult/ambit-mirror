package org.opentox.aa.opensso;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Hashtable;

import org.opentox.aa.IOpenToxUser;
import org.opentox.aa.OTAAParams;
import org.opentox.aa.OpenToxPolicy;
import org.opentox.aa.policy.IPolicyHandler;
import org.opentox.aa.policy.PolicyHandler;
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
		return createGroupPolicy(group,token, uri, methods,b.toString());
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
		System.out.println(xml);
		ClientResource client = new ClientResource(policyService);
		
		Form headers = new Form();  
		client.getRequest().getAttributes().put(headers_tag, headers);  
		headers.add(OTAAParams.subjectid.toString(), token.getToken());
		
		Representation response = null;
		try {
			
			response = client.post(r);
			return client.getStatus().getCode();
			
		} catch (ResourceException x) {
			x.printStackTrace();
			throw new ResourceException(x.getStatus(),String.format("Error querying policy service %s %d %s",
					policyService,x.getStatus().getCode(), x.getMessage()),
					x);			
		} catch (Exception x) {
			throw new Exception(String.format("Error querying policy service %s %s",policyService,x.getMessage()),x);
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
			
		} catch (ResourceException x) {
			throw new ResourceException(x.getStatus(),String.format("Error querying policy service %s %d %s",
					policyService,x.getStatus().getCode(), x.getMessage()),
					x);			
		} catch (Exception x) {
			throw new Exception(String.format("Error querying policy service %s %s",policyService,x.getMessage()),x);
		} finally {
			try {response.release(); } catch (Exception x) {}
		//	try {r.release(); } catch (Exception x) {}
			try {client.release(); } catch (Exception x) {}
		}		
	}

	@Override
	public int getURIOwner(OpenSSOToken token, String uri, IOpenToxUser user) throws Exception {
		return getURIOwner(token, uri, user,(IPolicyHandler)null);
	}
	@Override
	public int getURIOwner(OpenSSOToken token, String uri,IOpenToxUser user, final Hashtable<String, String> policies) throws Exception {
		if (policies==null) return getURIOwner(token, uri, user,(IPolicyHandler)null);
		
		return getURIOwner(token, uri, user, new PolicyHandler() {
	
			@Override
			public void handlePolicy(String policyID, String content) throws Exception {
				policies.put(policyID,content);
			}
			
			@Override
			public void handlePolicy(String policyID) throws Exception {
				policies.put(policyID,policyID);
				
			}
		});		
	}
	
	public int getURIOwner(OpenSSOToken token, String uri,IOpenToxUser user, IPolicyHandler handler) throws Exception {
		if ((token==null) || (token.getToken()==null)) throw new Exception(OpenSSOToken.MSG_EMPTY_TOKEN,null);
		if (uri==null) throw new Exception(MSG_EMPTY_URI,null);
		
		if (!token.isTokenValid()) throw new Exception("Invalid token",null);
			
		Form headers = new Form();  
		headers.add(OTAAParams.subjectid.toString(), token.getToken());
		headers.add(OTAAParams.uri.toString(), uri);
		headers.add(OTAAParams.polnames.toString(), Boolean.toString(handler!=null));
		
		
		
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
					if (count==0) { 
						line = line==null?null:line.trim();
						if (handler!=null) handler.handleOwner(line); 
						user.setUserName(line);
					}
					else {
						if (handler==null) break;
						else handler.handlePolicy(line);
					}
					count++;
				}
			} 
			return client.getStatus().getCode();
		} catch (ResourceException x) {
			throw new ResourceException(x.getStatus(),String.format("Error querying policy service %s %d %s",
					policyService,x.getStatus().getCode(), x.getMessage()),
					x);			
		} catch (Exception x) {
			throw new Exception(String.format("Error querying policy service %s %s",policyService,x.getMessage()),x);
		} finally {
			try {response.release(); } catch (Exception x) {}
		//	try {r.release(); } catch (Exception x) {}
			try {client.release(); } catch (Exception x) {}
		}	
	}

	public int listPolicy(OpenSSOToken token, String policyId, final Hashtable<String, String> policies) throws Exception {
		return listPolicy(token, policyId, new PolicyHandler() {

			@Override
			public void handlePolicy(String policyID, String content) throws Exception {
				policies.put(policyID,content);
			}
			
			@Override
			public void handlePolicy(String policyID) throws Exception {
				policies.put(policyID,policyID);
				
			}
		});
	}

	public int listPolicy(OpenSSOToken token, String policyId, IPolicyHandler handler) throws Exception {
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
				handler.handlePolicy(policyId, b.toString());

				
			} 
			return client.getStatus().getCode();
		} catch (ResourceException x) {
			throw new ResourceException(x.getStatus(),
					String.format("Error querying policy service PolicyID=%s subjectid=%s %s %d %s",
					policyService,policyId,token.getToken(),x.getStatus().getCode(), x.getMessage()),
					x);			
		} catch (Exception x) {
			throw new Exception(String.format("Error querying policy service %s %s",policyService,x.getMessage()),x);
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
	public int listPolicies(OpenSSOToken token,final Hashtable<String, String> policies)  throws Exception{
		return listPolicies(token, new PolicyHandler() {

			@Override
			public void handlePolicy(String policyID, String content)
					throws Exception {
				policies.put(policyID,content);
				
			}
			@Override
			public void handlePolicy(String policyID) {
				policies.put(policyID,policyID);
			}
		});
	}
	public int listPolicies(OpenSSOToken token,IPolicyHandler handler)  throws Exception{		
		if (!token.isTokenValid()) throw new Exception("Invalid token",null);
		//Form headers = new Form();  
		//headers.add(OTAAParams.subjectid.toString(), token.getToken());
		
		InputStream in = null;
		HttpURLConnection uc = null;
		
		int code = -1;
		try {
			URL url = new URL(policyService);
			uc = (HttpURLConnection) url.openConnection();
			((HttpURLConnection)uc).setRequestProperty(OTAAParams.subjectid.toString(), token.getToken());
			uc.setDoOutput(true);
			uc.setRequestMethod("GET"); 

			code = uc.getResponseCode();
			in = uc.getInputStream();
			if (HttpURLConnection.HTTP_OK == code) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				String line = null;
	
				while ((line = reader.readLine())!=null) {
					if ("".equals(line.trim())) continue;
					handler.handlePolicy(line.trim());

				}
				
			} 
			return code;
		} catch (ResourceException x) {
			throw new ResourceException(x.getStatus(),String.format("Error querying policy service %s %d %s",
					policyService,x.getStatus().getCode(), x.getMessage()),
					x);
		} catch (Exception x) {
			throw new Exception(String.format("Error querying policy service %s %s",policyService,x.getMessage()),x);
		} finally {
			try {in.close(); } catch (Exception x) {}
		//	try {r.release(); } catch (Exception x) {}
			try {uc.disconnect(); } catch (Exception x) {}
		}		
	}

}
