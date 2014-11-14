package ambit2.user.rest.resource;

import java.io.Writer;
import java.sql.Connection;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.reporter.Reporter;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.c.html.HTMLBeauty;
import net.idea.restnet.c.task.CallableProtectedTask;
import net.idea.restnet.c.task.FactoryTaskConvertor;
import net.idea.restnet.c.task.TaskCreator;
import net.idea.restnet.db.DBConnection;
import net.idea.restnet.i.task.ITaskStorage;
import net.idea.restnet.rdf.FactoryTaskConvertorRDF;
import net.idea.restnet.user.DBUser;
import net.idea.restnet.user.db.ReadUser;
import net.idea.restnet.user.resource.UserURIReporter;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Parameter;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.config.AMBITConfig;
import ambit2.user.rest.AdminResetCallable;


public class PwdResetResource<T> extends MyAccountResource<T> {
	protected Form params;
	@Override
	public boolean isHtmlbyTemplate() {
		return true;
	}
	@Override
	public String getTemplateName() {
		return "a/pwd_admin.ftl";
	}
	
	@Override
	protected Representation get(Variant variant) throws ResourceException {
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}
	
	@Override
	protected ReadUser<T> createUpdateQuery(Method method, Context context,
			Request request, Response response) throws ResourceException {
		if (Method.PUT.equals(method)) {
			return createQuery(context, request, response);
		}
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}
	
	@Override
	protected TaskCreator getTaskCreator(Representation entity, Variant variant, Method method, boolean async) throws Exception {

		if (entity!=null && MediaType.APPLICATION_WWW_FORM.equals(entity.getMediaType())) {
			Form form = getParams();
			final Reference reference = new Reference(getObjectURI(form));
			return getTaskCreator(form, method,async,reference);
		} 
		throw new ResourceException(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE);
	}	

	
	@Override
	protected ReadUser createQuery(Context context, Request request, Response response)
			throws ResourceException {
		Form form = getParams();
		final String param_uri = "uri";
		final String param_name = "username";
		String value_uri = null;
		String value_username = null;
		value_uri = form.getFirstValue(param_uri);
		value_username = form.getFirstValue(param_name);
		if (value_uri == null || value_username == null)
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"User not defined");
		int p = value_uri.indexOf(Resources.user);
		if (p<=0) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"User not defined");
		
		try {
			String id = value_uri.substring(p+7);
			DBUser user = new DBUser(new Integer(Reference.decode(id)));
			user.setUserName(value_username);
			return new ReadUser<T>(user);
		}catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(
					Status.CLIENT_ERROR_BAD_REQUEST,
					"Invalid user",
					x
					);
		}
	} 
	@Override
	protected CallableProtectedTask<String> createCallable(Method method,
			Form form, DBUser item) throws ResourceException {
		Connection conn = null;
		try {
			if (item.getUserName()==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
			if ((getClientInfo()!=null) && 
				(getClientInfo().getUser()!=null) &&
				(getClientInfo().getUser().getIdentifier()!=null))	 {
				
				if ((getClientInfo().getRoles()!=null) && DBRoles.isAdmin(getClientInfo().getRoles())) {
				
					String usersdbname = getContext().getParameters().getFirstValue(AMBITConfig.users_dbname.name());
	
					UserURIReporter reporter = new UserURIReporter(getRequest(),"");
					DBConnection dbc = new DBConnection(getApplication().getContext(),getConfigFile());
					conn = dbc.getConnection();
					return new AdminResetCallable(
								method,
								item,
								reporter, 
								form,
								getRequest().getRootRef().toString(),
								conn,
								getToken(),
								true,
								false,
								usersdbname==null?getDefaultUsersDB():usersdbname
								);
				}
			} 
			throw new ResourceException(Status.CLIENT_ERROR_FORBIDDEN);
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
				return null;
			}			
		};
	}
	
	@Override
	protected Form getParams() {
		if (params == null) 
			if (Method.GET.equals(getRequest().getMethod())) {
				params = getResourceRef(getRequest()).getQueryAsForm();
				Iterator<Parameter> p = params.iterator();
				while (p.hasNext()) {
					Parameter param = p.next();
					String value = param.getValue();
					if (value==null) continue;
					if (value.contains("script") || value.contains(">") || value.contains("<")) param.setValue(""); 
				}
			}	
			//if POST, the form should be already initialized
			else params = getRequest().getEntityAsForm();
		return params;
	}
	
	@Override
	protected Map<String, Object> getMap(Variant variant)
			throws ResourceException {
		Map<String, Object> map = super.getMap(variant);
		map.put("myprofile", false);
		return map;
	}

}
