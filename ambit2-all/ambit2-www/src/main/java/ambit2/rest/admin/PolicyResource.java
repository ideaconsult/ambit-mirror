package ambit2.rest.admin;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.opentox.aa.OTAAParams;
import org.opentox.aa.OpenToxUser;
import org.opentox.aa.opensso.OpenSSOPolicy;
import org.opentox.aa.opensso.OpenSSOToken;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.rest.aa.opensso.OpenSSOServicesConfig;
import ambit2.rest.algorithm.CatalogResource;

public class PolicyResource extends CatalogResource<String>{
	public static final String resource = "policy";
	protected String user;
	protected String password;
	protected String[] methods;
	protected String uri;
	protected OpenSSOPolicy policy;
	protected OpenSSOToken ssoToken;
	protected boolean logout = false;
	protected List<String> topics = new ArrayList<String>();
	

	public PolicyResource() {
		super();
		OpenSSOServicesConfig config = OpenSSOServicesConfig.getInstance();
		ssoToken = new OpenSSOToken(config.getOpenSSOService());
		policy = new OpenSSOPolicy(config.getPolicyService());
	}
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		
		getVariants().clear();
		getVariants().add(new Variant(MediaType.TEXT_HTML));
	}

	protected void authenticate() throws Exception {
		if (ssoToken.getToken()==null) {
			ssoToken.login(user, password);
			logout = true;
		}	
	}
	protected void getValues(Form form) {
		user = getUserToken("user");
		password = getUserToken("password");
		methods = form.getValuesArray("method");
		uri = form.getFirstValue("uri");		
		if (user ==null) user = form.getFirstValue("user");
		if (password ==null) password = form.getFirstValue("password");
	}
	@Override
	protected Iterator<String> createQuery(Context context, Request request,
			Response response) throws ResourceException {
		if (getMethod().equals(Method.GET)) {
			topics.clear();
		
			getValues(getRequest().getResourceRef().getQueryAsForm());
			ssoToken.setToken(getUserToken(OTAAParams.subjectid.toString()));
	
			try {
				authenticate();
				for (String action : methods) 
					try {
						boolean ok = ssoToken.authorize(uri, action);
						topics.add(String.format("Authorized=%s METHOD=%s URI=%s",Boolean.toString(ok), action,uri));
					} catch (Exception x) {
						topics.add(String.format("Authorized=%s METHOD=%s URI=%s",x.getMessage(), action,uri));
					}
			} catch (Exception x) {
				topics.add(x.getMessage());
			} finally {
				try { if (logout) ssoToken.logout(); ssoToken.setToken(null);} catch (Exception x) {}
			}
		}
		return topics.iterator();
	}


	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		Form form = new Form(entity);
		getValues(form);
		return createPolicy(form,variant,true);
	}
	

	protected Representation createPolicy(Form form, Variant variant,boolean deleteOld)
			throws ResourceException {
		
		UUID policyid = UUID.randomUUID();
		StringBuffer b = new StringBuffer();

		try {
			authenticate();

			
			int r = policy.createGroupPolicy("opentox", ssoToken, uri, methods,policyid.toString());
			b.append(String.format("Result code=%d METHOD=%s URI=%s PolicyID=%s\n",r, methods,uri,policyid));
			try {
				OpenToxUser u = new OpenToxUser();
				policy.getURIOwner(ssoToken, uri, u);
				b.append(String.format("URI owner=%s\n",u.getUsername()));
/*
				Hashtable<String, String> policies = new Hashtable<String, String>();
				policy.listPolicies(ssoToken, policies);
				Enumeration<String> keys = policies.keys();
				while (keys.hasMoreElements()) {
					String key = keys.nextElement();
					policy.listPolicy(ssoToken, key, policies);
				}
				
				b.append(policies.toString());
*/
			} catch (Exception x) {
				b.append(x);
				throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,x);
			}
			//try if can authorize agains the new policy
			try {
				for (String action : methods) 
					try {
						boolean ok = ssoToken.authorize(uri, action);
						b.append(String.format("Authorized=%s METHOD=%s URI=%s\n",Boolean.toString(ok), action,uri));
						if (!ok) throw 
						new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED,b.toString());
					} catch (ResourceException x) {
						throw x;
					} catch (Exception x) {
						b.append(String.format("Authorized=%s METHOD=%s URI=%s\n",x.getMessage(), action,uri));
						throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,x);
					}
			} catch (ResourceException x) {
				throw x;
			} catch (Exception x) {
				throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,x);
			}			
			
			if (deleteOld)
			try { policy.deletePolicy(ssoToken,policyid.toString()); } catch (Exception x) {
				b.append(x);
			}
		} catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,x);
		} finally {
			
			try { if (logout) ssoToken.logout();} catch (Exception x) {
				throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,x);
			}
		}
		setStatus(Status.SUCCESS_OK);
		return new StringRepresentation(b.toString());
	}	
	@Override
	protected Representation put(Representation representation, Variant variant)
			throws ResourceException {
		Form form = new Form(representation);
		getValues(form);
		return createPolicy(form,variant,false);
	}

}
