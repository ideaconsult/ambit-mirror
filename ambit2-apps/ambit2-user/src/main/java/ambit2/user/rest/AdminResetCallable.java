package ambit2.user.rest;

import java.sql.Connection;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.query.IQueryUpdate;
import net.idea.restnet.db.aalocal.DBRole;
import net.idea.restnet.db.aalocal.user.IDBConfig;
import net.idea.restnet.db.update.CallableDBUpdateTask;
import net.idea.restnet.u.UserCredentials;
import net.idea.restnet.user.DBUser;
import net.idea.restnet.user.resource.UserURIReporter;

import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.user.aa.AdminUpdateCredentials;

public class AdminResetCallable extends CallableDBUpdateTask<DBUser,Form,String> implements IDBConfig {
	protected UserURIReporter<IQueryRetrieval<DBUser>> reporter;
	protected DBUser user;
	protected UserCredentials credentials;
	protected String aadbname;
	
	public AdminResetCallable(Method method,DBUser item,UserURIReporter<IQueryRetrieval<DBUser>> reporter,
						Form input,
						String baseReference,
						Connection connection,
						String token,
						boolean passwordChange,
						boolean enableEmailVerification,
						String usersdbname)  {
		super(method, input,connection,token);
		this.reporter = reporter;
		this.user = item;
		this.baseReference = baseReference;
		setDatabaseName(usersdbname);
	}

	@Override
	protected DBUser getTarget(Form input) throws Exception {
	
		if (input != null && (input.getFirstValue("pwd1")!=null) && (input.getFirstValue("pwd2")!=null) 
				&& input.getFirstValue("pwd2").equals(input.getFirstValue("pwd1"))
				&& (input.getFirstValue("pwd1").length()>=6)
				) 
			credentials = new UserCredentials(
					input.getFirstValue("pwd1"),
					input.getFirstValue("pwd2")
				);
		else 
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		
		if (Method.PUT.equals(method)) 
			if (this.user.getUserName()!= null && (this.user.getID()>0)) {
				user.setCredentials(credentials);
		 		return user;				
			}	
		throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);			
	}

	protected DBRole getDefaultRole() {
		return new DBRole("user", "Any user");
	}
	@Override
	protected IQueryUpdate<? extends Object, DBUser> createUpdate(DBUser user)
			throws Exception {
		if (Method.PUT.equals(method)) return new AdminUpdateCredentials(credentials,user,getDatabaseName());
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}

	@Override
	protected String getURI(DBUser user) throws Exception {
		return reporter.getURI(user);
	}

	@Override
	public String toString() {
		return String.format("Password reset");
	}

	@Override
	public void setDatabaseName(String name) {
		aadbname = name;
	}

	@Override
	public String getDatabaseName() {
		return aadbname;
	}



}