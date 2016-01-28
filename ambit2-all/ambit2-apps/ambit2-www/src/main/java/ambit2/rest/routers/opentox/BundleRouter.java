package ambit2.rest.routers.opentox;

import org.restlet.Context;
import org.restlet.routing.Filter;

import ambit2.rest.OpenTox;
import ambit2.rest.bundle.BundleMetadataResource;
import ambit2.rest.bundle.BundlePropertyResource;
import ambit2.rest.bundle.BundleSubstanceResource;
import ambit2.rest.bundle.BundleSubstanceStudyResource;
import ambit2.rest.bundle.BundleSummaryResource;
import ambit2.rest.bundle.BundleVersionsResource;
import ambit2.rest.bundle.dataset.BundleChemicalsResource;
import ambit2.rest.bundle.dataset.BundleDatasetResource;
import ambit2.rest.bundle.dataset.BundleMatrixResource;
import ambit2.rest.dataset.MetadatasetResource;
import ambit2.rest.routers.MyRouter;
import ambit2.rest.substance.SubstanceResource;

public class BundleRouter extends MyRouter {

	public BundleRouter(Context context, Filter authz) {
		super(context);
		// attach(String.format("/{%s}",DatasetResource.datasetKey), new
		// DatasetRouter(getContext(),cmpdRouter,tupleRouter,
		// smartsRouter,similarityRouter));
		attachDefault(BundleMetadataResource.class);

		MyRouter bundleidrouter = new MyRouter(context);
		bundleidrouter.attachDefault(BundleMetadataResource.class);
		bundleidrouter.attach(
				MetadatasetResource.metadata,
				BundleMetadataResource.class);
		bundleidrouter.attach(
				SubstanceResource.substance,
				BundleSubstanceResource.class);
		bundleidrouter.attach("/property",
				BundlePropertyResource.class);
		bundleidrouter.attach(
				OpenTox.URI.dataset.getURI(),
				BundleDatasetResource.class);
		bundleidrouter.attach("/matrix",
				BundleMatrixResource.class);
		bundleidrouter.attach("/matrix/{list}",
				BundleMatrixResource.class);
		bundleidrouter.attach(
				 OpenTox.URI.compound.getURI(),
				BundleChemicalsResource.class);
		bundleidrouter.attach(
				BundleSubstanceStudyResource.resource,
				BundleSubstanceStudyResource.class);
		bundleidrouter.attach(
				BundleSummaryResource.resource,
				BundleSummaryResource.class);
		bundleidrouter.attach("/version",
				BundleVersionsResource.class);

		if (authz == null)
			attach("/{idbundle}", bundleidrouter);
		else {
			authz.setNext(bundleidrouter);
			attach("/{idbundle}", authz);
		}

	}

}
