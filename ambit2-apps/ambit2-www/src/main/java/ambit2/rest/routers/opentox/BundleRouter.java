package ambit2.rest.routers.opentox;

import org.restlet.Context;

import ambit2.rest.OpenTox;
import ambit2.rest.bundle.BundleMetadataResource;
import ambit2.rest.bundle.BundlePropertyResource;
import ambit2.rest.bundle.BundleSubstanceResource;
import ambit2.rest.bundle.BundleSubstanceStudyResource;
import ambit2.rest.bundle.BundleSummaryResource;
import ambit2.rest.bundle.dataset.BundleChemicalsResource;
import ambit2.rest.bundle.dataset.BundleDatasetResource;
import ambit2.rest.bundle.dataset.BundleMatrixResource;
import ambit2.rest.dataset.MetadatasetResource;
import ambit2.rest.routers.MyRouter;
import ambit2.rest.substance.SubstanceResource;

public class BundleRouter extends MyRouter {

	public BundleRouter(Context context) {
		super(context);
		//attach(String.format("/{%s}",DatasetResource.datasetKey), new DatasetRouter(getContext(),cmpdRouter,tupleRouter, smartsRouter,similarityRouter));
		attachDefault(BundleMetadataResource.class);
		attach("/{idbundle}", BundleMetadataResource.class);
		attach(String.format("/{idbundle}%s",MetadatasetResource.metadata), BundleMetadataResource.class);
		attach(String.format("/{idbundle}%s",SubstanceResource.substance), BundleSubstanceResource.class);
		attach(String.format("/{idbundle}/property"), BundlePropertyResource.class);
		attach(String.format("/{idbundle}%s",OpenTox.URI.dataset.getURI()), BundleDatasetResource.class);
		attach(String.format("/{idbundle}%s","/matrix"), BundleMatrixResource.class);
		attach(String.format("/{idbundle}%s","/matrix/{list}"), BundleMatrixResource.class);
		attach(String.format("/{idbundle}%s",OpenTox.URI.compound.getURI()), BundleChemicalsResource.class);
		attach(String.format("/{idbundle}%s",BundleSubstanceStudyResource.resource), BundleSubstanceStudyResource.class);
		attach(String.format("/{idbundle}%s",BundleSummaryResource.resource), BundleSummaryResource.class);

		
	}

}
