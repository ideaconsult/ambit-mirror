package ambit2.user.rest.resource;

import java.sql.Connection;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.i.query.IQueryUpdate;
import net.idea.modbcum.p.UpdateExecutor;
import net.idea.modbcum.q.update.AbstractUpdate;
import net.idea.restnet.db.DBConnection;
import net.idea.restnet.db.aalocal.DBRole;
import net.idea.restnet.db.aalocal.user.DeleteUserRole;
import net.idea.restnet.db.aalocal.user.IUser;
import net.idea.restnet.db.aalocal.user.ReadUserRoles;
import net.idea.restnet.user.DBUser;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.config.AMBITConfig;
import ambit2.user.rest.CreateUserRole;

public class RoleDBResource extends AmbitDBQueryResource<ReadUserRoles,String> {

	
	@Override
	public IProcessor<ReadUserRoles, Representation> createConvertor(
			Variant variant) throws AmbitException, ResourceException {
		return null;
	}

	@Override
	protected ReadUserRoles createQuery(Context context, Request request,
			Response response) throws ResourceException {
		return null;
	}

	@Override
	protected Representation get(Variant variant) throws ResourceException {
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}
	@Override
	protected Representation get() throws ResourceException {
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
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
				DBRole role = null;
				if ("data manager role".equals(columnName.toLowerCase())) role = DBRoles.datasetManager;
				else if ("admin role".equals(columnName.toLowerCase())) role = DBRoles.adminRole;
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
}
