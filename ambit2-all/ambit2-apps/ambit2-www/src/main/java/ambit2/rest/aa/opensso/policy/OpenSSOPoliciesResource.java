package ambit2.rest.aa.opensso.policy;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.NotFoundException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.i.reporter.Reporter;
import net.idea.restnet.i.task.ICallableTask;
import net.idea.restnet.i.task.ITask;
import net.idea.restnet.i.task.ITaskApplication;
import net.idea.restnet.i.task.ITaskResult;
import net.idea.restnet.i.task.ITaskStorage;

import org.opentox.aa.opensso.OpenSSOPolicy;
import org.opentox.aa.opensso.OpenSSOToken;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
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
import ambit2.rest.task.CallablePOST;
import ambit2.rest.task.FactoryTaskConvertor;

/**
 * <pre>
 * /opentoxuser/userid/policy?uri=<uri-to-retrieve-policy-for>
 * </pre>
 * @author nina
 *
 */
public class OpenSSOPoliciesResource extends CatalogResource<Policy> {
	public static final String resource = "policy";
	protected List<Policy> policiesList = null;
	@Override
	public String getTemplateName() {
		return "a/policy_openam.ftl";
	}
	@Override
	public boolean isHtmlbyTemplate() {
		return true;
	}
	@Override
	protected Iterator<Policy> createQuery(Context context, Request request,
			Response response) throws ResourceException {
		
		if (policiesList!=null) return policiesList.iterator();
		policiesList = new ArrayList<Policy>();
		
		Form form = getResourceRef(request).getQueryAsForm();
		final String uri = form.getFirstValue(search_param);
		if (uri==null) return policiesList.iterator(); //throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Parameter missing: ?search=<uri-to-retrieve-policy-for>");
		
		User user = request.getClientInfo().getUser();
		if (user == null) {
			user = new OpenSSOUser();
			((OpenSSOUser)user).setUseSecureCookie(useSecureCookie(request));
		}
		if (user instanceof OpenSSOUser) {
			String token = getToken();
			if (token==null) throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED);
			
			OpenSSOServicesConfig config = null;
			Hashtable<String, String> policies = new Hashtable<String, String>();
			String policyService=null;
			try {
				config = OpenSSOServicesConfig.getInstance();
				policyService = config.getPolicyService();
				OpenSSOPolicy policy = new OpenSSOPolicy(policyService);				
				OpenSSOToken ssotoken = new OpenSSOToken(config.getOpenSSOService());
				ssotoken.setToken(token);
				policy.getURIOwner(ssotoken,uri,(OpenSSOUser)user, policies);
				if (policies.size()==0) {
					return new Iterator<Policy>() {
						@Override
						public boolean hasNext() {
							return false;
						}
						@Override
						public Policy next() {
							return null;
						}
						@Override
						public void remove() {
						}
					};
				}
				//too bad, refactor the policy class to not use hashtable
				
				Enumeration<String> e = policies.keys();
				while (e.hasMoreElements()) {
					Policy ssoPolicy = new Policy(e.nextElement());
					ssoPolicy.setUri(uri);
					policiesList.add(ssoPolicy);
					
				}
				return policiesList.iterator();
					
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
			return new StringConvertor(createHTMLReporter(),MediaType.TEXT_HTML);
		} else if (variant.getMediaType().equals(MediaType.APPLICATION_JSON)) {
			PolicyJSONReporter r = new PolicyJSONReporter(getRequest(),null);
			return new StringConvertor(r,MediaType.APPLICATION_JSON);
		} else if (variant.getMediaType().equals(MediaType.APPLICATION_JAVASCRIPT)) {
			Form params = getResourceRef(getRequest()).getQueryAsForm();
			String jsonpcallback = params.getFirstValue("jsonp");
			if (jsonpcallback==null) jsonpcallback = params.getFirstValue("callback");
			PolicyJSONReporter r = new PolicyJSONReporter(getRequest(),jsonpcallback);
			return new StringConvertor(	r,MediaType.APPLICATION_JAVASCRIPT);		
		} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
			return new StringConvertor(	new PolicyURIReporter(getRequest()),MediaType.TEXT_URI_LIST);
			
		} else //html 	
			return new StringConvertor(createHTMLReporter(),MediaType.TEXT_HTML);
		
	}
	/**
	 * POST to create / delete policy
	 * expects
	 * curl -d "uri= " -d "name= " -d "type=group|user" -d "get=on" -d "post=on"
	 */
	@Override
	protected ICallableTask createCallable(Form form, Policy item)
			throws ResourceException {
		return new CallablePolicyCreator<String>(form, getToken(),getRequest().getRootRef());
	}
	
	protected Reporter createHTMLReporter() {
		return new PolicyHTMLReporter(getRequest(),true);
	}
	

	@Override
	protected Reference getSourceReference(Form form, Policy model)
			throws ResourceException {

		return null;
	}
	
	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		synchronized (this) {
			ArrayList<UUID> tasks = new ArrayList<UUID>();
		
			try {
			Form form = entity.isAvailable()?new Form(entity):new Form();
			
				ICallableTask callable= createCallable(form,null);
				ITask<ITaskResult,String> task =  ((ITaskApplication)getApplication()).addTask(
						String.format("Create policy"),
						callable,
						getRequest().getRootRef(),
						callable instanceof CallablePOST, 
						getToken()
						);	
				task.update();
				setStatus(task.isDone()?Status.SUCCESS_OK:Status.SUCCESS_ACCEPTED);
				tasks.add(task.getUuid());

			} catch (ResourceException x) {
				throw x;
			} catch (Exception x) {
				throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x.getMessage(),x);
			}
			if (tasks.size()==0)
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
			else {
				
				ITaskStorage storage = ((ITaskApplication)getApplication()).getTaskStorage();
				FactoryTaskConvertor<Object> tc = new FactoryTaskConvertor<Object>(storage);
				if (tasks.size()==1)
					return tc.createTaskRepresentation(tasks.get(0), variant, getRequest(),getResponse(),null);
				else
					return tc.createTaskRepresentation(tasks.iterator(), variant, getRequest(),getResponse(),null);				
			}
		}
	}	
	
	@Override
	protected Representation processNotFound(NotFoundException x,Variant variant)
			throws Exception {
		if (MediaType.TEXT_HTML.equals(variant.getMediaType())) {
			StringWriter output = new StringWriter();
			PolicyHTMLReporter r = new PolicyHTMLReporter(getRequest(),true);
			r.setOutput(output);
			r.header(output, null);
			r.footer(output, null);
			output.flush();
			return new StringRepresentation(output.toString(),MediaType.TEXT_HTML);
		} else return super.processNotFound(x, variant);
	}
	

	
}
