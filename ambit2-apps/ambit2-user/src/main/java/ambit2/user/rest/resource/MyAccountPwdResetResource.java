package ambit2.user.rest.resource;

import java.io.Writer;
import java.sql.Connection;
import java.util.Iterator;
import java.util.UUID;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.reporter.Reporter;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.c.html.HTMLBeauty;
import net.idea.restnet.c.task.CallableProtectedTask;
import net.idea.restnet.c.task.FactoryTaskConvertor;
import net.idea.restnet.db.DBConnection;
import net.idea.restnet.i.freemarker.IFreeMarkerApplication;
import net.idea.restnet.i.task.ITaskStorage;
import net.idea.restnet.rdf.FactoryTaskConvertorRDF;
import net.idea.restnet.user.DBUser;
import net.idea.restnet.user.db.ReadUser;
import net.idea.restnet.user.resource.UserURIReporter;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.config.AMBITConfig;
import ambit2.user.rest.AMBITCallableUserCreator;


public class MyAccountPwdResetResource<T> extends MyAccountResource<T> {

	@Override
	public boolean isHtmlbyTemplate() {
		return true;
	}
	@Override
	public String getTemplateName() {
		return "a/pwd_body.ftl";
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
	protected CallableProtectedTask<String> createCallable(Method method,
			Form form, DBUser item) throws ResourceException {
		Connection conn = null;
		try {
			if (item.getUserName()==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
			if ((getClientInfo()!=null) && 
				(getClientInfo().getUser()!=null) &&
				(getClientInfo().getUser().getIdentifier()!=null) && 
				getClientInfo().getUser().getIdentifier().equals(item.getUserName())
				) {
				String usersdbname = getContext().getParameters().getFirstValue(AMBITConfig.users_dbname.name());
				boolean enableEmailVerification  = true;
				try {
					enableEmailVerification  = ((IFreeMarkerApplication)getApplication()).isEnableEmailVerification();
				} catch (Exception x) {}

				UserURIReporter reporter = new UserURIReporter(getRequest(),"");
				DBConnection dbc = new DBConnection(getApplication().getContext(),getConfigFile());
				conn = dbc.getConnection();
				return new AMBITCallableUserCreator(
							method,
							item,
							reporter, 
							form,
							getRequest().getRootRef().toString(),
							conn,
							getToken(),
							true,
							enableEmailVerification,
							usersdbname==null?getDefaultUsersDB():usersdbname
							);
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
}
