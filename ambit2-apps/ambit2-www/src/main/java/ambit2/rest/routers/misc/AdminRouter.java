package ambit2.rest.routers.misc;

import org.restlet.Context;
import org.restlet.routing.Router;

import ambit2.rest.aa.opensso.policy.OpenSSOPoliciesResource;
import ambit2.rest.aa.opensso.policy.OpenSSOPolicyResource;
import ambit2.rest.admin.AdminResource;
import ambit2.rest.admin.DatabaseResource;
import ambit2.rest.admin.fingerprints.FingerprintResource;
import ambit2.rest.admin.fingerprints.StructuresByFingerprintResource;
import ambit2.rest.dataset.filtered.StatisticsResource;
import ambit2.rest.routers.MyRouter;
import ambit2.user.rest.resource.AmbitRESTPolicyResource;
import ambit2.user.rest.resource.PwdResetResource;
import ambit2.user.rest.resource.Resources;
import ambit2.user.rest.resource.RoleDBResource;

/**
 * /admin 
 * @author nina
 *
 */
public class AdminRouter extends MyRouter {

	public AdminRouter(Context context) {
		super(context);
		attachDefault(AdminResource.class);
		/**
		 * Database info and admin interface to create the database
		 */
		attach(String.format("/%s",DatabaseResource.resource),DatabaseResource.class);
		/**
		 * Policy creation
		 */
		attach(String.format("/%s",OpenSSOPoliciesResource.resource),OpenSSOPoliciesResource.class);
		attach(String.format("/%s/{%s}",OpenSSOPoliciesResource.resource,OpenSSOPolicyResource.policyKey),OpenSSOPolicyResource.class);
		
		/**
		 * Fingerprint statistincs
		 */
		Router adminFingerprintRouter = new MyRouter(getContext()); //to browse fingerprints/stats
		adminFingerprintRouter.attachDefault(FingerprintResource.class);
		adminFingerprintRouter.attach(String.format("/{%s}",FingerprintResource.resourceKey),FingerprintResource.class);
		adminFingerprintRouter.attach(String.format("/{%s}%s",FingerprintResource.resourceKey,StructuresByFingerprintResource.resource),StructuresByFingerprintResource.class);
		attach(FingerprintResource.resource,adminFingerprintRouter);
		/**
		 * /stats - database statistics
		 */
		attach(StatisticsResource.resource,StatisticsResource.class);
		attach(String.format("%s/{%s}",StatisticsResource.resource,StatisticsResource.resourceKey),
				StatisticsResource.class);	
		
		attach(Resources.role, RoleDBResource.class);
		attach(Resources.restpolicy, AmbitRESTPolicyResource.class);
		attach(String.format("%s/{%s}",Resources.restpolicy,AmbitRESTPolicyResource.resourceid),AmbitRESTPolicyResource.class);
		
		attach(String.format("%s%s",Resources.user,Resources.reset), PwdResetResource.class);		

	}

}
