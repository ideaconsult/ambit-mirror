package ambit2.user.rest.resource;

import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.query.IQueryUpdate;
import net.idea.modbcum.p.UpdateExecutor;
import net.idea.restnet.db.aalocal.user.IDBConfig;
import net.idea.restnet.u.UserRegistration;
import net.idea.restnet.u.mail.Notification;
import net.idea.restnet.user.DBUser;
import net.idea.restnet.user.db.ReadUser;
import net.idea.restnet.user.resource.UserURIReporter;

import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.user.rest.AMBITCallableUserCreator;
import ambit2.user.rest.ForgottenPasswordReset;
import ambit2.user.rest.ResetCredentials;

public class CallablePasswordReset extends AMBITCallableUserCreator implements IDBConfig {
	protected Object count = null;
	
	public CallablePasswordReset(Method method,UserRegistration reg,
						UserURIReporter<IQueryRetrieval<DBUser>> reporter,
						Form input,
						String baseReference,
						Connection connection,
						String token,
						boolean enableEmailVerification,
						String usersdbname)  {
		super(method,null,reporter, input,baseReference, connection,token,true,enableEmailVerification,usersdbname);
		subject = "AMBIT password reset";
		this.registration = reg;
		emailContent = 
			"Dear %s,\n\n"+
			"Someone requested your %s password reset. Please click the following link to reset the password :\n"+
			"%s%s%s?code=%s\n\n"+
			"If you click the link and it appears to be broken, please copy and paste it into a new browser window.\n\n"+
			"Please note that the link will be invalid if not used within 24 hours " +
			"(before %s). " +
			"If you miss this deadline you should start over the password reset procedure and obtain a new activation code.\n\n"+
			"If you did not request a password reset, please ignore this message and let the request expire on its own.\n\n"+
			"Yours faithfully,\n"+
			"%s\n%s\n";		
	}

	@Override
	protected DBUser getTarget(Form input) throws Exception {
		if (Method.POST.equals(method)) {
			DBUser user = new DBUser();
			user.setEmail(input.getFirstValue(ReadUser.fields.email.name()));
			if (user.getEmail()==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"e-mail address not specified!");
			user.setUserName(input.getFirstValue(ReadUser.fields.username.name()));
			return user;
		} else if (Method.PUT.equals(method)) {
			String pwd1 = input.getFirstValue("pwd1");
			String pwd2 = input.getFirstValue("pwd2");
			if ((pwd1==null) || (pwd2==null) || !pwd1.equals(pwd2)) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Invalid password!");
			DBUser user = new DBUser();
			user.setPassword(pwd1);
			return user;
		}
 		return user;
	}
	
	@Override
	protected IQueryUpdate<? extends Object, DBUser> createUpdate(DBUser user)
			throws Exception {
		if (Method.POST.equals(method)) {
			registration = new UserRegistration();
			registration.setTitle("Forgotten password");
			registration.setTimestamp_created(System.currentTimeMillis());
			user.setRegisteredAt(registration.getTimestamp_created());
			return  new ForgottenPasswordReset(user,registration,getDatabaseName());
		} else if (Method.PUT.equals(method)) {
			ResetCredentials q = new ResetCredentials(24,registration); //24h
			if ((user==null)||(registration==null)) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
			q.setObject(user);
			q.setDatabaseName(getDatabaseName());
			return q;
		}
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}

	@Override
	protected Object executeQuery(IQueryUpdate<? extends Object, DBUser> query)	throws Exception {
		exec = new UpdateExecutor<IQueryUpdate>();
		exec.setConnection(connection);
		count = exec.process(query);
		return count;
	}
	@Override
	protected String getURI(DBUser target, Method method) throws Exception {
		if ((count!=null) && Integer.parseInt(count.toString())==0) {
			//update failed
			return String.format("%s%s%s", baseReference, Resources.forgotten, Resources.failed);
		}
		if (Method.POST.equals(method) && registration!=null && target != null && target.getEmail()!=null) {
			Date registeredAt = new Date(target.getRegisteredAt());
			Calendar cal = Calendar.getInstance();
		    cal.setTime(registeredAt);
		    cal.add(Calendar.DATE, 1);
			Notification notification = new Notification(getConfig());
			notification.sendNotification(target.getEmail(), 
					String.format("%s ",subject), 
					String.format(emailContent,
							target.getUserName(),
							getSystemName(),
							baseReference,
							Resources.forgotten,
							Resources.confirm,
							registration.getConfirmationCode(),
							dateFormat.format(cal.getTime()),
							getSystemName(),
							getSenderName(),getSender()),
					"text/plain");
			return String.format("%s%s%s", baseReference, Resources.forgotten, Resources.notify);
		} else if (Method.PUT.equals(method) && registration!=null && target != null && target.getPassword()!=null) {
			return String.format("%s%s", baseReference, Resources.login);
		} else	throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);	
		
	}
	
}
