package ambit2.rest.routers.opentox;

import org.restlet.Context;
import org.restlet.routing.Router;

import ambit2.rest.dataset.DatasetResource;
import ambit2.rest.dataset.DatasetsResource;
import ambit2.rest.routers.MyRouter;

public class DatasetsRouter extends MyRouter {

	public DatasetsRouter(Context context,CompoundInDatasetRouter cmpdRouter, Router smartsRouter, Router similarityRouter) {
		super(context);
		
		attachDefault(DatasetsResource.class);
		attach(String.format("/{%s}",DatasetResource.datasetKey), 
					new DatasetRouter(getContext(),cmpdRouter, smartsRouter,similarityRouter));	
	}

	
}
