package ambit2.user.rest.resource;

import java.io.Writer;
import java.sql.Connection;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.CacheDirective;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.config.AMBITConfig;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.reporter.Reporter;
import net.idea.restnet.c.RepresentationConvertor;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.c.html.HTMLBeauty;
import net.idea.restnet.c.task.CallableProtectedTask;
import net.idea.restnet.c.task.FactoryTaskConvertor;
import net.idea.restnet.db.DBConnection;
import net.idea.restnet.db.convertors.OutputWriterConvertor;
import net.idea.restnet.i.task.ITaskStorage;
import net.idea.restnet.rdf.FactoryTaskConvertorRDF;
import net.idea.restnet.user.DBUser;
import net.idea.restnet.user.app.CallableTokenCreator;
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
	protected boolean isAllowedMediaType(MediaType mediaType) throws ResourceException {
		return MediaType.APPLICATION_WWW_FORM.equals(mediaType);
	}

	@Override
	protected String getObjectURI(Form queryForm) throws ResourceException {
		return null;
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
		if (Method.POST.equals(method)) {
			// just one record is fine
			ReadApp q = createQuery(context, request, response);
			q.setHowMany(3);
			q.setPage(0);
			q.setPageSize(1);
			return q;
		} else if (Method.PUT.equals(method)) {
			// takes the username only, token as form parameter
			ReadApp q = createQuery(context, request, response);
			q.setPage(0);
			q.setPageSize(1);
			return q;
		}
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}

	private Object getCurrentUserName() {
		return (getClientInfo() == null || getClientInfo().getUser() == null) ? null
				: getClientInfo().getUser().getIdentifier();
	}

	@Override
	protected ReadApp createQuery(Context context, Request request, Response response) throws ResourceException {
		Object uname = null;

		try {
			uname = getCurrentUserName();
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

	@Override
	protected CallableProtectedTask<String> createCallable(Method method, Form form, DBUApp item)
			throws ResourceException {
		Object uname = null;
		try {
			uname = getCurrentUserName();
		} catch (Exception x) {
			uname = null;
		}
		if (uname == null)
			throw new ResourceException(Status.CLIENT_ERROR_FORBIDDEN);

		Connection conn = null;
		try {

			String usersdbname = getContext().getParameters().getFirstValue(AMBITConfig.users_dbname.name());

			DBConnection dbc = new DBConnection(getApplication().getContext(), getConfigFile());
			conn = dbc.getConnection(30, true, 8);

			return new CallableTokenCreator(method, item, form, getRequest().getRootRef().toString(), conn, getToken(),
					usersdbname == null ? getDefaultUsersDB() : usersdbname);

		} catch (Exception x) {
			try {
				conn.close();
			} catch (Exception xx) {
			}
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL, x);
		}
	}

	@Override
	protected FactoryTaskConvertor getFactoryTaskConvertor(ITaskStorage storage) throws ResourceException {
		return new FactoryTaskConvertorRDF<Object>(storage, null) {
			@Override
			public synchronized Reporter<Iterator<UUID>, Writer> createTaskReporterHTML(Request request,
					ResourceDoc doc, HTMLBeauty htmlbeauty) throws AmbitException, ResourceException {
				return null;
			}
		};
	}

	@Override
	protected Representation post(Representation entity, Variant variant) throws ResourceException {
		// TODO Auto-generated method stub
		return super.post(entity, variant);
	}
}
