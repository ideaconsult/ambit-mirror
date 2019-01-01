package ambit2.rest.routers.opentox;

import org.restlet.Context;

import ambit2.rest.property.PropertyResource;
import ambit2.rest.property.annotations.PropertyAnnotationResource;
import ambit2.rest.routers.MyRouter;

/**
 * OpenTox Features /feature
 */
public class FeaturesRouter extends MyRouter {

	public FeaturesRouter(Context context) {
		super(context);
		init();
	}

	protected void init() {

		attachDefault(PropertyResource.class);
		attach(PropertyResource.featuredefID, PropertyResource.class);
		attach(String.format("%s%s", PropertyResource.featuredefID,
				PropertyAnnotationResource.annotation),
				PropertyAnnotationResource.class);
	}

}
