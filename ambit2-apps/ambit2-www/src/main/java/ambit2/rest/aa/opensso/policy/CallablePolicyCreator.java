package ambit2.rest.aa.opensso.policy;

import java.util.Hashtable;

import org.opentox.aa.opensso.OpenSSOPolicy;
import org.opentox.aa.opensso.OpenSSOToken;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.rest.aa.opensso.OpenSSOServicesConfig;
import ambit2.rest.admin.AdminResource;
import ambit2.rest.task.CallableProtectedTask;
import ambit2.rest.task.TaskResult;

public class CallablePolicyCreator<USERID> extends CallableProtectedTask<USERID> {
	protected Reference applicationRootReference;
	public enum _type {
		user,group
	}
	public enum _method {
		GET {
			@Override
			public String getDescription() {
				return "Allow reading the content of the resource at the specified URI" ;
			}
		},
		POST {
			@Override
			public String getDescription() {
				return "Allow creating new resources under specified URI";
			}
		},		
		PUT {
			@Override
			public String getDescription() {
				return "Allow updating the content of the resource at the specified URI" ;
			}
		},		
		DELETE {
			@Override
			public String getDescription() {
				return "Allow deleting the resource at the specified URI" ;
			}
		};		
		public abstract String getDescription();
	}
	protected String uri;
	protected String name;
	protected _type type;
	protected boolean[] methods = new boolean[_method.values().length];
	
	
	public CallablePolicyCreator(Form form,USERID token,Reference root) throws ResourceException {
		super(token);
		this.applicationRootReference = root;
		uri = form.getFirstValue("uri");
		if (uri==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"'uri' parameter not found!");
		try { 
			type = _type.valueOf(form.getFirstValue("type"));
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"'type' parameter not found! Should be type=user|group"); 
		}
		name = form.getFirstValue("name");
		if (name==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"'name' parameter not found! Should be name of the user or group");
		
		for (_method method:_method.values()) try {
			boolean allow = new Boolean("on".equals(form.getFirstValue(method.name().toUpperCase())));
			methods[method.ordinal()] = allow;
		} catch (Exception x) {
			methods[method.ordinal()] = false;
		}
	}
	@Override
	public TaskResult doCall() throws Exception {

		OpenSSOServicesConfig config = OpenSSOServicesConfig.getInstance();
		OpenSSOPolicy policy = new OpenSSOPolicy(config.getPolicyService());
		Hashtable<String, String> policies = new Hashtable<String, String>();
		try {
			OpenSSOToken ssotoken = new OpenSSOToken(config.getOpenSSOService());
			ssotoken.setToken(getToken());
			int allowed = 0;
			for (boolean method : methods) allowed += method?1:0;
			String[] m = new String[allowed];
			int j=0;
			for (_method x : _method.values()) if (methods[x.ordinal()]) { m[j] = x.name();j++; } 
			
			StringBuffer b = new StringBuffer();
			b.append(uri.trim().replace(":","").replace("/",""));
			for (String method: m) b.append(method);		
			
			String polname = String.format("%s%s%s",type,name,b.toString());
			int httpcode = -1;
			switch (type) {
			case group: {
				httpcode = policy.createGroupPolicy(name,ssotoken, uri, m,polname);
				break;
			}
			case user: {
				httpcode = policy.createUserPolicy(name,ssotoken, uri, m,polname);
				break;
			}
			}
			if (httpcode == 200)
				return new TaskResult(String.format("%s/%s/%s/%s",
						applicationRootReference,
						AdminResource.resource,OpenSSOPoliciesResource.resource,b.toString()));
			else throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,
					String.format("The policy service %s returned %d", config.getPolicyService(), httpcode));
				
		} catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,config.getPolicyService(),x);
		}
		
	}

}
