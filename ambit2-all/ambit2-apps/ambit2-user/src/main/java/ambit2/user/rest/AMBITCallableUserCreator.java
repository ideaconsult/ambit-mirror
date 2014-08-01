package ambit2.user.rest;

import java.sql.Connection;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.restnet.db.aalocal.DBRole;
import net.idea.restnet.user.CallableUserCreator;
import net.idea.restnet.user.DBUser;
import net.idea.restnet.user.resource.UserURIReporter;

import org.restlet.data.Form;
import org.restlet.data.Method;

import ambit2.user.rest.resource.DBRoles;

public class AMBITCallableUserCreator extends CallableUserCreator {
	
	public AMBITCallableUserCreator(Method method,DBUser item,
						UserURIReporter<IQueryRetrieval<DBUser>> reporter,
						Form input,
						String baseReference,
						Connection connection,
						String token,
						boolean passwordChange,
						boolean enableEmailVerification,
						String usersdbname)  {
		super(method,item,reporter, input,baseReference, connection,token,passwordChange,enableEmailVerification,usersdbname);
		subject = "AMBIT User Confirmation";
	}

	@Override
	protected DBRole getDefaultRole() {
		return DBRoles.userRole;
	}
	
	@Override
	protected String getConfig() {
		return "ambit2/rest/config/config.prop";
	}

	@Override
	protected String getSender() {
		return "AMBIT2";
	}


	@Override
	protected String getSenderName() {
		return "The Support Team";
	}


	@Override
	protected String getSystemName() {
		return "AMBIT2";
	}
}