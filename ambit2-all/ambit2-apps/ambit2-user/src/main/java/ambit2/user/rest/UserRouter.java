package ambit2.user.rest;

import java.util.UUID;

import net.idea.restnet.aa.cookie.CookieAuthenticator;
import net.idea.restnet.aa.local.UserLoginPOSTResource;
import net.idea.restnet.aa.local.UserLogoutPOSTResource;
import net.idea.restnet.c.routers.MyRouter;
import net.idea.restnet.db.aalocal.DBVerifier;
import net.idea.restnet.db.aalocal.DbEnroller;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.routing.Filter;
import org.restlet.routing.Router;
import org.restlet.security.User;

import ambit2.base.config.AMBITConfig;
import ambit2.user.aa.AMBITLoginFormResource;
import ambit2.user.aa.AMBITLoginPOSTResource;
import ambit2.user.aa.AMBITLogoutPOSTResource;
import ambit2.user.aa.AmbitPolicyAuthorizer;
import ambit2.user.groups.OrganisationRouter;
import ambit2.user.groups.ProjectRouter;
import ambit2.user.rest.resource.AMBITRegistrationNotifyResource;
import ambit2.user.rest.resource.MyAccountPwdResetResource;
import ambit2.user.rest.resource.MyAccountResource;
import ambit2.user.rest.resource.PwdForgottenConfirmResource;
import ambit2.user.rest.resource.PwdForgottenFailedResource;
import ambit2.user.rest.resource.PwdForgottenNotifyResource;
import ambit2.user.rest.resource.PwdForgottenResource;
import ambit2.user.rest.resource.RegistrationConfirmResource;
import ambit2.user.rest.resource.RegistrationResource;
import ambit2.user.rest.resource.Resources;
import ambit2.user.rest.resource.UserDBResource;

public class UserRouter extends MyRouter {
	public UserRouter(Context context ,OrganisationRouter orgRouter, ProjectRouter projectRouter) {
		//, AlertRouter alertRouter) {
		super(context);
		attachDefault(UserDBResource.class);
		attach(String.format("/{%s}",UserDBResource.resourceKey), UserDBResource.class);
		attach(String.format("/{%s}%s",UserDBResource.resourceKey,Resources.project),projectRouter);
		attach(String.format("/{%s}%s",UserDBResource.resourceKey,Resources.organisation), orgRouter);
		
		/*
		attach(String.format("/{%s}%s",UserDBResource.resourceKey,Resources.alert), alertRouter);
		*/
	}
	
	public Restlet attachLocalDBAuthNZ(Router router,Context context,String secret, long sessionLength) {
		String usersdbname = getContext().getParameters().getFirstValue(AMBITConfig.users_dbname.name());
		if (usersdbname==null) usersdbname = "ambit_users";
		Filter auth = createCookieAuthenticator(context,usersdbname,"ambit2/rest/config/config.prop",secret,sessionLength);
		Router setCookieUserRouter = new MyRouter(context);
		/*
		Filter authz = new ProtocolAuthorizer(testAuthZ,DBRoles.adminRole);
		auth.setNext(authz);
		authz.setNext(setCookieUserRouter);
		*/
		auth.setNext(setCookieUserRouter);
		setCookieUserRouter.attach(Resources.login, AMBITLoginFormResource.class);
		
		MyRouter myAccountRouter = new MyRouter(context);
		myAccountRouter.attachDefault(MyAccountResource.class);
		//AlertRouter alertRouter = new AlertRouter(context);
		//myAccountRouter.attach(Resources.alert,alertRouter);
		myAccountRouter.attach(Resources.reset,MyAccountPwdResetResource.class);
		//myAccountRouter.attach(Resources.protocol,MyObservationsResource.class);
		setCookieUserRouter.attach(Resources.myaccount, myAccountRouter);
		setCookieUserRouter.attach(Resources.user, new UserRouter(context,null,null));
		
		/*
		setCookieUserRouter.attach(String.format("%s/{%s}",Resources.dataset,DatasetResource.datasetKey), DatasetResource.class);
		setCookieUserRouter.attach(Resources.admin, createAdminRouter());
		
		setCookieUserRouter.attach("", UIResource.class);
		setCookieUserRouter.attach("/", UIResource.class);
		*/
		
		Router protectedRouter = new MyRouter(context);
		protectedRouter.attach("/roles", AMBITLoginFormResource.class);
		protectedRouter.attach(String.format("/%s", UserLoginPOSTResource.resource),AMBITLoginPOSTResource.class);
		protectedRouter.attach(String.format("/%s", UserLogoutPOSTResource.resource),AMBITLogoutPOSTResource.class);

		auth = createCookieAuthenticator(context,usersdbname,"ambit2/rest/config/config.prop",secret,sessionLength);
		auth.setNext(protectedRouter);
		router.attach("/protected", auth);
		
		router.attach(Resources.register, RegistrationResource.class);
		router.attach(String.format("%s%s", Resources.register, Resources.confirm), RegistrationConfirmResource.class);
		router.attach(String.format("%s%s", Resources.register, Resources.notify), AMBITRegistrationNotifyResource.class);

		router.attach(Resources.forgotten, PwdForgottenResource.class);
		router.attach(String.format("%s%s", Resources.forgotten, Resources.confirm), PwdForgottenConfirmResource.class);
		router.attach(String.format("%s%s", Resources.forgotten, Resources.notify), PwdForgottenNotifyResource.class);
		router.attach(String.format("%s%s", Resources.forgotten, Resources.failed), PwdForgottenFailedResource.class);

		
		return  setCookieUserRouter;
	}
	
	
	public static Filter createCookieAuthenticator(Context context, 
				String default_userdb, String config, String secret, long sessionLength) {

		String usersdbname = context.getParameters().getFirstValue(AMBITConfig.users_dbname.name());
		if (usersdbname==null) usersdbname = "ambit_users";
		
		CookieAuthenticator cookieAuth = new CookieAuthenticator(context,
				usersdbname, (secret==null?UUID.randomUUID().toString():secret).getBytes());
		cookieAuth.setCookieName("ambitdb");
		if (sessionLength<600000) sessionLength =  600000; //10 min in case the config is broken
		cookieAuth.setSessionLength(sessionLength);
		cookieAuth.setLoginFormPath("/login");
		cookieAuth.setLoginPath("/provider/signin");
		cookieAuth.setLogoutPath("/provider/signout");

		cookieAuth.setVerifier(new DBVerifier(context, config,usersdbname) {
				@Override
				public int verify(Request request, Response response) {
					if (request.getResourceRef().toString().indexOf("/provider/")>=0)
						return super.verify(request, response);
					else { //just check the cookie
						int result = RESULT_VALID;
						if (request.getChallengeResponse() != null) {
							String identifier = getIdentifier(request, response);
							request.getClientInfo().setUser(new User(identifier));
						}
						return result;
					}
				}
			});
		cookieAuth.setEnroler(new DbEnroller(context, config,default_userdb));
		return cookieAuth;
	}

	public static Filter createPolicyAuthorizer(Context context,String default_userdb, String config ) {
		String usersdbname = context.getParameters().getFirstValue(AMBITConfig.users_dbname.name());
		if (usersdbname==null) usersdbname = "ambit_users";
		 return new AmbitPolicyAuthorizer(context,config,usersdbname);
	}
	
}
