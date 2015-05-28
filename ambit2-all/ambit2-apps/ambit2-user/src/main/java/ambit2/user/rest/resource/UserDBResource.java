package ambit2.user.rest.resource;

import java.io.Writer;
import java.sql.Connection;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.reporter.Reporter;
import net.idea.modbcum.q.conditions.EQCondition;
import net.idea.restnet.c.RepresentationConvertor;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.c.StringConvertor;
import net.idea.restnet.c.html.HTMLBeauty;
import net.idea.restnet.c.task.CallableProtectedTask;
import net.idea.restnet.c.task.FactoryTaskConvertor;
import net.idea.restnet.c.task.TaskCreator;
import net.idea.restnet.db.DBConnection;
import net.idea.restnet.db.QueryURIReporter;
import net.idea.restnet.db.aalocal.user.ReadUserRoles;
import net.idea.restnet.db.convertors.OutputWriterConvertor;
import net.idea.restnet.db.convertors.QueryHTMLReporter;
import net.idea.restnet.db.convertors.RDFJenaConvertor;
import net.idea.restnet.i.freemarker.IFreeMarkerApplication;
import net.idea.restnet.i.task.ITaskStorage;
import net.idea.restnet.rdf.FactoryTaskConvertorRDF;
import net.idea.restnet.user.DBUser;
import net.idea.restnet.user.db.ReadUser;
import net.idea.restnet.user.resource.UserCSVReporter;
import net.idea.restnet.user.resource.UserJSONReporter;
import net.idea.restnet.user.resource.UserRDFReporter;
import net.idea.restnet.user.resource.UserURIReporter;
import net.toxbank.client.io.rdf.TOXBANK;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.CacheDirective;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.config.AMBITConfig;
import ambit2.user.rest.AMBITCallableUserCreator;

/**
 * Protocol resource
 * 
 * @author nina
 * 
 * @param <Q>
 */
public class UserDBResource<T> extends AmbitDBQueryResource<ReadUser<T>, DBUser> {

    public static final String resourceKey = "user";

    protected boolean singleItem = false;
    protected boolean editable = true;

    public UserDBResource() {
	super();
	setHtmlbyTemplate(true);
    }

    @Override
    public boolean isHtmlbyTemplate() {
	return freeMarkerSupport.isHtmlbyTemplate();
    }

    @Override
    public String getTemplateName() {
	return singleItem ? "a/myprofile_body.ftl" : "a/users_body.ftl";
    }

    @Override
    protected void doInit() throws ResourceException {
	super.doInit();
	getVariants().add(new Variant(MediaType.TEXT_XML));
    }

    @Override
    public RepresentationConvertor createConvertor(Variant variant) throws AmbitException, ResourceException {
	/*
	 * if (variant.getMediaType().equals(MediaType.TEXT_PLAIN)) {
	 * 
	 * return new StringConvertor(new
	 * PropertyValueReporter(),MediaType.TEXT_PLAIN);
	 * 
	 * } else
	 */
	String filenamePrefix = getRequest().getResourceRef().getPath();
	if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
	    return new StringConvertor(new UserURIReporter<IQueryRetrieval<DBUser>>(getRequest()),
		    MediaType.TEXT_URI_LIST, filenamePrefix);
	} else if (variant.getMediaType().equals(MediaType.TEXT_CSV)) {
	    return new OutputWriterConvertor(new UserCSVReporter<IQueryRetrieval<DBUser>>(getRequest()),
		    MediaType.TEXT_CSV);
	} else if (variant.getMediaType().equals(MediaType.APPLICATION_RDF_XML)
		|| variant.getMediaType().equals(MediaType.APPLICATION_RDF_TURTLE)
		|| variant.getMediaType().equals(MediaType.TEXT_RDF_N3)
		|| variant.getMediaType().equals(MediaType.TEXT_RDF_NTRIPLES)) {
	    return new RDFJenaConvertor<DBUser, IQueryRetrieval<DBUser>>(new UserRDFReporter<IQueryRetrieval<DBUser>>(
		    getRequest(), variant.getMediaType(), getDocumentation()), variant.getMediaType(), filenamePrefix) {
		/**
					     * 
					     */
		private static final long serialVersionUID = -2520251520974057933L;

		@Override
		protected String getDefaultNameSpace() {
		    return TOXBANK.URI;
		}
	    };
	} else {

	    return createJSONConvertor(variant);
	}
    }

    public RepresentationConvertor createJSONConvertor(Variant variant) throws AmbitException, ResourceException {
	String usersdbname = getContext().getParameters().getFirstValue(AMBITConfig.users_dbname.name());
	final ReadUserRoles query = new ReadUserRoles();
	query.setDatabaseName(usersdbname == null ? getDefaultUsersDB() : usersdbname);
	String regDBName = null;
	if ((getClientInfo() != null) && (getClientInfo().getUser() != null) && (getClientInfo().getRoles() != null)
		&& (DBRoles.isAdmin(getClientInfo().getRoles()))) {
	    regDBName = query.getDatabaseName();
	}
	return new OutputWriterConvertor(new UserJSONReporter<IQueryRetrieval<DBUser>>(getRequest(), regDBName) {
	    /**
					     * 
					     */
	    private static final long serialVersionUID = -3652772856548240919L;

	    @Override
	    protected ReadUserRoles createUserRolesQuery() {
		return query;
	    }

	    @Override
	    protected String writeRoles(DBUser user) {
		StringBuilder roles = null;
		for (AMBITDBRoles role : AMBITDBRoles.values()) {
		    if (roles == null)
			roles = new StringBuilder();
		    else
			roles.append(",");
		    roles.append(String.format("\"%s\":%s", role.name(), user.getRoles() != null
			    && user.getRoles().indexOf(role.name()) >= 0));
		}
		return roles == null ? "" : roles.toString();
	    }
	}, MediaType.APPLICATION_JSON);
    }

    @Override
    protected QueryHTMLReporter createHTMLReporter(boolean headless) throws ResourceException {
	return null;
    }

    protected ReadUser getUserQuery(Object key, String search_name, Object search_value) throws ResourceException {
	if (key == null) {
	    ReadUser query = new ReadUser();
	    query.setUsertable("user");
	    if (search_value != null) {
		if ("search".equals(search_name)) {
		    DBUser user = new DBUser();
		    String s = String.format("^%s", search_value.toString());
		    user.setLastname(s);
		    user.setFirstname(s);
		    query.setValue(user);

		} else if ("username".equals(search_name)) {
		    DBUser user = new DBUser();
		    query.setCondition(EQCondition.getInstance());
		    user.setUserName(search_value.toString());
		    query.setValue(user);
		}
	    }
	    return query;
	} else {
	    if (key.toString().startsWith("U")) {
		singleItem = true;
		return new ReadUser(new Integer(Reference.decode(key.toString().substring(1))));
	    } else
		throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
	}
    }

    @Override
    protected ReadUser createQuery(Context context, Request request, Response response) throws ResourceException {
	Form form = request.getResourceRef().getQueryAsForm();
	String search_name = null;
	Object search_value = null;
	try {
	    search_name = "q";
	    search_value = form.getFirstValue(search_name);
	} catch (Exception x) {
	    search_value = null;
	}

	Object key = request.getAttributes().get(UserDBResource.resourceKey);
	try {
	    return getUserQuery(key, search_name, search_value);
	} catch (ResourceException x) {
	    throw x;
	} catch (Exception x) {
	    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, String.format("Invalid protocol id %d", key),
		    x);
	}
    }

    @Override
    protected QueryURIReporter<DBUser, ReadUser<T>> getURIReporter(Request baseReference) throws ResourceException {
	return new UserURIReporter(getRequest());
    }

    protected void check(Form form, Method method, boolean async, Reference reference) throws Exception {
	if (Method.POST.equals(method) && (form.getFirstValue(ReadUser.fields.email.name()) == null)) {
	    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "e-mail address not specified!");
	}
    }

    @Override
    protected TaskCreator getTaskCreator(Form form, Method method, boolean async, Reference reference) throws Exception {
	check(form, method, async, reference);
	return super.getTaskCreator(form, method, async, reference);
    }

    @Override
    protected CallableProtectedTask<String> createCallable(Method method, Form form, DBUser item)
	    throws ResourceException {

	Connection conn = null;
	try {
	    String usersdbname = getContext().getParameters().getFirstValue(AMBITConfig.users_dbname.name());

	    boolean enableEmailVerification = true;
	    try {
		enableEmailVerification = ((IFreeMarkerApplication) getApplication()).isEnableEmailVerification();
	    } catch (Exception x) {
	    }
	    UserURIReporter reporter = new UserURIReporter(getRequest(), "");
	    DBConnection dbc = new DBConnection(getApplication().getContext(), getConfigFile());
	    conn = dbc.getConnection(30,true,8);
	    return new AMBITCallableUserCreator(method, item, reporter, form, getRequest().getRootRef().toString(),
		    conn, getToken(), false, enableEmailVerification, usersdbname == null ? getDefaultUsersDB()
			    : usersdbname);
	} catch (Exception x) {
	    try {
		conn.close();
	    } catch (Exception xx) {
	    }
	    throw new ResourceException(Status.SERVER_ERROR_INTERNAL, x);
	}
    }

    protected String getObjectURI(Form queryForm) throws ResourceException {
	return null;
    }

    @Override
    protected ReadUser<T> createUpdateQuery(Method method, Context context, Request request, Response response)
	    throws ResourceException {
	Object key = request.getAttributes().get(UserDBResource.resourceKey);
	if (Method.POST.equals(method)) {
	    if (key == null)
		return null;// post allowed only on /user level, not on /user/id
	} else {
	    if (key != null)
		return super.createUpdateQuery(method, context, request, response);
	}
	throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
    }

    @Override
    protected FactoryTaskConvertor getFactoryTaskConvertor(ITaskStorage storage) throws ResourceException {
	return new FactoryTaskConvertorRDF<Object>(storage, getHTMLBeauty()) {
	    @Override
	    public synchronized Reporter<Iterator<UUID>, Writer> createTaskReporterHTML(Request request,
		    ResourceDoc doc, HTMLBeauty htmlbeauty) throws AmbitException, ResourceException {
		return null;
	    }
	};
    }

    @Override
    protected boolean isAllowedMediaType(MediaType mediaType) throws ResourceException {
	return MediaType.APPLICATION_WWW_FORM.equals(mediaType);
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

}
