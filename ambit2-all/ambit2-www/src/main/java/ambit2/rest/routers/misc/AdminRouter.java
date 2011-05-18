package ambit2.rest.routers.misc;

import org.restlet.Context;
import org.restlet.routing.Router;

import ambit2.rest.admin.AdminResource;
import ambit2.rest.admin.DatabaseResource;
import ambit2.rest.admin.PolicyResource;
import ambit2.rest.admin.fingerprints.FingerprintResource;
import ambit2.rest.admin.fingerprints.StructuresByFingerprintResource;
import ambit2.rest.dataset.filtered.StatisticsResource;
import ambit2.rest.routers.MyRouter;

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


	}

}
