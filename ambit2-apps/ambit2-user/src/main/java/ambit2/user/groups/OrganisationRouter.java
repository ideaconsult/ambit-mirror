package ambit2.user.groups;

import net.idea.restnet.c.routers.MyRouter;

import org.restlet.Context;

public class OrganisationRouter extends MyRouter {
	public OrganisationRouter(Context context) {
		super(context);
		attachDefault(OrganisationDBResource.class);
		attach(String.format("/{%s}",OrganisationDBResource.resourceKey), OrganisationDBResource.class);
	
	}
}
