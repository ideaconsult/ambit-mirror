package ambit2.user.rest.resource;

import java.util.Map;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.CacheDirective;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.config.AMBITConfig;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.restnet.c.RepresentationConvertor;
import net.idea.restnet.db.convertors.OutputWriterConvertor;
import net.idea.restnet.user.DBUser;
import net.idea.restnet.user.app.db.DBUApp;
import net.idea.restnet.user.app.db.ReadApp;
import net.idea.restnet.user.app.db.UAppJsonReporter;

public class MyAccountAppsResource<T> extends AmbitDBQueryResource<ReadApp, DBUApp> {

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		setHtmlbyTemplate(true);
	}

	@Override
	public String getTemplateName() {
		return "a/myprofile_apps.ftl";
	}

	@Override
	public RepresentationConvertor createConvertor(Variant variant) throws AmbitException, ResourceException {

		return createJSONConvertor(variant);

	}

	public RepresentationConvertor createJSONConvertor(Variant variant) throws AmbitException, ResourceException {
		return new OutputWriterConvertor(new UAppJsonReporter(getRequest()), MediaType.APPLICATION_JSON);
	}

	@Override
	protected ReadApp createUpdateQuery(Method method, Context context, Request request, Response response)
			throws ResourceException {
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}

	@Override
	protected ReadApp createQuery(Context context, Request request, Response response) throws ResourceException {
		Object uname = null;

		try {
			uname = (getClientInfo() == null || getClientInfo().getUser() == null) ? null
					: getClientInfo().getUser().getIdentifier();
		} catch (Exception x) {
			uname = null;

		}
		if (uname == null)
			throw new ResourceException(Status.CLIENT_ERROR_FORBIDDEN);
		DBUser user = new DBUser();
		user.setUserName(uname.toString());
		ReadApp q = new ReadApp(null, user);
		String usersdbname = getContext().getParameters().getFirstValue(AMBITConfig.users_dbname.name());
		q.setDatabaseName(usersdbname == null ? getDefaultUsersDB() : usersdbname);
		return q;
	}

	@Override
	protected Map<String, Object> getMap(Variant variant) throws ResourceException {
		Map<String, Object> map = super.getMap(variant);
		map.put("myprofile", false);
		return map;
	}

	@Override
	protected void setCacheHeaders() {
		getResponse().getCacheDirectives().add(CacheDirective.noCache());
	}

	@Override
	protected String getItemName(DBUApp item) {
		return item.getName();
	}
}
