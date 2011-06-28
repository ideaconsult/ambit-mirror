package ambit2.rest.routers.opentox;

import org.restlet.Context;
import org.restlet.routing.Router;

import ambit2.rest.dataset.DatasetCompoundResource;
import ambit2.rest.routers.misc.DataEntryRouter;


public class CompoundInDatasetRouter extends CompoundRouter {
	
	public CompoundInDatasetRouter(Context context,
			FeaturesRouter featuresRouter,
			DataEntryRouter tupleRouter,
			Router smartsRouter) {
		super(context,  featuresRouter,  tupleRouter, smartsRouter);
	}
	@Override
	public void attachDefault() {
		attachDefault(DatasetCompoundResource.class);
	}
}
