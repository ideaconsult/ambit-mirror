package net.idea.ambit.app.router;

import org.restlet.Context;

import ambit2.rest.routers.MyRouter;

/**
 * OpenTox models  
 * /model
 * /model/id 
 */
public class ModelRouter extends MyRouter {
	public ModelRouter(Context context) {
		super(context);
		init();
	}
	protected void init() {
		/*
		attachDefault(ModelResource.class);
		attach(String.format("/{%s}",MLResources.model_resourcekey),ModelResource.class);
		attach(String.format("/{%s}%s",
				MLResources.model_resourcekey,
									PropertyModelResource.resourceID),
							PropertyModelResource.class);
							*/
	}
}
