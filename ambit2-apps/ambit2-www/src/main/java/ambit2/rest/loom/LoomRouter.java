package ambit2.rest.loom;

import org.restlet.Context;

import ambit2.rest.routers.MyRouter;
/**
 * Entry point for remote services wrappers. See {@link LoomResource}
 * /loom/{resourcetype}/{resourceid}
 * /loom/{resourcetype}/{resourceid}/{command}
 * @author nina
 *
 */
public class LoomRouter  extends MyRouter {

	public LoomRouter(Context context) {
		super(context);
		attachDefault(LoomResource.class);
		/**
		 * Database info and admin interface to create the database
		 */
		attach(String.format("/{%s}",LoomResource.resourceType),LoomResource.class);
		attach(String.format("/{%s}/{%s}/%s",LoomResource.resourceType,LoomResource.resourceID,"ping"),PingResource.class);

	}
}
