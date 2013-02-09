package ambit2.rest.admin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

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

/**
 * OpenSSO & Policy server tested workflows  
 * @author nina
 *
 */
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
		try {
		OpenSSOServicesConfig config = OpenSSOServicesConfig.getInstance();
		ssoToken = new OpenSSOToken(config.getOpenSSOService());
		policy = new OpenSSOPolicy(config.getPolicyService());
		} catch (Exception x) {
			getLogger().log(Level.WARNING,x.getMessage(),x);
		}
	}
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		
		getVariants().clear();
		getVariants().add(new Variant(MediaType.TEXT_HTML));
	}

	protected boolean authenticate() throws Exception {
		if (ssoToken.getToken()==null) {
			logout = ssoToken.login(user, password);
			return logout;
		} else return true;	
	}
	protected void getValues(Form form) {
		user = getUserName();
		password = getPassword();
		methods = form.getValuesArray("method");
		uri = form.getFirstValue("uri");		
		if (uri==null) uri = String.format("%s/%s",getRequest().getRootRef(),UUID.randomUUID());
		if (user ==null) user = form.getFirstValue("user");
		if (password ==null) password = form.getFirstValue("password");
	}
	@Override
	protected Iterator<String> createQuery(Context context, Request request,
			Response response) throws ResourceException {
		if (getMethod().equals(Method.GET)) {
			topics.clear();
		
			getValues(getResourceRef(getRequest()).getQueryAsForm());
			ssoToken.setToken(getToken());
	
			try {
				if (authenticate()) {
					for (String action : methods) 
						try {
							if (ssoToken.authorize(uri, action)) 
								topics.add(String.format("Authorized METHOD=%s URI=%s", action,uri));
							else
								throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED,uri);
						} catch (ResourceException x) {
							throw x;
						} catch (Exception x) {
							throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x);
							//topics.add(String.format("Authorized=%s METHOD=%s URI=%s",x.getMessage(), action,uri));
						}
				} else throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED);	
			} catch (ResourceException x) {
				throw x;				
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

			
			int r = policy.createGroupPolicy("partner", ssoToken, uri, methods,policyid.toString());
			//int r = policy.createUserPolicy("nina", ssoToken, uri, methods,policyid.toString());
			b.append(String.format("Result code=%d URI=%s PolicyID=%s METHOD=",r, uri,policyid));
			for (String method:methods) b.append(method);
			b.append("\n");
			try {
				OpenToxUser u = new OpenToxUser();
				r=policy.getURIOwner(ssoToken, uri, u);
				b.append(String.format("Result code=%d\tURI owner=%s\n",r,u.getUsername()));
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
			//try if can authorize against the new policy
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

		} catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,x);
		} finally {
			
			if (deleteOld)
			try { policy.deletePolicy(ssoToken,policyid.toString()); } catch (Exception x) {
				b.append(x);
			}
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
