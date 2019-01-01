package ambit2.rest.routers.opentox;

import org.restlet.Context;
import org.restlet.routing.Router;

import ambit2.rest.dataset.DatasetCompoundResource;


public class CompoundInDatasetRouter extends CompoundRouter {
	
	public CompoundInDatasetRouter(Context context,
			FeaturesRouter featuresRouter,
			Router smartsRouter) {
		super(context,  featuresRouter,   smartsRouter);
	}
	@Override
	public void attachDefault() {
		attachDefault(DatasetCompoundResource.class);
	}
}
