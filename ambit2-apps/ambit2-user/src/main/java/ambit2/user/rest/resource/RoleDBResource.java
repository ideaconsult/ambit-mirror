package ambit2.user.rest.resource;

import java.sql.Connection;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.i.query.IQueryUpdate;
import net.idea.modbcum.p.UpdateExecutor;
import net.idea.modbcum.q.update.AbstractUpdate;
import net.idea.restnet.db.DBConnection;
import net.idea.restnet.db.aalocal.DBRole;
import net.idea.restnet.db.aalocal.user.DeleteUserRole;
import net.idea.restnet.db.aalocal.user.IUser;
import net.idea.restnet.db.aalocal.user.ReadRole;
import net.idea.restnet.db.aalocal.user.RoleJSONReporter;
import net.idea.restnet.db.convertors.OutputWriterConvertor;
import net.idea.restnet.u.RegistrationStatus;
import net.idea.restnet.u.db.UpdateRegistrationStatus;
import net.idea.restnet.user.DBUser;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.CacheDirective;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.config.AMBITConfig;
import ambit2.user.rest.CreateUserRole;

public class RoleDBResource extends AmbitDBQueryResource<IQueryRetrieval<String>,String> {

	
	public RoleDBResource() {
		super();
		setHtmlbyTemplate(true);
	}
	@Override
	public String getTemplateName() {
		return "a/roles.ftl";
	}
	@Override
	public IProcessor<IQueryRetrieval<String>, Representation> createConvertor(
			Variant variant) throws AmbitException, ResourceException {
			
		if (variant.getMediaType().equals(MediaType.APPLICATION_JAVASCRIPT)) {
			String jsonpcallback = getParams().getFirstValue("jsonp");
			if (jsonpcallback==null) jsonpcallback = getParams().getFirstValue("callback");
			return new OutputWriterConvertor(
					new RoleJSONReporter(getRequest().getResourceRef().toString(),jsonpcallback),
					MediaType.APPLICATION_JAVASCRIPT);				
		} else // if (variant.getMediaType().equals(MediaType.APPLICATION_JSON)) {
			return new OutputWriterConvertor(
					new RoleJSONReporter(getRequest().getResourceRef().toString()),
					MediaType.APPLICATION_JSON);		
	}	

	@Override
	protected IQueryRetrieval<String> createQuery(Context context, Request request,
			Response response) throws ResourceException {
		IQueryRetrieval<String> q = new ReadRole();
		return q;
	}

	

	@Override
	protected Representation put(Representation entity, Variant variant)
			throws ResourceException {
		try {
			Form form = new Form(entity);
			String id = form.getFirstValue("id");
			if (id==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
			if ("".equals(id.trim())) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
			DBUser user = new DBUser();
			try {
				user.setUserName(id);
				//user.setID(Integer.parseInt(id.replace("U", "")));
			} catch (Exception x) {
				 throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,id);
			}
			String columnName = form.getFirstValue("columnName");
			
			if (columnName!=null)  try {
				String cname = columnName.toLowerCase();
				if ("status".equals(cname)) {
					try {
						String usersdbname = getContext().getParameters().getFirstValue(AMBITConfig.users_dbname.name());
						String value = form.getFirstValue("value");
						UpdateRegistrationStatus q = new UpdateRegistrationStatus(user,
								RegistrationStatus.valueOf(value));
						q.setDatabaseName(usersdbname==null?getDefaultUsersDB():usersdbname);
						execUpdate(q);
						return new StringRepresentation(value.toString(),MediaType.TEXT_PLAIN);
					} catch (Exception x) {
						throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,columnName);
					}
				} else {
					DBRole role = null;
					if ("data manager role".equals(cname)) role = DBRoles.datasetManager;
					else if ("admin role".equals(cname)) role = DBRoles.adminRole;
					else throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Unknown role "+columnName);
					AbstractUpdate<DBRole,IUser> query = null;
					Boolean value = Boolean.parseBoolean(form.getFirstValue("value"));
					String usersdbname = getContext().getParameters().getFirstValue(AMBITConfig.users_dbname.name());
					if (value) {
						CreateUserRole q = new CreateUserRole(role, user);
						q.setDatabaseName(usersdbname==null?getDefaultUsersDB():usersdbname);
						query = q;
					} else {
						DeleteUserRole q = new DeleteUserRole();
						q.setGroup(role);
						q.setObject(user);
						q.setDatabaseName(usersdbname==null?getDefaultUsersDB():usersdbname);
						query = q;
					}
					execUpdate(query);
					return new StringRepresentation(value.toString(),MediaType.TEXT_PLAIN);
				}
			} catch (ResourceException x) {
				throw x;
			} catch (Exception x) {
				 throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
			}
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,columnName);
		} catch (ResourceException x) {
			throw x;
		} catch (IllegalArgumentException x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x);
		} catch (NullPointerException x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x);
		} catch (Exception x) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x);
		}
	}
	
	protected void execUpdate(IQueryUpdate query) throws ResourceException { 
		Connection conn = null;
		UpdateExecutor x = null;
		try {
			DBConnection dbc = new DBConnection(getApplication().getContext(),getConfigFile());
			conn = dbc.getConnection();
			x = new UpdateExecutor();
			x.setConnection(conn);
			x.process(query);
		} catch (Exception e) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,e.getMessage(),e);
		} finally {
			try { if (conn != null) conn.close(); } catch (Exception xx) {}
			try { if (x !=null) x.close(); } catch (Exception xx) {}
		}			
	}
	
	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}
	@Override
	protected void setCacheHeaders() {
		getResponse().getCacheDirectives().add(CacheDirective.noCache());
	}
}
