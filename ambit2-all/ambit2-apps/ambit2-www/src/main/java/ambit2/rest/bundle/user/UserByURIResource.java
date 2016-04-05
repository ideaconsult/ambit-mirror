package ambit2.rest.bundle.user;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Set;
import java.util.TreeSet;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.restnet.c.RepresentationConvertor;
import net.idea.restnet.c.task.CallableProtectedTask;
import net.idea.restnet.db.DBConnection;
import net.idea.restnet.db.convertors.OutputWriterConvertor;
import net.idea.restnet.user.DBUser;
import net.idea.restnet.user.db.ReadUser;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.config.AMBITConfig;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.db.search.QueryExecutor;
import ambit2.db.update.bundle.BundleOwnerByNumber;
import ambit2.db.update.bundle.ReadBundle;
import ambit2.db.update.bundle.UpdateBundle._published_status;
import ambit2.rest.AmbitApplication;
import ambit2.rest.OpenTox;
import ambit2.user.policy.CallableBundlePolicyCreator;
import ambit2.user.policy.CallablePolicyUsersCreator;
import ambit2.user.rest.ReadUserByBundleNumber;
import ambit2.user.rest.resource.SimpleUserJSONReporter;
import ambit2.user.rest.resource.UserDBResource;

/**
 * See http://ideaconsult.github.io/examples-ambit/apidocs/#!/myaccount/
 * assignRignts
 * 
 * @author nina
 * 
 * @param <T>
 */
public class UserByURIResource<T> extends UserDBResource<T> {
	protected boolean addPublicGroup = false;
	private static final String param_q = "q";
	private static final String param_bundle_uri = "bundle_uri";
	private static final String param_mode = "mode";

	@Override
	public RepresentationConvertor createJSONConvertor(Variant variant)
			throws AmbitException, ResourceException {
		String usersdbname = getContext().getParameters().getFirstValue(
				AMBITConfig.users_dbname.name());
		return new OutputWriterConvertor(new SimpleUserJSONReporter(
				getRequest(), usersdbname, addPublicGroup),
				MediaType.APPLICATION_JSON);
	}

	public void check(Context context, Request request, Response response)
			throws ResourceException {
		if (getClientInfo() == null || getClientInfo().getUser() == null)
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
	}

	@Override
	protected ReadUser createQuery(Context context, Request request,
			Response response) throws ResourceException {
		if (!Method.GET.equals(request.getMethod()))
			return null;
		check(context, request, response);
		String usersdbname = getContext().getParameters().getFirstValue(
				AMBITConfig.users_dbname.name());
		Form form = request.getResourceRef().getQueryAsForm();
		Object search_value = null;
		try {
			search_value = form.getFirstValue(param_q);
			if (search_value == null)
				search_value = form.getFirstValue("search");
			if (search_value != null) {
				ReadUser query = new ReadUser();

				DBUser user = new DBUser();
				String s = String.format("^%s", search_value.toString());
				user.setLastname(s);
				user.setFirstname(s);
				query.setValue(user);
				addPublicGroup = true;
				return query;
			}
		} catch (Exception x) {
			search_value = null;
		}

		try {
			search_value = form.getFirstValue(param_bundle_uri);
			Integer idbundle = search_value == null ? null : getIdBundle(
					search_value, request);
			SubstanceEndpointsBundle bundle = readBundle(idbundle);
			ReadUserByBundleNumber query = new ReadUserByBundleNumber();
			query.setDatabaseName(usersdbname);
			query.setFieldname(bundle.getBundle_number().toString());
			search_value = form.getFirstValue(param_mode);
			if ("W".equals(search_value))
				query.setAllowWrite(true);
			else
				query.setAllowWrite(false);
			return query;
		} catch (Exception x) {
			search_value = null;
		}
		throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
	}

	protected SubstanceEndpointsBundle readBundle(int idbundle)
			throws ResourceException {
		SubstanceEndpointsBundle dataset = new SubstanceEndpointsBundle();
		dataset.setID(idbundle);
		Connection c = null;
		ResultSet rs = null;
		QueryExecutor xx = null;
		try {
			DBConnection dbc = new DBConnection(getContext(),
					getAmbitConfigFile());
			c = dbc.getConnection();
			Set<_published_status> status = new TreeSet<_published_status>();
			status.add(_published_status.published);
			ReadBundle read = new ReadBundle(status);
			read.setValue(dataset);
			xx = new QueryExecutor();
			xx.setConnection(c);
			rs = xx.process(read);
			while (rs.next()) {
				dataset = read.getObject(rs);
			}
			return dataset;
		} catch (Exception x) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,
					x.getMessage(), x);
		} finally {
			try {
				rs.close();
			} catch (Exception x) {
			}
			try {
				c.close();
			} catch (Exception x) {
			}
			try {
				xx.close();
			} catch (Exception x) {
			}
		}
	}

	/**
	 * 
	 * @param bundle_number
	 *            bundle uuid
	 * @param username
	 *            the user to check as owner
	 * @return bundle id if owner, -1 otherwise
	 * @throws ResourceException
	 */
	protected int readBundleOwner(String bundle_number)
			throws ResourceException {

		String uname = null;
		try {
			uname = getClientInfo().getUser().getIdentifier();
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED);
		}
		if (uname == null)
			throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED);

		Connection c = null;
		ResultSet rs = null;
		QueryExecutor xx = null;
		try {
			DBConnection dbc = new DBConnection(getContext(),
					getAmbitConfigFile());
			c = dbc.getConnection();
			Set<_published_status> status = new TreeSet<_published_status>();
			status.add(_published_status.published);
			BundleOwnerByNumber bundleq = new BundleOwnerByNumber();
			bundleq.setFieldname(bundle_number);
			bundleq.setValue(uname);
			xx = new QueryExecutor();
			xx.setConnection(c);
			rs = xx.process(bundleq);
			while (rs.next()) {
				return bundleq.getObject(rs);
			}
			return 0;

		} catch (Exception x) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,
					x.getMessage(), x);
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception x) {
			}
			try {
				if (c != null)
					c.close();
			} catch (Exception x) {
			}
			try {
				if (xx != null)
					xx.close();
			} catch (Exception x) {
			}
		}
	}

	public String getAmbitConfigFile() {
		return "ambit2/rest/config/ambit2.pref";
	}

	protected Integer getIdBundle(Object bundleURI, Request request) {
		if (bundleURI != null) {
			Object id = OpenTox.URI.bundle.getId(bundleURI.toString(),
					request.getRootRef());
			if (id != null && (id instanceof Integer))
				return (Integer) id;
		}
		return null;
	}

	@Override
	public RepresentationConvertor createConvertor(Variant variant)
			throws AmbitException, ResourceException {
		String filenamePrefix = getRequest().getResourceRef().getPath();
		return createJSONConvertor(variant);
	}

	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		return super.post(entity, variant);
	}

	@Override
	protected Representation post(Representation entity)
			throws ResourceException {
		return super.post(entity);
	}

	@Override
	protected ReadUser<T> createUpdateQuery(Method method, Context context,
			Request request, Response response) throws ResourceException {
		if (Method.POST.equals(method)) {
			return null;
		}
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}

	@Override
	protected void check(Form form, Method method, boolean async,
			Reference reference) throws Exception {
		if (!Method.POST.equals(method))
			throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}

	@Override
	protected CallableProtectedTask<String> createCallable(Method method,
			Form form, DBUser item) throws ResourceException {
		String usersdbname = getContext().getParameters().getFirstValue(
				AMBITConfig.users_dbname.name());
		Connection conn = null;
		try {
			String bundle_uri = form.getFirstValue("bundle_number");

			int bundleid = readBundleOwner(bundle_uri);
			if (bundleid <= 0)
				throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED);

			int hd = ((AmbitApplication) getApplication()).getHOMEPAGE_DEPTH();

			DBConnection dbc = new DBConnection(getApplication().getContext(),
					getConfigFile());
			conn = dbc.getConnection(30, true, 8);
			if (bundle_uri != null)
				return new CallableBundlePolicyCreator(
						method,
						form,
						getRequest().getRootRef().toString(),
						conn,
						getToken(),
						usersdbname == null ? getDefaultUsersDB() : usersdbname,
						hd);
			else
				return new CallablePolicyUsersCreator(
						method,
						form,
						getRequest().getRootRef().toString(),
						conn,
						getToken(),
						usersdbname == null ? getDefaultUsersDB() : usersdbname,
						hd);
		} catch (ResourceException x) {
			try {
				if (conn != null)
					conn.close();
			} catch (Exception xx) {
			}
			throw x;
		} catch (Exception x) {
			try {
				if (conn != null)
					conn.close();
			} catch (Exception xx) {
			}
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL, x);
		} finally {

		}

	}
}
