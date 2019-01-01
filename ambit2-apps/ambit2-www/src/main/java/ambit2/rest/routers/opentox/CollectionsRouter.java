package ambit2.rest.routers.opentox;

import org.restlet.Context;
import org.restlet.routing.Filter;

import ambit2.rest.bundle.BundleDraftMetadataResource;
import ambit2.rest.routers.MyRouter;

/**
 * Placeholder for collections
 * initially bundle drafts only, e.g. related to https://sourceforge.net/p/ambit/bugs/98/
 * @author nina
 *
 */
public class CollectionsRouter extends MyRouter {
	public CollectionsRouter(Context context, Filter authz) {
		super(context);
		attachDefault(BundleDraftMetadataResource.class);
	}

}
