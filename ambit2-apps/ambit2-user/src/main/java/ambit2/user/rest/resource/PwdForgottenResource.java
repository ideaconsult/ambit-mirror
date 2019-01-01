package ambit2.user.rest.resource;

import java.sql.Connection;
import java.util.Map;

import net.idea.restnet.db.DBConnection;
import net.idea.restnet.i.freemarker.IFreeMarkerApplication;
import net.idea.restnet.i.task.ICallableTask;
import net.idea.restnet.user.DBUser;
import net.idea.restnet.user.resource.UserURIReporter;

import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.config.AMBITConfig;

public class PwdForgottenResource extends RegistrationResource {

	@Override
	public String getTemplateName() {
		return "a/pwd_forgotten.ftl";
	}

	@Override
	protected ICallableTask createCallable(Method method, Form form, DBUser item)
			throws ResourceException {
		Connection conn = null;
		try {
			String usersdbname = getContext().getParameters().getFirstValue(AMBITConfig.users_dbname.name());
			boolean enableEmailVerification  = true;
			try {
				enableEmailVerification  = ((IFreeMarkerApplication)getApplication()).isEnableEmailVerification();
			} catch (Exception x) {}

			UserURIReporter reporter = new UserURIReporter(getRequest(),"");
			DBConnection dbc = new DBConnection(getApplication().getContext(),getConfigFile());
			conn = dbc.getConnection(30,true,8);
			return new CallablePasswordReset(method,null,reporter, form,getRequest().getRootRef().toString(),
					conn,getToken(),
					enableEmailVerification,usersdbname==null?getDefaultUsersDB():usersdbname);
		} catch (Exception x) {
			try { conn.close(); } catch (Exception xx) {}
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x);
		}
	}
	@Override
	public void configureTemplateMap(Map<String, Object> map, Request request,
			IFreeMarkerApplication app) {
		super.configureTemplateMap(map, request, app);
        try {
        	
        	map.put(AMBITConfig.ambit_version_short.name(),app.getVersionShort());
	    	map.put(AMBITConfig.ambit_version_long.name(),app.getVersionLong());
	    	map.put(AMBITConfig.googleAnalytics.name(),app.getGACode());
	    	map.put(AMBITConfig.menu_profile.name(),app.getProfile());
        } catch (Exception x) {}
	}
	
}
