package ambit2.rest.routers.opentox;

import org.restlet.Context;

import ambit2.rest.bundle.BundleMetadataResource;
import ambit2.rest.dataset.MetadatasetResource;
import ambit2.rest.routers.MyRouter;

public class BundleRouter extends MyRouter {

	public BundleRouter(Context context) {
		super(context);
		//attach(String.format("/{%s}",DatasetResource.datasetKey), new DatasetRouter(getContext(),cmpdRouter,tupleRouter, smartsRouter,similarityRouter));
		attachDefault(BundleMetadataResource.class);
		attach(String.format("/{idbundle}%s",MetadatasetResource.metadata), BundleMetadataResource.class);
	}

}
