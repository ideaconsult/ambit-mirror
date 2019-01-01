package ambit2.user.rest.resource;

import java.sql.Connection;
import java.util.Map;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.NotFoundException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.p.UpdateExecutor;
import net.idea.restnet.c.StringConvertor;
import net.idea.restnet.db.DBConnection;
import net.idea.restnet.db.convertors.QueryHTMLReporter;
import net.idea.restnet.u.RegistrationJSONReporter;
import net.idea.restnet.u.UserRegistration;
import net.idea.restnet.u.db.ConfirmRegistration;
import net.idea.restnet.u.db.ReadRegistration;
import net.idea.restnet.user.resource.UserURIReporter;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.CacheDirective;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.config.AMBITConfig;

public class RegistrationConfirmResource extends  AmbitDBQueryResource<ReadRegistration,UserRegistration> {
	public static String confirmationCode = "code";
	public RegistrationConfirmResource() {
		super();
		setHtmlbyTemplate(true);
	}
	
	@Override
	public String getTemplateName() {
		return "a/register_confirm.ftl";
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
	protected Representation getRepresentation(Variant variant)
			throws ResourceException {
		//confirm the registration only if JSON requested; 
		//this means it is either requested by JS enabled browser or by a knowledgeable client :)
	
		if (MediaType.APPLICATION_JSON.equals(variant.getMediaType())) {
			Connection conn = null;
			UpdateExecutor exec = null;
			Object code = getRequest().getResourceRef().getQueryAsForm().getFirstValue(confirmationCode);
			if (code!=null) 
			try {
				String usersdbname = getContext().getParameters().getFirstValue(AMBITConfig.users_dbname.name());
				if (usersdbname==null) usersdbname = getDefaultUsersDB();
				UserURIReporter reporter = new UserURIReporter(getRequest(),"");
				DBConnection dbc = new DBConnection(getApplication().getContext(),getConfigFile());
				conn = dbc.getConnection();
				UserRegistration reg = new  UserRegistration(code.toString());
				reg.setTitle("Confirm reset");
				ConfirmRegistration q = new ConfirmRegistration(reg);
				q.setDatabaseName(usersdbname);
				exec = new UpdateExecutor();
				exec.setConnection(conn);
				exec.process(q);
			} catch (Exception x) {
				try {if (conn!=null) {conn.close(); conn=null;} } catch (Exception xx) {}
				throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x);
			} finally {
				try {exec.close();} catch (Exception x) {}
				try {if (conn!=null) conn.close();} catch (Exception x) {}				
			}
		}
		return super.getRepresentation(variant);
	}
	
	@Override
	protected Representation processNotFound(NotFoundException x,
			Variant variant) throws Exception {
		return null;
	}
	@Override
	protected void setCacheHeaders() {
		getResponse().getCacheDirectives().add(CacheDirective.noCache());
	}
}
