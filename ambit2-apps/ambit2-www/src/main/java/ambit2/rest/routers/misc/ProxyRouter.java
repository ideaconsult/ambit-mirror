package ambit2.rest.routers.misc;

import org.restlet.Context;

import ambit2.rest.ProxyResource;
import ambit2.rest.routers.MyRouter;
import ambit2.user.rest.resource.Resources;

public class ProxyRouter extends MyRouter {

	public ProxyRouter(Context context) {
		super(context);
		attachDefault(ProxyResource.class);
		attach(String.format("%s/{media}", Resources.proxy), ProxyResource.class);
	}

}
