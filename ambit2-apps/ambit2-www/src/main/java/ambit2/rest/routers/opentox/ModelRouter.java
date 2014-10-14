package ambit2.rest.routers.opentox;

import org.restlet.Context;

import ambit2.rest.model.ModelResource;
import ambit2.rest.property.PropertyModelResource;
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
		attachDefault(ModelResource.class);
		attach(String.format("/{%s}",ModelResource.resourceKey),ModelResource.class);
		attach(String.format("/{%s}%s",
									ModelResource.resourceKey,
									PropertyModelResource.resourceID),
							PropertyModelResource.class);
	}
}
