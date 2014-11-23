package ambit2.user.rest.resource;

import java.io.Writer;
import java.sql.Connection;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.NotFoundException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.i.reporter.Reporter;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.c.StringConvertor;
import net.idea.restnet.c.html.HTMLBeauty;
import net.idea.restnet.c.task.CallableProtectedTask;
import net.idea.restnet.c.task.FactoryTaskConvertor;
import net.idea.restnet.c.task.TaskCreator;
import net.idea.restnet.db.DBConnection;
import net.idea.restnet.db.convertors.QueryHTMLReporter;
import net.idea.restnet.i.freemarker.IFreeMarkerApplication;
import net.idea.restnet.i.task.ITaskStorage;
import net.idea.restnet.rdf.FactoryTaskConvertorRDF;
import net.idea.restnet.u.RegistrationJSONReporter;
import net.idea.restnet.u.UserRegistration;
import net.idea.restnet.u.db.ReadRegistration;
import net.idea.restnet.user.resource.UserURIReporter;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.CacheDirective;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.config.AMBITConfig;


public class PwdForgottenConfirmResource extends  AmbitDBQueryResource<ReadRegistration,UserRegistration> {
	public static String confirmationCode = "code";
	public PwdForgottenConfirmResource() {
		super();
		setHtmlbyTemplate(true);
	}
	
	@Override
	public String getTemplateName() {
		return "a/pwd_forgotten_confirm.ftl";
	}
	
	@Override
	protected QueryHTMLReporter createHTMLReporter(boolean headless)
			throws ResourceException {
		return null;
	}
	
	@Override
	protected Map<String, Object> getMap(Variant variant)
			throws ResourceException {
		Map<String, Object> map =  super.getMap(variant);
		Object code = getRequest().getResourceRef().getQueryAsForm().getFirstValue(confirmationCode);
		if (code!=null) map.put(AMBITConfig.ambit_reg_confirmed.name(), code);
		map.put("searchURI",Resources.confirm);
		return map;
	}

	@Override
	public IProcessor<ReadRegistration, Representation> createConvertor(
			Variant variant) throws AmbitException, ResourceException {
		RegistrationJSONReporter r = new RegistrationJSONReporter(getRequest());
		return new StringConvertor(	r,MediaType.APPLICATION_JSON,"");
	}

	@Override
	protected ReadRegistration createQuery(Context context, Request request,
			Response response) throws ResourceException {
		Object code = getRequest().getResourceRef().getQueryAsForm().getFirstValue(confirmationCode);
		if (code==null) return null;
		ReadRegistration q = new ReadRegistration(code.toString());
		String usersdbname = getContext().getParameters().getFirstValue(AMBITConfig.users_dbname.name());
		q.setDatabaseName(usersdbname==null?getDefaultUsersDB():usersdbname);
		return q;
	}
	
	@Override
	protected ReadRegistration createUpdateQuery(Method method, Context context,
			Request request, Response response) throws ResourceException {
		Object key = request.getAttributes().get(UserDBResource.resourceKey);
		if (Method.POST.equals(method)) {
			if (key==null) return null;//post allowed only on /user level, not on /user/id
		} else if (Method.PUT.equals(method)) {
			return createQuery(context, request, response);
		}
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}
	
	protected String getObjectURI(Form queryForm) throws ResourceException {
		return null;		
	}
	
	@Override
	protected Representation processNotFound(NotFoundException x,
			Variant variant) throws Exception {
		return null;
	}

	@Override
	protected TaskCreator getTaskCreator(Form form, Method method,
			boolean async, Reference reference) throws Exception {
		if (Method.PUT.equals(method) && (getRequest().getResourceRef().getQueryAsForm().getFirstValue("code")==null)) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Invalid code");
		}
		return super.getTaskCreator(form, method, async, reference);
	}
	@Override
	protected CallableProtectedTask<String> createCallable(Method method,
			Form form, UserRegistration item) throws ResourceException {
		Connection conn = null;
		try {
			String usersdbname = getContext().getParameters().getFirstValue(AMBITConfig.users_dbname.name());
			boolean enableEmailVerification  = true;
			try {
				enableEmailVerification  = ((IFreeMarkerApplication)getApplication()).isEnableEmailVerification();
			} catch (Exception x) {}
		
			UserURIReporter reporter = new UserURIReporter(getRequest(),"");
			DBConnection dbc = new DBConnection(getApplication().getContext(),getConfigFile());
			conn = dbc.getConnection();
			return new CallablePasswordReset(method,item,reporter, form,getRequest().getRootRef().toString(),
					conn,getToken(),
					enableEmailVerification,usersdbname==null?getDefaultUsersDB():usersdbname);
		} catch (Exception x) {
			try { conn.close(); } catch (Exception xx) {}
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x);
		}
	}
	
	@Override
	protected FactoryTaskConvertor getFactoryTaskConvertor(ITaskStorage storage)
			throws ResourceException {
		return new FactoryTaskConvertorRDF<Object>(storage,getHTMLBeauty()) {
			@Override
			public synchronized Reporter<Iterator<UUID>, Writer> createTaskReporterHTML(
					Request request,ResourceDoc doc,HTMLBeauty htmlbeauty) throws AmbitException, ResourceException {
				return	new RegistrationTaskHTMLReporter(storage,request,doc,htmlbeauty);
			}	
			@Override
			public synchronized Representation createTaskRepresentation(
					UUID task, Variant variant, Request request,
					Response response, ResourceDoc doc)
					throws ResourceException {
				String locationRef = String.format("%s/task/%s", getRequest().getRootRef(),task);
				Representation r = new StringRepresentation(locationRef+"\n",MediaType.TEXT_URI_LIST);				
				response.redirectSeeOther(locationRef);
				return r;
			}
			
		};
	}
	
	@Override
	protected Representation processAndGenerateTask(Method method,
			Representation entity, Variant variant, boolean async)
			throws ResourceException {
		return super.processAndGenerateTask(method, entity, variant, async);
	}

	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}
	@Override
	protected Representation delete(Variant variant) throws ResourceException {
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}
	
	@Override
	protected String getItemName(UserRegistration item) {
		return "forgotten password";
	}
	@Override
	protected void setCacheHeaders() {
		getResponse().getCacheDirectives().add(CacheDirective.noCache());
	}
}