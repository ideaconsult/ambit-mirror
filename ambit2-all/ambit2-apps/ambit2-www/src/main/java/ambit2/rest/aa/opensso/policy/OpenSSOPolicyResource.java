package ambit2.rest.aa.opensso.policy;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.i.reporter.Reporter;

import org.opentox.aa.opensso.OpenSSOPolicy;
import org.opentox.aa.opensso.OpenSSOToken;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.security.User;

import ambit2.rest.StringConvertor;
import ambit2.rest.aa.opensso.OpenSSOServicesConfig;
import ambit2.rest.aa.opensso.OpenSSOUser;
import ambit2.rest.algorithm.CatalogResource;

/**
 * A wrapper for OpenSSO policy GET/POST/DELETE
 * @author nina
 *
 */
public class OpenSSOPolicyResource extends CatalogResource<Policy>{
	public final static String policyKey = "policyKey";	
	@Override
	protected Iterator<Policy> createQuery(Context context, Request request,
			Response response) throws ResourceException {
		
		Object policyId = request.getAttributes().get(policyKey);
		if (policyId==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Policy identifier missing");
		
		User user = request.getClientInfo().getUser();
		if (user == null) {
			user = new OpenSSOUser();
			((OpenSSOUser)user).setUseSecureCookie(useSecureCookie(request));
		}
		if (user instanceof OpenSSOUser) {
			String token = getToken();
			if (token==null) throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED);
			
			String policyService = null;
			Hashtable<String, String> policies = new Hashtable<String, String>();
			try {

				OpenSSOServicesConfig config = OpenSSOServicesConfig.getInstance();
				policyService = config.getPolicyService();
				OpenSSOPolicy policy = new OpenSSOPolicy(policyService);				
				OpenSSOToken ssotoken = new OpenSSOToken(config.getOpenSSOService());
				ssotoken.setToken(token);
				policy.listPolicy(ssotoken, policyId.toString(), policies);
				if (policies.size()==0) throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
				//too bad, refactor the policy class to not use hashtable
				List<Policy> p = new ArrayList<Policy>();
				Enumeration<String> e = policies.keys();
				while (e.hasMoreElements()) {
					Policy pp = new Policy(e.nextElement());

					//pp.setUri(uri);
					pp.setXml(policies.get(pp.getId()));
					p.add(pp);
					
				}
				return p.iterator();
					
			} catch (ResourceException x) {
				throw x;
			} catch (Exception x) {
				throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,policyService,x);
			}
			
		} else throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
	} 

	@Override
	protected Representation post(Representation entity)
			throws ResourceException {
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}
	
	@Override
	protected Representation put(Representation representation)
			throws ResourceException {
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}
	
	@Override
	protected Representation delete(Variant variant) throws ResourceException {
		Object policyId = getRequest().getAttributes().get(policyKey);
		if (policyId==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Policy identifier missing");
		User user = getRequest().getClientInfo().getUser();
		if (user instanceof OpenSSOUser) {
			String token = getToken();
			if (token==null) throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED);
			
			String policyService = null;
			Hashtable<String, String> policies = new Hashtable<String, String>();
			try {

				OpenSSOServicesConfig config = OpenSSOServicesConfig.getInstance();
				policyService = config.getPolicyService();
				OpenSSOPolicy policy = new OpenSSOPolicy(policyService);				
				OpenSSOToken ssotoken = new OpenSSOToken(config.getOpenSSOService());
				ssotoken.setToken(token);
				int httpcode = policy.deletePolicy(ssotoken, policyId.toString());
				if (httpcode==200)
					return new StringRepresentation("Policy deleted",MediaType.TEXT_PLAIN);
				else throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,config.getPolicyService());
					
			} catch (ResourceException x) {
				throw x;
			} catch (Exception x) {
				throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,policyService,x);
			}
			
		} else throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);		
	}
	
	@Override
	public IProcessor<Iterator<Policy>, Representation> createConvertor(
			Variant variant) throws AmbitException, ResourceException {

		if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
			return new StringConvertor(
					createHTMLReporter(),MediaType.TEXT_HTML);
		} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
			return new StringConvertor(	new PolicyURIReporter(getRequest()),MediaType.TEXT_URI_LIST);
			
		} else //html 	
			return new StringConvertor(createHTMLReporter(),MediaType.TEXT_HTML);
		
	}
	protected Reporter createHTMLReporter() {
		return new PolicyHTMLReporter(getRequest(),false);
	}
}
